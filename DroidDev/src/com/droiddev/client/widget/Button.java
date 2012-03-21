package com.droiddev.client.widget;

import com.droiddev.client.property.StringProperty;

public class Button extends TextView {

	public static final String TAG_NAME = "Button";
	//NineWayImage img;
	//Image img_base;
	StringProperty onClick;

	public Button(String txt) {
		super(txt);
		this.setTagName(TAG_NAME);

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

	/*
	@Override
	protected int getContentHeight() {
		if (img_base != null) {
			return img_base.getHeight(null)-4;
		}
		else {
			return 10;
		}
	}

	@Override
	protected int getContentWidth() {
		int w = super.getContentWidth();
		if (img_base != null && w < img_base.getWidth(null)) {
			return img_base.getWidth(null);
		}
		return w;
	}
	*/

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
}