package com.droiddev.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import com.droiddev.client.file.File;
import com.droiddev.client.file.JavaFile;
import com.droiddev.client.file.XMLFile;
import com.droiddev.client.widget.Layout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class AndroidEditor {
    public static int OFFSET_X = 0;
    public static int OFFSET_Y = 0;
    HashMap<String, String> strings = new HashMap<String, String>();
    private static AndroidEditor inst;
    public static EventBus EVENT_BUS = GWT.create(SimpleEventBus.class);
    AbsolutePanel layoutPanel;
    CodeMirrorTextArea code;
    CanvasWidget selected;
    Layout layout;
    //String currFile;
    File currFile;
    String lastXMLFile;
    String lastJavaFile;
    //HashMap<String, String> fileContents = new HashMap<String, String>();
    TreeSet<String> imports = new TreeSet<String>();
	HashSet<File> files = new HashSet<File>();

    public String getScreenUnit() {
        return "dp";
    }

    public int getScreenX() {
        return 320;
    }

    public int getScreenY() {
        return 480;
    }

    public HashMap<String, String> getStrings() {
        return strings;
    }
    
    public AbsolutePanel getLayoutPanel() {
    	return layoutPanel;
    }
    
    public CodeMirrorTextArea getCodeMirror() {
    	return code;
    }
    
    public void setSelected(CanvasWidget c) {
    	if (selected != null) {
    		selected.canvas.removeStyleName("selectedWidget");
    	}
    	selected = c;
    	selected.canvas.addStyleName("selectedWidget");
    }
    
    public Layout getLayout() {
    	return layout;
    }
    
    /*
    public void switchToFile(String fileName) {
		if (currFile != null && (currFile.endsWith(".xml") || currFile.endsWith(".java")))
			fileContents.put(currFile, code.getText());
		
		currFile = fileName;
		if (currFile == null) {
			code.setText("");
		}
		else if (currFile.endsWith(".xml")) {
			code.setText(fileContents.get(currFile));
			code.setOption("mode", "xml");
			lastXMLFile = currFile;
		}
		else if (currFile.endsWith(".java")) {
			code.setText(fileContents.get(currFile));
			code.setOption("mode", "text/x-java");
			lastJavaFile = currFile;
		}
		else
			code.setText("");
    }
    */
    
    public void switchToFile(File file) {
    	if (currFile != null) {
    		if (currFile.getType() == File.JAVA)
    			((JavaFile)currFile).setContent(code.getText());
    		else if (currFile.getType() == File.XML)
    			((XMLFile)currFile).setContent(code.getText());
    	}
    	
    	currFile = file;
    	if (currFile == null) {
    		code.setText("");
    	}
    	else if (currFile.getType() == File.XML) {
    		code.setText(((XMLFile)currFile).getContent());
    		code.setOption("mode", "xml");
    		lastXMLFile = currFile.getPath();
    	}
    	else if (currFile.getType() == File.JAVA) {
    		code.setText(((JavaFile)currFile).getContent());
    		code.setOption("mode", "text/x-java");
    		lastJavaFile = currFile.getPath();
    	}
    	else
    		code.setText("");
    }
    
    public static AndroidEditor instance() {
        if (inst == null) {
            inst = new AndroidEditor();
        }
        
        return inst;
    }
}