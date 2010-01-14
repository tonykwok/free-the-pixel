package com.community.xanadu.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.RepaintManager;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.graphics.ReflectionRenderer;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallback;

import com.community.xanadu.utils.ThreadUtils;
import com.jhlabs.image.PerspectiveFilter;

public class CoverFlow extends JPanel {
	public static int ItemWidthSize = 100;
	public static int ItemHeighSize = 100;
	// private static final double angleLeft = Math.PI / 3;
	// private static final double angleRight = Math.PI / 3 * 2;
	// private static final double angleLeft = Math.PI / 7*3;
	// private static final double angleRight = Math.PI / 7 * 4;
	private static final double angleLeft = Math.PI / 8 * 3;
	private static final double angleRight = Math.PI / 8 * 5;
	public static int oneStep = 40;
	// private static final Color background = new Color(255, 255, 255, 255);

	private float animProgress = 0;
	private int selectedIndex;
	private final ArrayList<Item> items;
	private boolean goingNext;

	private JPanel scrollPanel;
	private JScrollBar scrollBar;

	private Timeline anim = new Timeline();
	private final int animDuration = 300;

	public void setAnimProgress(final float animProgress) {
		this.animProgress = animProgress;
	}

	public float getAnimProgress() {
		return this.animProgress;
	}

	public CoverFlow() {
		// not needed for java 6_11+ , only needed for 6_10
		RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);
		if (angleLeft + angleRight != Math.PI) {
			throw new RuntimeException("the sum right + angle should be  equal to PI");
		}
		if (angleLeft == Math.PI / 2) {
			throw new RuntimeException("angle can't be PI/2 => nothing to see");
		}
		this.items = new ArrayList<Item>();
		this.selectedIndex = 0;
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill,inset 0 0 0 0"));
		add(getScrollPanel(), "grow,push,wrap");
		add(getScrollBar(), "growx");
		setOpaque(false);
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(final MouseWheelEvent e) {
				thisMouseWheelMoved(e);
			}
		});
	}

	private void thisMouseWheelMoved(final MouseWheelEvent evt) {
		if (evt.getWheelRotation() > 0) {
			moveToNext();
		} else {
			moveToPrevious();
		}
	}

	private JPanel getScrollPanel() {
		if (this.scrollPanel == null) {
			this.scrollPanel = new JPanel();
			this.scrollPanel.setLayout(null);
			this.scrollPanel.setOpaque(false);
		}
		return this.scrollPanel;
	}

	private JScrollBar getScrollBar() {
		if (this.scrollBar == null) {
			this.scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
			this.scrollBar.addAdjustmentListener(new AdjustmentListener() {
				@Override
				public void adjustmentValueChanged(final AdjustmentEvent e) {
					scrollBarAdjustmentValueChanged(e);
				}
			});
		}
		return this.scrollBar;
	}

	private void scrollBarAdjustmentValueChanged(final AdjustmentEvent e) {
		moveTo(e.getValue());
	}

	public void addItem(final Item i) {
		i.setSize(ItemWidthSize, ItemHeighSize * 2);// *2 to see the reflection
		this.items.add(i);
		final int index = this.items.size() - 1;
		i.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (index == CoverFlow.this.selectedIndex) {
					if (i.action != null) {
						i.action.actionPerformed(null);
					}
				} else {
					moveTo(index);
				}
			}
		});

		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getScrollPanel().add(i);
				getScrollBar().setMaximum(CoverFlow.this.items.size());
			}
		});
	}

	public void moveTo(final int index) {
		if (this.selectedIndex == index) {
			return;
		} else if (index > this.selectedIndex) {
			moveToNext();
		} else {
			moveToPrevious();
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				moveTo(index);
			}
		}).start();
	}

	private void placeOneitem(final int i) {
		final float middle = getWidth() / 2;
		float xoffset = 0;

		if (i < this.selectedIndex) {// left
			if (this.goingNext) {
				if (this.selectedIndex == i + 1) {
					xoffset = -((this.selectedIndex - i) + (this.animProgress))
							* (oneStep - ((1f - this.animProgress) * (ItemWidthSize) / 2));
					if (xoffset > 0) {
						xoffset = 0;
					}
				} else {
					xoffset = -((this.selectedIndex - i) + (this.animProgress)) * oneStep;
				}
			} else {
				xoffset = -((this.selectedIndex - i) + ((this.animProgress) + 1)) * oneStep;
			}
			xoffset = xoffset - ItemWidthSize / 2;

			if (this.anim == null || (i < this.selectedIndex - 1 && this.items.get(i).getAngle() != angleLeft)
					|| this.anim.getState() != TimelineState.PLAYING_FORWARD) {
				this.items.get(i).setAngle(angleLeft);
			}
		} else if (this.selectedIndex == i) {// selected
			if (this.goingNext) {
				xoffset = (1 - this.animProgress) * ItemWidthSize;
			} else {
				xoffset = (-this.animProgress) * ItemWidthSize;
			}

			xoffset -= ItemWidthSize / 2;

			if (this.anim == null || this.anim.getState() != TimelineState.PLAYING_FORWARD) {
				this.items.get(i).setAngle(0.0f);
			}
		} else if (i > this.selectedIndex) {// right
			if (this.goingNext) {
				xoffset = ((i - this.selectedIndex) + (-this.animProgress + 1)) * oneStep;
				xoffset -= oneStep / 4;
			} else {
				if (this.selectedIndex == i - 1) {
					xoffset = (-ItemWidthSize / 2) + (1f - this.animProgress) * 2 * oneStep;

					if (xoffset < -ItemWidthSize) {
						xoffset = -ItemWidthSize;
					}
				} else {
					xoffset = ((i - this.selectedIndex) + (-this.animProgress)) * oneStep;
					xoffset -= oneStep / 4;
				}
			}
			if (this.anim == null || (i > this.selectedIndex + 1 && this.items.get(i).getAngle() != angleRight)
					|| this.anim.getState() != TimelineState.PLAYING_FORWARD) {
				this.items.get(i).setAngle(angleRight);
			}
		}
		getScrollPanel().remove(this.items.get(i));
		getScrollPanel().add(this.items.get(i));
		this.items.get(i).setLocation((int) (middle + xoffset), 20);
	}

	public void placeItems() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				// starting to add from the middle
				for (int i = CoverFlow.this.selectedIndex; i >= 0; i--) {
					placeOneitem(i);
				}
				for (int i = CoverFlow.this.selectedIndex + 1; i < CoverFlow.this.items.size(); i++) {
					placeOneitem(i);
				}
			}
		});
	}

	public void moveToNext() {
		if (this.selectedIndex < this.items.size() - 1) {
			this.anim.cancel();

			this.selectedIndex++;
			this.goingNext = true;
			this.anim = new Timeline();
			this.anim.setDuration(this.animDuration);
			this.anim.addCallback(this.targetNext);
			this.anim.play();

			ThreadUtils.invokeLater(new Runnable() {
				@Override
				public void run() {
					getScrollBar().setValue(CoverFlow.this.selectedIndex);
				}
			});
		}
	}

	private final TimelineCallback targetNext = new TimelineCallback() {
		@Override
		public void onTimelinePulse(final float durationFraction, final float timelinePosition) {
			ThreadUtils.invokeLater(new Runnable() {
				@Override
				public void run() {
					CoverFlow.this.animProgress = timelinePosition;
					CoverFlow.this.items.get(CoverFlow.this.selectedIndex).setAngle(
							timelinePosition * angleLeft + angleRight);
					CoverFlow.this.items.get(CoverFlow.this.selectedIndex - 1).setAngle(
							timelinePosition * angleLeft);
					placeItems();
				}
			});
		}

		@Override
		public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
				final float durationFraction, final float timelinePosition) {
			if (newState == TimelineState.DONE || newState == TimelineState.CANCELLED) {
				onTimelinePulse(1f, 1f);
			}
		}
	};
	private final TimelineCallback targetPrevious = new TimelineCallback() {
		@Override
		public void onTimelinePulse(final float durationFraction, final float timelinePosition) {
			ThreadUtils.invokeLater(new Runnable() {
				@Override
				public void run() {
					CoverFlow.this.animProgress = 1 - timelinePosition;
					CoverFlow.this.items.get(CoverFlow.this.selectedIndex).setAngle(
							(1 - timelinePosition) * angleLeft);
					CoverFlow.this.items.get(CoverFlow.this.selectedIndex + 1).setAngle(
							Math.PI - timelinePosition * angleLeft);
					placeItems();
				}
			});
		}

		@Override
		public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
				final float durationFraction, final float timelinePosition) {
			if (newState == TimelineState.DONE || newState == TimelineState.CANCELLED) {
				onTimelinePulse(1f, 1f);
			}
		}
	};

	public void moveToPrevious() {
		if (0 < this.selectedIndex) {
			this.anim.cancel();

			this.selectedIndex--;
			this.goingNext = false;
			this.anim = new Timeline();
			this.anim.setDuration(this.animDuration);
			this.anim.addCallback(this.targetPrevious);
			this.anim.play();

			ThreadUtils.invokeLater(new Runnable() {
				@Override
				public void run() {
					getScrollBar().setValue(CoverFlow.this.selectedIndex);
				}
			});
		}
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		// g.setColor(background);
		// g.fillRect(0, 0, getWidth(), getHeight());
	}

	public static class Item extends JPanel {
		private final BufferedImage front;
		private final BufferedImage back;
		private double angle = 0d;
		private final BufferedImage interImage;
		private final BufferedImage shadedImage;
		private final int PERSPECTIVE = 20;
		private int y;
		private int x;
		private final BufferedImage img;

		private final AbstractAction action;

		public Item(final BufferedImage buff) {
			this(buff, null);
		}

		public Item(final BufferedImage buff, final AbstractAction action) {
			this.img = buff;
			this.action = action;
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
			super.paintComponent(g);

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

			if (this.angle < Math.PI / 2) {
				final int dx0 = imageX + x0;
				final int dy0 = imageY + y0;

				g2.drawImage(this.interImage, dx0, dy0, null);

			} else if (this.angle <= Math.PI) {
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

			final double degree = Math.abs(((Math.PI / 2) - this.angle) / (Math.PI / 2));
			final int alpha = (int) (200 - (200 * degree));

			final GradientPaint gp = new GradientPaint(0, 0, new Color(0, 0, 0, 0), image.getWidth(), 0, new Color(0, 0, 0,
					alpha));
			g2.setPaint(gp);
			g2.fillRect(0, 0, image.getWidth(), ItemWidthSize);
			g2.dispose();

			// now perspective adjust
			final int ix = image.getWidth() / 2;

			this.x = (int) Math.abs(ix * (Math.cos(this.angle - Math.PI)));

			this.y = (int) (((this.PERSPECTIVE / 2) * Math.sin(this.angle)));

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
			if (this.angle < Math.PI / 2) {
				return this.front;
			} else {
				return this.back;
			}
		}

		private BufferedImage getImageFront() {
			final BufferedImage withR = new ReflectionRenderer().appendReflection(this.img);
			return withR;
		}

		private BufferedImage getImageBack() {
			final BufferedImage buff = GraphicsUtilities.createCompatibleTranslucentImage(ItemWidthSize, ItemHeighSize);
			final Graphics2D g = buff.createGraphics();
			g.scale(-1, 1);
			g.drawImage(this.img, -ItemWidthSize, 0, null);
			g.dispose();
			final BufferedImage withR = new ReflectionRenderer().appendReflection(buff);

			return withR;
		}

		public void setAngle(final double angle) {
			this.angle = angle;

			updateImage();
		}

		public double getAngle() {
			return this.angle;
		}

		@Override
		public boolean contains(final int x, final int y) {
			// small hack because the image of the component
			// is actully around 1/3 of the actual component
			// when the component is not selected

			if (x < getWidth() / 3 || x > getWidth() / 3 * 2) {
				return false;
			}
			return super.contains(x, y);
		}
	}
}