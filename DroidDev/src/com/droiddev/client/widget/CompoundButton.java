package com.droiddev.client.widget;

import com.droiddev.client.property.BooleanProperty;

public class CompoundButton extends Button {
	public CompoundButton(String text) {
		super(text);
		addProperty(new BooleanProperty("Checked", "android:checked", false));
	}
}
