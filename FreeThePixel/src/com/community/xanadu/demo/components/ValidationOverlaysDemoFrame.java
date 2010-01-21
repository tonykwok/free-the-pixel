package com.community.xanadu.demo.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.jxlayer.JXLayer;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel;

import com.community.xanadu.components.validation.TextValidationOverlayFactory;
import com.community.xanadu.components.validation.ValidationOverlay;
import com.community.xanadu.components.validation.ValidationOverlayFactory;
import com.community.xanadu.components.validation.ValidationPaint;
import com.community.xanadu.components.validation.Validator;
import com.community.xanadu.utils.PaintUtils;

public class ValidationOverlaysDemoFrame extends javax.swing.JFrame {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(new SubstanceGraphiteAquaLookAndFeel());
					JFrame.setDefaultLookAndFeelDecorated(true);
				} catch (final UnsupportedLookAndFeelException e) {
				}
				final ValidationOverlaysDemoFrame inst = new ValidationOverlaysDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public ValidationOverlaysDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {

			getContentPane().setLayout(new MigLayout("wrap 1,fill"));

			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			final ArrayList<Validator<JTextComponent>> checkers = new ArrayList<Validator<JTextComponent>>();
			checkers.add(new Validator<JTextComponent>() {

				@Override
				public String validate(final JTextComponent text) {
					if (!text.getText().startsWith("A")) {
						return "Must begin with an A";
					}
					return null;
				}
			});

			checkers.add(new Validator<JTextComponent>() {

				@Override
				public String validate(final JTextComponent text) {
					if (text.getText().length() < 5) {
						return "Must have at least 5 characters";
					} else {
						System.out.println(">5");
					}
					return null;
				}
			});

			JTextComponent t = new JTextField();
			getContentPane().add(TextValidationOverlayFactory.createBlinking(t, checkers), "growx");

			t = new JTextField();
			getContentPane().add(TextValidationOverlayFactory.createErrorOverlayedIcon(t, checkers), "growx");

			final JTextArea ta = new JTextArea();
			final ValidationOverlay vo = TextValidationOverlayFactory.createBlinkingAndIconComponent(ta, checkers);
			final ValidationPaint paint = new ValidationPaint() {
				@Override
				public void paint(final Graphics2D g, final List<String> msg, final JXLayer<JComponent> layer,
						final float animProgress) {
					g.setColor(Color.ORANGE);
					g.setComposite(AlphaComposite.SrcOver.derive(0.3f));
					g.fillRect(0, 0, layer.getWidth(), layer.getHeight());

					g.setComposite(AlphaComposite.SrcOver.derive(0.7f));
					final StringBuilder sb = new StringBuilder();
					if (!msg.isEmpty()) {
						for (final String s : msg) {
							sb.append(s).append("\n");
						}
						PaintUtils.drawMultiLineHighLightText(g, ta, sb.toString(), Color.red, Color.BLACK);
					}
				}
			};
			vo.setPaint(paint);

			getContentPane().add(vo, "grow");

			getContentPane().add(new JLabel("Must begin with an \"A\" and size >= 5 "), "");
			getContentPane().add(new JSeparator(JSeparator.HORIZONTAL), "grow");

			final Validator<JSpinner> c = new Validator<JSpinner>() {
				@Override
				public String validate(final JSpinner comp) {
					if ((Integer) comp.getValue() == 0) {
						return "cannot be equal to 0";
					}
					return null;
				}
			};

			final JSpinner spinner = new JSpinner();
			spinner.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(final ChangeEvent e) {
					ValidationOverlayFactory.validate(spinner);
				}
			});
			getContentPane().add(ValidationOverlayFactory.createBlinkingAndIcon(spinner, c), "growx");

			final JSpinner spinner2 = new JSpinner();
			spinner2.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(final ChangeEvent e) {
					ValidationOverlayFactory.validate(spinner2);
				}
			});
			getContentPane().add(ValidationOverlayFactory.createErrorOverlayedIcon(spinner2, c), "growx");

			getContentPane().add(new JLabel("Must be !=0"), "newline");

			setSize(300, 350);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
