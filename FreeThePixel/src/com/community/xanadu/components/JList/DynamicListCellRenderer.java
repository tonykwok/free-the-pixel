package com.community.xanadu.components.JList;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;


public abstract class DynamicListCellRenderer<T> implements ListCellRenderer {

	public abstract Component getListCellRendererComponent(final DynamicSizeJList<T> list, final T value,
			final int index, int selectingIndex, int deselectingIndex, float progress,boolean cellHasFocus);

	@Override
	public final Component getListCellRendererComponent(final JList list, final Object value, final int index,
			final boolean isSelected, final boolean cellHasFocus) {

		final DynamicSizeJList<T> dlist = (DynamicSizeJList<T>) list;
		return getListCellRendererComponent(dlist, (T) value, index, dlist.getSelectingIndex(), dlist
				.getDeselectingIndex(), dlist.getProgress(index),dlist.getFocusIndex()==index);
	}
}