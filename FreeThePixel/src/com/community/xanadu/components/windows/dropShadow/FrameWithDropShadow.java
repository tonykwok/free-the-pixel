package com.community.xanadu.components.windows.dropShadow;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceTitleButton;
import org.pushingpixels.substance.internal.utils.icon.SubstanceIconFactory;
import org.pushingpixels.substance.internal.utils.icon.TransitionAwareIcon;

import com.community.xanadu.components.windows.WindowFadeInManager;
import com.community.xanadu.demo.components.FrameWithDropShadowDemo;
import com.community.xanadu.listeners.Draggable;
import com.community.xanadu.utils.PaintUtils;
import com.community.xanadu.utils.WindowsUtils;

@SuppressWarnings("serial")
public class FrameWithDropShadow extends JFrame {
	public static void main(final String[] args) {
		FrameWithDropShadowDemo.main(args);
	}

	private JComponent titleLabel;
	private JButton buttonClose;
	private JButton buttonMini;
	private JButton buttonMax;
	private JButton buttonRestore;
	private JPanel panelButton;

	private DropShadowContentPane contentPane;

	public FrameWithDropShadow() {
		this(true, true);
	}

	public FrameWithDropShadow(final boolean draggable, final boolean withCloseButton) {
		super();
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
		getButtonMax().setVisible(resizable);
		getButtonRestore().setVisible(resizable);

		setPanelButtonBounds();

	}

	private void initGUI() {
		setUndecorated(true);
		WindowsUtils.setOpaque(this, false);
		setContentPane(getContentPane());
		setSize(800, 500);

		getLayeredPane().add(getPanelButton());
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
		if (this.titleLabel instanceof JLabel) {
			((JLabel) this.titleLabel).setText(getTitle());
		}
		this.titleLabel.repaint();
	}

	@Override
	public Container getContentPane() {
		if (this.contentPane == null) {
			this.contentPane = new DropShadowContentPane();
			this.contentPane.setOpaque(false);

			this.contentPane.setBorder(new LineBorder(new Color(0, 0, 0, 0), 25) {
				@Override
				public Insets getBorderInsets(final Component c) {
					return new Insets(this.thickness, 5, 5, 5);
				}
			});

			this.contentPane.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(final ComponentEvent e) {
					setPanelButtonBounds();
				}
			});
		}
		return this.contentPane;
	}

	private void setPanelButtonBounds() {
		if (isResizable()) {
			getPanelButton().setBounds(getWidth() - 75, 5, 65, 20);
		} else {
			getPanelButton().setBounds(getWidth() - 55, 5, 45, 20);
		}
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

	public void setContentPaneBackground(final Paint contentPaneBackground) {
		this.contentPane.setPaintBackground(contentPaneBackground);
	}

	private JButton getButtonClose() {
		if (this.buttonClose == null) {

			this.buttonClose = createTitleButton();
			this.buttonClose.setIcon(getIconClose());
			if (SubstanceLookAndFeel.isCurrentLookAndFeel()) {
				// //TODO WHAT s this?
				// this.buttonClose.putClientProperty(SubstanceButtonUI
				// .IS_TITLE_CLOSE_BUTTON, Boolean.TRUE);
			}

			this.buttonClose.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonCloseActionPerformed();
				}
			});
		}
		return this.buttonClose;
	}

	private JButton getButtonMini() {
		if (this.buttonMini == null) {

			this.buttonMini = createTitleButton();
			this.buttonMini.setIcon(getIconMini());

			this.buttonMini.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonMiniActionPerformed();
				}
			});
		}
		return this.buttonMini;
	}

	private JButton getButtonMax() {
		if (this.buttonMax == null) {

			this.buttonMax = createTitleButton();
			this.buttonMax.setIcon(getIconMax());

			this.buttonMax.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonMaxActionPerformed();
				}
			});
		}
		return this.buttonMax;
	}

	private JButton getButtonRestore() {
		if (this.buttonRestore == null) {
			this.buttonRestore = createTitleButton();
			this.buttonRestore.setIcon(getIconRestore());

			this.buttonRestore.setVisible(false);

			this.buttonRestore.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonRestoreActionPerformed();
				}
			});
		}
		return this.buttonRestore;
	}

	private JButton createTitleButton() {
		JButton res;

		if (SubstanceLookAndFeel.isCurrentLookAndFeel()) {
			res = new SubstanceTitleButton();
			res.putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.TRUE);
			SubstanceLookAndFeel.setDecorationType(res, DecorationAreaType.GENERAL);
		} else {
			res = new JButton();
			res.setContentAreaFilled(false);
		}

		res.setFocusPainted(false);
		res.setFocusable(false);
		res.setOpaque(true);
		res.setText(null);
		res.setBorder(null);

		return res;
	}

	private Icon getIconMini() {
		if (SubstanceLookAndFeel.isCurrentLookAndFeel()) {
			return new TransitionAwareIcon(this.buttonMini, new TransitionAwareIcon.Delegate() {
				public Icon getColorSchemeIcon(final SubstanceColorScheme scheme) {
					return SubstanceIconFactory.getTitlePaneIcon(SubstanceIconFactory.IconKind.MINIMIZE, scheme,
							SubstanceCoreUtilities.getSkin(FrameWithDropShadow.this.rootPane).getBackgroundColorScheme(
									DecorationAreaType.GENERAL));
				}
			}, "substance.titlePane.miniIcon");
		} else {
			return new Icon() {

				@Override
				public void paintIcon(final Component c, final Graphics g, final int x, final int y) {

					g.setColor(Color.BLACK);
					g.fillRect(2, 15, 10, 2);
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

	private Icon getIconMax() {

		if (SubstanceLookAndFeel.isCurrentLookAndFeel()) {
			return new TransitionAwareIcon(this.buttonMax, new TransitionAwareIcon.Delegate() {
				public Icon getColorSchemeIcon(final SubstanceColorScheme scheme) {
					return SubstanceIconFactory.getTitlePaneIcon(SubstanceIconFactory.IconKind.MAXIMIZE, scheme,
							SubstanceCoreUtilities.getSkin(FrameWithDropShadow.this.rootPane).getBackgroundColorScheme(
									DecorationAreaType.GENERAL));
				}
			}, "substance.titlePane.MaxIcon");
		}
		return new Icon() {

			@Override
			public void paintIcon(final Component c, final Graphics g, final int x, final int y) {

				final Graphics2D g2 = (Graphics2D) g;
				PaintUtils.turnOnAntialias(g2);
				final int size = 20;

				final int start = (size / 4) - 1;
				final int end = size - start - 1;

				g2.setColor(Color.BLACK);
				g2.drawRect(start, start, end - start, end - start);
				g2.drawLine(start, start + 1, end, start + 1);
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

	private Icon getIconRestore() {
		if (SubstanceLookAndFeel.isCurrentLookAndFeel()) {
			return new TransitionAwareIcon(this.buttonRestore, new TransitionAwareIcon.Delegate() {
				public Icon getColorSchemeIcon(final SubstanceColorScheme scheme) {
					return SubstanceIconFactory.getTitlePaneIcon(SubstanceIconFactory.IconKind.RESTORE, scheme,
							SubstanceCoreUtilities.getSkin(FrameWithDropShadow.this.rootPane).getBackgroundColorScheme(
									DecorationAreaType.GENERAL));
				}
			}, "substance.titlePane.RestoreIcon");
		}
		return new Icon() {

			@Override
			public void paintIcon(final Component c, final Graphics g, final int x, final int y) {

				final Graphics2D g2 = (Graphics2D) g;
				PaintUtils.turnOnAntialias(g2);
				final int isize = 20;

				final int start = (isize / 4) - 2;
				final int end = (3 * isize / 4) - 1;
				final int size = end - start - 3;

				g2.setColor(Color.BLACK);
				g2.drawRect(start, end - size + 1, size, size);
				g2.drawLine(start, end - size + 2, start + size, end - size + 2);
				g2.fillRect(end - size, start + 1, size + 1, 2);
				g2.drawLine(end, start + 1, end, start + size + 1);
				g2.drawLine(start + size + 2, start + size + 1, end, start + size + 1);
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

	private Icon getIconClose() {
		if (SubstanceLookAndFeel.isCurrentLookAndFeel()) {
			return new TransitionAwareIcon(this.buttonClose, new TransitionAwareIcon.Delegate() {
				public Icon getColorSchemeIcon(final SubstanceColorScheme scheme) {
					return SubstanceIconFactory.getTitlePaneIcon(SubstanceIconFactory.IconKind.CLOSE, scheme,
							SubstanceCoreUtilities.getSkin(FrameWithDropShadow.this.rootPane).getBackgroundColorScheme(
									DecorationAreaType.GENERAL));
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

	private JPanel getPanelButton() {
		if (this.panelButton == null) {
			this.panelButton = new JPanel();
			this.panelButton.setLayout(new MigLayout("inset 0 0 0 0,hidemode 3", "0[]0[]5[]0", ""));
			this.panelButton.add(getButtonMini(), "grow, h 20!, w 20!");
			this.panelButton.add(getButtonMax(), "grow, h 20!, w 20!");
			this.panelButton.add(getButtonRestore(), "grow, h 20!, w 20!");
			this.panelButton.add(getButtonClose(), "grow, h 20!, w 20!");
		}
		return this.panelButton;
	}

	protected void buttonCloseActionPerformed() {
		if (getDefaultCloseOperation() == JFrame.EXIT_ON_CLOSE) {
			System.exit(0);
		} else if (getDefaultCloseOperation() != JFrame.DO_NOTHING_ON_CLOSE) {
			startHideAnim();
		}
	}

	private void buttonRestoreActionPerformed() {
		getButtonRestore().setVisible(false);
		getButtonMax().setVisible(true);
		setExtendedState(Frame.NORMAL);
	}

	private void buttonMiniActionPerformed() {
		setExtendedState(Frame.ICONIFIED);
	}

	private void buttonMaxActionPerformed() {
		getButtonRestore().setVisible(true);
		getButtonMax().setVisible(false);
		setExtendedState(Frame.MAXIMIZED_BOTH);
	}

}
