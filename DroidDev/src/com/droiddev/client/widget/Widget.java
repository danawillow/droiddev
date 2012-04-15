package com.droiddev.client.widget;

import java.util.Vector;

import com.droiddev.client.CanvasWidget;
import com.droiddev.client.property.Property;
import com.google.gwt.canvas.client.Canvas;

public interface Widget extends Cloneable {
    public static final int TOP = 0;
    public static final int LEFT = 1;
    public static final int BOTTOM = 2;
    public static final int RIGHT = 3;

    public int getX();
    public int getY();
    public int getWidth();
    public int getHeight();
    public void setPosition(int x, int y);
    public void setSize(int width, int height);
    public void setWidth(int width);
    public void setHeight(int height);
    public void setSizeInternal(int w, int h);
    public void move(int dx, int dy);
    public void paint();
    public void apply();
    public Vector<Property> getProperties();
    public void setPropertyByAttName(String attName, String value);
    public Property getPropertyByAttName(String attName);
    public boolean propertyHasValueByAttName(String attName, Object value);
    public void addProperty(Property p);
    public void removeProperty(Property p);

    public String getTagName();
    public String getId();
    public Layout getParentLayout();
    public void setParentLayout(Layout w);
    public int getBaseline();
    public int getPadding(int which);
    public void setPadding(int pad);
    public void setPadding(int pad, int which);
    public boolean isVisible();
    public String[] getMenuItems();
    public String[] getMenuFunctions();
    public String[] getMenuImports();
    
    public Canvas getCanvas();
    public CanvasWidget getCanvasWidget();

    public int getMargin(int which);
}