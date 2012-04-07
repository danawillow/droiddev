package com.droiddev.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

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
		canvas.addDomHandler(new ContextMenuHandler() {
    		@Override
    		public void onContextMenu(ContextMenuEvent event) {
    			event.preventDefault();
    			event.stopPropagation();
    			menu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
    			menu.show();
    		}
    	}, ContextMenuEvent.getType());
		
		/*
		canvas.addDomHandler(new MouseDownHandler() {
    		@Override
    		public void onMouseDown(MouseDownEvent event) {
    			if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
    				GWT.log("Right clicked on " + widget.getTagName());
    				menu.setPopupPosition(event.getClientX(), event.getClientY());
    				menu.show();
    			}
    		}
    	}, MouseDownEvent.getType());
    	*/
		
		menu = new PopupPanel(true);
		createPopupMenu();
	}
	
	private void createPopupMenu() {
		MenuBar popupMenuBar = new MenuBar(true);
		MenuItem alertItem = new MenuItem("Show alert", true, new Command() {
			public void execute() {
				Window.alert(widget.getTagName());
				menu.hide();
			}
		});

		popupMenuBar.addItem(alertItem);

		popupMenuBar.setVisible(true);
		menu.add(popupMenuBar);
	}
}
