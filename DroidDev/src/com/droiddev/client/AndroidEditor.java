package com.droiddev.client;

import java.util.HashMap;

public class AndroidEditor {
    public static int OFFSET_X = 0;
    public static int OFFSET_Y = 0;
    HashMap<String, String> strings = new HashMap<String, String>();
    private static AndroidEditor inst;

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
    
    public static AndroidEditor instance() {
        if (inst == null) {
            inst = new AndroidEditor();
        }
        
        return inst;
    }
}