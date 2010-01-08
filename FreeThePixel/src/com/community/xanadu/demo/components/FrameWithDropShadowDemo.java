package com.community.xanadu.demo.components;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.substance.api.skin.SubstanceDustCoffeeLookAndFeel;

import com.community.xanadu.components.windows.dropShadow.FrameWithDropShadow;

public class FrameWithDropShadowDemo {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				try {
					UIManager.setLookAndFeel(new SubstanceDustCoffeeLookAndFeel());
				} catch (final UnsupportedLookAndFeelException e) {
				}
				final FrameWithDropShadow f = new FrameWithDropShadow();
				f.setSize(300, 200);

				f.setTitle("Person info");
				f.getContentPane().setLayout(new MigLayout("wrap 2"));

				f.getContentPane().add(new JLabel("Name:"), "");
				f.getContentPane().add(new JTextField(15), "growx,pushx");
				f.getContentPane().add(new JLabel("Adress"), "");
				f.getContentPane().add(new JTextField(15), "growx");

				f.startShowAnim();
			}
		});
	}
}
