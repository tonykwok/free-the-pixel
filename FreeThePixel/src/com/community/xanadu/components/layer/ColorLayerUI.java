package com.community.xanadu.components.layer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;
import org.pushingpixels.trident.Timeline;

import com.community.xanadu.demo.components.ColorLayerUIDemo;

public class ColorLayerUI extends AbstractLayerUI<JComponent> {

	public static void main(final String[] args) {
		ColorLayerUIDemo.main(args);
	}

	protected JXLayer<JComponent> comp;
	protected Timeline timeline;
	protected int animDuration = 500;
	protected float alpha;
	protected float maxAlpha;
	protected Color background;
	protected LayerUIPainter painter;

	public ColorLayerUI(final JXLayer<JComponent> comp) {
		super();
		this.background = Color.LIGHT_GRAY;
		this.maxAlpha = 0.7f;
		this.comp = comp;
	}

	@Override
	protected void paintLayer(final Graphics2D g, final JXLayer<JComponent> comp) {
		super.paintLayer(g, this.comp);

		if (this.painter != null) {
			this.painter.paint(g, comp, this, this.alpha);
		} else {
			if (this.alpha == 0) {
				return;
			}
			g.setColor(getBackground());
			g.setComposite(AlphaComposite.SrcOver.derive(getAlpha() * getMaxAlpha()));
			g.fillRect(0, 0, comp.getWidth(), comp.getHeight());
		}
	}

	public synchronized void ShowUI() {
		if (this.timeline != null) {
			this.timeline.cancel();
		}
		this.timeline = new Timeline(this);
		this.timeline.setDuration(this.animDuration);
		this.timeline.addPropertyToInterpolate("alpha", getAlpha(), 1f);
		this.timeline.play();
	}

	public synchronized void hideUI() {
		if (this.timeline != null) {
			this.timeline.cancel();
		}
		this.timeline = new Timeline(this);
		this.timeline.setDuration(this.animDuration);
		this.timeline.addPropertyToInterpolate("alpha", getAlpha(), 0f);
		this.timeline.play();
	}

	public void setAlpha(final float alpha) {
		this.alpha = alpha;
		this.comp.repaint();
	}

	public float getAlpha() {
		return this.alpha;
	}

	public float getMaxAlpha() {
		return this.maxAlpha;
	}

	public void setMaxAlpha(final float maxAlpha) {
		this.maxAlpha = maxAlpha;
	}

	public Color getBackground() {
		return this.background;
	}

	public void setBackground(final Color background) {
		this.background = background;
	}

	public void setPainter(final LayerUIPainter painter) {
		this.painter = painter;
	}
}