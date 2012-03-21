package com.droiddev.client.widget;

import com.droiddev.client.property.Property;

import java.util.Vector;

public interface Layout extends Widget {
    public void addWidget(Widget w);
    public Vector<Widget> getWidgets();
    public void removeWidget(Widget w);
    public void positionWidget(Widget w);
    public void repositionAllWidgets();
    public void addOutputProperties(Widget w, Vector<Property> properties);
    public void addEditableProperties(Widget w);
    public void removeEditableProperties(Widget w);
    public void removeAllWidgets();
    public boolean containsWidget(Widget w);
    public int getScreenX();
    public int getScreenY();

    public void resizeForRendering();
    public void clearRendering();
}