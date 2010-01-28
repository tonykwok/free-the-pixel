package com.community.xanadu.demo.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel;

import com.community.xanadu.components.buttons.shape.CheckButton;
import com.community.xanadu.components.buttons.shape.DeleteButton;
import com.community.xanadu.components.editor.ButtonCellEditor;
import com.community.xanadu.components.table.PanelTableWithOverlayButtons;
import com.community.xanadu.utils.ImageUtils;
import com.community.xanadu.utils.JTableUtils;
import com.community.xanadu.utils.PaintUtils;

public class TableAndActionsDemo {
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
				f.getContentPane().setLayout(new MigLayout("fill"));

				addCellEditorRendererWay(f);
				addOutsideButtonsWay(f);
				addPopupWay(f);
				addOverlayButtonWay(f);
				addOverlayButtonPopupWay(f);
				addCellEditorRendererPopupWay(f);

				f.setSize(800, 800);
				f.setVisible(true);
			}
		});

	}

	public static JTable getTable() {

		JTable table = null;

		final DefaultTableModel dtm = new DefaultTableModel();
		dtm.addColumn("col0");
		dtm.addColumn("col1");
		dtm.addColumn("col2");
		dtm.addColumn("col3");
		dtm.addColumn("col4");

		for (int i = 0; i < 50; i++) {
			dtm.addRow(new Object[] {});
		}

		table = new JTable(dtm);

		return table;

	}

	private static Icon iconValidate = new Icon() {

		@Override
		public void paintIcon(final Component c, final Graphics g, final int x, final int y) {

			final float w = getIconWidth() / 6;
			final float h = getIconHeight() / 6;

			final GeneralPath shape = new GeneralPath();
			shape.moveTo(0, 4 * h);
			shape.lineTo(0.5f * w, 3.5f * h);
			shape.lineTo(2 * w, 5 * h);
			shape.lineTo(5 * w, 1 * h);
			shape.lineTo(6 * w, 1.5f * h);
			shape.lineTo(2 * w, 6 * h);
			shape.closePath();
			g.setColor(Color.green.darker());
			PaintUtils.turnOnAntialias((Graphics2D) g);
			((Graphics2D) g).fill(shape);
		}

		@Override
		public int getIconWidth() {
			return 15;
		}

		@Override
		public int getIconHeight() {
			return 15;
		}
	};

	private static Icon iconRemove = new Icon() {

		@Override
		public void paintIcon(final Component c, final Graphics g, final int x, final int y) {

			final float w = getIconWidth() / 6;
			final float h = getIconHeight() / 6;

			final GeneralPath shape = new GeneralPath();
			shape.moveTo(0, h);
			shape.lineTo(w, 0);
			shape.lineTo(3 * w, 2 * h);
			shape.lineTo(5 * w, 0);
			shape.lineTo(6 * w, h);
			shape.lineTo(4 * w, 3 * h);
			shape.lineTo(6 * w, 5 * h);
			shape.lineTo(5 * w, 6 * h);
			shape.lineTo(3 * w, 4 * h);
			shape.lineTo(w, 6 * h);
			shape.lineTo(0, 5 * h);
			shape.lineTo(2 * w, 3 * h);
			shape.closePath();
			PaintUtils.turnOnAntialias((Graphics2D) g);
			g.setColor(Color.red.darker());
			((Graphics2D) g).fill(shape);
		}

		@Override
		public int getIconWidth() {
			return 15;
		}

		@Override
		public int getIconHeight() {
			return 15;
		}
	};

	public static void addCellEditorRendererWay(final JFrame f) {
		final JTable table = getTable();
		table.setRowHeight(20);
		f.getContentPane().add(new JLabel("Cell editor"), "wrap");
		f.getContentPane().add(new JScrollPane(table), "grow,h 20%,spanx");
		final DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		dtm.addColumn("remove");
		dtm.addColumn("validate");

		final JButton buttonRemoveE = new JButton() {
			@Override
			protected void paintComponent(final Graphics g) {
				g.translate(getWidth() / 2 - 7, 0);
				iconRemove.paintIcon(this, g, 0, 0);
			}
		};

		final JButton buttonValidateE = new JButton() {
			@Override
			protected void paintComponent(final Graphics g) {
				g.translate(getWidth() / 2 - 7, 0);
				iconValidate.paintIcon(this, g, 0, 0);
			}
		};
		final JButton buttonRemoveR = new JButton() {
			@Override
			protected void paintComponent(final Graphics g) {
				g.translate(getWidth() / 2 - 7, 0);
				iconRemove.paintIcon(this, g, 0, 0);
			}
		};

		final JButton buttonValidateR = new JButton() {
			@Override
			protected void paintComponent(final Graphics g) {
				g.translate(getWidth() / 2 - 7, 0);
				iconValidate.paintIcon(this, g, 0, 0);
			}
		};

		final ButtonCellEditor edi1 = new ButtonCellEditor(buttonRemoveE, buttonRemoveR) {
			@Override
			protected void buttonActionPerformed(final int row, final JTable table) {

			}

			@Override
			protected String getText(final JTable table, final int row) {

				return null;
			}
		};

		final ButtonCellEditor edi2 = new ButtonCellEditor(buttonValidateE, buttonValidateR) {
			@Override
			protected void buttonActionPerformed(final int row, final JTable table) {

			}

			@Override
			protected String getText(final JTable table, final int row) {

				return null;
			}
		};

		table.getColumnModel().getColumn(5).setCellRenderer(edi1);
		table.getColumnModel().getColumn(6).setCellRenderer(edi2);

		table.getColumnModel().getColumn(5).setCellEditor(edi1);
		table.getColumnModel().getColumn(6).setCellEditor(edi2);

		JTableUtils.lockWidth(table, 5, 55);
		JTableUtils.lockWidth(table, 6, 55);
	}

	public static void addOutsideButtonsWay(final JFrame f) {
		final JTable table = getTable();
		f.getContentPane().add(new JLabel("Buttons outside the JTable"), "wrap");
		f.getContentPane().add(new JScrollPane(table), "spany 2,pushx,grow,h 20%");
		final JButton removeButton = new JButton("Remove");
		final JButton validateButton = new JButton("Validate");
		f.getContentPane().add(removeButton, "wrap,pushy");
		f.getContentPane().add(validateButton, "wrap,pushy");
	}

	public static void addPopupWay(final JFrame f) {

		final JTable table = getTable();
		f.getContentPane().add(new JLabel("popup"), "wrap");
		f.getContentPane().add(new JScrollPane(table), "spanx,grow,h 20%");
		final JMenuItem itemRemove = new JMenuItem("Remove");
		final JMenuItem itemValidate = new JMenuItem("Validate");
		final JPopupMenu popup = new JPopupMenu();
		popup.add(itemRemove);
		popup.add(itemValidate);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					popup.show(table, e.getX(), e.getY());
				}
			}
		});
	}

	public static void addOverlayButtonWay(final JFrame f) {
		final JTable table = getTable();
		final PanelTableWithOverlayButtons panel = new PanelTableWithOverlayButtons(table, 30);
		final DeleteButton db = new DeleteButton();
		final CheckButton cb = new CheckButton();
		db.setSize(30, 30);
		cb.setSize(30, 30);
		panel.setButtonsXOffset(-30);
		panel.setButtons(new JButton[] { db, cb });
		f.getContentPane().add(new JLabel("Overlay buttons"), "wrap");
		f.getContentPane().add(panel, "spanx,grow,h 20%");
		table.setRowSelectionInterval(0, 0);
	}

	public static void addOverlayButtonPopupWay(final JFrame f) {
		final JTable table = getTable();
		final PanelTableWithOverlayButtons panel = new PanelTableWithOverlayButtons(table, 0);
		final JButton button = new JButton("");

		final JPopupMenu popup = new JPopupMenu();
		popup.add(new JMenuItem("Action 1"));
		popup.add(new JMenuItem("Action 2"));
		popup.add(new JMenuItem("Action 3"));
		popup.add(new JMenuItem("Action 4"));

		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				popup.show(panel, button.getX(), button.getY()+button.getHeight());

			}
		});
		button.setIcon(ImageUtils.getArrowIcon(SwingConstants.SOUTH, Color.BLACK));
		button.setSize(25, 20);
		panel.setButtonsXOffset(-43);
		panel.setButton(button);
		f.getContentPane().add(new JLabel("Overlay buttons + popup"), "wrap");
		f.getContentPane().add(panel, "spanx,grow,h 20%");
		table.setRowSelectionInterval(0, 0);
	}

	public static void addCellEditorRendererPopupWay(final JFrame f) {

		final JTable table = getTable();
		table.setRowHeight(20);
		f.getContentPane().add(new JLabel("Cell editor + popup"), "wrap");
		f.getContentPane().add(new JScrollPane(table), "grow,h 20%,spanx");
		final DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		dtm.addColumn("");

		final JButton buttonE = new JButton();
		buttonE.setIcon(ImageUtils.getArrowIcon(SwingConstants.SOUTH, Color.BLACK));
		final JButton buttonR = new JButton();
		buttonR.setIcon(ImageUtils.getArrowIcon(SwingConstants.SOUTH, Color.BLACK));

		
		final JPopupMenu popup = new JPopupMenu();
		
		popup.add(new JMenuItem("Action 1"));
		popup.add(new JMenuItem("Action 2"));
		popup.add(new JMenuItem("Action 3"));
		popup.add(new JMenuItem("Action 4"));
		
		final ButtonCellEditor edi1 = new ButtonCellEditor(buttonE, buttonR) {
			@Override
			protected void buttonActionPerformed(final int row, final JTable table) {
				final Point loc=MouseInfo.getPointerInfo().getLocation();
				SwingUtilities.convertPointFromScreen(loc, table);
				
				popup.show(table, loc.x, loc.y);
			}

			@Override
			protected String getText(final JTable table, final int row) {

				return null;
			}
		};

		table.getColumnModel().getColumn(5).setCellRenderer(edi1);
		table.getColumnModel().getColumn(5).setCellEditor(edi1);
		JTableUtils.lockWidth(table, 5, 35);

	}

}
