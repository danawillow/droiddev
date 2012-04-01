package com.droiddev.client.widget;

import com.droiddev.client.property.BooleanProperty;
import com.droiddev.client.property.SelectProperty;
import com.droiddev.client.property.StringProperty;

public class ListView extends AbstractWidget {

	public static final String TAG_NAME = "ListView";
	String font = "14px Monaco";

	public ListView() {
		super(TAG_NAME);
		props.add(new StringProperty("List Selector", "android:listSelector", ""));
		props.add(new BooleanProperty("Selector on Top", "android:drawSelectorOnTop", false));

		props.add(new StringProperty("Entry Array Id.", "android:entries", ""));
		props.add(new SelectProperty("Entry Gravity", "android:gravity", new String[] {"left", "center", "right"}, 0));
		apply();
	}

	@Override
	protected int getContentHeight() {
		return 16;
	}

	@Override
	protected int getContentWidth() {
		return 55;
	}

	/*
	public void paint(Graphics g) {
		g.setColor(Color.darkGray);
		g.drawString("ListView", getX()+2, getY()+14);
		g.drawRect(getX(), getY(), getWidth(), getHeight());
	}
	*/
	
	public void paint() {
		getCanvas().setCoordinateSpaceWidth(getWidth());
		getCanvas().setCoordinateSpaceHeight(getHeight());
		getCanvas().getContext2d().setFont(font);
		getCanvas().getContext2d().setFillStyle("#888");
		getCanvas().getContext2d().setStrokeStyle("#888");
		getCanvas().getContext2d().fillText("ListView", 2, 14);
		getCanvas().getContext2d().strokeRect(0, 0, getWidth(), getHeight());
	}
}