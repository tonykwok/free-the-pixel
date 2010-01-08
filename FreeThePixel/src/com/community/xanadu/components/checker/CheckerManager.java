package com.community.xanadu.components.checker;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.image.ColorTintFilter;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;

import com.jhlabs.image.GaussianFilter;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.OverlayableIconsFactory;
import com.jidesoft.swing.OverlayableUtils;

public class CheckerManager {
	private static final String CHECK_NEEDED = "com.sicpa.common.gui.components.layeredComponents.checker.CHECK_NEEDED";

	public static enum eViewError {
		blinking, icon, both
	}

	protected CheckerManager() {
	}

	/**
	 * return a component that give visual feedback if the component does not
	 * pass the checkers tests
	 * 
	 * @param <T>
	 * 
	 * @param compToTrack
	 *            the JComponent to be wrapped
	 * @param checkers
	 *            the checkers to control the input of the JComponent
	 * @param type
	 *            the type of visual feedback
	 * @return JComponent the wrapping JComponent around the JComponent
	 */
	public static <T extends JComponent> JComponent getComponentWithCheckers(final T compToTrack,
			final List<Checker<T>> checkers, final eViewError type, final Shape shape) {
		if (compToTrack == null) {
			throw new IllegalArgumentException("The compToTrack cannot be null");
		}
		if (checkers == null) {
			throw new IllegalArgumentException("The Checkers cannot be null");
		}
		if (checkers.size() == 0) {
			throw new IllegalArgumentException("The Checkers size must be greater than 0");
		}
		JComponent comp = null;

		switch (type) {
		case blinking:
			comp = getBlinking(compToTrack, checkers, shape);
			break;
		case icon:
			comp = getErrorOverlayedIcon(checkers, compToTrack, compToTrack);
			break;
		case both:

			// comp = getErrorOverlayedIcon(checkers, compToTrack, compToTrack);
			// comp = getBlinking(comp, compToTrack, checkers, shape);

			comp = getBlinking(compToTrack, compToTrack, checkers, shape);
			comp = getErrorOverlayedIcon(checkers, comp, compToTrack);

			break;
		}

		return comp;
	}

	public static <T extends JComponent> JComponent getComponentWithCheckers(final T compToTrack,
			final Checker<T> checker, final eViewError type) {
		return getComponentWithCheckers(compToTrack, checker, type, null);
	}

	public static <T extends JComponent> JComponent getComponentWithCheckers(final T compToTrack,
			final Checker<T> checker, final eViewError type, final Shape shape) {
		ArrayList<Checker<T>> checkers = new ArrayList<Checker<T>>();
		checkers.add(checker);
		return getComponentWithCheckers(compToTrack, checkers, type, shape);
	}

	private static <T extends JComponent> JComponent getBlinking(final JComponent overlayed,
			final T originalCompTotrack, final List<Checker<T>> checkers, final Shape shape) {
		JXLayer<JComponent> res = new JXLayer<JComponent>(overlayed);
		final BlinkingLayer<T> layer = new BlinkingLayer<T>(overlayed, originalCompTotrack, checkers, shape, res);
		res.setUI(layer);
		layer.check();
		return res;
	}

	private static <T extends JComponent> JComponent getBlinking(final T compToTrack, final List<Checker<T>> checkers,
			final Shape shape) {
		JXLayer<JComponent> res = new JXLayer<JComponent>(compToTrack);
		final BlinkingLayer<T> layer = new BlinkingLayer<T>(null, compToTrack, checkers, shape, res);
		res.setUI(layer);
		layer.check();
		return res;
	}

	private static class JPanelIcon extends JPanel {
		private static BufferedImage img;

		public JPanelIcon() {
			setPreferredSize(new Dimension(13, 13));
			if (img == null) {
				Icon icon = OverlayableUtils.getPredefinedOverlayIcon(OverlayableIconsFactory.ERROR);
				img = GraphicsUtilities.createCompatibleTranslucentImage(13, 13);
				Graphics2D g2 = (Graphics2D) img.getGraphics();
				icon.paintIcon(this, g2, 1, 1);
				g2.dispose();
				new ColorTintFilter(Color.BLACK, 1f).filter(img, img);
				new GaussianFilter(2).filter(img, img);
				g2 = (Graphics2D) img.getGraphics();
				icon.paintIcon(this, g2, 0, 0);
				g2.dispose();
			}
			setOpaque(false);
		}

		@Override
		protected void paintComponent(final java.awt.Graphics g) {
			g.drawImage(img, 0, 0, null);
		};

	}

	private static <T extends JComponent> JComponent getErrorOverlayedIcon(final List<Checker<T>> checkers,
			final JComponent comp, final T originalTrackedComp) {

		JPanelIcon icon = new JPanelIcon();

		JXLayer<JComponent> res = new JXLayer<JComponent>(comp);
		final IconLayer<T> layer = new IconLayer<T>(comp, originalTrackedComp, checkers, icon);
		res.setUI(layer);
		res.setOpaque(false);

		DefaultOverlayable over = new DefaultOverlayable(res, icon, DefaultOverlayable.NORTH_EAST);
		over.setOpaque(false);
		over.setOverlayLocationInsets(new Insets(4, 0, 0, 4));

		layer.check();

		return over;
	}

	public static class BlinkingLayer<T extends JComponent> extends AbstractLayerUI<JComponent> {
		private List<Checker<T>> checkers;
		private T trackedComponent;
		private String msg;
		private float alpha = 1;

		private Shape shape;

		private JComponent extraComp;

		private JXLayer<JComponent> jxlayer;

		public float getAlpha() {
			return this.alpha;
		}

		public void setAlpha(final float alpha) {
			this.alpha = alpha;
			this.jxlayer.repaint();
		}

		private Timeline animator;

		public BlinkingLayer(final JComponent extraComp, final T compToTrack, final List<Checker<T>> checkers,
				final Shape shape, final JXLayer<JComponent> jxlayer) {
			super();
			this.animator = new Timeline(this);
			this.animator.setDuration(2000);
			this.animator.addPropertyToInterpolate("alpha", 0.1f, 0.9f);
			this.extraComp = extraComp;
			this.shape = shape;
			this.checkers = checkers;
			this.trackedComponent = compToTrack;
			this.jxlayer = jxlayer;

			this.trackedComponent.addPropertyChangeListener(CHECK_NEEDED, new PropertyChangeListener() {
				@Override
				public void propertyChange(final PropertyChangeEvent evt) {
					check();
				}
			});
		}

		public void check() {
			this.msg = getErrors(this.checkers, this.trackedComponent);
			if (this.msg != null && !this.msg.isEmpty()) {
				this.animator.playLoop(RepeatBehavior.REVERSE);
			} else {
				this.animator.cancel();
				this.trackedComponent.repaint();
			}
		}

		@Override
		protected void paintLayer(final Graphics2D g2, final JXLayer<JComponent> l) {
			super.paintLayer(g2, l);
			// custom painting:
			// here we paint translucent foreground
			// over the whole layer
			if (this.msg != null && !this.msg.isEmpty()) {
				AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.alpha);
				g2.setComposite(composite);
				g2.setColor(new Color(250, 0, 0, 160));
				if (this.shape != null) {
					g2.fill(this.shape);
				} else {
					g2.fillRect(0, 0, this.trackedComponent.getWidth(), this.trackedComponent.getHeight());
				}

				composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.alpha);
				g2.setComposite(composite);
			}
		}
	}

	public static class IconLayer<T extends JComponent> extends AbstractLayerUI<JComponent> {
		private List<Checker<T>> checkers;
		private T trackedComp;

		private String msg;
		private JComponent label;
		private JComponent comp;

		public IconLayer(final JComponent comp, final T originalTrackedComp, final List<Checker<T>> checkers,
				final JComponent info) {
			super();
			this.checkers = checkers;
			this.trackedComp = originalTrackedComp;
			this.label = info;
			this.comp = comp;

			this.trackedComp.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(final PropertyChangeEvent evt) {
					check();
				}
			});
		}

		public void check() {
			this.msg = getErrors(this.checkers, this.trackedComp);
			if (this.msg != null && !this.msg.isEmpty()) {
				this.label.setVisible(true);
				this.label.setToolTipText(this.msg);
			} else {
				this.label.setVisible(false);
			}
		}

		private boolean needRepaint = false;

		@Override
		protected void paintLayer(final Graphics2D g2, final JXLayer<JComponent> l) {
			super.paintLayer(g2, l);
			if (this.needRepaint) {
				this.needRepaint = false;
				this.label.repaint();
			} else {
				this.needRepaint = true;
			}

		}
	}

	private static <T> String getErrors(final List<Checker<T>> checkers, final T compToTracked) {
		StringBuffer sb = new StringBuffer();
		String why;
		for (Checker<T> checker : checkers) {
			why = checker.validate(compToTracked);
			if (why != null && !why.isEmpty()) {
				sb.append(why).append("<br>");
			}
		}

		if (sb.length() > 0) {
			sb.insert(0, "<html>");
			sb.append("<html/>");
		}
		return sb.toString();
	}

	public static void check(final JComponent comp) {
		comp.firePropertyChange(CheckerManager.CHECK_NEEDED, true, false);
	}

}
