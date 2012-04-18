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
	
	public void addWidgetToCode(final String type, final String id) {
		setContent(AndroidEditor.instance().getCodeMirror().getText());
		
		addImport("android.widget." + type);
		addField(type, id);
		addDefinition(type, id);
	}
	
	public void addImport(String toImport) {
		// TODO fix so this only updates codemirror if this is active
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
			addCMLine(AndroidEditor.instance().getCodeMirror().getCM(), "import " + toImport + ";", lastImport);
		
		setContent(AndroidEditor.instance().getCodeMirror().getText());
	}
	
	public void addField(String type, String name) {
		setContent(AndroidEditor.instance().getCodeMirror().getText());
		
		String[] lines = content.split("\n");
		
		boolean firstBracketFound = false;
		int lastLineContainingSemicolon = -1;
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (firstBracketFound && line.contains("{"))
				break;
			if (line.contains("{"))
				firstBracketFound = true;
			if (line.contains(";")) // TODO also check that not comment
				lastLineContainingSemicolon = i;
		}
		
		if (lastLineContainingSemicolon >= 0)
			addCMLine(AndroidEditor.instance().getCodeMirror().getCM(), "private " + type + " m" + name + ";", lastLineContainingSemicolon);
		
		setContent(AndroidEditor.instance().getCodeMirror().getText());
	}
	
	public void addDefinition(String type, String id) {
		setContent(AndroidEditor.instance().getCodeMirror().getText());
		
		String[] lines = content.split("\n");
		
		int setContentViewLine = -1;
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.trim().startsWith("setContentView")) {
				setContentViewLine = i;
			}
		}
		
		if (setContentViewLine >= 0) {
			if (lines[setContentViewLine+1].trim().equals(""))
				setContentViewLine++;
			
			addCMLine(AndroidEditor.instance().getCodeMirror().getCM(),
					"m" + id + " = (" + type + ")findViewById(R.id." + id + ");", setContentViewLine);
		}

		setContent(AndroidEditor.instance().getCodeMirror().getText());
	}
	
	private static native void addCMLine(JavaScriptObject cm, String line, int lineNum) /*-{
		var lineContent = cm.getLine(lineNum);
		cm.setLine(lineNum, lineContent + "\n" + line);
		cm.indentLine(lineNum+1);
	}-*/;
}
