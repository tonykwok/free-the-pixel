package com.community.xanadu.demo.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel;

import com.community.xanadu.components.CoverFlow;
import com.community.xanadu.components.CoverFlow.Item;
import com.community.xanadu.components.transition.impl.PinchTransition;
import com.community.xanadu.listeners.Draggable;
import com.community.xanadu.utils.PaintUtils;
import com.community.xanadu.utils.WindowsUtils;

public class CoverFlowDemo {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
				} catch (final UnsupportedLookAndFeelException e1) {
				}

				final JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().setLayout(new MigLayout("fill,inset 0 0 0 0"));
				WindowsUtils.setOpaque(f, false);

				final CoverFlow scroller = new CoverFlow();
				f.getContentPane().add(scroller, "push,grow,wrap");
				new Draggable(f);
				final JPanel p = new JPanel();
				p.setBackground(new Color(0, 0, 0, 128));
				f.add(p, "grow,h 5");

				final JButton closeButton = new JButton("Close");
				closeButton.setContentAreaFilled(false);
				closeButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						new PinchTransition(f).startCloseTransition();
					}
				});
				p.add(closeButton, BorderLayout.EAST);

				for (int i = 0; i < 50; i++) {
					final BufferedImage img = GraphicsUtilities.createCompatibleTranslucentImage(
							CoverFlow.ItemWidthSize, CoverFlow.ItemHeighSize);
					final Graphics2D g = img.createGraphics();
					PaintUtils.turnOnAntialias(g);
					g.setColor(new Color((int) (Math.random() * 255), (int) (Math.random() * 255),
							(int) (Math.random() * 255)));
					g.fillRect(0, 0, img.getWidth(), img.getHeight());
					g.setFont(g.getFont().deriveFont(55f));
					g.setColor(Color.BLACK);
					g.drawString("" + i, 20, 50);
					g.dispose();

					scroller.addItem(new Item(img));
				}
				f.setSize(500, 250);
				f.setVisible(true);
				scroller.placeItems();
			}
		});
	}
}
