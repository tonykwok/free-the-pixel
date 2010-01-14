package com.community.xanadu.components.validation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.jdesktop.jxlayer.JXLayer;

public class ValidationOverlayFactory {
	protected static final String CHECK_NEEDED = "com.community.xanadu.components.validation.CHECK_NEEDED";

	// ------------------------------------
	public static <T extends JComponent> ValidationOverlay createBlinkingAndIcon(final T comp,
			final List<Validator<T>> validators) {
		return createErrorOverlayedIcon(validators, comp, Color.RED, true, true);
	}

	public static <T extends JComponent> ValidationOverlay createBlinkingAndIcon(final T comp,
			final Validator<T> validator) {
		final ArrayList<Validator<T>> validators = new ArrayList<Validator<T>>();
		validators.add(validator);
		return createBlinkingAndIcon(comp, validators);
	}

	// ------------------------------------------
	public static <T extends JComponent> ValidationOverlay createBlinking(final T comp,
			final List<Validator<T>> validators) {

		return internalCreateBlinking(comp, validators);
	}

	public static <T extends JComponent> ValidationOverlay createBlinking(final T comp, final Validator<T> validator) {
		final ArrayList<Validator<T>> validators = new ArrayList<Validator<T>>();
		validators.add(validator);
		return internalCreateBlinking(comp, validators);
	}

	private static <T extends JComponent> ValidationOverlay internalCreateBlinking(final T comp,
			final List<Validator<T>> validators) {
		return createErrorOverlayedIcon(validators, comp, Color.red, true, false);
	}

	// -------------------------------------
	public static <T extends JComponent> ValidationOverlay createErrorOverlayedIcon(final T comp,
			final Validator<T> validator) {
		final ArrayList<Validator<T>> validators = new ArrayList<Validator<T>>();
		validators.add(validator);
		return createErrorOverlayedIcon(validators, comp, null, false, true);
	}

	public static <T extends JComponent> ValidationOverlay createErrorOverlayedIcon(final T comp,
			final List<Validator<T>> validators) {
		return createErrorOverlayedIcon(validators, comp, null, false, true);
	}

	private static <T extends JComponent> ValidationOverlay createErrorOverlayedIcon(final List<Validator<T>> validators,
			final T originalTrackedComp, final Color color, final boolean animatedPaint, final boolean withIcon) {

		final ValidationPanelIcon icon = new ValidationPanelIcon(withIcon);

		final JXLayer<JComponent> jxlayer = new JXLayer<JComponent>(originalTrackedComp);
		final ValidationLayer<T> layer = new ValidationLayer<T>(originalTrackedComp, validators, icon);
		if (color != null) {
			layer.setPaint(ValidationPaint.getColorValidationPaint(color));
		}
		jxlayer.setUI(layer);
		jxlayer.setOpaque(false);

		final ValidationOverlay over = new ValidationOverlay(jxlayer, (ValidationLayer<JComponent>) layer, icon,
				ValidationPanelIcon.DEFAULT_ICON_LOCATION, ValidationPanelIcon.DEFAULT_XOFFSET,
				ValidationPanelIcon.DEFAULT_YOFFSET);
		over.setOpaque(false);
		layer.check();

		return over;
	}
//---------------------------
	public static void check(final JComponent comp) {
		comp.firePropertyChange(ValidationOverlayFactory.CHECK_NEEDED, true, false);
	}
}
