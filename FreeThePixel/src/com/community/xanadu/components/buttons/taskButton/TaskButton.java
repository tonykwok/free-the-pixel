package com.community.xanadu.components.buttons.taskButton;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;

import org.pushingpixels.substance.internal.utils.SubstanceColorUtilities;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallback;

import com.community.xanadu.utils.PaintUtils;

public class TaskButton extends JButton {
	private Dimension componentDimension = new Dimension(0, 0);
	protected BufferedImage image;
	private Shape clickable;
	protected Rectangle textRect;
	protected Rectangle imageRect;

	private float ghostValue = 0.0f;
	private float newFraction = 0.0f;

	private boolean mouseEnter = false;

	private final String name;
	private final String description;

	private Color categoryColor = Color.BLUE;
	private Font categoryFont = getFont().deriveFont(Font.PLAIN, 25);
	private Font categorySmallFont = getFont().deriveFont(Font.PLAIN, 18);
	private Color shadowColor = Color.black;
	private Color categoryHighlightColor = Color.red;

	private float shadowOffsetX = 2;
	private float shadowOffsetY = 2;

	private float shadowOpacity = 0.5f;
	private float categorySmallOpacity = 0.5f;

	private boolean clicked = false;

	private int animationDuration = 450;

	protected boolean textVCenter;

	// if true disable action performed => is true during the action performed
	// animation
	private boolean actionInProgress = false;
	private boolean withShadow = true;

	public TaskButton(final String name, final String description, final BufferedImage image) {
		this(name, description, image, false);
	}

	public TaskButton(final String name, final String description, final BufferedImage image, final boolean textVCenter) {
		this.name = name;
		this.description = description;
		this.image = image;
		this.textVCenter = textVCenter;

		setFocusable(false);

		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

		setOpaque(false);
		setContentAreaFilled(false);
		setFocusPainted(false);
		setBorderPainted(false);

		addMouseMotionListener(new GhostHandler());
		HiglightHandler higlightHandler = new HiglightHandler();
		addMouseListener(higlightHandler);
		addMouseMotionListener(higlightHandler);

		computeDimension();
	}

	private Dimension computeDimension() {
		Insets insets = getInsets();

		FontMetrics metrics = getFontMetrics(this.categoryFont);
		Rectangle2D bounds = metrics.getMaxCharBounds(getGraphics());
		int height = (int) bounds.getHeight() + metrics.getLeading();
		int nameWidth = SwingUtilities.computeStringWidth(metrics, this.name);

		metrics = getFontMetrics(this.categorySmallFont);
		bounds = metrics.getMaxCharBounds(getGraphics());
		height += bounds.getHeight();
		int descWidth = SwingUtilities.computeStringWidth(metrics, this.description == null ? "" : this.description);

		int width = Math.max(nameWidth, descWidth);
		width += this.image.getWidth() + 10;

		this.imageRect = new Rectangle(insets.left + 10, insets.top, this.image.getWidth(), this.image.getHeight());

		this.textRect = new Rectangle(insets.left + 10, insets.top, width, height);

		Area a = new Area(this.textRect);
		a.add(new Area(this.imageRect));
		this.clickable = a;

		height = Math.max(height, this.image.getHeight());
		height += 4;

		return new Dimension(width + insets.left + insets.right, height + insets.top + insets.bottom);
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getPreferredSize() {
		return this.componentDimension;
	}

	@Override
	protected void paintComponent(final Graphics g) {
		if (!isVisible()) {
			return;
		}

		if (this.image == null) {
			return;
		}

		Graphics2D g2 = (Graphics2D) g;
		setupGraphics(g2);

		float y = paintText(g2);
		paintImage(g2, y);

	}

	private void paintImage(final Graphics2D g2, final float y) {
		Insets insets = getInsets();
		insets.left = this.image.getWidth() / 4;

		// image first if clicked
		if (this.clicked) {
			g2.drawImage(this.image, null, insets.left, insets.top);
		}

		if (this.ghostValue > 0.0f) {
			int newWidth = (int) (this.image.getWidth() * (1.0 + this.ghostValue / 2.0));
			int newHeight = (int) (this.image.getHeight() * (1.0 + this.ghostValue / 2.0));

			Composite composite = g2.getComposite();
			if (this.clicked) {
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
						(float) (1.0f - this.ghostValue * 0.5)));
			} else {
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f * (1.0f - this.ghostValue)));
			}
			g2.drawImage(this.image,

			insets.left + (this.image.getWidth() - newWidth) / 2,

			insets.top + (this.image.getHeight() - newHeight) / 2,

			newWidth, newHeight, null);
			g2.setComposite(composite);
		}

		// image last if NOT clicked
		if (!this.clicked) {
			g2.drawImage(this.image, null, insets.left, insets.top);
		}
	}

	private float paintText(final Graphics2D g2) {
		g2.setFont(this.categoryFont);

		Insets insets = getInsets();
		insets.left = this.image.getWidth() / 4;

		FontRenderContext context = g2.getFontRenderContext();

		float y;
		float x;
		Composite composite;
		TextLayout layoutBigFont = new TextLayout(this.name, this.categoryFont, context);
		TextLayout layoutSmallFont = new TextLayout(this.description == null ? " " : this.description,
				this.categorySmallFont, context);
		Rectangle r = getRectangle();
		{

			x = this.image.getWidth() + 10.0f;
			x += insets.left;
			if (this.textVCenter) {
				y = (r.height - layoutBigFont.getAscent() - layoutSmallFont.getAscent());
			} else {
				y = 4.0f + (layoutBigFont.getAscent() - layoutBigFont.getDescent());
			}

			y += insets.top;

			if (this.withShadow) {
				g2.setColor(this.shadowColor);
				composite = g2.getComposite();
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.shadowOpacity));
				layoutBigFont.draw(g2, this.shadowOffsetX + x, this.shadowOffsetY + y);
				g2.setComposite(composite);
			}
			g2.setColor(SubstanceColorUtilities.getInterpolatedColor(this.categoryHighlightColor, this.categoryColor,
					this.newFraction));

			layoutBigFont.draw(g2, x, y);
			y += layoutBigFont.getDescent();
		}
		{

			y += layoutSmallFont.getAscent();
			composite = g2.getComposite();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.categorySmallOpacity));
			layoutSmallFont.draw(g2, x, y);
			g2.setComposite(composite);
		}
		return y;
	}

	private void setupGraphics(final Graphics2D g2) {
		PaintUtils.turnOnAntialias(g2);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	}

	private final class GhostHandler extends MouseMotionAdapter {
		private Timeline timer;

		public GhostHandler() {
			this.timer = new Timeline(this);
			this.timer.setDuration(TaskButton.this.animationDuration / 2);
			this.timer.addCallback(new AnimateGhost());
		}

		@Override
		public void mouseMoved(final MouseEvent e) {
			if (this.timer.getState() == TimelineState.PLAYING_FORWARD || TaskButton.this.mouseEnter) {
				return;
			}
			this.timer.play();
		}
	}

	@Override
	protected void fireActionPerformed(final ActionEvent e) {
		if (this.actionInProgress) {
			return;
		}
		this.actionInProgress = true;

		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(TaskButton.this.animationDuration);
				} catch (InterruptedException e) {
				}
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						// Guaranteed to return a non-null array
						Object[] listeners = TaskButton.this.listenerList.getListenerList();
						// Process the listeners last to first, notifying
						// those that are interested in this event
						for (int i = listeners.length - 2; i >= 0; i -= 2) {
							if (listeners[i] == ActionListener.class) {
								// Lazily create the event:
								if (TaskButton.this.changeEvent == null)
									TaskButton.this.changeEvent = new ChangeEvent(TaskButton.this);
								((ActionListener) listeners[i + 1]).actionPerformed(e);
							}
						}
						TaskButton.this.actionInProgress = false;
					}
				});
			}
		}).start();
	}

	private final class AnimateGhost implements TimelineCallback {
		@Override
		public void onTimelinePulse(final float durationFraction, final float timelinePosition) {
			TaskButton.this.ghostValue = timelinePosition;
			repaint();
		}

		@Override
		public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
				final float durationFraction, final float timelinePosition) {
			switch (newState) {
			case CANCELLED:
			case DONE:
			case SUSPENDED:
				TaskButton.this.ghostValue = 0.0f;
				TaskButton.this.clicked = false;
				repaint();
				break;
			case PLAYING_FORWARD:
				TaskButton.this.ghostValue = 0.0f;
			}
		}
	}

	private final class HiglightHandler extends MouseMotionAdapter implements MouseListener {
		private Timeline timer;

		@Override
		public void mouseMoved(final MouseEvent e) {
			if (!TaskButton.this.mouseEnter) {
				TaskButton.this.mouseEnter = true;
				if (this.timer != null && this.timer.getState() == TimelineState.PLAYING_FORWARD) {
					this.timer.abort();
				}

				this.timer = new Timeline(this);
				this.timer.setDuration(TaskButton.this.animationDuration / 2);
				this.timer.addCallback(new AnimateHighlight(true));

				this.timer.play();
			}
		}

		public void mouseClicked(final MouseEvent e) {
			if (this.timer != null && this.timer.getState() == TimelineState.PLAYING_FORWARD) {
				return;
			}

			TaskButton.this.clicked = true;

			this.timer = new Timeline(this);
			this.timer.setDuration(TaskButton.this.animationDuration);
			this.timer.addCallback(new AnimateGhost());

			this.timer.play();
		}

		public void mousePressed(final MouseEvent e) {
		}

		public void mouseReleased(final MouseEvent e) {
		}

		public void mouseEntered(final MouseEvent e) {
			TaskButton.this.mouseEnter = false;
			setHyperlinkCursor();
		}

		public void mouseExited(final MouseEvent e) {

			TaskButton.this.mouseEnter = false;
			setDefaultCursor();
			if (this.timer != null && this.timer.getState() == TimelineState.PLAYING_FORWARD) {
				this.timer.abort();
			}

			this.timer = new Timeline(this);
			this.timer.setDuration(TaskButton.this.animationDuration / 2);
			this.timer.addCallback(new AnimateHighlight(false));

			this.timer.play();
		}
	}

	private final class AnimateHighlight implements TimelineCallback {
		private boolean forward;
		private float oldValue;

		AnimateHighlight(final boolean forward) {
			this.forward = forward;
			this.oldValue = TaskButton.this.newFraction;
		}

		@Override
		public void onTimelinePulse(final float durationFraction, final float timelinePosition) {
			TaskButton.this.newFraction = this.oldValue + timelinePosition * (this.forward ? 1.0f : -1.0f);

			if (TaskButton.this.newFraction > 1.0f) {
				TaskButton.this.newFraction = 1.0f;
			} else if (TaskButton.this.newFraction < 0.0f) {
				TaskButton.this.newFraction = 0.0f;
			}
			repaint();

		}

		@Override
		public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
				final float durationFraction, final float timelinePosition) {
		}
	}

	@Override
	public String getToolTipText(final MouseEvent event) {
		if (this.clickable.contains(event.getPoint())) {
			return getToolTipText();
		} else {
			return null;
		}
	}

	@Override
	public boolean contains(final int x, final int y) {
		return this.clickable != null && this.clickable.contains(x, y);
	}

	private void setDefaultCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	private void setHyperlinkCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	public void setWithShadow(final boolean withShadow) {
		this.withShadow = withShadow;
	}

	protected Rectangle getRectangle() {
		Rectangle r = new Rectangle(this.textRect);
		r.add(this.imageRect);
		return r;
	}
}