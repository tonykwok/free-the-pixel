package com.community.xanadu.components.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

public class DuoTable<T> extends JPanel {

	private BeanReaderJTable<T> tableLeft;
	private BeanReaderJTable<T> tableRight;

	private String[] properties;
	private String[] titles;
	private JButton buttonAdd;
	private JButton buttonRemove;
	private JButton buttonUp;
	private JButton buttonDown;

	public DuoTable(final String[] properties, final String[] titles) {
		this.properties = properties;
		this.titles = titles;
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill"));
		add(new JScrollPane(getTableLeft()), "spany 5");
		add(new JScrollPane(getTableRight()), "spany 5,wrap,skip 1");
		add(getButtonUp(), "grow,h 40! , w 40! , sg 1,wrap,pushy");
		add(getButtonDown(), "wrap,grow, sg 1,pushy");
		add(getButtonAdd(), "wrap,grow, sg 1,pushy");
		add(getButtonRemove(), "grow, sg 1,pushy");
	}

	@SuppressWarnings("serial")
	private BeanReaderJTable<T> getTableLeft() {
		if (this.tableLeft == null) {
			this.tableLeft = new BeanReaderJTable<T>(this.properties, this.titles) {
				@Override
				public void setAutoCreateRowSorter(final boolean autoCreateRowSorter) {
				}
			};

			this.tableLeft.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(final ListSelectionEvent e) {
					tableLeftSelectionChanged();
				}
			});
		}
		return this.tableLeft;
	}

	private void tableLeftSelectionChanged() {
		updateState();
	}

	@SuppressWarnings("serial")
	private BeanReaderJTable<T> getTableRight() {
		if (this.tableRight == null) {
			this.tableRight = new BeanReaderJTable<T>(this.properties, this.titles) {
				@Override
				public void setAutoCreateRowSorter(final boolean autoCreateRowSorter) {
				}
			};
			this.tableRight.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(final ListSelectionEvent e) {
					tableRightSelectionChanged();
				}
			});
		}
		return this.tableRight;
	}

	private void tableRightSelectionChanged() {
		updateState();
	}

	private JButton getButtonAdd() {
		if (this.buttonAdd == null) {
			this.buttonAdd = new JButton("right");// DirectionButton(Direction.
			// RIGHT);
			this.buttonAdd.setEnabled(false);
			this.buttonAdd.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonAddActionPerformed();
				}
			});
		}
		return this.buttonAdd;
	}

	private void buttonAddActionPerformed() {
		T[] tab = getTableLeft().getSelectedObjects();

		for (T t : tab) {
			getTableRight().addRow(t);
			getTableLeft().removeRow(t);
		}
	}

	private JButton getButtonDown() {
		if (this.buttonDown == null) {
			this.buttonDown = new JButton("down");// DirectionButton(Direction.
			// DOWN);
			this.buttonDown.setEnabled(false);
			this.buttonDown.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonDownactionPerformed();
				}
			});
		}
		return this.buttonDown;
	}

	private void buttonDownactionPerformed() {
		int[] tab = getTableRight().getSelectedRows();

		for (int i = tab.length - 1; i >= 0; i--) {
			getTableRight().getAllObjects().add(tab[i] + 2, getTableRight().getObjectAtRow(tab[i]));
			getTableRight().getAllObjects().remove(tab[i]);
		}

		getTableRight().getSelectionModel().setSelectionInterval(tab[0] + 1, tab[tab.length - 1] + 1);
	}

	private JButton getButtonRemove() {
		if (this.buttonRemove == null) {
			this.buttonRemove = new JButton("left");// DirectionButton(Direction.
			// LEFT);
			this.buttonRemove.setEnabled(false);
			this.buttonRemove.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonRemoveActionPerformed();
				}
			});
		}
		return this.buttonRemove;
	}

	private void buttonRemoveActionPerformed() {
		T[] tab = getTableRight().getSelectedObjects();

		for (T t : tab) {
			getTableLeft().addRow(t);
			getTableRight().removeRow(t);
		}
	}

	private JButton getButtonUp() {
		if (this.buttonUp == null) {
			this.buttonUp = new JButton("up");// DirectionButton(Direction.UP);
			this.buttonUp.setEnabled(false);
			this.buttonUp.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonUpActionPerformed();
				}
			});
		}
		return this.buttonUp;
	}

	private void buttonUpActionPerformed() {
		int[] tab = getTableRight().getSelectedRows();

		for (int i : tab) {
			getTableRight().getAllObjects().add(i - 1, getTableRight().getObjectAtRow(i));
			getTableRight().getAllObjects().remove(i + 1);
		}

		getTableRight().getSelectionModel().setSelectionInterval(tab[0] - 1, tab[tab.length - 1] - 1);
	}

	public void addItem(final T t) {
		getTableLeft().addRow(t);
	}

	public void removeItem(final T t) {
		getTableLeft().removeRow(t);
		getTableRight().removeRow(t);
	}

	public List<T> getSelectedItems() {
		return getTableRight().getAllObjects();
	}

	private void updateState() {
		T tLeft = getTableLeft().getSelectedObject();
		T tRight = getTableRight().getSelectedObject();
		int[] tIndex = getTableRight().getSelectedRows();
		getButtonAdd().setEnabled(tLeft != null);
		getButtonRemove().setEnabled(tRight != null);
		getButtonUp().setEnabled(tRight != null);
		getButtonDown().setEnabled(tRight != null);

		if (tIndex.length > 0) {
			getButtonUp().setEnabled(tIndex[0] != 0);
			getButtonDown().setEnabled(tIndex[tIndex.length - 1] != getTableRight().getRowCount() - 1);
		}
	}
}
