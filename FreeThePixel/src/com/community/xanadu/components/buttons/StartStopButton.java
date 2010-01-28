package com.community.xanadu.components.buttons;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.trident.Timeline;

import com.community.xanadu.utils.PaintUtils;
import com.community.xanadu.utils.trident.triggers.MouseTrigger;
import com.community.xanadu.utils.trident.triggers.MouseTriggerEvent;
import com.jhlabs.image.GaussianFilter;

public class StartStopButton extends JButton {
	private static final long serialVersionUID = 1L;

	private float animProgress;

	public void setAnimProgress(final float animProgress) {
		this.animProgress = animProgress;
		repaint();
	}

	public StartStopButton() {
		setContentAreaFilled(false);
		setBorderPainted(false);
		setForeground(Color.white);
		setFocusPainted(false);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				if (StartStopButton.this.t.isRunning()) {
					StartStopButton.this.t.stop();
				}
				StartStopButton.this.t.start();
			}
		});
		final Timeline timeline = new Timeline(this);
		timeline.addPropertyToInterpolate("animProgress", 0f, 1f);
		timeline.setDuration(200);
		MouseTrigger.addTrigger(this, timeline, MouseTriggerEvent.PRESS, true);
	}

	// to coales the resize event
	private final Timer t = new Timer(500, new ActionListener() {

		@Override
		public void actionPerformed(final ActionEvent e) {
			StartStopButton.this.armedImage = null;
			StartStopButton.this.defaultImage = null;
			repaint();
			StartStopButton.this.t.stop();

		}
	});

	private BufferedImage armedImage;
	private BufferedImage defaultImage;

	@Override
	protected void paintComponent(final Graphics g) {

		if ((this.armedImage == null && getModel().isArmed())) {
			this.armedImage = createArmedImage();
		}
		if (this.defaultImage == null) {
			this.defaultImage = createDefaultImage();
		}

		final Graphics2D g2 = (Graphics2D) g.create();

		g2.drawImage(this.defaultImage, 0, 0, getWidth() - 0, getHeight() - 0, null);

		if (isEnabled()) {
			g2.setComposite(AlphaComposite.SrcOver.derive(this.animProgress));
			g2.drawImage(this.armedImage, 0, 0, getWidth() - 0, getHeight() - 0, null);
		}

		// paint the text
		super.paintComponent(g);
	}

	public void createBaseImageWithDropShadow() {

	}

	public BufferedImage createINImage() {
		final int h = getHeight() - 5;
		final int w = getWidth() - 5;
		final BufferedImage in = GraphicsUtilities.createCompatibleTranslucentImage(getWidth(), getHeight());
		final Graphics2D g2 = (Graphics2D) in.getGraphics();
		PaintUtils.turnOnAntialias(g2);

		g2.setColor(getBackground());

		if (!isEnabled()) {
			g2.setColor(Color.GRAY);
		}
		g2.fillOval(1, 1, w - 2, h - 2);
		g2.dispose();
		return in;
	}

	public BufferedImage createOUTImage(final boolean armed) {
		final int width = getWidth();
		final int height = getHeight();

		final int h = height - 5;
		final int w = width - 5;

		int blurradius = (int) (width / 3.5);
		float outSize = width / 8;

		if (armed) {
			blurradius = (int) (width / 2.5);
			outSize = width / 6;
		} else {
			blurradius = (int) (width / 3.5);
		}

		final BufferedImage out = GraphicsUtilities.createCompatibleTranslucentImage(width + blurradius, height
				+ blurradius);
		final Graphics2D g2 = (Graphics2D) out.getGraphics();
		Paint paint;

		if (getModel().isArmed()) {
			paint = Color.BLACK;
		} else {
			final Point p1 = new Point((int) (2.25 * blurradius), 2 * blurradius);
			final Point p2 = new Point(width - blurradius, height - blurradius);
			paint = new GradientPaint(p1, Color.white, p2, Color.BLACK);
		}

		g2.setPaint(paint);
		g2.setStroke(new BasicStroke(outSize));
		g2.drawOval(0, 0, w, h);
		g2.dispose();
		new GaussianFilter(blurradius).filter(out, out);
		return out;
	}

	public BufferedImage createArmedImage() {
		final BufferedImage armedImage = GraphicsUtilities.createCompatibleTranslucentImage(getWidth(), getHeight());
		final Graphics2D g2 = (Graphics2D) armedImage.getGraphics();

		g2.drawImage(createShadowImage(), 2, 2, null);
		g2.drawImage(createINImage(), 2, 2, null);
		g2.setComposite(AlphaComposite.SrcAtop);
		g2.drawImage(createOUTImage(true), 2, 2, null);

		g2.dispose();

		return armedImage;
	}

	public BufferedImage createDefaultImage() {
		final BufferedImage defaultImage = GraphicsUtilities.createCompatibleTranslucentImage(getWidth(), getHeight());
		final Graphics2D g2 = (Graphics2D) defaultImage.getGraphics();

		g2.drawImage(createShadowImage(), 2, 2, null);
		g2.drawImage(createINImage(), 2, 2, null);
		g2.setComposite(AlphaComposite.SrcAtop);
		g2.drawImage(createOUTImage(false), 2, 2, null);

		g2.dispose();
		return defaultImage;
	}

	public BufferedImage createShadowImage() {

		final int h = getHeight() - 5;
		final int w = getWidth() - 5;

		final BufferedImage shadow = GraphicsUtilities.createCompatibleTranslucentImage(getWidth(), getHeight());
		final Graphics2D gshadow = (Graphics2D) shadow.getGraphics();
		gshadow.setColor(Color.black);
		gshadow.fillOval(1, 1, w, h);
		gshadow.dispose();
		new GaussianFilter(5).filter(shadow, shadow);
		return shadow;
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final JFrame f = new JFrame();
				f.getContentPane().setBackground(Color.WHITE);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(150, 170);
				f.getContentPane().setLayout(new MigLayout("fill"));
				final JButton b = new StartStopButton();
				b.setBackground(Color.green.darker());
				b.setText("STOP");
				f.getContentPane().add(b, "grow");
				f.setVisible(true);
			}
		});
	}

	@Override
	public void setEnabled(final boolean b) {
		super.setEnabled(b);
		this.defaultImage = null;
		repaint();
	}

	@Override
	public boolean contains(final int x, final int y) {
		return new Ellipse2D.Float(0, 0, getWidth() - 5, getHeight() - 5).contains(x, y);
	}
}
