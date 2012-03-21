package com.droiddev.client.util;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;

public class FontMetrics {
	
	public static int stringWidth(String font, String str) {
		Element span = DOM.createSpan();
		span.setInnerText(str);
		DOM.setStyleAttribute(span, "font", font);
		//return DOM.getElementPropertyInt(span, "width");
		Window.alert("String is " + str + ", font is " + font + ", width is " + span.getOffsetWidth());
		return span.getOffsetWidth();
	}
}
