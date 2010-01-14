package com.community.xanadu.demo.components;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;

import com.jhlabs.image.PerspectiveFilter;

public class FlipDemo extends javax.swing.JFrame {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
					JFrame.setDefaultLookAndFeelDecorated(true);
				} catch (final UnsupportedLookAndFeelException e) {
				}

				final FlipDemo inst = new FlipDemo();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	private flipPanel panel;

	public FlipDemo() {
		super();
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add((this.panel = new flipPanel()), BorderLayout.CENTER);
		getContentPane().setBackground(Color.WHITE);
		((JPanel) getContentPane()).setOpaque(true);
		final JButton b = new JButton("start");
		getContentPane().add(b, BorderLayout.SOUTH);
		b.addActionListener(new ActionListener() {
			int i = 0;
			boolean first = true;

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (this.i % 2 == 0) {
					if (this.first) {
						FlipDemo.this.panel.startAnim();
						this.first = false;
					} else {
						FlipDemo.this.panel.resume();
					}
					b.setText("stop");
				} else {
					FlipDemo.this.panel.timeline.suspend();
					b.setText("start");
				}

				this.i++;

			}
		});
		setSize(400, 300);
	}

	public static class flipPanel extends JComponent {
		private BufferedImage front;
		private BufferedImage back;
		private float animProgress = 0f;
		private final BufferedImage interImage;
		private final BufferedImage shadedImage;
		private Timeline timeline;
		private final int PERSPECTIVE = 50;
		private int y;
		private int x;

		public flipPanel() {
			setOpaque(false);
			this.front = getImageFront();
			this.back = getImageBack();

			this.interImage = GraphicsUtilities.createCompatibleTranslucentImage(this.front.getWidth(), this.front
					.getHeight()
					+ (this.PERSPECTIVE * 2));
			this.shadedImage = GraphicsUtilities.createCompatibleTranslucentImage(this.front.getWidth(), this.front
					.getHeight());
		}

		@Override
		protected void paintComponent(final Graphics g) {

			if (this.interImage == null) {
				return;
			}
			final int imageX = (getWidth() / 2) - (this.interImage.getWidth() / 2);
			final int imageY = ((getHeight() / 2) - (this.interImage.getHeight() / 2));

			final int x0 = (this.interImage.getWidth() / 2) - this.x;
			final int y0 = this.PERSPECTIVE - this.y;

			final Graphics2D g2 = (Graphics2D) g.create();
			g.setClip(imageX + x0, imageY + y0, this.interImage.getWidth() - (2 * x0) - 1,
					(this.interImage.getHeight() - (2 * y0)));

			if (this.animProgress < Math.PI / 2) {
				final int dx0 = imageX + x0;
				final int dy0 = imageY + y0;

				g2.drawImage(this.interImage, dx0, dy0, null);

			} else if (this.animProgress <= Math.PI) {
				// where to paint the image
				final int dx0 = imageX + this.interImage.getWidth() - x0;
				final int dy0 = imageY + y0;
				final int dx1 = imageX - x0;
				final int dy1 = imageY + y0 + this.interImage.getHeight();

				// the entire image
				final int sx0 = 0;
				final int sy0 = 0;
				final int sx1 = this.interImage.getWidth();
				final int sy1 = this.interImage.getHeight();

				g2.drawImage(this.interImage, dx0, dy0, dx1, dy1, sx0, sy0, sx1, sy1, null);
			}
			g2.dispose();
		}

		private void updateImage() {
			// clear inter image
			final Graphics2D g2inter = this.interImage.createGraphics();
			g2inter.setComposite(AlphaComposite.Clear.derive(0.0f));
			Rectangle rect = new Rectangle(0, 0, this.interImage.getWidth(), this.interImage.getHeight());
			g2inter.fill(rect);
			g2inter.dispose();

			// darken the image progresively as it spins
			final BufferedImage image = getVisibleImage();
			final Graphics2D g2 = this.shadedImage.createGraphics();
			final Composite composite = g2.getComposite();
			g2.setComposite(AlphaComposite.Clear.derive(0.0f));
			rect = new Rectangle(0, 0, this.shadedImage.getWidth(), this.shadedImage.getHeight());
			g2.fill(rect);
			g2.setComposite(composite);
			g2.drawImage(image, 0, 0, null);

			final double degree = Math.abs(((Math.PI / 2) - this.animProgress) / (Math.PI / 2));
			final int alpha = (int) (200 - (200 * degree));

			final GradientPaint gp = new GradientPaint(0, 0, new Color(0, 0, 0, 0), image.getWidth(), 0, new Color(0, 0, 0,
					alpha));
			g2.setPaint(gp);
			g2.fillRect(0, 0, image.getWidth(), image.getHeight());
			g2.dispose();

			// now perspective adjust
			final int ix = image.getWidth() / 2;

			this.x = (int) Math.abs(ix * (Math.cos(this.animProgress - Math.PI)));

			this.y = (int) (((this.PERSPECTIVE / 2) * Math.sin(this.animProgress)));

			final int x0 = ix - this.x;
			final int y0 = -this.y;
			final int x1 = ix + this.x;
			final int y1 = this.y;
			final int x2 = x1;
			final int y2 = image.getHeight() - this.y;
			final int x3 = x0;
			final int y3 = image.getHeight() + this.y;

			final PerspectiveFilter pf = new PerspectiveFilter(x0, y0, x1, y1, x2, y2, x3, y3);
			pf.filter(this.shadedImage, this.interImage);
			repaint();
		}

		private BufferedImage getVisibleImage() {
			if (this.animProgress < Math.PI / 2) {
				return this.front;
			} else {
				return this.back;
			}
		}

		private BufferedImage getImageFront() {
			final BufferedImage img = GraphicsUtilities.createCompatibleTranslucentImage(200, 200);
			final Graphics g = img.createGraphics();
			g.setColor(Color.RED);
			g.fillRect(0, 0, img.getWidth(), img.getHeight());
			g.setFont(g.getFont().deriveFont(55f));
			g.setColor(Color.BLACK);
			g.drawString("Front", 20, 50);
			g.dispose();
			return img;
		}

		private BufferedImage getImageBack() {
			final BufferedImage img = GraphicsUtilities.createCompatibleTranslucentImage(200, 200);
			final Graphics g = img.createGraphics();
			g.setColor(Color.BLUE);
			g.fillRect(0, 0, img.getWidth(), img.getHeight());
			g.setFont(g.getFont().deriveFont(55f));
			g.setColor(Color.BLACK);
			g.drawString("Back", 20, 50);
			g.dispose();
			return img;
		}

		boolean first = true;

		public void setAnimProgress(final float animProgress) {
			this.animProgress = animProgress;
			if (!this.first && animProgress == 0f) {
				repeat();
			} else {
				this.first = false;
			}
			updateImage();
		}

		public float getAnimProgress() {
			return this.animProgress;
		}

		private void resume() {
			this.timeline.resume();
		}

		private void startAnim() {
			this.timeline = new Timeline(this);
			final float fraction = (float) (this.animProgress / Math.PI);
			this.timeline.setDuration(2000 - ((int) (fraction * 2000)));
			this.timeline.addPropertyToInterpolate("animProgress", getAnimProgress(), (float) Math.PI);
			prepareImage();
			this.timeline.playLoop(RepeatBehavior.LOOP);
		}

		public void prepareImage() {
			final AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
			tx.translate(-flipPanel.this.back.getWidth(null), 0);
			final AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			flipPanel.this.back = op.filter(flipPanel.this.back, null);
		}

		public void repeat() {
			BufferedImage tmp = flipPanel.this.front;
			flipPanel.this.front = flipPanel.this.back;
			flipPanel.this.back = tmp;
			tmp = null;

			AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
			tx.translate(-flipPanel.this.back.getWidth(null), 0);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			flipPanel.this.back = op.filter(flipPanel.this.back, null);

			tx = AffineTransform.getScaleInstance(-1, 1);
			tx.translate(-flipPanel.this.front.getWidth(null), 0);
			op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			flipPanel.this.front = op.filter(flipPanel.this.front, null);
		}
	}
}
