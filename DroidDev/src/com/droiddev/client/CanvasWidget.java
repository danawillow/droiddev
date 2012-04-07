package com.droiddev.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

public class CanvasWidget extends Composite{
	public Canvas canvas;
	public com.droiddev.client.widget.Widget widget;
	
	private PopupPanel menu;
	private boolean menuExists;
	
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
        			if (menuExists) {
        				menu.setPopupPosition(event.getClientX(), event.getClientY());
        				menu.show();
        			}
    			}
    		}
    	}, MouseDownEvent.getType());
		
		menu = new PopupPanel(true);
		menuExists = createPopupMenu();
	}
	
	private boolean createPopupMenu() {
		if (widget.getMenuItems() == null) return false;
		
		MenuBar popupMenuBar = new MenuBar(true);
		String[] menuItems = widget.getMenuItems();
		String[] menuFunctions = widget.getMenuFunctions();
		for (int i = 0; i < menuItems.length; i++) {
			final String fn = menuFunctions[i];
			MenuItem item = new MenuItem(menuItems[i], true, new Command() {
				public void execute() {
					//AndroidEditor.instance().code.setLine(widget.getId().split("/")[1], widget.getTagName());
					AndroidEditor.instance().code.setMethodLine(widget.getId().split("/")[1], fn);
					//Window.alert(s);
					menu.hide();
				}
			});

			popupMenuBar.addItem(item);
		}

		popupMenuBar.setVisible(true);
		menu.add(popupMenuBar);
		return true;
	}
}
