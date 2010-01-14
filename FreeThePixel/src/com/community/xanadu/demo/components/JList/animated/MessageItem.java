package com.community.xanadu.demo.components.JList.animated;


public class MessageItem {

	private String title;
	private String text;

	public MessageItem(final String title, final String text) {
		this.text = text;
		this.title = title;

	}

	@Override
	public String toString() {
		return this.title + " " + this.text;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getText() {
		return this.text;
	}

	public void setText(final String text) {
		this.text = text;
	}

}
