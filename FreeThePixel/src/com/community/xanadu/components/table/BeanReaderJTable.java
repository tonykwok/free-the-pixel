package com.community.xanadu.components.table;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

import com.community.xanadu.components.renderers.IconTableCellRenderer;
import com.community.xanadu.demo.components.BeanReaderJTableDemo;

/**
 * The BeanReaderJTable is a JTable that display in each row a single object of
 * type T. Each Column of this JTable will be a property of this T object. The
 * visible Column are defined in the constructor
 * 
 * @author DIelsch
 * 
 * @param <T>
 */
public class BeanReaderJTable<T> extends JTable {
	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		BeanReaderJTableDemo.main(args);
	}

	private GenericTableModel model;
	private String[] fields;
	private String[] title;
	private boolean byGetter;

	// to know if a row has been already added or not.
	// during the first call to addRow, renderers are set
	private boolean first;

	private Map<Class<?>, TableCellRenderer> renderers;

	/**
	 * 
	 * @param fields
	 */
	public BeanReaderJTable(final String[] fields) {
		this(fields, fields, true);
	}

	/**
	 * 
	 * @param fields
	 * @param title
	 */
	public BeanReaderJTable(final String[] fields, final String[] title) {
		this(fields, title, true);
	}

	/**
	 * 
	 * @param fields
	 * @param title
	 * @param byGetter
	 *            if properties should be access by getter or directly
	 */
	public BeanReaderJTable(final String[] fields, final String[] title, final boolean byGetter) {
		if (fields.length != title.length) {
			throw new IllegalArgumentException("field length must match title length: " + fields.length + "!="
					+ title.length);
		}

		this.first = true;
		this.byGetter = byGetter;
		this.fields = fields;
		this.title = title;

		initRenderers();

		if (byGetter) {
			for (int i = 0; i < fields.length; i++) {
				if (fields[i] != null && !fields[i].isEmpty()) {
					String tmpFull = "";
					for (final String field : fields[i].split("\\.")) {
						String tmpfield = String.valueOf(field.charAt(0)).toUpperCase();
						if (field.length() > 1) {
							tmpfield += field.substring(1);
						}
						tmpFull += tmpfield + ".";
					}
					tmpFull = tmpFull.substring(0, tmpFull.length() - 1);
					fields[i] = tmpFull;
				}
			}
		}
		setModel(getModel());
		setAutoCreateRowSorter(true);
	}

	private void initRenderers() {
		final TableCellRenderer booleanRenderer = new SubstanceDefaultTableCellRenderer.BooleanRenderer();
		getRenderers().put(boolean.class, booleanRenderer);
		getRenderers().put(Boolean.class, booleanRenderer);

		final IconTableCellRenderer iconRenderer = new IconTableCellRenderer();
		getRenderers().put(BufferedImage.class, iconRenderer);
		getRenderers().put(ImageIcon.class, iconRenderer);

		final TableCellRenderer dateRenderer = new SubstanceDefaultTableCellRenderer.DateRenderer();
		getRenderers().put(Date.class, dateRenderer);
		getRenderers().put(java.sql.Date.class, dateRenderer);
	}

	public Map<Class<?>, TableCellRenderer> getRenderers() {
		if (this.renderers == null) {
			this.renderers = new HashMap<Class<?>, TableCellRenderer>();
		}
		return this.renderers;
	}

	@Override
	public GenericTableModel getModel() {
		if (this.model == null) {
			this.model = new GenericTableModel();
		}
		return this.model;
	}

	/**
	 * remove all the row
	 */
	public void clear() {
		this.model.clear();
	}

	public void replace(final int row, final T t) {
		getModel().replace(convertRowIndexToModel(row), t);
	}

	/**
	 * 
	 * @param t
	 *            the object to add to display in added row
	 */

	public void addRow(final T t) {
		this.model.addRow(t);
		if (this.first) {
			this.first = false;
			for (int i = 0; i < this.fields.length; i++) {
				if (this.fields[i] != null && !this.fields[i].equals("")) {

					final Object value = getValueAt(0, i);
					if (value != null) {
						final Class<?> returnType = value.getClass();

						final TableCellRenderer renderer = getRenderers().get(returnType);

						if (renderer != null) {
							getColumnModel().getColumn(i).setCellRenderer(renderer);
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param list
	 *            a collection of T to add to the model, 1 object=>1 row
	 */
	public void addRow(final Collection<T> list) {
		for (final T t : list) {
			addRow(t);
		}
	}

	/**
	 * 
	 * @param list
	 *            an array of T to add to the model, 1 object=>1 row
	 */
	public void addRow(final T[] list) {
		for (final T t : list) {
			addRow(t);
		}
	}

	/**
	 * 
	 * @param t
	 *            the object to remove from the model
	 */
	public void removeRow(final T t) {
		getModel().removeRow(t);
	}

	/**
	 * 
	 * @param row
	 * @return the T that is visible at the given row
	 */
	public T getObjectAtRow(final int row) {
		final int modelIndex = convertRowIndexToModel(row);
		return getModel().getObjects().get(modelIndex);
	}

	/**
	 * 
	 * @return the selected T
	 */
	public T getSelectedObject() {
		final int[] rows = getSelectedRows();
		if (rows != null && rows.length > 0) {
			return getObjectAtRow(getSelectedRows()[0]);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @return the collection of T contained in the model
	 */
	public List<T> getAllObjects() {
		return getModel().getObjects();
	}

	/**
	 * 
	 * @return an array of selected T
	 */
	@SuppressWarnings("unchecked")
	public T[] getSelectedObjects() {
		final int[] rows = getSelectedRows();
		if (rows != null && rows.length > 0) {
			final int[] selected = getSelectedRows();
			final Object[] tab = new Object[selected.length];
			for (int i = 0; i < selected.length; i++) {
				tab[i] = getObjectAtRow(selected[i]);
			}
			return (T[]) tab;
		} else {
			return (T[]) new Object[] {};
		}
	}

	public class GenericTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private ArrayList<T> objects;

		private ArrayList<T> getObjects() {
			if (this.objects == null) {
				this.objects = new ArrayList<T>();
			}
			return this.objects;
		}

		private Map<String, Method> methods;

		private Map<String, Method> getMethods() {
			if (this.methods == null) {
				this.methods = new HashMap<String, Method>();
			}
			return this.methods;
		}

		private Method getMethod(final String field, final Class<?> c) throws NoSuchMethodException {
			return getMethod(field, c, c);
		}

		private Method getMethod(final String field, final Class<?> lookIn, final Class<?> baseClassObject)
				throws NoSuchMethodException {
			Method method = getMethods().get(field + ":" + baseClassObject);
			try {
				if (method == null) {
					try {
						method = lookIn.getDeclaredMethod("get" + field);
						method.setAccessible(true);
					} catch (final Exception e) {
						method = lookIn.getDeclaredMethod("is" + field);
						method.setAccessible(true);
					}
					getMethods().put(field + ":" + baseClassObject, method);
				}
			} catch (final Exception e) {
				if (lookIn.getSuperclass() != null) {
					return getMethod(field, lookIn.getSuperclass(), baseClassObject);
				} else {
					throw new NoSuchMethodException("get" + field + " or is" + field + " doesn't exists");
				}
			}
			return method;
		}

		private Object getValue(final String field, final Object t) {
			if (field == null || field.isEmpty()) {
				return null;
			}
			try {
				final String[] tab = field.split("\\.");
				if (BeanReaderJTable.this.byGetter) {
					Method method;
					Object caller = t;
					for (final String s : tab) {
						if (caller == null) {
							return null;
						} else {
							method = getMethod(s, caller.getClass());
							caller = method.invoke(caller);
						}
					}
					return caller;
				} else {
					Object caller = t;
					for (final String s : tab) {
						if (caller == null) {
							return null;
						} else {
							final Field f = caller.getClass().getDeclaredField(s);
							f.setAccessible(true);
							caller = f.get(caller);
						}
					}
					return caller;
				}
			} catch (final Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		private void addRow(final T t) {
			getObjects().add(t);
			fireTableDataChanged();
		}

		private void clear() {
			getObjects().clear();
			fireTableDataChanged();
		}

		private void removeRow(final T t) {
			getObjects().remove(t);
			fireTableDataChanged();
		}

		private void replace(final int row, final T t) {
			getObjects().remove(row);
			getObjects().add(row, t);
		}

		@Override
		public String getColumnName(final int column) {
			return BeanReaderJTable.this.title[column];
		}

		@Override
		public boolean isCellEditable(final int row, final int column) {
			return BeanReaderJTable.this.fields[column] == null || BeanReaderJTable.this.fields[column] == "";
		}

		@Override
		public Object getValueAt(final int row, final int column) {
			final Object res = getValue(BeanReaderJTable.this.fields[column], this.objects.get(row));
			return res;
		}

		@Override
		public int getColumnCount() {
			if (BeanReaderJTable.this.fields != null) {
				return BeanReaderJTable.this.fields.length;
			} else {
				return 0;
			}
		}

		@Override
		public int getRowCount() {
			return getObjects().size();
		}
	}
}
