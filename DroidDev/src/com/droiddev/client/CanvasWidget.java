package com.droiddev.client;

import java.util.HashMap;
import java.util.Vector;

import com.droiddev.client.file.File;
import com.droiddev.client.file.JavaFile;
import com.droiddev.client.property.Property;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CanvasWidget extends Composite{
	public Canvas canvas;
	public com.droiddev.client.widget.Widget widget;
	
	private PopupPanel menu;
	
	public int mode;

	public static final int NORMAL = 0;
	public static final int E = 1;
	public static final int SE = 2;
	public static final int S = 3;
	
	public CanvasWidget(Canvas canvas, com.droiddev.client.widget.Widget widget) {
		this.canvas = canvas;
		this.widget = widget;
		initWidget(this.canvas);
	}
	
	public void addClickHandlers() {
		canvas.addDomHandler(new MouseDownHandler() {
    		@Override
    		public void onMouseDown(MouseDownEvent event) {
    			if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
        			event.preventDefault();
        			event.stopPropagation();
        			menu = new PopupPanel(true);
        			createPopupMenu();
        			menu.setPopupPosition(event.getClientX(), event.getClientY());
        			menu.show();
    			}
    			else {
    				AndroidEditor.instance().setSelected(CanvasWidget.this);
    			}
    		}
    	}, MouseDownEvent.getType());
		
		
		canvas.addDomHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				addToCode();
			}
		}, DoubleClickEvent.getType());
		
		canvas.addDomHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				int ex = event.getRelativeX(canvas.getElement());
				int ey = event.getRelativeY(canvas.getElement());
				
				int distance_x = canvas.getOffsetWidth()-ex;
				int distance_y = canvas.getOffsetHeight()-ey;
				
				boolean close_r = distance_x < 8 && distance_x >= 1;
				boolean close_b = distance_y < 8 && distance_y >= 1;
				
				if (close_r && close_b) {
					DOM.setStyleAttribute(getElement(), "cursor", "se-resize");
					mode = SE;
				}
				else if (close_r) {
					DOM.setStyleAttribute(getElement(), "cursor", "e-resize");
					mode = E;
				}
				else if (close_b) {
					DOM.setStyleAttribute(getElement(), "cursor", "s-resize");
					mode = S;
				}
				else {
					DOM.setStyleAttribute(getElement(), "cursor", "move");
					mode = NORMAL;
				}
			}
		}, MouseMoveEvent.getType());
	}
	
	private void addToCode() {
		/*
		for (File f: AndroidEditor.instance().files)
			if (f.getType() == File.JAVA)
			*/
		if (AndroidEditor.instance().currFile.getType() == File.JAVA)
			((JavaFile)(AndroidEditor.instance().currFile)).addWidgetToCode(widget.getTagName(), widget.getId().split("/")[1]);
	}
	
	private void createPopupMenu() {
		MenuBar popupMenuBar = new MenuBar(true);
		
		MenuItem addToCode = new MenuItem("Add to code", new Command() {
			public void execute() {
				addToCode();
				menu.hide();
			}
		});
		addToCode.setEnabled(AndroidEditor.instance().currFile.getType() == File.JAVA);
		System.out.println(AndroidEditor.instance().currFile.getType() == File.JAVA);
		popupMenuBar.addItem(addToCode);
		
		popupMenuBar.addItem("Edit Properties", new Command() {
			public void execute() {
				menu.hide();
				launchPropertiesPanel();
			}
		});
		
		if (widget.getMenuItems() != null) {
			popupMenuBar.addSeparator();
			String[] menuItems = widget.getMenuItems();
			String[] menuFunctions = widget.getMenuFunctions();
			String[] menuImports = widget.getMenuImports();
			for (int i = 0; i < menuItems.length; i++) {
				final String fn = menuFunctions[i];
				final String menuImport = menuImports[i];
				MenuItem item = new MenuItem(menuItems[i], true, new Command() {
					public void execute() {
						((JavaFile)(AndroidEditor.instance().currFile)).addMethodToCode(
								widget.getTagName(), menuImport, widget.getId().split("/")[1], fn);
						menu.hide();
					}
				});
				
				item.setEnabled(AndroidEditor.instance().currFile.getType() == File.JAVA);

				popupMenuBar.addItem(item);
			}
		}
		
		popupMenuBar.addSeparator();
		popupMenuBar.addItem("Remove widget", new Command() {
			public void execute() {
				widget.getParentLayout().removeWidget(widget);
				if (AndroidEditor.instance().selected == widget) {
					AndroidEditor.instance().selected = null;
				}
				AndroidEditor.instance().getLayoutPanel().remove(CanvasWidget.this);
				AndroidEditor.instance().getLayout().paint();
				AndroidEditor.instance().generateXML();
				
				menu.hide();
			}
		});

		popupMenuBar.setVisible(true);
		menu.add(popupMenuBar);
	}
	
	public void launchPropertiesPanel() {
		final HashMap<Property, TextBox> propVals = new HashMap<Property, TextBox>();
		Vector<Property> properties = widget.getProperties();
		
		final DialogBox dialog = new DialogBox(true) {
			@Override
		    protected void onPreviewNativeEvent(NativePreviewEvent event) {
		        super.onPreviewNativeEvent(event);
		        switch (event.getTypeInt()) {
		            case Event.ONKEYDOWN:
		                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
		                	applyProperties(propVals);
		                    hide();
		                }
		                break;
		        }
		    }
		};
		dialog.setHTML("Edit Properties");
		
		VerticalPanel propertyPanel = new VerticalPanel();
		propertyPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		Grid grid = new Grid(properties.size() + 1, 2);
		int i = 0;
		for (Property prop: properties) {
			grid.setText(i, 0, prop.getEnglishName());
			if (prop.getEditable()) {
				TextBox t = new TextBox();
				if (prop.getValue() != null)
					t.setText(prop.getValue().toString());
				grid.setWidget(i, 1, t);
				propVals.put(prop, t);
			}
			else {
				grid.setText(i, 1, prop.getValue().toString());
			}
			i++;
		}
		
		ScrollPanel sp = new ScrollPanel(grid);
		propertyPanel.add(sp);
		sp.setHeight("500px");
		sp.setWidth("500px");
		
		HorizontalPanel buttons = new HorizontalPanel();
		Button okButton = new Button("Apply", new ClickHandler() {
			public void onClick(ClickEvent event) {
				applyProperties(propVals);
				dialog.hide();
			}
		});
		
		Button closeButton = new Button("Cancel", new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialog.hide();
			}
		});
		buttons.add(okButton);
		buttons.add(closeButton);
		propertyPanel.add(buttons);
		dialog.setWidget(propertyPanel);
		dialog.center();
		dialog.show();
	}
	
	private void applyProperties(HashMap<Property, TextBox> propVals) {
		for (Property prop: propVals.keySet()) {
			String val = propVals.get(prop).getText();
			widget.setPropertyByAttName(prop.getAttributeName(), val);
		}
		widget.apply();
		AndroidEditor.instance().getLayout().paint();

		AndroidEditor.instance().generateXML();
	}
}
