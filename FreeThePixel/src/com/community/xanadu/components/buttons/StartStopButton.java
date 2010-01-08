package com.community.xanadu.components.buttons;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.trident.Timeline;

import com.community.xanadu.utils.PaintUtils;
import com.community.xanadu.utils.trident.triggers.MouseTrigger;
import com.community.xanadu.utils.trident.triggers.MouseTriggerEvent;
import com.jhlabs.image.GaussianFilter;

public class StartStopButton extends JButton {
	private static final long serialVersionUID = 1L;

	public static enum eStartStop {
		START, STOP
	}

	private eStartStop type;
	private float animProgress;

	public void setAnimProgress(final float animProgress) {
		this.animProgress = animProgress;
		repaint();
	}

	public StartStopButton(final eStartStop type) {
		this.type = type;
		setContentAreaFilled(false);
		setBorderPainted(false);
		setForeground(Color.white);
		setFocusPainted(false);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				StartStopButton.this.armedImage = null;
				StartStopButton.this.defaultImage = null;
				StartStopButton.this.shadow = null;
				repaint();
			}
		});
		Timeline timeline = new Timeline(this);
		timeline.addPropertyToInterpolate("animProgress", 0f, 1f);
		timeline.setDuration(200);
		MouseTrigger.addTrigger(this, timeline, MouseTriggerEvent.PRESS, true);
	}

	private BufferedImage armedImage;
	private BufferedImage defaultImage;
	private BufferedImage shadow;

	@Override
	protected void paintComponent(final Graphics g) {
		int width = getWidth();
		int height = getHeight();

		int h = (int) (height) - 5;
		int w = (int) (width) - 5;

		int blurradius = (int) (width / 3.5);
		float outSize = width / 8;

		boolean enabled = isEnabled();
		boolean armed = getModel().isArmed();

		if (armed) {
			blurradius = (int) (width / 2.5);
			outSize = width / 6;
		} else {
			blurradius = (int) (width / 3.5);
		}

		if ((this.armedImage == null && armed) || this.defaultImage == null) {
			// the content
			BufferedImage in = GraphicsUtilities.createCompatibleTranslucentImage(width, height);
			// border/effect
			BufferedImage out = GraphicsUtilities.createCompatibleTranslucentImage(width + blurradius, height
					+ blurradius);

			// in image
			{
				Graphics2D g2 = (Graphics2D) in.getGraphics();
				PaintUtils.turnOnAntialias(g2);

				if (this.type == eStartStop.START) {
					g2.setColor(Color.GREEN);
				} else {
					g2.setColor(Color.RED);
				}

				if (!enabled) {
					g2.setColor(Color.GRAY);
				}
				g2.fillOval(1, 1, w - 2, h - 2);
				g2.dispose();
			}
			// ---out
			{
				Graphics2D g2 = (Graphics2D) out.getGraphics();
				Paint paint;

				if (armed) {
					Point p1 = new Point((int) (2.25 * blurradius), 2 * blurradius);
					Point p2 = new Point(width - blurradius, height - blurradius);
					paint = new GradientPaint(p1, Color.BLACK, p2, Color.WHITE);
					paint = Color.BLACK;
				} else {
					Point p1 = new Point((int) (2.25 * blurradius), 2 * blurradius);
					Point p2 = new Point(width - blurradius, height - blurradius);
					paint = new GradientPaint(p1, Color.white, p2, Color.BLACK);
				}

				g2.setPaint(paint);
				g2.setStroke(new BasicStroke(outSize));
				g2.drawOval(0, 0, w, h);
				g2.dispose();

				new GaussianFilter(blurradius).filter(out, out);
			}
			// merge in and out image
			{
				Graphics2D g2;
				if (armed) {
					this.armedImage = GraphicsUtilities.createCompatibleTranslucentImage(width, height);
					g2 = (Graphics2D) this.armedImage.getGraphics();
				} else {
					this.defaultImage = GraphicsUtilities.createCompatibleTranslucentImage(width, height);
					g2 = (Graphics2D) this.defaultImage.getGraphics();
				}

				// add drop shadow
				{
					g2.drawImage(in, 1, 1, null);

					if (this.shadow == null) {
						this.shadow = GraphicsUtilities.createCompatibleTranslucentImage(width, height);
						Graphics2D gshadow = (Graphics2D) this.shadow.getGraphics();
						gshadow.setColor(Color.black);
						gshadow.fillOval(1, 1, w, h);
						gshadow.dispose();
						new GaussianFilter(5).filter(this.shadow, this.shadow);
					}

					g2.drawImage(this.shadow, 2, 2, null);
				}
				g2.drawImage(in, 2, 2, null);
				g2.setComposite(AlphaComposite.SrcAtop);
				g2.drawImage(out, 2, 2, null);

				g2.dispose();
			}
		}
		Graphics2D g2 = (Graphics2D) g.create();
		if (isEnabled()) {
			g2.setComposite(AlphaComposite.SrcOver.derive(this.animProgress));
			g2.drawImage(this.armedImage, 0, 0, getWidth() - 0, getHeight() - 0, null);
		}
		if (isEnabled()) {
			g2.setComposite(AlphaComposite.SrcOver.derive(1 - this.animProgress));
		}
		g2.drawImage(this.defaultImage, 0, 0, getWidth() - 0, getHeight() - 0, null);

		// paint the text
		g.translate(0, 0);
		super.paintComponent(g);
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame();
				f.getContentPane().setBackground(Color.WHITE);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(250, 150);
				f.getContentPane().setLayout(new MigLayout("fill"));
				JButton b = new StartStopButton(eStartStop.STOP);
				b.setText("STOP");
				f.getContentPane().add(b, "grow");
				b = new StartStopButton(eStartStop.START);
				b.setText("START");
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
