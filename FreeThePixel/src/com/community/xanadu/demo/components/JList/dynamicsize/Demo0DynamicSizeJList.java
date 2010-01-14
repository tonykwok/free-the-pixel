package com.community.xanadu.demo.components.JList.dynamicsize;

import java.awt.Component;

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

import org.pushingpixels.substance.api.renderers.SubstanceDefaultListCellRenderer;
import org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel;

public class Demo0DynamicSizeJList {

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
						} else {
							label.setFont(label.getFont().deriveFont(10f));
						}
						return label;
					}
				};
				list.setCellRenderer(renderer);

				for (int i = 0; i < 10; i++) {
					model.addElement("element:" + i);
				}

				f.getContentPane().add(new JScrollPane(list));

				f.setVisible(true);
			}
		});
	}
}
