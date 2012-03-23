package com.droiddev.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class DirInfo extends JavaScriptObject {

	protected DirInfo() {}
	
	public final native String getName() /*-{ return this.name; }-*/;
	
	public final native JsArray<DirInfo> getChildren() /*-{ return this.children; }-*/;
}
