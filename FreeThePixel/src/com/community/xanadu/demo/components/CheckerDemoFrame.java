package com.community.xanadu.demo.components;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
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

import org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel;

import com.community.xanadu.components.checker.Checker;
import com.community.xanadu.components.checker.CheckerManager;
import com.community.xanadu.components.checker.TextCheckerManager;
import com.community.xanadu.components.checker.CheckerManager.eViewError;

public class CheckerDemoFrame extends javax.swing.JFrame {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(new SubstanceGraphiteAquaLookAndFeel());
					JFrame.setDefaultLookAndFeelDecorated(true);
				} catch (UnsupportedLookAndFeelException e) {
				}
				CheckerDemoFrame inst = new CheckerDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public CheckerDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {

			getContentPane().setLayout(new MigLayout("wrap 2,fill"));

			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			ArrayList<Checker<JTextComponent>> checkers = new ArrayList<Checker<JTextComponent>>();
			checkers.add(new Checker<JTextComponent>() {

				@Override
				public String validate(final JTextComponent text) {
					if (!text.getText().startsWith("A")) {
						return "Must begin with an A";
					}
					return null;
				}
			});

			checkers.add(new Checker<JTextComponent>() {

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
			t.setPreferredSize(new Dimension(50, 20));
			getContentPane().add(TextCheckerManager.getTextComponentWithCheckers(t, checkers, eViewError.blinking));
			t = new JTextField();
			t.setPreferredSize(new Dimension(150, 25));
			getContentPane().add(TextCheckerManager.getTextComponentWithCheckers(t, checkers, eViewError.icon));
			t = new JTextArea();
			t.setPreferredSize(new Dimension(200, 200));
			getContentPane().add(TextCheckerManager.getTextComponentWithCheckers(t, checkers, eViewError.both));

			Checker<JSpinner> c = new Checker<JSpinner>() {
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
					CheckerManager.check(spinner);
				}
			});
			spinner.setPreferredSize(new Dimension(100, 30));
			getContentPane().add(CheckerManager.getComponentWithCheckers(spinner, c, eViewError.both, null), "growx");

			getContentPane().add(new JLabel("Must begin with an \"A\" and size >= 5 "));

			setSize(600, 350);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
