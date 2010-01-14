package com.community.xanadu.demo.components;

import java.awt.Container;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.substance.api.skin.SubstanceDustCoffeeLookAndFeel;

import com.community.xanadu.components.windows.dropShadow.DialogWithDropShadow;
import com.community.xanadu.components.windows.dropShadow.FrameWithDropShadow;

public class FrameWithDropShadowDemo {
	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				if (args.length > 1) {

					if (args[0].equals("substance")) {
						try {
							UIManager.setLookAndFeel(new SubstanceDustCoffeeLookAndFeel());
						} catch (final UnsupportedLookAndFeelException e) {
						}
					}

					if (args[1].equals("frame")) {
						final FrameWithDropShadow f = new FrameWithDropShadow();
						f.setSize(300, 200);
						f.setTitle("Person info");
						addContent(f.getContentPane());
						f.startShowAnim();
					} else {
						final DialogWithDropShadow f = new DialogWithDropShadow(null);
						f.setSize(300, 200);
						f.setTitle("Person info");
						addContent(f.getContentPane());
						f.startShowAnim();
					}
				} else {
					final FrameWithDropShadow f = new FrameWithDropShadow();
					f.setSize(300, 200);
					f.setTitle("Person info");
					addContent(f.getContentPane());
					f.startShowAnim();
				}

			}
		});
	}

	private static void addContent(Container cont) {
		cont.setLayout(new MigLayout("wrap 2"));
		cont.add(new JLabel("Name:"), "");
		cont.add(new JTextField(15), "growx,pushx");
		cont.add(new JLabel("Adress"), "");
		cont.add(new JTextField(15), "growx");
	}
}
