package com.community.xanadu.utils;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.jdesktop.swingx.JXImagePanel;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.image.ColorTintFilter;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.internal.utils.SubstanceImageCreator;
import org.pushingpixels.substance.internal.utils.SubstanceSizeUtils;

import com.jhlabs.image.GaussianFilter;

public class ImageUtils {

	public static void showImage(final Image img) {
		JFrame f = new JFrame();
		f.getContentPane().setLayout(new BorderLayout());
		f.setSize(800, 600);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JXImagePanel panel = new JXImagePanel();
		panel.setImage(img);
		f.getContentPane().add(panel);

		f.setVisible(true);
	}

	public static BufferedImage getShadowedImage(final Image img) {
		return getShadowedImage(img, 2, 2, 5, Color.BLACK, 1f, 0.6f, true);
	}

	public static BufferedImage getShadowedImage(final Image img, final int xoffset, final int yoffset,
			final int blurrRadius, final Color shadowColor, final float shadowColorLikeness, final float alpha,
			final boolean increaseSize) {
		BufferedImage shadow;
		if (increaseSize) {
			shadow = GraphicsUtilities.createCompatibleTranslucentImage(img.getWidth(null) + xoffset + blurrRadius, img
					.getHeight(null)
					+ yoffset + blurrRadius);
		} else {
			shadow = GraphicsUtilities.createCompatibleTranslucentImage(img.getWidth(null), img.getHeight(null));
		}

		// create the shadow
		Graphics2D g2 = (Graphics2D) shadow.getGraphics();
		g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
		g2.drawImage(img, xoffset, yoffset, null);
		g2.dispose();

		new ColorTintFilter(shadowColor, shadowColorLikeness).filter(shadow, shadow);
		if (blurrRadius > 0) {
			new GaussianFilter(blurrRadius).filter(shadow, shadow);
		}

		// paint the original image
		g2 = (Graphics2D) shadow.getGraphics();
		g2.drawImage(img, 0, 0, null);
		g2.dispose();

		return shadow;
	}

	/**
	 * 
	 * @param direction
	 *            SwingConstant.South/North/East/West
	 * @param color
	 * @return
	 */
	public static ImageIcon getArrowIcon(final int direction, final Color color) {
		final SubstanceColorScheme mainActiveScheme = SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme();
		Icon icon = SubstanceImageCreator.getArrowIcon(SubstanceSizeUtils.getControlFontSize(), direction,
				mainActiveScheme);
		BufferedImage buff = GraphicsUtilities.createCompatibleTranslucentImage(icon.getIconWidth(), icon
				.getIconHeight());
		Graphics2D g = (Graphics2D) buff.getGraphics();
		icon.paintIcon(null, g, 0, 0);
		g.setComposite(AlphaComposite.SrcAtop);
		g.setColor(color);
		g.fillRect(0, 0, buff.getWidth(), buff.getHeight());
		g.dispose();

		return new ImageIcon(buff);
	}
}
