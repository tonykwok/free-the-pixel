package com.community.xanadu.components.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;
import org.pushingpixels.substance.internal.utils.HashMapKey;

import com.community.xanadu.utils.PaintUtils;
import com.community.xanadu.utils.lazyMap.LazyResettableHashMap;

@SuppressWarnings("serial")
public class IconTableCellRenderer extends SubstanceDefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame f = new JFrame();
				JTable table = new JTable();
				table.setRowHeight(50);
				DefaultTableModel model = new DefaultTableModel() {
					@Override
					public boolean isCellEditable(final int row, final int column) {
						return false;
					}
				};
				model.addColumn("icon");
				model.addColumn("col 2");

				model.addRow(new Object[] { getImageForMain(), "" });
				model.addRow(new Object[] { getImageForMain(), "" });
				model.addRow(new Object[] { getImageForMain(), "" });
				model.addRow(new Object[] { getImageForMain(), "" });

				table.setModel(model);

				table.getColumnModel().getColumn(0).setCellRenderer(new IconTableCellRenderer());

				f.getContentPane().add(new JScrollPane(table));
				f.setSize(500, 500);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setVisible(true);
			}
		});
	}

	private static BufferedImage getImageForMain() {
		BufferedImage image = GraphicsUtilities.createCompatibleTranslucentImage(500, 500);
		Graphics g = image.createGraphics();
		Random r = new Random();

		PaintUtils.fillCircle(g, 500, new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)), 0, 0);
		g.dispose();
		return image;
	}

	private LazyResettableHashMap<ImageIcon> mapImage;

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		JLabel res = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (value != null) {
			if (this.mapImage == null) {
				this.mapImage = new LazyResettableHashMap<ImageIcon>();
			}
			ImageIcon icon = this.mapImage.get(new HashMapKey(value.hashCode()));
			if (icon == null) {
				BufferedImage buff = null;
				int h = table.getRowHeight();
				int w = table.getColumnModel().getColumn(table.convertColumnIndexToModel(column)).getWidth();

				if (value instanceof Image) {

					float ih = ((Image) value).getHeight(null);
					float iw = ((Image) value).getWidth(null);

					float factorH = ih / h;
					float factorW = iw / w;

					float factor = Math.max(factorH, factorW);

					buff = GraphicsUtilities.createCompatibleTranslucentImage((int) (iw / factor), (int) (ih / factor));

					Graphics g = buff.createGraphics();
					g.drawImage((Image) value, 0, 0, buff.getWidth(), buff.getHeight(), null);
					g.dispose();
				} else if (value instanceof Icon) {
					int ih = ((Icon) value).getIconHeight();
					int iw = ((Icon) value).getIconWidth();

					float factorH = ih / h;
					float factorW = iw / w;

					float factor = Math.max(factorH, factorW);

					BufferedImage imageIcon = GraphicsUtilities.createCompatibleTranslucentImage(iw, ih);
					Graphics g = imageIcon.createGraphics();
					((Icon) value).paintIcon(table, g, 0, 0);
					g.dispose();

					buff = GraphicsUtilities.createCompatibleTranslucentImage((int) (iw / factor), (int) (ih / factor));
					g = buff.createGraphics();
					g.drawImage(imageIcon, 0, 0, buff.getWidth(), buff.getHeight(), null);
					g.dispose();
				}
				icon = new ImageIcon(buff);
				this.mapImage.put(new HashMapKey(value.hashCode()), icon);
			}
			res.setIcon(icon);
		} else {
			res.setIcon(null);
		}
		res.setText("");

		return res;
	}

	public void clearCache() {
		this.mapImage.reset();
	}
}