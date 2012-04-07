package com.droiddev.client;

import java.util.HashMap;

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
    
    public static AndroidEditor instance() {
        if (inst == null) {
            inst = new AndroidEditor();
        }
        
        return inst;
    }
}