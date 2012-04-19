package com.droiddev.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Vector;

import com.droiddev.client.file.File;
import com.droiddev.client.file.JavaFile;
import com.droiddev.client.file.XMLFile;
import com.droiddev.client.property.Property;
import com.droiddev.client.property.StringProperty;
import com.droiddev.client.widget.CheckBox;
import com.droiddev.client.widget.Layout;
import com.droiddev.client.widget.Widget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;
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
    File currFile;
    XMLFile lastXMLFile;
    JavaFile lastJavaFile;
    TreeSet<String> imports = new TreeSet<String>();
	HashSet<File> files = new HashSet<File>();
	private DroidDevServiceAsync service = GWT.create(DroidDevService.class);

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
    		lastXMLFile = (XMLFile)currFile;
    	}
    	else if (currFile.getType() == File.JAVA) {
    		code.setText(((JavaFile)currFile).getContent());
    		code.setOption("mode", "text/x-java");
    		lastJavaFile = (JavaFile)currFile;
    	}
    	else
    		code.setText("");
    }
    
    public File getFileByName(String name) {
    	for (File f: files) {
    		if (f.getFileName().equals(name))
    			return f;
    	}
    	return null;
    }
    
    public DroidDevServiceAsync getService() {
    	return service;
    }
    
    public static AndroidEditor instance() {
        if (inst == null) {
            inst = new AndroidEditor();
        }
        
        return inst;
    }
    

    public void generateXML() {
    	String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
    	xml += generateWidget(layout);
		lastXMLFile.setContent(xml);
    	if (currFile.getType() == File.XML) {
    		code.setText(xml);
    	}
    }
    
    @SuppressWarnings("unchecked")
	private String generateWidget(Widget w) {
    	String xml = "";
		xml += "<"+w.getTagName();
		Vector<Property> props = (Vector<Property>)w.getProperties().clone();
		if (w != layout)
			w.getParentLayout().addOutputProperties(w, props);
		for (Property prop : props) {
			if (prop.getValue() != null && prop.getValue().toString().length() > 0 && !prop.isDefault()) {
				// Work around an android bug... *sigh*
				if (w instanceof CheckBox && prop.getAttributeName().equals("android:padding"))
					continue;
				String value;
				if (prop instanceof StringProperty) {
					Document d = XMLParser.createDocument();
					Text textNode= d.createTextNode(((StringProperty)prop).getRawStringValue());
					value = textNode.toString();
				} else {
					value = prop.getValue().toString();
				}
				xml += "\n";
				xml += "\t" + prop.getAttributeName()+"=\""+ value +"\"";
			}
		}
		if (w instanceof Layout) {
			xml += ">\n";
			for (Widget wt : ((Layout)w).getWidgets()) {
				xml += generateWidget(wt);
			}
			xml += "</"+w.getTagName()+">\n";
		} else {
			xml += " />\n";
		}
		return xml;
	}
}