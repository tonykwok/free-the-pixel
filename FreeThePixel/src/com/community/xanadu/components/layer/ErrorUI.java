package com.community.xanadu.components.layer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;

import javax.swing.JComponent;

import org.jdesktop.jxlayer.JXLayer;

import com.community.xanadu.utils.PaintUtils;

public class ErrorUI extends LockUI {
	private final GeneralPath closeButtonShape;

	private boolean closeable;
	private Color outterColor = Color.RED;
	private Color innerColor = Color.BLACK;
	private int fontSize = 35;
	private String text;

	public ErrorUI(final JXLayer<JComponent> comp) {
		super(comp);

		this.closeable = false;

		setBackground(new Color(55, 25, 25));

		final float w = 10;
		final float h = 10;
		this.closeButtonShape = new GeneralPath();
		this.closeButtonShape.moveTo(0, h);
		this.closeButtonShape.lineTo(w, 0);
		this.closeButtonShape.lineTo(3 * w, 2 * h);
		this.closeButtonShape.lineTo(5 * w, 0);
		this.closeButtonShape.lineTo(6 * w, h);
		this.closeButtonShape.lineTo(4 * w, 3 * h);
		this.closeButtonShape.lineTo(6 * w, 5 * h);
		this.closeButtonShape.lineTo(5 * w, 6 * h);
		this.closeButtonShape.lineTo(3 * w, 4 * h);
		this.closeButtonShape.lineTo(w, 6 * h);
		this.closeButtonShape.lineTo(0, 5 * h);
		this.closeButtonShape.lineTo(2 * w, 3 * h);
		this.closeButtonShape.closePath();

		comp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				thisMouseClicked(e);
			}
		});
	}

	@Override
	protected void paintLayer(final Graphics2D g, final JXLayer<JComponent> comp) {
		super.paintLayer(g, comp);
		final Graphics2D g2 = (Graphics2D) g.create();
		g2.setComposite(AlphaComposite.SrcOver);
		if (isCloseable()) {
			paintCloseButton(g2);
		}
		paintText(g2);
	}

	public void thisMouseClicked(final MouseEvent evt) {
		if (evt.getClickCount() == 1 && this.closeable) {
			final Rectangle bounds = this.closeButtonShape.getBounds();
			bounds.x = this.comp.getWidth() - 70;
			bounds.y = this.comp.getHeight() - 70;
			if (bounds.contains(evt.getPoint())) {
				hideError();
			}
		}
	}

	public synchronized void showError(final String text) {
		this.text = text;
		Lock();
	}

	public synchronized void hideError() {
		unLock();
	}

	public GeneralPath getCloseButtonShape() {
		return this.closeButtonShape;
	}

	public boolean isCloseable() {
		return this.closeable;
	}

	public void setCloseable(final boolean closeable) {
		this.closeable = closeable;
	}

	private void paintCloseButton(final Graphics2D g) {
		if (isCloseable()) {
			final Graphics2D g2 = (Graphics2D) g.create();
			PaintUtils.turnOnAntialias(g2);
			g.setComposite(AlphaComposite.SrcOver.derive(getAlpha()));
			g2.setColor(Color.white);
			final Area front = new Area(getCloseButtonShape());
			front
					.transform(AffineTransform.getTranslateInstance(this.comp.getWidth() - 70,
							this.comp.getHeight() - 70));
			g2.fill(front);

			// ---shadow
			g2.setColor(Color.black);
			// shadow
			final Area shadow = new Area(getCloseButtonShape());
			shadow.transform(AffineTransform
					.getTranslateInstance(this.comp.getWidth() - 67, this.comp.getHeight() - 68));
			// only get the visible shadow
			shadow.subtract(front);
			g2.fill(shadow);

			g2.dispose();
		}
	}

	private void paintText(final Graphics2D g) {
		if (this.text != null) {
			g.setFont(g.getFont().deriveFont((float) getFontSize()));
			g.setComposite(AlphaComposite.SrcOver.derive(getAlpha()));
			PaintUtils.drawMultiLineHighLightText(g, this.comp, this.text, getInnerColor(), getOutterColor());
		}
	}

	public Color getOutterColor() {
		return this.outterColor;
	}

	public void setOutterColor(final Color outterColor) {
		this.outterColor = outterColor;
	}

	public Color getInnerColor() {
		return this.innerColor;
	}

	public void setInnerColor(final Color innerColor) {
		this.innerColor = innerColor;
	}

	public int getFontSize() {
		return this.fontSize;
	}

	public void setFontSize(final int fontSize) {
		this.fontSize = fontSize;
	}
}
