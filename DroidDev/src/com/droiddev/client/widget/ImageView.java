package com.droiddev.client.widget;

import com.droiddev.client.util.ImageResources;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

public class ImageView extends AbstractWidget {
	public static final String TAG_NAME = "ImageView";
	Image paint;
	//BufferedImage img;

	//ImageProperty src;

	public ImageView() {
		super(TAG_NAME);
		paint = ImageResources.instance().get("paint.png");
		//src = new ImageProperty("Image Source", "android:src", "");
		//addProperty(src);
		apply();
	}

	@Override
	protected int getContentHeight() {
		/*
		if (img == null)
			return 30;
		else
			return img.getHeight();
			*/
		if (paint == null)
			return 30;
		else
			return paint.getHeight();
	}

	@Override
	protected int getContentWidth() {
		/*
		if (img == null)
			return 30;
		else
			return img.getWidth();
			*/
		if (paint == null)
			return 30;
		else
			return paint.getHeight();
	}

	@Override
	public void apply() {
		super.apply();
		/*
		if (src.getStringValue() != null && src.getStringValue().startsWith("@drawable")) {
			img = AndroidEditor.instance().findDrawable(src.getStringValue());
		}
		*/
	}

	/*
	public void paint(Graphics g) {
		if (img != null) {
			g.drawImage(img, getX(), getY(), getWidth(), getHeight(), null);
		}
		else if (paint != null) {
			g.drawImage(paint, getX(), getY(), getWidth(), getHeight(), null);
		}
	}
	 */
	
	public void paint() {
		getCanvas().setCoordinateSpaceWidth(getWidth());
		getCanvas().setCoordinateSpaceHeight(getHeight());
		if (paint != null) {
			ImageElement imageElement = ImageElement.as(paint.getElement());
			getCanvas().getContext2d().drawImage(imageElement, 0, 0);
		}
	}
}
