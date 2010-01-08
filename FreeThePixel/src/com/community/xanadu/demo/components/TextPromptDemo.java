package com.community.xanadu.demo.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel;

import com.community.xanadu.components.text.TextPrompt;
import com.community.xanadu.components.windows.dropShadow.FrameWithDropShadow;
import com.community.xanadu.utils.PaintUtils;

public class TextPromptDemo {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
					UIManager.put(SubstanceLookAndFeel.COLORIZATION_FACTOR, new Double(1));
				} catch (final UnsupportedLookAndFeelException e) {
				}

				final JFrame f = new FrameWithDropShadow();
				// uncomment this line if you use java 6 u 10 , not needed for
				// later version
				// RepaintManager.currentManager(f).setDoubleBufferingEnabled(false);

				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().setLayout(new MigLayout("fill"));

				final JTextField jtf = new JTextField(20);
				jtf.setFont(jtf.getFont().deriveFont(35f));

				final BufferedImage img = GraphicsUtilities.createCompatibleTranslucentImage(15, 15);
				final Graphics2D g = img.createGraphics();
				final Color foreground = new Color(0, 0, 255, 35);
				PaintUtils.fillCircle(g, 15, foreground, 0, 0);
				g.dispose();

				// invoke later needed only if running with substancelaf
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						final TextPrompt tp = new TextPrompt("Scan or enter a barcode", jtf);
						tp.setForeground(foreground);
						tp.changeStyle(Font.BOLD + Font.ITALIC);
						tp.setIcon(new ImageIcon(img));
					}
				});

				f.getContentPane().add(jtf, "growx");
				f.setSize(600, 150);
				f.setVisible(true);
			}
		});
	}
}
