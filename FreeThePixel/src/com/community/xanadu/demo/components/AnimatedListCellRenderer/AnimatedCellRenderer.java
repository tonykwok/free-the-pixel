package com.community.xanadu.demo.components.AnimatedListCellRenderer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceColorUtilities;

import com.community.xanadu.utils.PaintUtils;
import com.community.xanadu.utils.TextUtils;

public class AnimatedCellRenderer extends JPanel implements ListCellRenderer {
	private static final long serialVersionUID = 1937896638257639825L;
	private DateFormat formatter;
	private String text;

	private Font smallFont = new Font("arial", Font.PLAIN, 11);
	private Font bigFont = new Font("arial", Font.BOLD, 15);
	private Color color;

	private final Color lightColor = Color.ORANGE;
	private final Color darkColor = Color.ORANGE.darker();

	// store the animprogress for all elements
	private Map<Integer, Float> mapAnimWarning = new HashMap<Integer, Float>();

	public AnimatedCellRenderer() {
		this.setOpaque(false);
		this.setPreferredSize(new Dimension(100, 100));
		this.formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());
	}

	private int index;

	@Override
	public Component getListCellRendererComponent(final JList list, final Object value, final int index,
			final boolean isSelected, final boolean cellHasFocus) {
		this.index = index;
		if (value instanceof Message) {
			Message w = (Message) value;
			StringBuilder s = new StringBuilder();

			s.append(this.formatter.format(w.getTime())).append("\n");
			s.append(w.getCode()).append("\n");
			s.append(w.getMessage()).append("\n ");
			this.text = s.toString();

			// get a dummy graphics to compute the text size
			Graphics g = GraphicsUtilities.createCompatibleImage(1, 1).getGraphics();
			// if the last item
			if (index == list.getModel().getSize() - 1) {
				this.color = (this.lightColor);
				this.setFont(this.bigFont);
				g.setFont(this.bigFont);
				Rectangle r = TextUtils.getMultiLineStringBounds(this.text, g);
				setPreferredSize(new Dimension(r.width, r.height));
			} else {
				this.color = this.darkColor;
				// the color of the one before last get darker over time when a
				// new warning is added
				// and the font get smaller
				if (list.getModel().getSize() - 2 == index) {
					if (this.mapAnimWarning.get(index + 1) != null) {
						float animProgress = this.mapAnimWarning.get(index + 1);
						if (animProgress != 0) {
							this.color = (SubstanceColorUtilities.getInterpolatedColor(this.darkColor, this.lightColor,
									animProgress));
							setFont(this.smallFont.deriveFont((float) -4 * animProgress + 15));
						} else {
							setFont(this.smallFont);
						}
					} else {
						setFont(this.smallFont);
					}
				} else {
					setFont(this.smallFont);
				}

				g.setFont(this.smallFont);
				Rectangle r = TextUtils.getMultiLineStringBounds(this.text, g);
				setPreferredSize(new Dimension(r.width, r.height));
			}
			g.dispose();
		} else {
			this.text = "";
		}
		return this;
	}

	@Override
	protected void paintComponent(final Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();

		float animProgress = this.mapAnimWarning.get(this.index);

		g2.setComposite(AlphaComposite.SrcOver.derive(animProgress));

		Area oldClip = new Area(g2.getClip());

		Area newCLip = new Area(new Rectangle2D.Float(

		(this.getWidth() / 2 * (1 - animProgress)) - 1,

		(this.getHeight() / 2 * (1 - animProgress)) - 1,

		(this.getWidth() * animProgress) + 1,

		(this.getHeight() * animProgress) + 1));

		g2.setColor(this.color);

		if (animProgress != 1) {
			g2.setClip(newCLip);
		}

		g2.fill(newCLip);
		PaintUtils.drawMultiLineText(g2, AnimatedCellRenderer.this.text, this, Color.BLACK);

		GradientPaint gp = new GradientPaint(getWidth() / 2, 0, new Color(0, 0, 0, 50), getWidth() / 2, getHeight(),
				new Color(255, 255, 255, 0));
		g2.setPaint(gp);
		g2.fill(g2.getClip());

		g2.setClip(oldClip);
		if (animProgress == 1) {
			g2.setColor(Color.BLACK);
			g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
		}

		g2.dispose();
	}

	public void setAnimProgress(final int index, final float progress) {
		this.mapAnimWarning.put(index, progress);
	}

	public static class Message {
		private Timestamp time;
		private String code;
		private String message;
		private boolean removeable;

		public Timestamp getTime() {
			return this.time;
		}

		public void setTime(final Timestamp time) {
			this.time = time;
		}

		public String getCode() {
			return this.code;
		}

		public void setCode(final String code) {
			this.code = code;
		}

		public String getMessage() {
			return this.message;
		}

		public void setMessage(final String message) {
			this.message = message;
		}

		public Message() {
			this.code = "";
			this.message = "";
			this.removeable = false;
			this.time = new Timestamp(System.currentTimeMillis());
		}

		public Message(final String code, final String message, final Timestamp time) {
			super();
			this.code = code;
			this.message = message;
			this.time = time;
		}

		public Message(final String code, final String message, final boolean removeable) {
			super();
			this.code = code;
			this.message = message;
			this.removeable = removeable;
			this.time = new Timestamp(System.currentTimeMillis());
		}

		public Message(final String code, final String message) {
			this(code, message, true);
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof Message) {
				return ((Message) o).code.equals(this.code) && ((Message) o).message.equals(this.message);

			} else {
				return false;
			}
		}

		public boolean isRemoveable() {
			return this.removeable;
		}

		public void setRemoveable(final boolean removeable) {
			this.removeable = removeable;
		}

	}

}
