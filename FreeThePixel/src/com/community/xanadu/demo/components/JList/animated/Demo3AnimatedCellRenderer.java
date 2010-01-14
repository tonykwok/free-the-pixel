package com.community.xanadu.demo.components.JList.animated;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXPanel;
import org.pushingpixels.substance.internal.utils.SubstanceColorUtilities;

import com.community.xanadu.components.JList.DynamicListCellRenderer;
import com.community.xanadu.components.JList.DynamicSizeJList;

public class Demo3AnimatedCellRenderer extends DynamicListCellRenderer<MessageItem> {

	private static final int CELL_UNSELECTED_HEIGHT = 40;
	private static final int CELL_SELECTED_HEIGHT = 160;

	private static final Color SELECTED_FOREGROUND = Color.BLACK;
	private static final Color NOT_SELECTED_FOREGROUND = Color.WHITE;

	private static final Color SELECTED_BACKGROUND = Color.RED;
	private static final Color NOT_SELECTED_BACKGROUND = Color.RED.darker();
	// store the animprogress for all the items

	private JLabel labelTitle;
	private JPanel mainPanel;
	private JXPanel alphaPanel;
	private JLabel labelInfo;
	private final Dimension smallDim;
	private JLabel labelCanOpen;
	private JLabel helpLabel;
	private boolean hasFocus;

	public Demo3AnimatedCellRenderer() {
		this.smallDim = new Dimension(50, CELL_UNSELECTED_HEIGHT);
		getMainPanel();
	}

	private JLabel getLabelCanOpen() {
		if (this.labelCanOpen == null) {
			this.labelCanOpen = new JLabel(">>");
			this.labelCanOpen.setForeground(NOT_SELECTED_FOREGROUND);
		}
		return this.labelCanOpen;
	}

	private JLabel getLabelInfo() {
		if (this.labelInfo == null) {
			this.labelInfo = new JLabel();
		}
		return this.labelInfo;
	}

	private JLabel getLabelTitle() {
		if (this.labelTitle == null) {
			this.labelTitle = new JLabel();
		}
		return this.labelTitle;
	}

	private JPanel getMainPanel() {
		if (this.mainPanel == null) {
			this.mainPanel = new JPanel(new MigLayout("fill")) {
				@Override
				protected void paintComponent(final Graphics g) {
					super.paintComponent(g);

					final Graphics2D g2 = (Graphics2D) g;

					final Point start = new Point(0, 0);
					final Point end = new Point(getWidth(), getHeight());
					final Color[] color = new Color[] { new Color(0, 0, 0, 100), new Color(0, 0, 0, 0) };
					final float[] fraction = new float[] { 0f, 1f };

					final LinearGradientPaint lgp = new LinearGradientPaint(start, end, fraction, color);

					g2.setPaint(lgp);
					g2.fillRect(0, 0, getWidth(), getHeight());

					if (Demo3AnimatedCellRenderer.this.hasFocus) {
						g2.fillRect(0, 0, getWidth(), getHeight());
					}
					g2.setColor(new Color(0, 0, 0, 155));
					g2.drawRect(0, 0, getWidth(), getHeight());
				}
			};
			this.mainPanel.add(getLabelTitle(), "");
			this.mainPanel.add(getLabelCanOpen(), "wrap");
			this.mainPanel.add(this.getAlphaPanel(), "grow");
			this.mainPanel.setOpaque(true);
		}
		return this.mainPanel;
	}

	private JLabel getHelpLabel() {
		if (this.helpLabel == null) {
			this.helpLabel = new JLabel();
		}
		return this.helpLabel;
	}

	private JXPanel getAlphaPanel() {
		if (this.alphaPanel == null) {
			this.alphaPanel = new JXPanel(new MigLayout("fill"));
			this.alphaPanel.add(getLabelInfo(), "gap bottom 25");
			this.alphaPanel.add(getHelpLabel(), "east");
			this.alphaPanel.setOpaque(false);
		}
		return this.alphaPanel;
	}

	private void prepareForAnim(final Float progress, final MessageItem item) {
		final Color foreground = SubstanceColorUtilities.getInterpolatedColor(SELECTED_FOREGROUND,
				NOT_SELECTED_FOREGROUND, progress);
		this.labelTitle.setForeground(foreground);
		this.labelInfo.setForeground(foreground);
		this.mainPanel.setBackground(SubstanceColorUtilities.getInterpolatedColor(SELECTED_BACKGROUND,
				NOT_SELECTED_BACKGROUND, progress));
		this.labelInfo.setText(item.getText());
		this.alphaPanel.setAlpha(progress);
	}

	@Override
	public Component getListCellRendererComponent(final DynamicSizeJList<MessageItem> list, final MessageItem value,
			final int index, final int selectingIndex, final int deselectingIndex, final float progress,
			final boolean cellHasFocus) {

		this.labelTitle.setText(value.getTitle());
		this.hasFocus = cellHasFocus&&index!=selectingIndex;

		if (index == selectingIndex && selectingIndex != -1) {
			// Selected item
			prepareForAnim(progress, value);
			this.mainPanel.setPreferredSize(new Dimension(50, (int) ((CELL_SELECTED_HEIGHT) * progress)));
			this.labelCanOpen.setVisible(false);
			this.labelInfo.setVisible(true);
		} else if (index == deselectingIndex && deselectingIndex != -1) {
			// deselecting
			prepareForAnim(progress, value);
			this.mainPanel.setPreferredSize(new Dimension(50,
					(int) (CELL_SELECTED_HEIGHT - (CELL_SELECTED_HEIGHT - CELL_UNSELECTED_HEIGHT) * (1 - progress))));
			this.labelCanOpen.setVisible(true);
			this.labelInfo.setVisible(true);
		} else {
			this.labelTitle.setForeground(NOT_SELECTED_FOREGROUND);
			this.labelTitle.setBackground(NOT_SELECTED_BACKGROUND);
			this.mainPanel.setPreferredSize(this.smallDim);
			getHelpLabel().setVisible(false);
			this.alphaPanel.setAlpha(1f);
			this.mainPanel.setBackground(NOT_SELECTED_BACKGROUND);
			this.labelInfo.setForeground(this.labelTitle.getForeground());
			this.labelCanOpen.setVisible(true);
			this.labelInfo.setVisible(false);
		}
		return this.mainPanel;
	}
}
