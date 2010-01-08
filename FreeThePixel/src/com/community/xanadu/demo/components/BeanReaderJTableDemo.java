package com.community.xanadu.demo.components;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel;

import com.community.xanadu.components.editor.ButtonCellEditor;
import com.community.xanadu.components.table.BeanReaderJTable;

public class BeanReaderJTableDemo {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				try {
					UIManager.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
					JFrame.setDefaultLookAndFeelDecorated(true);
				} catch (UnsupportedLookAndFeelException e) {
				}

				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(800, 300);

				// declare the type + column +title
				final BeanReaderJTable<Component> table = new BeanReaderJTable<Component>(new String[] { "size",
						"size.width", "size.height", "class", "visible", null }, new String[] { "size", "width",
						"height", "class", "is visible", "" });
				frame.getContentPane().add(new JScrollPane(table));

				// populate the table
				table.addRow(table);
				table.addRow(frame.getContentPane());
				table.addRow(frame);
				table.addRow(frame.getRootPane());
				table.addRow(frame.getLayeredPane());

				table.setRowHeight(30);

				// --button renderer
				ButtonCellEditor buttonRenderer = new ButtonCellEditor() {
					@Override
					protected String getText(final JTable table, final int row) {
						return "click me:" + row;
					}

					@Override
					protected void buttonActionPerformed(final int row, final JTable t) {
						System.out.println(table.getSelectedObject());
					}
				};

				table.getColumnModel().getColumn(5).setCellRenderer(buttonRenderer);
				table.getColumnModel().getColumn(5).setCellEditor(buttonRenderer);

				frame.setVisible(true);
			}
		});
	}
}
