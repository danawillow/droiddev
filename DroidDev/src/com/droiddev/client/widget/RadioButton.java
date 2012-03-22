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
	public void apply() {/*
		String theme = AndroidEditor.instance().getTheme();
		if (theme == null || theme.equals("default")) {
			off =  ImageResources.instance().getImage("def/btn_radio_off");
			on = ImageResources.instance().getImage("def/btn_radio_on");
		}
		else if (theme.equals("light")) {
			off = ImageResources.instance().getImage("light/radiobutton_off_background");
			on = ImageResources.instance().getImage("light/radiobutton_on_background");
		}
	 */

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

	/*
	@Override
	public void paint(Graphics g) {
		int off_x, off_y;

		if (off == null || on == null) {
			g.setColor(Color.white);
			g.fillOval(getX()+2, getY()+2, 16, 16);

			g.setColor(Color.black);
			g.drawOval(getX()+2, getY()+2, 16, 16);

			if ("true".equals(this.getPropertyByAttName("android:checked").getValue())) {
				g.fillOval(getX()+6,getY()+6,8,8);
			}

			off_x = 20;
			off_y = 18;
		}
		else {
			Image img = off;
			if ("true".equals(this.getPropertyByAttName("android:checked").getValue())) {
				img = on;
			}
			g.drawImage(img, getX(), getY(), null);
			g.setColor(Color.black);

			off_x = img.getWidth(null);
			off_y = img.getHeight(null);
		}

		baseline = (off_y+fontSize)/2;

		setTextColor(g);
		g.setFont(f);
		g.drawString(text.getStringValue(), getX()+off_x, getY()+baseline-4);
	}
	 */

	@Override
	public void paint() {
		canvas.setCoordinateSpaceWidth(getWidth());
		canvas.setCoordinateSpaceHeight(getHeight());
		if ("true".equals(this.getPropertyByAttName("android:checked").getValue())) {
			ImageElement imageElement = ImageElement.as(on.getElement());
			canvas.getContext2d().drawImage(imageElement, 0, 0);
			canvas.getContext2d().setFont(font);
			canvas.getContext2d().fillText(getText(), on.getWidth(), (on.getHeight()+fontSize)/2 - 4);
		}
		else {
			ImageElement imageElement = ImageElement.as(off.getElement());
			canvas.getContext2d().drawImage(imageElement, 0, 0);
			canvas.getContext2d().setFont(font);
			canvas.getContext2d().fillText(getText(), off.getWidth(), (off.getHeight()+fontSize)/2 - 4);
		}
	}
}