package com.droiddev.client.widget;

import com.droiddev.client.util.ImageResources;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

public class CheckBox extends CompoundButton {
	public static final String TAG_NAME = "CheckBox";
	Image off;
	Image on;

	CheckBox cb;

	public CheckBox(String text) {
		super(text);
		setTagName(TAG_NAME);

		off = ImageResources.instance().get("btn_check_off.png");
		on = ImageResources.instance().get("images/btn_check_on.png");

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
