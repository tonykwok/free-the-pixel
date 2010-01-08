package com.community.xanadu.components.table;

import java.awt.BorderLayout;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import com.community.xanadu.components.panels.layerSynchro.SizeSynchroLayerPanel;
import com.community.xanadu.demo.components.PanelTableWithButtonsDemo;

public class PanelTableWithButtons extends JPanel {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		PanelTableWithButtonsDemo.main(args);
	}

	private SizeSynchroLayerPanel layerPane;
	private JTable table;
	private JPanel panelTable;
	private JScrollPane scroll;
	private JPanel panelButtons;
	private JButton[] buttons;
	private int buttonXOffset;
	private int spaceForButtons;

	public PanelTableWithButtons(final JTable table) {
		this(table, 110);
	}

	public PanelTableWithButtons(final JTable table, final int spaceForButtons) {
		this.table = table;
		this.spaceForButtons = spaceForButtons;
		initGUI();

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					tableRowSelected();
				}
			}
		});
		getScroll().getVerticalScrollBar().getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent e) {
				scrollBarStateChanged();
			}
		});
	}

	private void scrollBarStateChanged() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				showButton();
			}
		});
		showButton();
	}

	private void tableRowSelected() {
		showButton();
	}

	private void showButton() {
		if (this.table.getRowCount() == 0 || this.table.getSelectedRow() == -1) {
			for (JButton b : getButton()) {
				b.setVisible(false);
			}
			return;
		}

		int row = this.table.getSelectedRow();
		int rowH = this.table.getRowHeight();

		int xoffset = 0;

		for (JButton b : getButton()) {
			int y = row * rowH - (b.getHeight() - rowH) / 2;
			int x = this.scroll.getWidth() + this.buttonXOffset + xoffset;
			Point p = SwingUtilities.convertPoint(this.table, x, y, getPanelButtons());

			b.setLocation(p);
			b.setVisible(true);
			xoffset += b.getWidth();
		}
		getPanelButtons().validate();
	}

	private void initGUI() {
		setLayout(new BorderLayout());
		add(getLayerPane(), BorderLayout.CENTER);
	}

	private JPanel getPanelTable() {
		if (this.panelTable == null) {
			this.panelTable = new JPanel();
			this.panelTable.setLayout(new MigLayout("fill"));
			this.panelTable.add(getScroll(), "grow,gap right " + this.spaceForButtons + "");
			this.panelTable.setOpaque(false);
		}
		return this.panelTable;
	}

	public JScrollPane getScroll() {
		if (this.scroll == null) {
			this.scroll = new JScrollPane(getTable());
		}
		return this.scroll;
	}

	public JTable getTable() {
		return this.table;
	}

	private JPanel getPanelButtons() {
		if (this.panelButtons == null) {
			this.panelButtons = new JPanel();
			this.panelButtons.setLayout(null);
			for (JButton b : getButton()) {
				this.panelButtons.add(b);
			}
			this.panelButtons.setOpaque(false);
		}
		return this.panelButtons;
	}

	public JButton[] getButton() {
		if (this.buttons == null) {
			this.buttons = new JButton[] { new JButton("...") };
			this.buttons[0].setSize(100, 50);
			this.buttons[0].setVisible(false);
		}
		return this.buttons;
	}

	public void setButtons(final JButton[] _buttons) {
		if (this.buttons != null && this.buttons.length > 0) {
			for (JButton b : this.buttons) {
				this.panelButtons.remove(b);
			}
		}

		if (_buttons != null) {
			for (JButton b : _buttons) {
				this.panelButtons.add(b);
				b.setVisible(false);
				if (b.getSize() == null || b.getWidth() == 0 || b.getHeight() == 0) {
					b.setSize(b.getPreferredSize());
				}
			}
		}
		this.buttons = _buttons;

	}

	public void setButton(final JButton _button) {
		setButtons(new JButton[] { _button });
	}

	private SizeSynchroLayerPanel getLayerPane() {
		if (this.layerPane == null) {
			this.layerPane = new SizeSynchroLayerPanel();
			this.layerPane.addLayer(getPanelTable(), 1);
			this.layerPane.addLayer(getPanelButtons(), 2);
			getPanelButtons().setVisible(true);
			getPanelTable().setVisible(true);
		}
		return this.layerPane;
	}

	/**
	 * by default (buttonXOffset=0) the buttons are outside the JTable<br>
	 * if(buttonXOffset<0) the button will go over the JTable<br>
	 * if(buttonXOffset>0) there will be a gap between the buttons and the
	 * JTable
	 * 
	 * @param buttonXOffset
	 */
	public void setButtonsXOffset(final int buttonXOffset) {
		this.buttonXOffset = buttonXOffset;
	}
}
