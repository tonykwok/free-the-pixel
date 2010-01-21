package com.community.xanadu.components.validation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JPanel;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.image.ColorTintFilter;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.UIThreadTimelineCallbackAdapter;
import org.pushingpixels.trident.ease.Spline;

import com.jhlabs.image.GaussianFilter;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.OverlayableIconsFactory;
import com.jidesoft.swing.OverlayableUtils;

public class ValidationPanelIcon extends JPanel {

	private static BufferedImage defaultIcon;
	public static int DEFAULT_XOFFSET = 4;
	public static int DEFAULT_YOFFSET = 4;

	public static int DEFAULT_ICON_LOCATION = DefaultOverlayable.NORTH_EAST;

	public static void setDefaultIcon(final BufferedImage defaultImage) {
		ValidationPanelIcon.defaultIcon = defaultImage;
	}

	public static BufferedImage getDefaultIcon() {
		if (defaultIcon == null) {
			final Icon icon = OverlayableUtils.getPredefinedOverlayIcon(OverlayableIconsFactory.ERROR);
			defaultIcon = GraphicsUtilities.createCompatibleTranslucentImage(13, 13);
			Graphics2D g2 = (Graphics2D) defaultIcon.getGraphics();
			icon.paintIcon(null, g2, 1, 1);
			g2.dispose();
			new ColorTintFilter(Color.BLACK, 1f).filter(defaultIcon, defaultIcon);
			new GaussianFilter(2).filter(defaultIcon, defaultIcon);
			g2 = (Graphics2D) defaultIcon.getGraphics();
			icon.paintIcon(null, g2, 0, 0);
			g2.dispose();
		}
		return defaultIcon;
	}

	private BufferedImage image;

	private float alpha;

	public ValidationPanelIcon(final boolean showIcon) {
		setPreferredSize(new Dimension(13, 13));
		this.alpha = 1;
		if (showIcon) {
			this.image = getDefaultIcon();
		}
		setOpaque(false);
	}

	@Override
	protected void paintComponent(final java.awt.Graphics g) {
		if (this.image == null) {
			return;
		}
		final Graphics2D g2 = (Graphics2D) g;
		g2.setComposite(AlphaComposite.SrcOver.derive(this.alpha));
		g2.drawImage(this.image, 0, 0, null);
	};

	private Timeline timelineShow;
	private Timeline timelineHide;

	public void startShowAnim() {

		if (this.timelineHide != null && this.timelineHide.getState() == TimelineState.PLAYING_FORWARD) {
			this.timelineHide.cancel();
		}

		if (this.timelineShow != null && this.timelineShow.getState() == TimelineState.PLAYING_FORWARD) {
			return;
		}
		this.timelineShow = new Timeline(this);
		this.timelineShow.setDuration(300);
		this.timelineShow.setEase(new Spline(0.7f));
		this.timelineShow.addPropertyToInterpolate("alpha", 0f, 1f);
		setVisible(true);
		this.timelineShow.play();
	}

	public void startHideAnim() {

		if (this.timelineShow != null && this.timelineShow.getState() == TimelineState.PLAYING_FORWARD) {
			this.timelineShow.cancel();
		}

		if (this.timelineHide != null && this.timelineHide.getState() == TimelineState.PLAYING_FORWARD) {
			return;
		}

		this.timelineHide = new Timeline(this);
		this.timelineHide.setDuration(300);
		this.timelineHide.setEase(new Spline(0.7f));
		this.timelineHide.addPropertyToInterpolate("alpha", 1f, 0f);
		this.timelineHide.addCallback(new UIThreadTimelineCallbackAdapter() {
			@Override
			public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {

				if (newState == TimelineState.DONE) {
					setVisible(false);
				}
			}
		});
		this.timelineHide.play();
	}

	public void setAlpha(final float alpha) {
		this.alpha = alpha;
		repaint();
	}

	public void setImage(final BufferedImage image) {
		this.image = image;
		repaint();
	}
}