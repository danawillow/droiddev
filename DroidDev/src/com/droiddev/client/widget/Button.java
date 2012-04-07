package com.droiddev.client.widget;

import com.droiddev.client.property.StringProperty;
import com.droiddev.client.util.ImageResources;
import com.droiddev.client.util.NineWayImage;
import com.google.gwt.user.client.ui.Image;

public class Button extends TextView {

	public static final String TAG_NAME = "Button";
	public static final String[] menuItems = {"void setOnClickListener(View.OnClickListener l)"};
	public static final String[] menuFunctions = {"setOnClickListener(View.OnClickListener l)"};
	NineWayImage img;
	Image img_base;
	StringProperty onClick;

	public Button(String txt) {
		super(txt);
		this.setTagName(TAG_NAME);
		this.setMenuItems(menuItems);
		this.setMenuFunctions(menuFunctions);

		pad_x = 10;
		pad_y = 0;

		/*
		img_base = null;
		String theme = AndroidEditor.instance().getTheme();
		if (theme == null || theme.equals("default")) {
			img_base = ImageResources.instance().getImage("def/btn_default_normal.9");
			if (img_base != null) {
				this.img = new NineWayImage(img_base, 10, 10);
			}
		}
		else if (theme.equals("light")) {
			img_base = ImageResources.instance().getImage("light/button_background_normal.9");
			if (img_base != null) {
				this.img = new NineWayImage(img_base, 10, 10);
			}
		}
		 */
		img_base = ImageResources.instance().get("btn_default_normal.9.png");
		img = new NineWayImage(img_base, 10, 10);

		this.onClick = new StringProperty("Click Listener Classname", "android:onClickListener", null);
		addProperty(onClick);
		apply();
	}

	@Override
	public void apply() {
		super.apply();
		this.baseline = fontSize+2;
		//this.addStyleDependentName("button");
	}


	@Override
	protected int getContentHeight() {
		if (img_base != null) {
			return img_base.getHeight()-4;
		}
		else {
			return 10;
		}
	}

	@Override
	protected int getContentWidth() {
		int w = super.getContentWidth();
		if (img_base != null && w < img_base.getWidth()) {
			return img_base.getWidth();
		}
		return w;
	}


	/*
	@Override
	public void paint(Graphics g) {
		if (img == null) {
			g.setColor(Color.white);
			g.fillRoundRect(getX(), getY(), getWidth(), getHeight(), 8, 8);

			g.setColor(Color.black);
			g.drawRoundRect(getX(), getY(), getWidth(), getHeight(), 8, 8);
		}
		else {
			img.paint(g, getX(), getY(), getWidth(), getHeight());
			g.setColor(Color.black);
		}
		g.setFont(f);
		//int w = g.getFontMetrics(f).stringWidth(text.getStringValue());
		g.setColor(textColor.getColorValue());

		drawText(g, 0, getHeight()/2+fontSize/2-5, CENTER);
		//g.drawString(text.getStringValue(), getX()+getWidth()/2-w/2, getY()+fontSize+2);
	}
	 */

	@Override
	public void paint() {
		getCanvas().setCoordinateSpaceWidth(getWidth());
		getCanvas().setCoordinateSpaceHeight(getHeight());
		img.paint(getCanvas().getContext2d(), 0, 0, getWidth(), getHeight());
		drawText(0, getHeight()/2+fontSize/2-5, CENTER);
	}
}