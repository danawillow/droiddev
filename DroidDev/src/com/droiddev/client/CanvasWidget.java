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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
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
	
	public CanvasWidget(Canvas canvas, com.droiddev.client.widget.Widget widget) {
		this.canvas = canvas;
		this.widget = widget;
		initWidget(this.canvas);
	}
	
	public void addRightClickHandler() {
		/*
		canvas.addDomHandler(new ContextMenuHandler() {
    		@Override
    		public void onContextMenu(ContextMenuEvent event) {
    			event.preventDefault();
    			event.stopPropagation();
    		}
    	}, ContextMenuEvent.getType());*/
		
		canvas.addDomHandler(new MouseDownHandler() {
    		@Override
    		public void onMouseDown(MouseDownEvent event) {
    			if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
        			event.preventDefault();
        			event.stopPropagation();
        			menu.setPopupPosition(event.getClientX(), event.getClientY());
        			menu.show();
    			}
    			else {
    				AndroidEditor.instance().setSelected(CanvasWidget.this);
    			}
    		}
    	}, MouseDownEvent.getType());
		
		menu = new PopupPanel(true);
		createPopupMenu();
	}
	
	private void createPopupMenu() {
		MenuBar popupMenuBar = new MenuBar(true);
		
		popupMenuBar.addItem("Add to code", new Command() {
			public void execute() {
				/*
				if (!AndroidEditor.instance().imports.contains("android.widget." + widget.getTagName())) {
					AndroidEditor.instance().imports.add("android.widget." + widget.getTagName());
					for (File f: AndroidEditor.instance().files)
						if (f.getType() == File.JAVA)
							((JavaFile)f).addImport("android.widget." + widget.getTagName());
					//AndroidEditor.instance().code.addImport("android.widget." + widget.getTagName());
				}
				AndroidEditor.instance().code.setFindViewLine(widget.getId().split("/")[1], widget.getTagName());
				*/
				for (File f: AndroidEditor.instance().files)
					if (f.getType() == File.JAVA)
						((JavaFile)f).addWidgetToCode(widget.getTagName(), widget.getId().split("/")[1]);
				menu.hide();
			}
		});
		
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
						if (menuImport != null && !AndroidEditor.instance().imports.contains(menuImport)) {
							AndroidEditor.instance().imports.add(menuImport);
							//AndroidEditor.instance().code.addImport(menuImport);
						}
						AndroidEditor.instance().code.setMethodLine(widget.getId().split("/")[1], fn);
						menu.hide();
					}
				});

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
		                	// TODO: put this in its own method, it's the same as pressing apply
		                	for (Property prop: propVals.keySet()) {
		    					String val = propVals.get(prop).getText();
		    					widget.setPropertyByAttName(prop.getAttributeName(), val);
		    				}
		    				widget.apply();
		    				AndroidEditor.instance().getLayout().paint();
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
				for (Property prop: propVals.keySet()) {
					String val = propVals.get(prop).getText();
					widget.setPropertyByAttName(prop.getAttributeName(), val);
				}
				widget.apply();
				AndroidEditor.instance().getLayout().paint();
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
}
