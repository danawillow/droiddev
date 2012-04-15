package com.droiddev.client.widget;

import com.droiddev.client.property.StringProperty;
import com.droiddev.client.util.ImageResources;
import com.droiddev.client.util.NineWayImage;
import com.google.gwt.user.client.ui.Image;

public class Button extends TextView {

	public static final String TAG_NAME = "Button";
	public static final String[] menuItems = {"void setOnClickListener(View.OnClickListener l)"};
	public static final String[] menuFunctions = {"setOnClickListener(View.OnClickListener l)"};
	public static final String[] menuImports = {"android.view.View"};
	NineWayImage img;
	Image img_base;
	StringProperty onClick;

	public Button(String txt) {
		super(txt, TAG_NAME);
		this.setTagName(TAG_NAME);
		this.setMenuItems(menuItems);
		this.setMenuFunctions(menuFunctions);
		this.setMenuImports(menuImports);

		pad_x = 10;
		pad_y = 0;

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

	@Override
	public void paint() {
		getCanvas().setCoordinateSpaceWidth(getWidth());
		getCanvas().setCoordinateSpaceHeight(getHeight());
		img.paint(getCanvas().getContext2d(), 0, 0, getWidth(), getHeight());
		font = fontSize + "px Arial";
		getCanvas().getContext2d().setFont(font);
		drawText(0, getHeight()/2+fontSize/2-5, CENTER);
	}
}