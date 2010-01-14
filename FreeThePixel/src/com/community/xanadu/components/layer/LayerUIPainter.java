package com.community.xanadu.components.layer;

import java.awt.Graphics2D;

import javax.swing.JComponent;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;

public interface LayerUIPainter {
	public void paint(Graphics2D g2, JXLayer<JComponent> layer, AbstractLayerUI<JComponent> ui,float animProgress);
}