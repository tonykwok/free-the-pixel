package com.community.xanadu.demo.components.JList.dynamicsize;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultListCellRenderer;
import org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel;

import com.community.xanadu.utils.JlistUtils;

public class Demo2DynamicSizeJList {

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
					JFrame.setDefaultLookAndFeelDecorated(true);
				} catch (final UnsupportedLookAndFeelException e) {
				}
				final JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(300, 300);

				final DefaultListModel model = new DefaultListModel();
				final JList list = new JList(model);
				list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

				final ListCellRenderer renderer = new SubstanceDefaultListCellRenderer() {
					@Override
					public Component getListCellRendererComponent(final JList list, final Object value,
							final int index, final boolean isSelected, final boolean cellHasFocus) {

						final JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index,
								isSelected, cellHasFocus);
						if (isSelected) {
							label.setFont(label.getFont().deriveFont(35f));
							label.setPreferredSize(new Dimension(50, 50));
						} else {
							label.setFont(label.getFont().deriveFont(10f));
							label.setPreferredSize(new Dimension(50, 15));
						}

						return label;
					}
				};
				list.setCellRenderer(renderer);

				for (int i = 0; i < 10; i++) {
					model.addElement("element:" + i);
				}

				f.getContentPane().add(new JScrollPane(list));

				list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

					@Override
					public void valueChanged(final ListSelectionEvent e) {
						JlistUtils.computeListSize(list);
					}
				});
				f.setVisible(true);
			}
		});
	}
}
