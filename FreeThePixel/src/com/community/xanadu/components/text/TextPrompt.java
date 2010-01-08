package com.community.xanadu.components.text;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXPanel;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;

import com.community.xanadu.demo.components.TextPromptDemo;

//http://tips4java.wordpress.com/2009/11/29/text-prompt/
/**
 * The TextPrompt class will display a prompt over top of a text component when
 * the Document of the text field is empty. The Show property is used to
 * determine the visibility of the prompt.
 * 
 * The Font and foreground Color of the prompt will default to those properties
 * of the parent text component. You are free to change the properties after
 * class construction.
 */

public class TextPrompt extends JLabel implements FocusListener, DocumentListener {
	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		TextPromptDemo.main(args);
	}

	public enum Show {
		ALWAYS, FOCUS_GAINED, FOCUS_LOST;
	}

	private final JTextComponent component;
	private final Document document;

	private Show show;
	private boolean showPromptOnce;
	private int focusLost;

	private float alpha = 1f;
	Timeline timeline;
	private float animProgress = 1f;

	private final JXPanel panel;

	public TextPrompt(final String text, final JTextComponent component) {
		this(text, component, Show.ALWAYS);
	}

	/**
	 * @param text
	 * @param component
	 * @param show
	 */
	/**
	 * @param text
	 * @param component
	 * @param show
	 */
	public TextPrompt(final String text, final JTextComponent component, final Show show) {
		this.component = component;
		setShow(show);
		this.document = component.getDocument();

		setText(text);
		setFont(component.getFont());
		setForeground(component.getForeground());
		// setBorder(new EmptyBorder(component.getInsets()));
		setHorizontalAlignment(JLabel.LEADING);

		component.addFocusListener(this);
		this.document.addDocumentListener(this);

		component.setLayout(new BorderLayout());

		this.panel = new JXPanel(new MigLayout("inset 0 0 0 0"));
		this.panel.add(this, "");
		component.add(this.panel);
		this.panel.setFocusable(false);
		this.panel.setOpaque(false);

		checkForPrompt();
	}

	/**
	 * Convenience method to change the alpha value of the current foreground
	 * Color to the specifice value.
	 * 
	 * @param alpha
	 *            value in the range of 0 - 1.0.
	 */
	public void changeAlpha(final float alpha) {
		this.alpha = alpha;
		changeColorWithAlpha();
	}

	private void changeColorWithAlpha() {
		int alphaInt = ((int) (this.alpha * this.animProgress * 255));

		alphaInt = alphaInt > 255 ? 255 : alphaInt < 0 ? 0 : alphaInt;

		final Color foreground = getForeground();
		final int red = foreground.getRed();
		final int green = foreground.getGreen();
		final int blue = foreground.getBlue();

		final Color withAlpha = new Color(red, green, blue, alphaInt);
		super.setForeground(withAlpha);
	}

	/**
	 * Convenience method to change the style of the current Font. The style
	 * values are found in the Font class. Common values might be: Font.BOLD,
	 * Font.ITALIC and Font.BOLD + Font.ITALIC.
	 * 
	 * @param style
	 *            value representing the the new style of the Font.
	 */
	public void changeStyle(final int style) {
		setFont(getFont().deriveFont(style));
	}

	/**
	 * Get the Show property
	 * 
	 * @return the Show property.
	 */
	public Show getShow() {
		return this.show;
	}

	/**
	 * Set the prompt Show property to control when the promt is shown. Valid
	 * values are:
	 * 
	 * Show.AWLAYS (default) - always show the prompt Show.Focus_GAINED - show
	 * the prompt when the component gains focus (and hide the prompt when focus
	 * is lost) Show.Focus_LOST - show the prompt when the component loses focus
	 * (and hide the prompt when focus is gained)
	 * 
	 * @param show
	 *            a valid Show enum
	 */
	public void setShow(final Show show) {
		this.show = show;
	}

	/**
	 * Get the showPromptOnce property
	 * 
	 * @return the showPromptOnce property.
	 */
	public boolean getShowPromptOnce() {
		return this.showPromptOnce;
	}

	/**
	 * Show the prompt once. Once the component has gained/lost focus once, the
	 * prompt will not be shown again.
	 * 
	 * @param showPromptOnce
	 *            when true the prompt will only be shown once, otherwise it
	 *            will be shown repeatedly.
	 */
	public void setShowPromptOnce(final boolean showPromptOnce) {
		this.showPromptOnce = showPromptOnce;
	}

	/**
	 * Check whether the prompt should be visible or not. The visibility will
	 * change on updates to the Document and on focus changes.
	 */
	private void checkForPrompt() {
		// Text has been entered, remove the prompt

		if (this.document.getLength() > 0) {
			hidePrompt();
			return;
		}

		// Prompt has already been shown once, remove it

		if (this.showPromptOnce && this.focusLost > 0) {
			hidePrompt();
			return;
		}

		// Check the Show property and component focus to determine if the
		// prompt should be displayed.

		if (this.component.hasFocus()) {
			if (this.show == Show.ALWAYS || this.show == Show.FOCUS_GAINED) {
				showPrompt();
			} else {
				hidePrompt();
			}
		} else {
			if (this.show == Show.ALWAYS || this.show == Show.FOCUS_LOST) {
				showPrompt();
			} else {
				hidePrompt();
			}
		}
	}

	private Timeline createTimeline() {
		if (this.timeline != null) {
			this.timeline.cancel();
		}
		this.timeline = new Timeline(this);
		this.timeline.setDuration(200);
		return this.timeline;
	}

	public void setAnimProgress(final float animProgress) {
		this.animProgress = animProgress;
		// changeColorWithAlpha();
		this.panel.setAlpha(animProgress);
	}

	public float getAnimProgress() {
		return this.animProgress;
	}

	private void hidePrompt() {
		createTimeline();
		this.timeline.addPropertyToInterpolate("animProgress", getAnimProgress(), 0f);
		this.timeline.addCallback(new TimelineCallbackAdapter() {
			@Override
			public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {
				if (newState == TimelineState.DONE) {
					setVisible(false);
					TextPrompt.this.panel.setVisible(false);
				}
			}
		});
		this.timeline.play();
	}

	private void showPrompt() {
		createTimeline();
		setVisible(true);
		this.panel.setVisible(true);
		this.timeline.addPropertyToInterpolate("animProgress", getAnimProgress(), 1f);
		this.timeline.play();
	}

	// Implement FocusListener

	public void focusGained(final FocusEvent e) {
		checkForPrompt();
	}

	public void focusLost(final FocusEvent e) {
		this.focusLost++;
		checkForPrompt();
	}

	// Implement DocumentListener

	public void insertUpdate(final DocumentEvent e) {
		checkForPrompt();
	}

	public void removeUpdate(final DocumentEvent e) {
		checkForPrompt();
	}

	public void changedUpdate(final DocumentEvent e) {
	}
}
