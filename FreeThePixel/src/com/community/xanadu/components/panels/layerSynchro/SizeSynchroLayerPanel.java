package com.community.xanadu.components.panels.layerSynchro;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 * this panel is used as a layerpane<br>
 * you can add multiple panel inside<br>
 * only the one higher indexed visibled panel will be displayed<br>
 * 
 * same use as a JLayerPane except child panels take all the available space
 * without the need to specifiy it
 * 
 * @author DIelsch
 * 
 */
public class SizeSynchroLayerPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Map<Integer, JComponent> layers;
	private Map<Integer, SizeSynchronizer> sizeSyncListeners;
	private JLayeredPane layerPane;

	public SizeSynchroLayerPanel() {
		this.layers = new HashMap<Integer, JComponent>();
		this.sizeSyncListeners = new HashMap<Integer, SizeSynchronizer>();
		setLayout(new BorderLayout());
		this.layerPane = new JLayeredPane();
		add(this.layerPane, BorderLayout.CENTER);
		setOpaque(false);
	}

	public void setPanels(final JPanel[] panels) {
		this.layerPane.removeAll();
		this.layers.clear();
		this.sizeSyncListeners.clear();
		int index = 1000;
		for (JPanel p : panels) {
			addLayer(p, index);
			index++;
		}
	}

	public void addLayer(final JComponent comp) {
		addLayer(comp, this.layerPane.highestLayer() + 1, false);
	}

	public void addLayer(final JComponent comp, final int position) {
		addLayer(comp, position, false);
	}

	public void addLayer(final JComponent comp, final int position, final boolean halfSize) {
		if (this.layers.get(position) == null) {
			this.layerPane.add(comp);
			comp.setVisible(false);
			this.layerPane.setLayer(comp, position);
			this.layers.put(position, comp);

			SizeSynchronizer ss;
			if (halfSize) {
				ss = new HalfSizeSynchronizer(this.layerPane, comp);
			} else {
				ss = new SizeSynchronizer(this.layerPane, comp);
			}
			this.sizeSyncListeners.put(position, ss);
			this.layerPane.addComponentListener(ss);

			ss.componentShown(null);
		} else {
			throw new IllegalStateException("There is already a layer at " + position);
		}
	}

	public void removeLayer(final Component comp) {
		int position = this.layerPane.getLayer(comp);
		SizeSynchronizer ss = this.sizeSyncListeners.get(position);
		this.layerPane.removeComponentListener(ss);
		this.sizeSyncListeners.remove(position);
		this.layerPane.remove(comp);
		this.layers.remove(position);
	}

	public void removeLayer(final int position) {
		Component[] comps = this.layerPane.getComponentsInLayer(position);
		if (comps != null) {
			for (Component comp : comps) {
				removeLayer(comp);
			}
		}
	}

}
