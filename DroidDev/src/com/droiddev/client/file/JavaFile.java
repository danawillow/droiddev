package com.droiddev.client.file;

import java.util.ArrayList;

import com.droiddev.client.AndroidEditor;
import com.google.gwt.core.client.JavaScriptObject;



public class JavaFile extends File {
	private String content;
	
	public JavaFile(String fileName, String path) {
		super(fileName, path);
	}

	@Override
	public int getType() {
		return File.JAVA;
	}
	
	public void setContent(String s) {
		content = s;
	}
	
	public String getContent() {
		return content;
	}
	
	public void addImport(String toImport) {
		setContent(AndroidEditor.instance().getCodeMirror().getText());
		
		ArrayList<String> imports = new ArrayList<String>();
		
		String[] lines = content.split("\n");
		
		int lastImport = 1;
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.startsWith("import ")) {
				lastImport = i;
				imports.add(line);
			}
		}
		
		if (!imports.contains("import " + toImport + ";"))
			addCMImport(AndroidEditor.instance().getCodeMirror().getCM(), toImport, lastImport);
		
		setContent(AndroidEditor.instance().getCodeMirror().getText());
	}
	
	public void getImports() {
		String[] lines = content.split("\n");
		
		for (String line: lines) {
			if (line.startsWith("import "))
				System.out.println(line);
		}
	}
	
	private static native void addCMImport(JavaScriptObject cm, String imp, int line) /*-{
		var lineContent = cm.getLine(line);
		var nextLine = "import " + imp + ";";
		cm.setLine(line, lineContent + "\n" + nextLine);
	}-*/;
}
