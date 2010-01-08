package com.community.xanadu.components.buttons.morphing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JButton;

import org.jdesktop.swingx.geom.Morphing2D;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.painter.fill.StandardFillPainter;
import org.pushingpixels.substance.api.painter.fill.SubstanceFillPainter;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.ease.Spline;

import com.community.xanadu.utils.PaintUtils;
import com.community.xanadu.utils.trident.triggers.MouseTrigger;
import com.community.xanadu.utils.trident.triggers.MouseTriggerEvent;

public abstract class AbstractMorphingButton extends JButton {
	private static final long serialVersionUID = 1L;
	protected float morphing = 0.0f;
	protected Shape sourceShape;
	protected Shape destinationShape;

	protected Paint textColor;
	protected Paint textShadowColor;
	protected Paint clickedPaint;
	protected Morphing2D morph;

	protected int animationDuration = 400;

	private final static SubstanceFillPainter painter = new StandardFillPainter();

	public AbstractMorphingButton(final String text) {
		super(text);

		setupTriggers();
		setFont(getFont().deriveFont(Font.BOLD));
		setOpaque(false);
		setBorderPainted(false);
		setContentAreaFilled(false);
		setFocusPainted(false);
		this.textColor = Color.white;
		this.textShadowColor = Color.BLACK;
		addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(final ComponentEvent e) {
				createSourceShape();
				createDestinationShape();
				AbstractMorphingButton.this.morph = createMorph();
				computeMorphing();
				repaint();
			}

			@Override
			public void componentHidden(final ComponentEvent e) {
			}

			@Override
			public void componentMoved(final ComponentEvent e) {
			}

			@Override
			public void componentShown(final ComponentEvent e) {
				createSourceShape();
				createSourceShape();
				AbstractMorphingButton.this.morph = createMorph();
				computeMorphing();
				repaint();
			}
		});
	}

	protected void setupTriggers() {
		Timeline timeline = new Timeline(this);
		timeline.addPropertyToInterpolate("morphing", 0f, 1f);
		timeline.setDuration(this.animationDuration);
		timeline.setEase(new Spline(0.8f));
		MouseTrigger.addTrigger(this, timeline, MouseTriggerEvent.ENTER, true);
	}

	protected void createSourceShape() {
		this.sourceShape = new RoundRectangle2D.Double(2.0, 2.0, getWidth() - 4.0, getHeight() - 4.0, 12.0, 12.0);
	}

	protected abstract void createDestinationShape();

	protected Morphing2D createMorph() {
		return new Morphing2D(this.sourceShape, this.destinationShape);
	}

	public float getMorphing() {
		return this.morphing;
	}

	public void setMorphing(final float morphing) {
		this.morphing = morphing;
		repaint();
	}

	@Override
	protected void paintComponent(final Graphics g) {
		initShape();

		computeMorphing();

		Graphics2D g2 = (Graphics2D) g.create();
		PaintUtils.turnOnAntialias(g2);
		if (getModel().isArmed()) {
			g2.setPaint(this.clickedPaint);
		}
		PaintUtils.turnOnAntialias(g2);
		if (isEnabled()) {
			painter.paintContourBackground(g2, this, getWidth(), getHeight(), this.morph, false, SubstanceLookAndFeel
					.getCurrentSkin().getColorScheme(this, ComponentState.DEFAULT), true);

		} else {
			painter.paintContourBackground(g2, this, getWidth(), getHeight(), this.sourceShape, false,
					SubstanceLookAndFeel.getCurrentSkin().getColorScheme(this, ComponentState.DEFAULT), true);
		}
	}

	protected void computeMorphing() {
		if (this.morph == null) {
			this.morph = createMorph();
		}

		this.morph.setMorphing(getMorphing());
	}

	protected void initShape() {
		if (this.sourceShape == null) {
			createSourceShape();
		}
		if (this.destinationShape == null) {
			createDestinationShape();
		}
	}

	public Shape getSourceShape() {
		return this.sourceShape;
	}

	public void setSourceShape(final Shape sourceShape) {
		this.sourceShape = sourceShape;
	}

	public Shape getDestinationShape() {
		return this.destinationShape;
	}

	public void setDestinationShape(final Shape destinationShape) {
		this.destinationShape = destinationShape;
	}

	public Paint getTextColor() {
		return this.textColor;
	}

	public void setTextColor(final Paint textColor) {
		this.textColor = textColor;
	}

	public Paint getTextShadowColor() {
		return this.textShadowColor;
	}

	public void setTextShadowColor(final Paint textShadowColor) {
		this.textShadowColor = textShadowColor;
	}

	public Paint getClickedPaint() {
		return this.clickedPaint;
	}

	public void setClickedPaint(final Paint clickedPaint) {
		this.clickedPaint = clickedPaint;
	}

	public int getAnimationDuration() {
		return this.animationDuration;
	}

	public void setAnimationDuration(final int animationDuration) {
		this.animationDuration = animationDuration;
	}

}