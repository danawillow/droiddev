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

	private native JavaScriptObject addCodeMirrorJS(Object e) /*-{
		var that = this;
		return $wnd.CodeMirror.fromTextArea(e, {
			lineNumbers: true,
			matchBrackets: true,
			extraKeys: { "Ctrl-I": function(cm) { cm.autoFormatRange(cm.getCursor(true), cm.getCursor(false)); } }
			});
	}-*/;
	
	public JavaScriptObject getCM() {
		return cm;
	}

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

	public void setFindViewLine(String id, String nodeName) {
		setCMFindViewLine(cm, id, nodeName);
	}

	private static native void setCMFindViewLine(JavaScriptObject cm, String id, String nodeName) /*-{
		var cursor = cm.getCursor();
		var lineContent = cm.getLine(cursor.line);
		var nextLine = nodeName + " " + id + " = (" + nodeName + ")findViewById(R.id." + id + ");";
		cm.setLine(cursor.line, lineContent + '\n' + nextLine);
		cm.setSelection({line: cursor.line+1, ch: nodeName.length+1},
                        {line: cursor.line+1, ch: (nodeName + " " + id).length});
		cm.indentLine(cursor.line+1);
		cm.focus();
	}-*/;
	
	public int getCursorLine() {
		return getCMCursorLine(cm);
	}
	
	private static native int getCMCursorLine(JavaScriptObject cm) /*-{
		return cm.getCursor().line;
	}-*/;
}
