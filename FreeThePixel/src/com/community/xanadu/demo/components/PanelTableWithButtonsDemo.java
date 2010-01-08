package com.community.xanadu.demo.components;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

import org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel;

import com.community.xanadu.components.buttons.shape.DeleteButton;
import com.community.xanadu.components.table.PanelTableWithButtons;

public class PanelTableWithButtonsDemo {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				try {
					UIManager.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
					JFrame.setDefaultLookAndFeelDecorated(true);
				} catch (UnsupportedLookAndFeelException e) {
				}
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(800, 600);
				JTable table = new JTable();
				DefaultTableModel dtm = new DefaultTableModel();
				dtm.addColumn("1111");
				dtm.addColumn("222");
				dtm.addColumn("3333");
				for (int i = 0; i < 100; i++) {
					dtm.addRow(new Object[] {});
				}
				table.setModel(dtm);
				PanelTableWithButtons p = new PanelTableWithButtons(table, 200);
				JButton[] buttons = new JButton[] { new DeleteButton(), new JButton("DO\nSOMETHING") };

				buttons[0].setPreferredSize(new Dimension(50, 50));

				p.setButtons(buttons);
				p.setButtonsXOffset(-25);

				f.getContentPane().add(p);
				f.setVisible(true);
			}
		});
	}

}
