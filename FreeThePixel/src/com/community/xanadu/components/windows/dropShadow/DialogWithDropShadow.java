package com.community.xanadu.components.windows.dropShadow;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceTitleButton;
import org.pushingpixels.substance.internal.utils.icon.SubstanceIconFactory;
import org.pushingpixels.substance.internal.utils.icon.TransitionAwareIcon;

import com.community.xanadu.components.windows.WindowFadeInManager;
import com.community.xanadu.demo.components.DialogWithDropShadowDemo;
import com.community.xanadu.listeners.Draggable;
import com.community.xanadu.utils.PaintUtils;
import com.community.xanadu.utils.WindowsUtils;

@SuppressWarnings("serial")
public class DialogWithDropShadow extends JDialog {
	public static void main(final String[] args) {
		DialogWithDropShadowDemo.main(args);
	}

	private JComponent titleLabel;
	private JButton buttonClose;
	private DropShadowContentPane contentPane;
	private boolean withCloseButton;

	public DialogWithDropShadow(final Window frame) {
		this(frame, true, true);
	}

	public DialogWithDropShadow(final Window frame, final boolean draggable, final boolean withCloseButton) {
		super(frame);
		setModal(true);
		this.withCloseButton = withCloseButton;
		initGUI();
		if (draggable) {
			Draggable.makeDraggable(this);
			Draggable.makeDraggable(getTitleLabel(), this);
			Draggable.makeDraggable(getContentPane(), this);
		}

		addPropertyChangeListener("title", new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				titleBarStateChanged();
			}
		});
	}

	@Override
	public void setResizable(final boolean resizable) {
		super.setResizable(resizable);

		((DropShadowContentPane) getContentPane()).setResizable(resizable);
	}

	private void initGUI() {
		setUndecorated(true);
		WindowsUtils.setOpaque(this, false);
		setContentPane(getContentPane());
		setSize(800, 500);

		getLayeredPane().add(getButtonClose());
		getLayeredPane().add(getTitleLabel());

		titleBarStateChanged();
	}

	public JComponent getTitleLabel() {

		if (this.titleLabel == null) {

			if (SubstanceLookAndFeel.isCurrentLookAndFeel()) {
				this.titleLabel = new TitleLabel(this);
			} else {
				this.titleLabel = new JLabel(getTitle());
			}
			this.titleLabel.setBounds(8, 7, getWidth() - 35, 25);
		}
		return this.titleLabel;
	}

	private void titleBarStateChanged() {

		if (getTitle() != null && getTitle().length() > 0 || this.withCloseButton) {
			this.contentPane.setBorder(new LineBorder(new Color(0, 0, 0, 0), 25) {
				@Override
				public Insets getBorderInsets(final Component c) {
					return new Insets(this.thickness, 5, 5, 5);
				}
			});
		} else {
			this.contentPane.setBorder(new LineBorder(new Color(0, 0, 0, 0), 5));
		}

		new LineBorder(Color.red);

		if (this.titleLabel instanceof JLabel) {
			((JLabel) this.titleLabel).setText(getTitle());
		}
		this.titleLabel.repaint();
		getButtonClose().setVisible(this.withCloseButton);
	}

	@Override
	public Container getContentPane() {
		if (this.contentPane == null) {
			this.contentPane = new DropShadowContentPane();
			this.contentPane.setOpaque(false);
			this.contentPane.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(final ComponentEvent e) {
					getButtonClose().setBounds(getWidth() - 28, 5, 20, 20);
				}
			});
		}
		return this.contentPane;
	}

	public void startShowAnim() {
		WindowFadeInManager.fadeIn(this);
	}

	public void startShowAnim(final int duration) {
		WindowFadeInManager.fadeIn(this, duration);
	}

	public void startHideAnim() {
		WindowFadeInManager.fadeOut(this);
	}

	public void startHideAnim(final int duration) {
		WindowFadeInManager.fadeOut(this, duration);
	}

	public void setWithCloseButton(final boolean withCloseButton) {
		this.withCloseButton = withCloseButton;
		titleBarStateChanged();
	}

	public void setContentPaneBackground(final Paint contentPaneBackground) {
		this.contentPane.setPaintBackground(contentPaneBackground);
	}

	private JButton getButtonClose() {
		if (this.buttonClose == null) {

			if (SubstanceLookAndFeel.isCurrentLookAndFeel()) {
				this.buttonClose = new SubstanceTitleButton();
				this.buttonClose.putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.TRUE);
				SubstanceLookAndFeel.setDecorationType(this.buttonClose, DecorationAreaType.GENERAL);
			} else {
				this.buttonClose = new JButton();
				this.buttonClose.setContentAreaFilled(false);
			}
			this.buttonClose.setIcon(getIconClose());
			this.buttonClose.setFocusPainted(false);
			this.buttonClose.setFocusable(false);
			this.buttonClose.setOpaque(true);

			this.buttonClose.setText(null);
			this.buttonClose.setBorder(null);

			this.buttonClose.setFocusable(false);

			this.buttonClose.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonCloseActionPerformed();
				}
			});
		}
		return this.buttonClose;
	}

	protected void buttonCloseActionPerformed() {
		if (getDefaultCloseOperation() != JDialog.DO_NOTHING_ON_CLOSE) {
			startHideAnim();
		}
	}

	private Icon getIconClose() {
		if (SubstanceLookAndFeel.isCurrentLookAndFeel()) {
			return new TransitionAwareIcon(this.buttonClose, new TransitionAwareIcon.Delegate() {
				public Icon getColorSchemeIcon(final SubstanceColorScheme scheme) {
					return SubstanceIconFactory.getTitlePaneIcon(SubstanceIconFactory.IconKind.CLOSE, scheme,
							SubstanceCoreUtilities.getSkin(DialogWithDropShadow.this.rootPane)
									.getBackgroundColorScheme(DecorationAreaType.GENERAL));
				}
			}, "substance.titlePane.closeIcon");
		}
		return new Icon() {

			@Override
			public void paintIcon(final Component c, final Graphics g, final int x, final int y) {

				final Graphics2D g2 = (Graphics2D) g;
				PaintUtils.turnOnAntialias(g2);
				final int size = 20;
				final int start = (size / 4);
				final int end = (3 * size / 4);

				final Stroke stroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

				g2.setStroke(stroke);
				g2.setColor(Color.BLACK);
				g2.drawLine(start, start, end, end);
				g2.drawLine(start, end, end, start);
			}

			@Override
			public int getIconWidth() {
				return 20;
			}

			@Override
			public int getIconHeight() {
				return 20;
			}
		};
	}
}
