package com.community.xanadu.components.windows.dropShadow;

import java.awt.Window;

import javax.swing.JComponent;

import net.miginfocom.swing.MigLayout;

import com.community.xanadu.components.flippable.ReversableComponent;

public class DialogWithShadowAndFlip extends DialogWithDropShadow {

	private static final long serialVersionUID = 1L;

	protected ReversableComponent reversableComp;

	public DialogWithShadowAndFlip(final Window parent) {
		super(parent);
		this.reversableComp = new ReversableComponent();

		getContentPane().setLayout(new MigLayout("fill"));

		getContentPane().add(this.reversableComp, "grow, wrap , span, gap top 20 , gap left 20");

	}

	public String getPanelMiglayoutContraint() {
		return "grow, gap left 100, gap top 320 , gap right 100 , gap bottom 50";
	}

	public void setFront(final JComponent comp) {
		this.reversableComp.setFront(comp);
	}

	public void setBack(final JComponent comp) {
		this.reversableComp.setBack(comp);
	}

	public void setAnimDuration(final int animDuration) {
		this.reversableComp.setAnimDuration(animDuration);
	}
}
