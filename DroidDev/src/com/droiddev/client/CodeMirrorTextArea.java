package com.droiddev.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.TextArea;

public class CodeMirrorTextArea extends TextArea {
	JavaScriptObject cm;
	String cmText;

	public CodeMirrorTextArea(String id) {
		super();
		getElement().setId(id);
	}
	
	public void onAttach() {
		super.onAttach();
		cm = addCodeMirrorJS(getElement());
		if (cmText != null)
			setCodeMirrorText(cm, cmText);
	}
	
	private static native JavaScriptObject addCodeMirrorJS(Object e) /*-{
		return $wnd.CodeMirror.fromTextArea(e, {lineNumbers: true});
	}-*/;
	
	@Override
	public void setText(String s) {
		if (cm == null)
			cmText = s;
		else
			setCodeMirrorText(cm, s);
	}
	
	private static native void setCodeMirrorText(JavaScriptObject cm, String s) /*-{
		cm.setValue(s);
	}-*/;
}
