package com.community.xanadu.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicListUI;

public class JlistUtils {
	public static void computeListSize(final JList list) {
		if (list.getUI() instanceof BasicListUI) {
			final BasicListUI ui = (BasicListUI) list.getUI();

			try {
				final Method method = BasicListUI.class.getDeclaredMethod("updateLayoutState");
				method.setAccessible(true);
				method.invoke(ui);
				list.revalidate();
				list.repaint();
			} catch (final SecurityException e) {
				e.printStackTrace();
			} catch (final NoSuchMethodException e) {
				e.printStackTrace();
			} catch (final IllegalArgumentException e) {
				e.printStackTrace();
			} catch (final IllegalAccessException e) {
				e.printStackTrace();
			} catch (final InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
}
