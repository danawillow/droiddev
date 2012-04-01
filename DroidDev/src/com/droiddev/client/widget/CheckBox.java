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
		/*
		String theme = AndroidEditor.instance().getTheme();
		if (theme == null || theme.equals("default")) {
			off =  ImageResources.instance().getImage("def/btn_check_off");
			on = ImageResources.instance().getImage("def/btn_check_on");
		}
		else if (theme.equals("light")) {
			off = ImageResources.instance().getImage("light/checkbox_off_background");
			on = ImageResources.instance().getImage("light/checkbox_on_background");
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
		Image img;
		int off_x;
		int off_y;

		if (on == null || off == null) {
			g.setColor(Color.white);
			g.fillRect(getX()+2, getY()+2, 16, 16);

			g.setColor(Color.black);
			g.drawRect(getX()+2, getY()+2, 16, 16);

			if ("true".equals(this.getPropertyByAttName("android:checked").getValue())) {
				g.drawLine(getX()+2, getY()+2, getX()+18, getY()+18);
				g.drawLine(getX()+2, getY()+18, getX()+18, getY()+2);
			}
			off_x = 20;
			off_y = 18;
		}
		else {
			if ("true".equals(this.getPropertyByAttName("android:checked").getValue())) {
				img = on;
			}
			else {
				img = off;
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
