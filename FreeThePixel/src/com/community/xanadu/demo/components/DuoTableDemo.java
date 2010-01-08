package com.community.xanadu.demo.components;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel;

import com.community.xanadu.components.table.DuoTable;

public class DuoTableDemo {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
				} catch (UnsupportedLookAndFeelException e) {
				}
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				DuoTable<Component> duotable = new DuoTable<Component>(new String[] { "class", "width" }, new String[] {
						"class", "width" });
				f.getContentPane().add(duotable);

				f.setSize(600, 300);

				for (Component comp : duotable.getComponents()) {
					duotable.addItem(comp);
				}

				f.setVisible(true);
			}
		});
	}
}
