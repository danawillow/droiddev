package com.droiddev.client.widget;
import java.util.Vector;

import com.droiddev.client.AndroidEditor;
import com.droiddev.client.CanvasWidget;
import com.droiddev.client.property.Property;
import com.droiddev.client.property.SelectProperty;
import com.droiddev.client.property.StringProperty;
import com.droiddev.client.property.WidthProperty;
import com.droiddev.client.util.DisplayMetrics;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;


public abstract class AbstractWidget implements Widget {
    int x, y;
    int[] padding;

    int baseline;
    int width, height;
    private String tagName;
    Vector<Property> props;

    protected StringProperty id;
    protected static int widget_num = 0;
    Layout parent;

    WidthProperty widthProp;
    WidthProperty heightProp;
    StringProperty pad;
    StringProperty visibility;

    StringProperty marginBottom;
    StringProperty marginTop;
    StringProperty marginLeft;
    StringProperty marginRight;
    
    CanvasWidget canvas;
    
    private String[] menuItems;
    private String[] menuFunctions;
    private String[] menuImports;

    public AbstractWidget(String tagName) {
        this.setTagName(tagName);
        this.props = new Vector<Property>();
        this.id = new StringProperty("Id", "android:id", "");
        this.id.setStringValue("@+id/" + tagName + (widget_num++));
        this.widthProp = new WidthProperty("Width", "android:layout_width", "");
        this.widthProp.setStringValue("wrap_content");
        this.heightProp = new WidthProperty("Height", "android:layout_height", "");
        this.heightProp.setStringValue("wrap_content");
        this.pad = new StringProperty("Padding", "android:padding", "0dp");
        this.visibility = new SelectProperty("Visible", "android:visibility", new String[] {"visible", "invisible", "gone"}, 0);

        this.marginTop = new StringProperty("Top Margin", "android:layout_marginTop", "0dp");
        this.marginBottom = new StringProperty("Bottom Margin", "android:layout_marginBottom", "0dp");
        this.marginLeft = new StringProperty("Left Margin", "android:layout_marginLeft", "0dp");
        this.marginRight = new StringProperty("Right Margin", "android:layout_marginRight", "0dp");


        this.padding = new int[4];

        this.props.add(id);
        this.props.add(widthProp);
        this.props.add(heightProp);
        this.props.add(pad);
        this.props.add(visibility);

        this.props.add(marginTop);
        this.props.add(marginBottom);
        this.props.add(marginLeft);
        this.props.add(marginRight);

        this.baseline = 14;

        this.parent = null;
        
        this.canvas = new CanvasWidget(Canvas.createIfSupported(), this);
    }
    
    public CanvasWidget getCanvasWidget() {
    	return canvas;
    }
    
    public Canvas getCanvas() {
    	return canvas.canvas;
    }

    public Layout getParentLayout() {
        return parent;
    }

    public void setParentLayout(Layout parent) {
        this.parent = parent;
        ((AbstractWidget)parent).parentTest(this);
    }

    public void parentTest(Widget w) {
        try {
            if (w == this)
                throw new IllegalArgumentException("BAD!");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        if (getParentLayout() != null) {
            ((AbstractWidget)getParentLayout()).parentTest(w);
        }
    }

    public String getId() {
        return id.getStringValue();
    }

    public void setId(String id) {
        this.id.setStringValue(id);
    }

    public Vector<Property> getProperties() {
        return props;
    }

    public void addProperty(Property p) {
        if (!props.contains(p)) {
            props.add(p);
        }
    }

    public void removeProperty(Property p) {
        props.remove(p);
    }

    public Property getPropertyByAttName(String attName) {
        for (Property prop : props) {
            if (prop.getAttributeName().equals(attName)) {
                return prop;
            }
        }
        return null;
    }

    public boolean propertyHasValueByAttName(String attName, Object value) {
        for (Property prop : props) {
            if (prop.getAttributeName().equals(attName) && prop.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }


    public void setPropertyByAttName(String attName, String value) {
        Property p = getPropertyByAttName(attName);
        if (p != null) {
            p.setValue(value);
        }
        else {
            StringProperty prop = new StringProperty(attName, attName, "");
            prop.setValue(value);
            props.add(prop);
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        if (getCanvasWidget().getParent() != null) {
        	int actualX = x;
        	int actualY = y;
        	if (this.getParentLayout() != null) {
        		
        		if (this.getParentLayout().getCanvasWidget().getParent().equals(AndroidEditor.instance().getLayoutPanel())) {
        			actualX += AndroidEditor.instance().getLayoutPanel().getWidgetLeft(this.getParentLayout().getCanvasWidget());
            		actualY += AndroidEditor.instance().getLayoutPanel().getWidgetTop(this.getParentLayout().getCanvasWidget());
            	}
            	else if (this.getParentLayout().getCanvasWidget().getParent().getParent().equals(AndroidEditor.instance().getLayoutPanel())) {
            		actualX += AndroidEditor.instance().getLayoutPanel().getWidgetLeft(this.getParentLayout().getCanvasWidget().getParent());
            		actualY += AndroidEditor.instance().getLayoutPanel().getWidgetTop(this.getParentLayout().getCanvasWidget().getParent());
            	}
        	}
        	
        	if (getCanvasWidget().getParent().equals(AndroidEditor.instance().getLayoutPanel())) {
        		AndroidEditor.instance().getLayoutPanel().setWidgetPosition(getCanvasWidget(), actualX, actualY);
        	}
        	else if (getCanvasWidget().getParent().getParent().equals(AndroidEditor.instance().getLayoutPanel())) {
        		AndroidEditor.instance().getLayoutPanel().setWidgetPosition(getCanvasWidget().getParent(), actualX, actualY);
        	}
        	else {
        		GWT.log("Can't change position because " + getTagName() + "'s parent is not LayoutPanel");
        		GWT.log("Widget's parent is " + getCanvasWidget().getParent().toString());
        		GWT.log("Widget's parent's parent is " + getCanvasWidget().getParent().getParent().toString());
        	}
        }
    }

    public void setWidth(int width) {
    	canvas.setWidth(width + "px");
        this.widthProp.setStringValue(width + AndroidEditor.instance().getScreenUnit());
        apply();
    }

    public void setHeight(int height) {
    	canvas.setHeight(height + "px");
        this.heightProp.setStringValue(height + AndroidEditor.instance().getScreenUnit());
        apply();
    }

    public void setSize(int width, int height) {
    	canvas.setWidth(width + "px");
    	canvas.setHeight(height + "px");
        this.widthProp.setStringValue(width + AndroidEditor.instance().getScreenUnit());
        this.heightProp.setStringValue(height + AndroidEditor.instance().getScreenUnit());
        apply();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void move(int dx, int dy) {
        setPosition(this.x + dx, this.y + dy);
    }

    public String getTagName() {
        return tagName;
    }
    
    public String[] getMenuItems() {
    	return menuItems;
    }
    
    public String[] getMenuFunctions() {
    	return menuFunctions;
    }
    
    public String[] getMenuImports() {
    	return menuImports;
    }

    protected void readWidthHeight() {
        int w = DisplayMetrics.readSize(widthProp);
        int h = DisplayMetrics.readSize(heightProp);
        if (w < 0) {
            w = getWidth();
        }
        if (h < 0) {
            h = getHeight();
        }

        try {
            setPadding(DisplayMetrics.readSize(pad.getStringValue()));      
            } catch (NumberFormatException ex) {}

        if (widthProp.getStringValue().equals("wrap_content")) {
            w = getContentWidth();
        }
        if (heightProp.getStringValue().equals("wrap_content")) {
            h = getContentHeight();
        }

        if (widthProp.getStringValue().equals("fill_parent") ||
        widthProp.getStringValue().equals("match_parent")) {
            if (getParentLayout() != null) {
                StringProperty prop = (StringProperty)parent.getPropertyByAttName("android:layout_width");
                if (prop.getStringValue().equals("wrap_content"))
                    w = getContentWidth();
                else
                    w = getParentLayout().getWidth();
            }
            else {
                w = AndroidEditor.instance().getScreenX()-AndroidEditor.OFFSET_X;
            }
            w = w-getX()-padding[RIGHT];
        }
        if (heightProp.getStringValue().equals("fill_parent") ||
        heightProp.getStringValue().equals("match_parent")) {
            if (getParentLayout() != null) {
                StringProperty prop = (StringProperty)parent.getPropertyByAttName("android:layout_height");
                if (prop.getStringValue().equals("wrap_content"))
                    h = getContentHeight();
                else
                    h = getParentLayout().getHeight();
            }
            else {
                h = AndroidEditor.instance().getScreenY();
            }
            h = h-getY()-padding[BOTTOM];
        }
        setSizeInternal(w, h);
    }

    public void apply() {
        readWidthHeight();
        if (getParentLayout() == null) {
            setPosition(getPadding(LEFT)+AndroidEditor.OFFSET_X, getPadding(TOP)+AndroidEditor.OFFSET_Y);
        }
        if (widthProp.getStringValue().equals("fill_parent")) {
            x = padding[LEFT];
        }
        if (heightProp.getStringValue().equals("fill_parent") &&
            this.getParentLayout() != null) 
        {
            y = padding[TOP];
        }
    }

    public void setSizeInternal(int w, int h) {
    	canvas.setWidth(w + "px");
    	canvas.setHeight(h + "px");
        this.width = w;
        this.height = h;
    }

    public int getBaseline() {
        return baseline;
    }

    public void setPadding(int pad) {
        setPadding(pad, TOP);
        setPadding(pad, LEFT);
        setPadding(pad, BOTTOM);
        setPadding(pad, RIGHT);
    }

    public void setPadding(int pad, int which) {
        padding[which] = pad;
    }

    public int getPadding(int which) {
        return padding[which];
    }

    public int getMargin(int which) {
        switch (which) {
            case TOP:
            return DisplayMetrics.readSize(marginTop);
            case BOTTOM:
            return DisplayMetrics.readSize(marginBottom);
            case LEFT:
            return DisplayMetrics.readSize(marginLeft);
            case RIGHT:
            return DisplayMetrics.readSize(marginRight);
            default:
            return 0;
        }
    }

    public boolean isVisible() {
        return visibility.getStringValue().equals("visible");
    }

    protected abstract int getContentWidth();
    protected abstract int getContentHeight();

    /**
    * @param tagName the tagName to set
    */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
    
    public void setMenuItems(String[] menuItems) {
    	this.menuItems = menuItems;
    }
    
    public void setMenuFunctions(String[] menuFunctions) {
    	this.menuFunctions = menuFunctions;
    }
    
    public void setMenuImports(String[] menuImports) {
    	this.menuImports = menuImports;
    }
}