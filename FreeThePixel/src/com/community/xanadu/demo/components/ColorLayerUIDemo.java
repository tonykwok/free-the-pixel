package com.community.xanadu.demo.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.jxlayer.JXLayer;
import org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel;

import com.community.xanadu.components.layer.ColorLayerUI;
import com.community.xanadu.components.windows.dropShadow.DialogWithDropShadow;

public class ColorLayerUIDemo extends JFrame {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
					JFrame.setDefaultLookAndFeelDecorated(true);
				} catch (final UnsupportedLookAndFeelException e) {
				}
				final ColorLayerUIDemo frame = new ColorLayerUIDemo();
				frame.setVisible(true);
			}
		});
	}

	private JXLayer<JComponent> xlayer;
	private ColorLayerUI lockui;
	private JPanel mainPanel;
	private JButton buttonShowDialog;

	public ColorLayerUIDemo() {
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setSize(800, 600);
		getContentPane().add(getXlayer(), BorderLayout.CENTER);
	}

	private JXLayer<JComponent> getXlayer() {
		if (this.xlayer == null) {
			this.xlayer = new JXLayer<JComponent>(getMainPanel());
			this.lockui = new ColorLayerUI(this.xlayer);
			this.xlayer.setUI(this.lockui);
		}
		return this.xlayer;
	}

	private JPanel getMainPanel() {
		if (this.mainPanel == null) {
			this.mainPanel = new JPanel();
			this.mainPanel.setLayout(new MigLayout("fill"));
			this.mainPanel.add(new JScrollPane(new JTree()), "grow");
			this.mainPanel.add(new JScrollPane(new JTextArea()), "grow,wrap");
			this.mainPanel.add(new JLabel("field 1"), "split 2");
			this.mainPanel.add(new JTextField(), "growx,wrap");
			this.mainPanel.add(new JLabel("field 2"), "split 2");
			this.mainPanel.add(new JTextField(), "growx,wrap");
			this.mainPanel.add(getButtonShowDialog());

		}
		return this.mainPanel;
	}

	private JButton getButtonShowDialog() {
		if (this.buttonShowDialog == null) {
			this.buttonShowDialog = new JButton("show dialog");
			this.buttonShowDialog.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonShowDialogActionPerformed();
				}
			});
		}
		return this.buttonShowDialog;
	}

	private void buttonShowDialogActionPerformed() {

		final DialogWithDropShadow dialog = new DialogWithDropShadow(this,false,false);
		dialog.setResizable(false);
		dialog.setTitle("Enter some data");

		dialog.getContentPane().setLayout(new MigLayout("wrap 2"));
		dialog.getContentPane().add(new JLabel("Name:"), "");
		dialog.getContentPane().add(new JTextField(15), "growx,pushx");
		dialog.getContentPane().add(new JLabel("Adress"), "");
		dialog.getContentPane().add(new JTextField(15), "growx");
		final JButton ok = new JButton("ok");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				dialog.startHideAnim();
				ColorLayerUIDemo.this.lockui.hideUI();
			}
		});
		final JButton cancel = new JButton("cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				dialog.startHideAnim();
				ColorLayerUIDemo.this.lockui.hideUI();
			}
		});
		dialog.getContentPane().add(ok, "split 2 , spanx ,bottom, right,sg 1,pushy");
		dialog.getContentPane().add(cancel, "sg 1,bottom");
		dialog.setBounds(250, 200, 300, 200);

		this.lockui.ShowUI();
		dialog.startShowAnim();
	}
}
