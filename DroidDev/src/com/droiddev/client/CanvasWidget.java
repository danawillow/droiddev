package com.droiddev.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
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
				AndroidEditor.instance().code.setFindViewLine(widget.getId().split("/")[1], widget.getTagName());
				menu.hide();
			}
		});
		
		popupMenuBar.addItem("Edit Properties", new Command() {
			public void execute() {
				menu.hide();
				final DialogBox dialog = new DialogBox();
				dialog.setHTML("Edit Properties");
				//dialog.setWidth("500px");
				//dialog.setHeight("500px");
				
				VerticalPanel propertyPanel = new VerticalPanel();
				
				/*propertyPanel.add(new HTML("Text"));
				propertyPanel.add(new TextBox());*/
				propertyPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
				
				Grid grid = new Grid(1, 2);
				grid.setText(0, 0, "Text");
				final TextBox text = new TextBox();
				grid.setWidget(0, 1, text);
				propertyPanel.add(grid);
				
				Button okButton = new Button("OK", new ClickHandler() {
					public void onClick(ClickEvent event) {
						if (text.getText() != "") {
							widget.setPropertyByAttName("android:text", text.getText());
							AndroidEditor.instance().getLayout().paint();
						}
						dialog.hide();
					}
				});
				
				Button closeButton = new Button("Cancel", new ClickHandler() {
					public void onClick(ClickEvent event) {
						dialog.hide();
					}
				});
				propertyPanel.add(okButton);
				propertyPanel.add(closeButton);
				propertyPanel.setWidth("500px");
				propertyPanel.setHeight("500px");
				dialog.setWidget(propertyPanel);
				dialog.center();
				dialog.show();
			}
		});
		
		if (widget.getMenuItems() != null) {
			popupMenuBar.addSeparator();
			String[] menuItems = widget.getMenuItems();
			String[] menuFunctions = widget.getMenuFunctions();
			for (int i = 0; i < menuItems.length; i++) {
				final String fn = menuFunctions[i];
				MenuItem item = new MenuItem(menuItems[i], true, new Command() {
					public void execute() {
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
}
