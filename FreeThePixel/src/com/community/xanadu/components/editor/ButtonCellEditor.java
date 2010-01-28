package com.community.xanadu.components.editor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.CellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;
import org.pushingpixels.substance.internal.utils.SubstanceStripingUtils;

public abstract class ButtonCellEditor extends SubstanceDefaultTableCellRenderer implements CellEditor, TableCellEditor, TableCellRenderer {
	private static final long serialVersionUID = 1L;
	protected JPanel panelEditor;
	protected JPanel panelRenderer;
	protected JButton editor;
	protected JButton renderer;
	private int currentRow;
	private JTable currentTable;

	public ButtonCellEditor() {
		this(new JButton("editor"), new JButton("renderer"));
	}

	public ButtonCellEditor(final JButton editor, final JButton renderer) {
		this.editor = editor;
		this.panelEditor = new JPanel(new MigLayout("fill,inset 0 0 0 0"));
		this.panelEditor.add(this.editor, "grow, h 80%!, w 80%!  ,center");
		this.editor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				buttonActionPerformed(ButtonCellEditor.this.currentRow, ButtonCellEditor.this.currentTable);
				stopCellEditing();
			}
		});
		this.renderer = renderer;
		this.panelRenderer = new JPanel(new MigLayout("fill,inset 0 0 0 0"));
		this.panelRenderer.add(this.renderer, "grow, h 80%!, w 80%!  ,center");
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
		this.currentRow = row;
		this.currentTable = table;
		prepareButton(this.editor, table, value, row, column);
		if (SubstanceLookAndFeel.isCurrentLookAndFeel()) {
			SubstanceStripingUtils.applyStripedBackground(table, row, this.panelEditor);
		}
		return this.panelEditor;
	}

	@Override
	public Object getCellEditorValue() {
		return "";
	}

	protected abstract void buttonActionPerformed(int row, JTable table);

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus,
			final int row, final int column) {
		prepareButton(this.renderer, table, value, row, column);
		if (SubstanceLookAndFeel.isCurrentLookAndFeel()) {
			SubstanceStripingUtils.applyStripedBackground(table, row, this.panelRenderer);
		}
		return this.panelRenderer;
	}

	/**
	 * @return the text on the button
	 * */
	protected abstract String getText(JTable table, int row);

	protected void prepareButton(final JButton button, final JTable table, final Object value, final int row, final int column) {
		button.setText(getText(table, row));
	}

	// ------------------- code pasted from abstract cell editor
	// -------------------- can't extends abstractCellEditor because to have the
	// highlight effect the renderer must
	// extend substanceDefaultCellRenderer
	protected EventListenerList listenerList = new EventListenerList();
	transient protected ChangeEvent changeEvent = null;

	/**
	 * Returns true.
	 * 
	 * @param e
	 *            an event object
	 * @return true
	 */
	public boolean isCellEditable(final EventObject e) {
		return true;
	}

	/**
	 * Returns true.
	 * 
	 * @param anEvent
	 *            an event object
	 * @return true
	 */
	public boolean shouldSelectCell(final EventObject anEvent) {
		return true;
	}

	/**
	 * Calls <code>fireEditingStopped</code> and returns true.
	 * 
	 * @return true
	 */
	public boolean stopCellEditing() {
		fireEditingStopped();
		return true;
	}

	/**
	 * Calls <code>fireEditingCanceled</code>.
	 */
	public void cancelCellEditing() {
		fireEditingCanceled();
	}

	/**
	 * Adds a <code>CellEditorListener</code> to the listener list.
	 * 
	 * @param l
	 *            the new listener to be added
	 */
	public void addCellEditorListener(final CellEditorListener l) {
		this.listenerList.add(CellEditorListener.class, l);
	}

	/**
	 * Removes a <code>CellEditorListener</code> from the listener list.
	 * 
	 * @param l
	 *            the listener to be removed
	 */
	public void removeCellEditorListener(final CellEditorListener l) {
		this.listenerList.remove(CellEditorListener.class, l);
	}

	/**
	 * Returns an array of all the <code>CellEditorListener</code>s added to this AbstractCellEditor with
	 * addCellEditorListener().
	 * 
	 * @return all of the <code>CellEditorListener</code>s added or an empty array if no listeners have been added
	 * @since 1.4
	 */
	public CellEditorListener[] getCellEditorListeners() {
		return this.listenerList.getListeners(CellEditorListener.class);
	}

	/**
	 * Notifies all listeners that have registered interest for notification on this event type. The event instance is
	 * created lazily.
	 * 
	 * @see EventListenerList
	 */
	protected void fireEditingStopped() {
		// Guaranteed to return a non-null array
		final Object[] listeners = this.listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CellEditorListener.class) {
				// Lazily create the event:
				if (this.changeEvent == null) {
					this.changeEvent = new ChangeEvent(this);
				}
				((CellEditorListener) listeners[i + 1]).editingStopped(this.changeEvent);
			}
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on this event type. The event instance is
	 * created lazily.
	 * 
	 * @see EventListenerList
	 */
	protected void fireEditingCanceled() {
		// Guaranteed to return a non-null array
		final Object[] listeners = this.listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CellEditorListener.class) {
				// Lazily create the event:
				if (this.changeEvent == null) {
					this.changeEvent = new ChangeEvent(this);
				}
				((CellEditorListener) listeners[i + 1]).editingCanceled(this.changeEvent);
			}
		}
	}
}