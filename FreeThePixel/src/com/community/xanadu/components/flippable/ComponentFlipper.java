package com.community.xanadu.components.flippable;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;
import org.pushingpixels.trident.ease.Spline;

import com.community.xanadu.utils.ThreadUtils;
import com.jhlabs.image.PerspectiveFilter;

/**
 * A class to animate the visibility of two components by visually flipping them
 * so it looks like one component is on the back (flip side) of the other.
 * 
 * For the animation to look the best the components should be the same size and
 * centered vertically.
 * 
 * The animcation takes place on a layered pane above the actual components.
 * Call flip(...) with the the two components to flip. The flip process will
 * start the animation on the layer pane, call setVisible(false) on the first
 * component, then before the animation finishes setVisible(true) is called on
 * the second.
 */
public class ComponentFlipper {
	private JComponent frontComponent;
	private JComponent backComponent;
	private JLayeredPane layeredPane;
	private RenderComponent renderComp;

	private Insets frontInsets;
	private Insets backInsets;

	private BufferedImage frontImage;
	private BufferedImage backImage;
	private boolean noAnimation;

	private final int EXTRA_SPACE = 50;// modify the perspective

	private int animDuration = 500;

	public ComponentFlipper() {
		if (this.noAnimation) {
			this.animDuration = 1;
		}
	}

	public ComponentFlipper(final int animDuration) {
		this.animDuration = animDuration;
	}

	public void flip(final JComponent frontComponent, final JComponent backComponent, final JLayeredPane layeredPane) {
		this.frontComponent = frontComponent;
		this.backComponent = backComponent;
		this.layeredPane = layeredPane;
		this.renderComp = new RenderComponent();
		layeredPane.add(this.renderComp, JLayeredPane.POPUP_LAYER);

		doSizing();

		boolean b1 = frontComponent.isVisible();
		boolean b2 = backComponent.isVisible();

		frontComponent.setVisible(true);
		backComponent.setVisible(false);
		this.frontImage = grabImage(frontComponent, this.frontInsets);

		frontComponent.setVisible(false);
		backComponent.setVisible(true);
		this.backImage = grabImage(backComponent, this.backInsets);

		frontComponent.setVisible(b1);
		backComponent.setVisible(b2);

		// flip the back horizontal
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-this.backImage.getWidth(null), 0);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		this.backImage = op.filter(this.backImage, null);

		this.renderComp.interImage = GraphicsUtilities.createCompatibleTranslucentImage(this.frontImage.getWidth(),
				this.frontImage.getHeight() + (this.EXTRA_SPACE * 2));
		this.renderComp.shadedImage = GraphicsUtilities.createCompatibleTranslucentImage(this.frontImage.getWidth(),
				this.frontImage.getHeight());
		this.renderComp.updateImage();
		frontComponent.setVisible(false);

		if (this.noAnimation) {
			setAnimProgress(Math.PI);
			completeFlip();
		} else {
			Timeline timeline = new Timeline(this);
			timeline.setDuration(this.animDuration);
			timeline.setEase(new Spline(0.7f));
			timeline.addPropertyToInterpolate("animProgress", 0f, (float) Math.PI);
			timeline.addCallback(new TimelineCallbackAdapter() {
				@Override
				public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
						final float durationFraction, final float timelinePosition) {
					if (newState == TimelineState.DONE) {
						completeFlip();
					}
				}
			});
			timeline.play();
		}
	}

	public void setAnimProgress(final double animProgress) {
		ThreadUtils.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				ComponentFlipper.this.renderComp.rads = animProgress;
				ComponentFlipper.this.renderComp.updateImage();
			}
		});
	}

	private void completeFlip() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				ComponentFlipper.this.renderComp.interImage = ComponentFlipper.this.backImage;
				ComponentFlipper.this.backComponent.setVisible(true);
				ComponentFlipper.this.layeredPane.remove(ComponentFlipper.this.renderComp);
				Window w = SwingUtilities.getWindowAncestor(ComponentFlipper.this.layeredPane);
				if (w != null) {
					w.repaint();
				}
			}
		});
	}

	private void doSizing() {
		int maxWidth = Math.max(this.frontComponent.getWidth(), this.backComponent.getWidth());
		int maxHeight = Math.max(this.frontComponent.getHeight(), this.backComponent.getHeight()) + this.EXTRA_SPACE;

		int fhd = (maxHeight - this.frontComponent.getHeight()) / 2;
		int fwd = (maxWidth - this.frontComponent.getWidth()) / 2;
		this.frontInsets = new Insets(fhd, fwd, fhd, fwd);

		int bhd = (maxHeight - this.backComponent.getHeight()) / 2;
		int bwd = (maxWidth - this.backComponent.getWidth()) / 2;
		this.backInsets = new Insets(bhd, bwd, bhd, bwd);

		Point p = (this.frontComponent.isShowing()) ? this.frontComponent.getLocationOnScreen() : this.backComponent
				.getLocationOnScreen();
		p.x = p.x - this.frontInsets.left;
		p.y = p.y - this.frontInsets.top;
		SwingUtilities.convertPointFromScreen(p, this.layeredPane);

		this.renderComp.setBounds(p.x, p.y, maxWidth, maxHeight);
	}

	private BufferedImage grabImage(final JComponent comp, final Insets insets) {
		BufferedImage image = GraphicsUtilities.createCompatibleTranslucentImage(comp.getWidth(), comp.getHeight()
				+ insets.top + insets.bottom);

		Graphics2D g = image.createGraphics();

		g.translate(insets.left, insets.top);
		comp.paint(g);
		g.translate(insets.left, insets.top);
		g.dispose();

		return image;
	}

	public void setNoAnimation(final boolean noAnimation) {
		this.noAnimation = noAnimation;
	}

	/**
	 * The class where the intermediate images are rendered to. It is added to
	 * the layer pane, the animation is triggered and this component repaints
	 * the animation frames. The component is then removed after animation stops
	 * and the flipped side is displayed.
	 */
	private class RenderComponent extends JComponent {
		private double rads = 0;
		private int x;
		private int y;

		private BufferedImage interImage;
		private BufferedImage shadedImage;

		RenderComponent() {
			setOpaque(false);
		}

		private BufferedImage getVisibleImage() {
			if (this.rads < Math.PI / 2)
				return ComponentFlipper.this.frontImage;
			else
				return ComponentFlipper.this.backImage;
		}

		@Override
		public void paintComponent(final Graphics g) {
			if (this.interImage == null)
				return;
			int imageX = (getWidth() / 2) - (this.interImage.getWidth() / 2);
			int imageY = ((getHeight() / 2) - (this.interImage.getHeight() / 2));

			int x0 = (this.interImage.getWidth() / 2) - this.x;
			int y0 = ComponentFlipper.this.EXTRA_SPACE - this.y;

			Graphics2D g2 = (Graphics2D) g.create();
			g2.setClip(imageX + x0, imageY + y0, this.interImage.getWidth() - (2 * x0) - 1, (this.interImage
					.getHeight() - (2 * y0)));
			if (this.rads < Math.PI / 2) {
				g2.drawImage(this.interImage, imageX + x0, imageY + y0, imageX + x0 + this.interImage.getWidth(),
						imageY + y0 + this.interImage.getHeight(), 0, 0, this.interImage.getWidth(), this.interImage
								.getHeight(), null);
			} else {
				g2.drawImage(this.interImage, imageX + this.interImage.getWidth() - x0, imageY + y0, imageX - x0,
						imageY + y0 + this.interImage.getHeight(), 0, 0, this.interImage.getWidth(), this.interImage
								.getHeight(), null);
			}
			g2.dispose();
		}

		private void updateImage() {
			// clear inter image
			Graphics2D g2inter = this.interImage.createGraphics();
			g2inter.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
			Rectangle rect = new Rectangle(0, 0, this.interImage.getWidth(), this.interImage.getHeight());
			g2inter.fill(rect);
			g2inter.dispose();

			// darken the image progresively as it spins
			BufferedImage image = getVisibleImage();

			Graphics2D g2 = this.shadedImage.createGraphics();
			Composite composite = g2.getComposite();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
			rect = new Rectangle(0, 0, this.shadedImage.getWidth(), this.shadedImage.getHeight());
			g2.fill(rect);
			g2.setComposite(composite);
			g2.drawImage(image, 0, 0, null);

			double degree = Math.abs(((Math.PI / 2) - this.rads) / (Math.PI / 2));
			int alpha = (int) (200 - (200 * degree));
			GradientPaint gp = new GradientPaint(0, 0, new Color(0, 0, 0, 0), image.getWidth(), 0, new Color(0, 0, 0,
					alpha));
			g2.setPaint(gp);
			g2.fillRect(0, ComponentFlipper.this.EXTRA_SPACE / 2, image.getWidth(), image.getHeight()
					- ComponentFlipper.this.EXTRA_SPACE);
			g2.dispose();

			// now perspective adjust
			int ix = image.getWidth() / 2;

			this.x = (int) Math.abs(ix * (Math.cos(this.rads)));
			this.y = (int) (((ComponentFlipper.this.EXTRA_SPACE / 2) * Math.sin(this.rads)));

			int x0 = ix - this.x;
			int y0 = -this.y;
			int x1 = ix + this.x;
			int y1 = this.y;
			int x2 = x1;
			int y2 = image.getHeight() - this.y;
			int x3 = x0;
			int y3 = image.getHeight() + this.y;

			PerspectiveFilter pf = new PerspectiveFilter(x0, y0, x1, y1, x2, y2, x3, y3);
			this.interImage = pf.filter(this.shadedImage, this.interImage);
			repaint();
		}
	}

}
