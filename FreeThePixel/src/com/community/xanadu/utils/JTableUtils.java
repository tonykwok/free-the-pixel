package com.community.xanadu.utils;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class JTableUtils {
	public static void setHeaderTooltip(final JTable table, final Map<TableColumn, String> tooltips) {
		JTableHeader header = table.getTableHeader();
		ColumnHeaderToolTips tips = new ColumnHeaderToolTips();
		tips.setToolTips(tooltips);
		header.addMouseMotionListener(tips);
	}

	private static class ColumnHeaderToolTips extends MouseMotionAdapter {
		// Current column whose tooltip is being displayed.
		// This variable is used to minimize the calls to setToolTipText().
		private TableColumn curCol;
		private Map<TableColumn, String> tooltips;

		// If tooltip is null, removes any tooltip text.
		public void setToolTips(final Map<TableColumn, String> tooltips) {
			this.tooltips = tooltips;
		}

		@Override
		public void mouseMoved(final MouseEvent evt) {
			TableColumn col = null;
			JTableHeader header = (JTableHeader) evt.getSource();
			JTable table = header.getTable();
			TableColumnModel colModel = table.getColumnModel();
			int column = colModel.getColumnIndexAtX(evt.getX());

			// Return if not clicked on any column header
			if (column >= 0) {
				col = colModel.getColumn(column);
			}

			if (col != this.curCol) {
				header.setToolTipText((String) this.tooltips.get(col));
				this.curCol = col;
			}
		}
	}

	public static void lockWidth(final TableColumn column, final int width) {
		column.setMinWidth(width);
		column.setMaxWidth(width);
		column.setPreferredWidth(width);
	}

	public static void lockWidth(final JTable table, final int column, final int width) {
		lockWidth(table.getColumnModel().getColumn(column), width);
	}
}
