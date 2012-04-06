package com.droiddev.client.util;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;


public class NineWayImage {
	double dx1, dy1, dx2, dy2, wx, wy, w, h;
	ImageElement image;

	public NineWayImage(Image img, double dx, double dy) {
		this(img, dx, dy, dx, dy);
	}

	public NineWayImage(Image img, double dx1, double dy1, double dx2, double dy2) {
		if (img == null) {
			throw new IllegalArgumentException();
		}
		this.dx1 = dx1;
		this.dy1 = dy1;
		this.dx2 = dx2;
		this.dy2 = dy2;
		this.wx = img.getWidth()-(dx1+dx2);
		this.wy = img.getHeight()-(dy1+dy2);
		this.w = img.getWidth();
		this.h = img.getHeight();
		image = ImageElement.as(img.getElement());
	}

	public void paint(Context2d context, double x, double y, double sx, double sy) {
		if (dx1 > 0 && dy1 > 0) context.drawImage(image, 1, 1, dx1, dy1, x, y, dx1, dy1);
		if (dy1 > 0)			context.drawImage(image, dx1, 1, wx, dy1, x+dx1, y, sx-(dx1+dx2), dy1);
		if (dx2 > 0 && dy1 > 0) context.drawImage(image, w-dx2 - 1, 1, dx2, dy1, x+sx-dx2, y, dx2, dy1);

		if (dx1 > 0) 			context.drawImage(image, 1, dy1, dx1, wy, x, y+dy1, dx1, sy-(dy1+dy2));
								context.drawImage(image, dx1, dy1, wx, wy, x+dx1, y+dy1, sx-(dx1+dx2), sy-(dy1+dy2));
		if (dx2 > 0)			context.drawImage(image, w-dx2 - 1,dy1,dx2,wy, x+sx-dx2, y+dy1, dx2, sy-(dy1+dy2));

		if (dx1 > 0 && dy2 > 0) context.drawImage(image, 1,h-dy2 - 1,dx1,dy2, x, y+sy-dy2, dx1, dy2);
		if (dy2 > 0)			context.drawImage(image, dx1,h-dy2 - 1,wx,dy2, x+dx1, y+sy-dy2, sx-(dx1+dx2), dy2);
		if (dx2 > 0 && dy2 > 0) context.drawImage(image, w-dx2 - 1,h-dy2 - 1, dx2, dy2, x+sx-dx2, y+sy-dy2, dx2, dy2);
	}
}