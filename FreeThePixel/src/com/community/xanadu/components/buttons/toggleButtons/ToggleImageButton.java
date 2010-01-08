package com.community.xanadu.components.buttons.toggleButtons;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.ease.Spline;

import com.community.xanadu.utils.PaintUtils;
import com.jhlabs.image.GaussianFilter;

/**
 *Button that behave like a JToggleButton/JRadioButton/JCheckBox<br>
 *when selected it shows the full image + a selection ring(optional)<br>
 *when not selected it shows a small transparent image<br>
 *unselected size= ratio*size<br>
 *unselected alpha= alphaMin<br>
 *prefered size=image size
 * 
 * @author DIelsch
 * 
 */

public class ToggleImageButton extends JToggleButton {
	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				JFrame frame = new JFrame();
				frame.getContentPane().setLayout(new FlowLayout());
				ButtonGroup bg = new ButtonGroup();
				for (int i = 0; i < 4; i++) {
					ToggleImageButton is = new ToggleImageButton();
					bg.add(is);
					frame.getContentPane().add(is);
				}

				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(900, 300);
				frame.setVisible(true);
			}
		});
	}

	private BufferedImage image;
	private float alphaMin;
	private float alpha;

	private float currentRatio;
	private float ratio;

	private Timeline time;

	private boolean withSelectionRing;
	private BufferedImage selectionRingImage;

	private float alphaSelectionRing;

	private boolean oldSelected;

	private int selectionRingThickness;
	private Color selectionRingColor;

	private int blurRadius = 5;

	public ToggleImageButton(final BufferedImage image) {
		this(image, 0.3f, .3f, true);
	}

	public ToggleImageButton(final BufferedImage image, final float alphaMin, final float ratio,
			final boolean withSelectionRing) {
		if (alphaMin > 1 || alphaMin < 0) {
			throw new IllegalArgumentException("alphaMin not in range: [0,1]");
		}
		if (ratio <= 0 || ratio > 1) {
			throw new IllegalArgumentException("ratio not in range: ]0,1]");
		}
		this.image = image;
		this.alphaMin = alphaMin;
		this.currentRatio = ratio;
		this.ratio = ratio;
		this.alpha = alphaMin;
		this.withSelectionRing = withSelectionRing;
		this.oldSelected = false;
		this.selectionRingThickness = 5;
		if (SubstanceLookAndFeel.isCurrentLookAndFeel()) {
			this.selectionRingColor = SubstanceColorSchemeUtilities.getColorScheme(this, ComponentState.ACTIVE)
					.getMidColor();
		} else {
			this.selectionRingColor = Color.CYAN;
		}
		setModel(new JToggleButton.ToggleButtonModel());
		initGUI();
	}

	public ToggleImageButton() {
		this(getEmptyImage());
	}

	private void initGUI() {
		setBorder(new EmptyBorder(0, 0, 0, 0));
		setOpaque(false);
		setPreferredSize(new Dimension(this.image.getWidth() + this.blurRadius + this.selectionRingThickness * 2,
				this.image.getHeight() + this.blurRadius + this.selectionRingThickness * 2));
		addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent e) {
				thisStateChanged();
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				ToggleImageButton.this.selectionRingImage = null;
				repaint();
			}
		});
	}

	private static BufferedImage getEmptyImage() {
		BufferedImage image = GraphicsUtilities.createCompatibleTranslucentImage(100, 100);
		Graphics g = image.createGraphics();
		Random r = new Random();
		g.setColor(Color.BLACK);

		PaintUtils.fillCircle(g, 100, new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)), 0, 0);

		// g.drawLine(0, 0, 100, 100);
		// g.drawLine(100, 0, 0, 100);
		//
		// for (int i = 0; i <= 100; i += 20)
		// {
		// g.drawLine(i, 0, i, 100);
		// }

		g.dispose();
		return image;
	}

	private void thisStateChanged() {

		if (this.oldSelected == isSelected()) {
			return;
		}
		if (this.time != null) {
			this.time.abort();
		}
		this.alphaSelectionRing = 0;

		this.time = new Timeline(this);
		this.time.setDuration(500);
		this.time.setEase(new Spline(0.7f));
		if (isWithSelectionRing()) {
			this.time.addPropertyToInterpolate("alphaSelectionRing", 0f, 1f);
		}
		this.time.addPropertyToInterpolate("currentRatio", getCurrentRatio(), isSelected() ? 1f : this.ratio);
		this.time.addPropertyToInterpolate("alpha", getAlpha(), isSelected() ? 1f : this.alphaMin);
		this.time.play();
		this.oldSelected = isSelected();
	}

	@Override
	protected void paintComponent(final Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		int w = (int) (this.currentRatio * this.image.getWidth());
		int h = (int) (this.currentRatio * this.image.getHeight());
		int x = (getWidth() - w) / 2;
		int y = (getHeight() - h) / 2;

		g2.setComposite(AlphaComposite.SrcOver.derive(getAlpha()));
		g2.drawImage(this.image, x, y, w, h, null);

		if (isSelected() && this.withSelectionRing) {
			g2.setComposite(AlphaComposite.SrcOver.derive(getAlphaSelectionRing()));
			g2.drawImage(getSelectionRing(), 0, 0, null);
		}
		g2.dispose();
	}

	private BufferedImage getSelectionRing() {
		if (this.selectionRingImage == null) {
			this.selectionRingImage = GraphicsUtilities.createCompatibleTranslucentImage(getWidth(), getHeight());
			Graphics2D g = (Graphics2D) this.selectionRingImage.createGraphics();

			g.setColor(this.selectionRingColor);
			g.setStroke(new BasicStroke((float) this.selectionRingThickness));
			g.drawRoundRect(this.blurRadius, this.blurRadius, getWidth() - this.selectionRingThickness
					- this.blurRadius, (int) getHeight() - this.selectionRingThickness - this.blurRadius,
					this.selectionRingThickness * 2, this.selectionRingThickness * 2);
			g.dispose();

			new GaussianFilter(this.blurRadius).filter(this.selectionRingImage, this.selectionRingImage);
		}

		return this.selectionRingImage;
	}

	public float getCurrentRatio() {
		return this.currentRatio;
	}

	public void setCurrentRatio(final float currentRatio) {
		this.currentRatio = currentRatio;
		repaint();
	}

	public float getAlpha() {
		return this.alpha;
	}

	public void setAlpha(final float alpha) {
		this.alpha = alpha;
		repaint();
	}

	@Override
	public boolean contains(final int x, final int y) {
		int w = (int) (this.currentRatio * this.image.getWidth());
		int h = (int) (this.currentRatio * this.image.getHeight());
		int x1 = (getWidth() - w) / 2;
		int y1 = (getHeight() - h) / 2;

		return new Rectangle(x1, y1, w, h).contains(x, y);
	}

	public boolean isWithSelectionRing() {
		return this.withSelectionRing;
	}

	public void setWithSelectionRing(final boolean withSelectionRing) {
		this.withSelectionRing = withSelectionRing;
	}

	public float getAlphaSelectionRing() {
		return this.alphaSelectionRing;
	}

	public void setAlphaSelectionRing(final float alphaSelectionRing) {
		this.alphaSelectionRing = alphaSelectionRing;
	}

	public void setSelectionRingThickness(final int selectionRingThickness) {
		this.selectionRingThickness = selectionRingThickness;
		this.selectionRingImage = null;
		repaint();
	}

	public void setSelectionRingColor(final Color selectionRingColor) {
		this.selectionRingColor = selectionRingColor;
		this.selectionRingImage = null;
		repaint();
	}

	public Color getSelectionRingColor() {
		return this.selectionRingColor;
	}

	public int getSelectionRingThickness() {
		return this.selectionRingThickness;
	}
}
