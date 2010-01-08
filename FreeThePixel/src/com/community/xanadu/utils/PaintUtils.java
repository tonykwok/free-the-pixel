package com.community.xanadu.utils;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.JComponent;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.image.AbstractFilter;
import org.jdesktop.swingx.image.StackBlurFilter;
import org.pushingpixels.substance.internal.utils.SubstanceColorUtilities;

public class PaintUtils {
	/**
	 * Paint an highlighted text
	 * 
	 * @param g
	 *            the graphics where to paint the text
	 * @param size
	 *            size of the highlight effect
	 * @param opacity
	 *            opacity of the effect, range 0..1
	 * @param text
	 *            The text to be painted
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param in
	 *            inside color
	 * @param out
	 *            outside color
	 */
	public static void drawHighLightText(final Graphics2D g, final int size, final float opacity, final String text,
			final int x, final int y, final Color in, final Color out) {
		Graphics2D g2 = (Graphics2D) g.create();
		PaintUtils.turnOnAntialias(g2);
		g2.translate(x, y);
		Composite oldComposite = g2.getComposite();

		float preAlpha = 1f;
		if (g.getComposite() instanceof AlphaComposite) {
			if (((AlphaComposite) g.getComposite()).getRule() == AlphaComposite.SRC_OVER) {
				preAlpha = ((AlphaComposite) g.getComposite()).getAlpha();
			}
		}

		FontMetrics metrics = g2.getFontMetrics();
		int ascent = metrics.getAscent();
		int heightDiff = (metrics.getHeight() - ascent) / 2;

		g2.setColor(out);

		double tx = 0;
		double ty = -1 + heightDiff - size;
		g2.translate(tx, ty);

		for (int i = -size; i <= size; i++) {
			for (int j = -size; j <= size; j++) {
				double distance = i * i + j * j;
				float alpha = opacity;
				if (distance > 0.0d) {
					alpha = (float) (1.0f / ((distance * size) * (1 - opacity)));
				}
				alpha *= preAlpha;
				if (alpha > 1.0f) {
					alpha = 1.0f;
				} else if (alpha < 0) {
					alpha = 0;
				}
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
				g2.drawString(text, i + size, j + size);
			}
		}
		g2.setComposite(oldComposite);
		g2.setColor(in);
		g2.drawString(text, size, size);

		g2.translate(-tx, -ty);
		g2.dispose();
	}

	/**
	 * Paint an highlighted text
	 * 
	 * @param g
	 *            the graphics where to paint the text
	 * @param text
	 *            The text to be painted
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param in
	 *            inside color
	 * @param out
	 *            outside color
	 */
	public static void drawHighLightText(final Graphics2D g, final String text, final int x, final int y,
			final Color in, final Color out) {
		drawHighLightText(g, 3, 0.9f, text, x, y, in, out);
	}

	public static void turnOnAntialias(final Graphics2D g2) {
		if (g2 == null) {
			throw new IllegalArgumentException("the graphics can not be null");
		}
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	public static void addShadowAndLightEffect(final Graphics2D g, final AbstractButton button, final float progress) {
		Graphics2D graphics = (Graphics2D) g.create();

		int w = button.getWidth();
		int h = button.getHeight();

		if (w == 0 && h == 0) {
			return;
		}

		if (w < 50 && h < 50) {
			return;
		}

		BufferedImage img = GraphicsUtilities.createCompatibleTranslucentImage(w, h);
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		PaintUtils.turnOnAntialias(g2);

		Color c1 = SubstanceColorUtilities.getInterpolatedColor(Color.white, Color.black, 1 - progress);
		Color c2 = SubstanceColorUtilities.getInterpolatedColor(Color.black, Color.white, 1 - progress);

		if (w < 70 || h < 70) {// small component
			g2.setStroke(new BasicStroke(3f));
		} else {
			g2.setStroke(new BasicStroke(10f));
		}

		g2.setColor(c1);
		g2.drawLine(2, 0, w, 0);// north
		g2.drawLine(0, 5, 0, h - 10);// west

		g2.setColor(c2);
		g2.drawLine(w - 1, 0, w - 1, h);// east
		g2.drawLine(0, h - 1, w, h - 1);// south
		g2.dispose();

		BufferedImage thumbnail = GraphicsUtilities.createThumbnailFast(img, img.getWidth() / 2, img.getHeight() / 2);
		AbstractFilter filter;

		int radius;

		int r1 = w / 20;
		int r2 = h / 20;
		radius = Math.min(r1, r2);
		radius = Math.min(radius, 20);

		filter = new StackBlurFilter(radius);
		filter.filter(thumbnail, thumbnail);

		// ----------apply effect on original graphics
		graphics.setComposite(AlphaComposite.SrcOver.derive(0.85f));
		graphics.drawImage(thumbnail, 0, 0, w, h, null);
		graphics.dispose();
	}

	public static void drawMultiLineText(final Graphics g, final String text, final JComponent comp, final Paint paint) {
		drawMultiLineText(g, text, new Rectangle(0, 0, comp.getWidth(), comp.getHeight()), true, 0, 0, paint);
	}

	public static void drawMultiLineText(final Graphics g, final String text, final Rectangle viewRect,
			final boolean hCenter, final int xOffset, final int yOffset, final Paint paint) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setPaint(paint);

		String[] splitedText;
		if (text != null && !text.isEmpty()) {
			splitedText = text.split("\n");

			FontMetrics fm = g2.getFontMetrics();

			int fontSize = fm.getHeight();
			int x = xOffset;
			int y = fontSize + yOffset;
			int textWidth;
			for (String s : splitedText) {
				if (hCenter) {
					textWidth = (int) fm.getStringBounds(s, g2).getWidth();
					x = xOffset + viewRect.width / 2 - textWidth / 2;
				}
				g2.drawString(s, x, y);
				y += fontSize;
			}

		}
		g2.dispose();
	}

	public static void drawMultiLineHighLightText(final Graphics2D g, final Rectangle viewRect, final int size,
			final float opacity, final String text, final int xOffset, final int yOffset, final Color in,
			final Color out, final boolean hCenter, final boolean vCenter) {
		String[] splitedText;
		if (text != null && !text.isEmpty()) {
			splitedText = text.split("\n");

			FontMetrics fm = g.getFontMetrics();

			int fontSize = fm.getFont().getSize();
			int x = xOffset;
			int y = fontSize + yOffset;
			int textWidth;

			if (vCenter) {
				y = viewRect.height / 2;
				y += fontSize / 2;
				y -= (fontSize + size) / 2 * (splitedText.length - 1);// =0 if
				// one
				// line
				y += yOffset;
			}
			for (String s : splitedText) {
				if (hCenter) {
					textWidth = (int) fm.getStringBounds(s, g).getWidth() + size * 2;
					x = xOffset + (viewRect.width - textWidth) / 2;
				}
				drawHighLightText(g, size, opacity, s, x, y, in, out);

				y += fontSize + size;
			}
		}
	}

	public static void drawMultiLineHighLightText(final Graphics2D g, final int size, final float opacity,
			final String text, final int xOffset, final int yOffset, final Color in, final Color out) {
		String[] splitedText;
		if (text != null && !text.isEmpty()) {
			splitedText = text.split("\n");

			FontMetrics fm = g.getFontMetrics();

			int fontSize = fm.getFont().getSize();
			int x = xOffset;
			int y = fontSize + yOffset;

			for (String s : splitedText) {
				drawHighLightText(g, size, opacity, s, x, y, in, out);

				y += fontSize;
			}
		}
	}

	public static void drawMultiLineHighLightText(final Graphics2D g, final JComponent comp, final String text,
			final Color in, final Color out) {
		drawMultiLineHighLightText(g, new Rectangle(0, 0, comp.getWidth(), comp.getHeight()), 2, 0.75f, text, 0, 0, in,
				out, true, true);
	}

	public static void fillCircle(final Graphics g, final int width, final Color color, final int x, final int y) {
		BufferedImage img = GraphicsUtilities.createCompatibleTranslucentImage(width, width);

		Graphics2D g2 = (Graphics2D) img.createGraphics();
		PaintUtils.turnOnAntialias(g2);

		g2.setColor(color);
		g2.fillOval(1, 1, width - 2, width - 2);

		g2.setComposite(AlphaComposite.DstIn.derive(0.9f));

		Point center = new Point(width / 2, width / 2);
		float[] dist = { 0.0f, 0.7f, 1.0f };
		Color[] colors = { new Color(0, 0, 0, 255), new Color(0, 0, 0, 255), new Color(0, 0, 0, 0) };
		int radius = width / 2;

		RadialGradientPaint rgp = new RadialGradientPaint(center, radius, center, dist, colors, CycleMethod.NO_CYCLE);
		g2.setPaint(rgp);

		g2.fillOval(0, 0, width, width);
		g2.dispose();

		g.drawImage(img, x, y, null);
	}
}
