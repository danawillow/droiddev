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
	
	@Override
	public String getText() {
		return getCodeMirrorText(cm);
	}
	
	private static native String getCodeMirrorText(JavaScriptObject cm) /*-{
		return cm.getValue();
	}-*/;
	
	public void setOption(String name, String value) {
		setCMOption(cm, name, value);
	}
	
	private static native void setCMOption(JavaScriptObject cm, String name, String value) /*-{
		cm.setOption(name, value);
	}-*/;
}
