package com.droiddev.client.widget;

import com.droiddev.client.util.ImageResources;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

public class RadioButton extends CompoundButton {
	public static final String TAG_NAME = "RadioButton";
	Image on;
	Image off;

	public RadioButton(String text) {
		super(text);
		this.setTagName(TAG_NAME);

		pad_y = 6;

		on = ImageResources.instance().get("btn_radio_on.png");
		off = ImageResources.instance().get("btn_radio_off.png");

		apply();
	}

	@Override
	public void apply() {

		if (off != null){
			if (off != null) {
				pad_x = off.getWidth();
			}
			else {
				pad_x = 24;
			}
			super.apply();
		}
	}


	@Override
	protected int getContentHeight() {
		if (off != null) {
			return off.getHeight();
		}
		else {
			return super.getContentHeight();
		}
	}

	@Override
	public void paint() {
		getCanvas().setCoordinateSpaceWidth(getWidth());
		getCanvas().setCoordinateSpaceHeight(getHeight());
		if ("true".equals(this.getPropertyByAttName("android:checked").getValue())) {
			ImageElement imageElement = ImageElement.as(on.getElement());
			getCanvas().getContext2d().drawImage(imageElement, 0, 0);
			getCanvas().getContext2d().setFont(font);
			getCanvas().getContext2d().fillText(getText(), on.getWidth(), (on.getHeight()+fontSize)/2 - 4);
		}
		else {
			ImageElement imageElement = ImageElement.as(off.getElement());
			getCanvas().getContext2d().drawImage(imageElement, 0, 0);
			getCanvas().getContext2d().setFont(font);
			getCanvas().getContext2d().fillText(getText(), off.getWidth(), (off.getHeight()+fontSize)/2 - 4);
		}
	}
}