package com.community.xanadu.demo.components.JList.animated;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel;

import com.community.xanadu.components.JList.DynamicSizeJList;

public class Demo3AnimatedJList {
	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
					JFrame.setDefaultLookAndFeelDecorated(true);
				} catch (final UnsupportedLookAndFeelException e) {
				}
				final JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				final DynamicSizeJList<MessageItem> list = new DynamicSizeJList<MessageItem>(new Demo3AnimatedCellRenderer());
				final JScrollPane scroll = new JScrollPane(list);
				f.getContentPane().add(scroll);

				for (int i = 0; i < 20; i++) {
					list.addItem(new MessageItem("title", "<html><li>some text<li>some more text"));
				}

				f.setSize(400, 300);
				f.setVisible(true);
			}
		});
	}
}
