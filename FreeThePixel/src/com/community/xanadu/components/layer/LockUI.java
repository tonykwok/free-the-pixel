package com.community.xanadu.components.layer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.Timer;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.ext.LockableUI;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;

import com.community.xanadu.utils.ThreadUtils;

public class LockUI extends LockableUI {
	protected JXLayer<JComponent> comp;
	protected Timeline timeline;
	protected int animDuration = 500;
	protected boolean withRefresh;
	protected int refreshTime;

	protected float alpha;

	protected float maxAlpha;

	protected Color background;
	protected LayerUIPainter painter;

	// allow to have an up to date image of the locked panel
	protected Timer timer;

	public LockUI(final JXLayer<JComponent> comp) {
		super();
		this.background = Color.GRAY;
		this.maxAlpha = 0.7f;
		this.comp = comp;
		this.withRefresh = true;
		setRefreshTime(500);
	}

	@Override
	protected void paintLayer(final Graphics2D g, final JXLayer<JComponent> comp) {
		super.paintLayer(g, this.comp);
		if (!isLocked()) {
			return;
		}

		if (this.painter != null) {
			this.painter.paint(g, comp, this, this.alpha);
		} else {
			final Color bg = getBackground();
			g.setColor(bg);
			g.setComposite(AlphaComposite.SrcOver.derive(getAlpha() * getMaxAlpha()));
			g.fillRect(0, 0, comp.getWidth(), comp.getHeight());
		}
	}

	boolean addAnimRunning = false;

	public synchronized void Lock() {
		if (this.timeline != null) {
			this.timeline.cancel();
		}
		this.timeline = new Timeline(this);
		this.timeline.setDuration(this.animDuration);
		this.timeline.addPropertyToInterpolate("alpha", getAlpha(), 1f);
		this.timeline.addCallback(new TimelineCallbackAdapter() {
			@Override
			public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {
				if (newState == TimelineState.READY) {
					LockUI.this.addAnimRunning = true;
					setLocked(true);
				}
			}
		});
		this.timeline.play();
		this.timer.start();
	}

	public synchronized void unLock() {
		this.timer.stop();
		if (this.timeline != null) {
			this.timeline.cancel();
		}
		this.addAnimRunning = false;
		this.timeline = new Timeline(this);
		this.timeline.setDuration(this.animDuration);
		this.timeline.addPropertyToInterpolate("alpha", getAlpha(), 0f);
		this.timeline.addCallback(new TimelineCallbackAdapter() {
			@Override
			public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {
				if (newState == TimelineState.DONE) {
					if (!LockUI.this.addAnimRunning) {
						ThreadUtils.invokeLater(new Runnable() {
							@Override
							public void run() {
								setLocked(false);
							}
						});
					}
				}
			}
		});
		this.timeline.play();
	}

	public void setAlpha(final float alpha) {
		this.alpha = alpha;
		this.comp.repaint();
		setDirty(true);
	}

	public float getAlpha() {
		return this.alpha;
	}

	public void setRefreshTime(final int refreshTime) {
		this.refreshTime = refreshTime;
		if (refreshTime <= 0 && this.timer != null) {
			this.timer.stop();
		} else {
			if (this.timer == null) {
				this.timer = new Timer(this.refreshTime, new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						setDirty(true);
					}
				});
			} else {
				final boolean running = this.timer.isRunning();
				if (running) {
					this.timer.stop();
				}
				this.timer = new Timer(this.refreshTime, new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						setDirty(true);
					}
				});
				if (running) {
					this.timer.start();
				}
			}
		}
	}

	public boolean isWithRefresh() {
		return this.withRefresh;
	}

	public void setWithRefresh(final boolean withRefresh) {
		this.withRefresh = withRefresh;
		if (!this.withRefresh && this.timer.isRunning()) {
			this.timer.stop();
		}
	}

	public int getRefreshTime() {
		return this.refreshTime;
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
}
