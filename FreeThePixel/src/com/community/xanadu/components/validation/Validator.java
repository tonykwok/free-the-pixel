package com.community.xanadu.components.validation;

import javax.swing.JComponent;

public interface Validator<T extends JComponent> {
	String validate(T comp);
}
