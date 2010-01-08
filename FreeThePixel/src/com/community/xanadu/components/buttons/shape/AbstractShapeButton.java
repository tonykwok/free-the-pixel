package com.community.xanadu.components.buttons.shape;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.JButton;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.internal.utils.SubstanceColorUtilities;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.ease.Spline;

import com.community.xanadu.utils.PaintUtils;
import com.community.xanadu.utils.trident.triggers.MouseTrigger;
import com.community.xanadu.utils.trident.triggers.MouseTriggerEvent;
import com.jhlabs.image.GaussianFilter;

public abstract class AbstractShapeButton extends JButton {
	private static final long serialVersionUID = 1L;
	protected Shape shape;

	private BufferedImage shadowBuffer;

	protected boolean withShadow;
	private float animProgress;
	private Color armedColor;
	private Color defaultColor;
	private Color currentColor;

	public AbstractShapeButton() {
		super();

		if (SubstanceLookAndFeel.isCurrentLookAndFeel()) {
			SubstanceColorScheme activeColor = SubstanceLookAndFeel.getCurrentSkin().getColorScheme(this,
					ComponentState.ACTIVE);
			SubstanceColorScheme defaultColor = SubstanceLookAndFeel.getCurrentSkin().getColorScheme(this,
					ComponentState.DEFAULT);
			this.armedColor = activeColor.getMidColor();
			this.defaultColor = defaultColor.getMidColor();
		} else {
			this.armedColor = Color.CYAN;
			this.defaultColor = Color.BLUE;
		}

		this.currentColor = this.defaultColor;

		this.withShadow = true;
		setOpaque(false);
		setBorderPainted(false);
		setContentAreaFilled(false);
		setFocusPainted(false);
		this.shape = getShape();

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				AbstractShapeButton.this.shadowBuffer = null;
				AbstractShapeButton.this.shape = getShape();
				repaint();
			}

			@Override
			public void componentShown(final ComponentEvent e) {
				AbstractShapeButton.this.shadowBuffer = null;
				AbstractShapeButton.this.shape = getShape();
			}
		});

		Timeline timeline = new Timeline(this);
		timeline.addPropertyToInterpolate("animProgress", 0f, 1f);
		timeline.setDuration(200);
		timeline.setEase(new Spline(0.7f));
		MouseTrigger.addTrigger(this, timeline, MouseTriggerEvent.PRESS, true);
	}

	protected abstract Shape getShape();

	@Override
	protected void paintComponent(final Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();

		PaintUtils.turnOnAntialias(g2);

		if (this.withShadow) {
			if (this.shadowBuffer == null) {
				this.shadowBuffer = GraphicsUtilities.createCompatibleTranslucentImage(getWidth(), getHeight());
				Graphics2D g2shadow = (Graphics2D) this.shadowBuffer.getGraphics();
				PaintUtils.turnOnAntialias(g2shadow);
				int blurRadius = 3;
				g2shadow.translate(1, 1);

				g2shadow.setComposite(AlphaComposite.SrcOver.derive(0.6f));
				g2shadow.setColor(Color.BLACK);
				g2shadow.fill(this.shape);

				GaussianFilter gausfilter = new GaussianFilter(blurRadius);
				gausfilter.filter(this.shadowBuffer, this.shadowBuffer);

				g2shadow.dispose();
			}
			g2.drawImage(this.shadowBuffer, 0, 0, null);
		}

		paintShape(g2, this.animProgress, this.shape);
		g2.dispose();
	}

	protected void paintShape(final Graphics2D g, final float fraction, final Shape shape) {
		g.setColor(isEnabled() ? this.currentColor : Color.GRAY);
		g.fill(shape);
	}

	public void setAnimProgress(final float animProgress) {
		this.animProgress = animProgress;
		this.currentColor = (SubstanceColorUtilities.getInterpolatedColor(this.armedColor, this.defaultColor,
				animProgress));
		repaint();
	}

	public void setDefaultColor(final Color defaultColor) {
		this.defaultColor = defaultColor;
		this.currentColor = (SubstanceColorUtilities.getInterpolatedColor(this.armedColor, this.defaultColor,
				this.animProgress));
		repaint();
	}

	public void setArmedColor(final Color armedColor) {
		this.armedColor = armedColor;
		this.currentColor = (SubstanceColorUtilities.getInterpolatedColor(this.armedColor, this.defaultColor,
				this.animProgress));
		repaint();
	}

	public Color getDefaultColor() {
		return this.defaultColor;
	}

	public Color getArmedColor() {
		return this.armedColor;
	}

	public Color getCurrentColor() {
		return this.currentColor;
	}

	public void resetShape() {
		this.shadowBuffer = null;
		this.shape = getShape();
		repaint();
	}
}