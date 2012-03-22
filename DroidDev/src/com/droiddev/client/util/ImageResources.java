package com.droiddev.client.util;

import java.util.HashMap;

import com.droiddev.client.AndroidEditor;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class ImageResources {

	private String[] imageList = {"btn_check_off.png","btn_check_on.png", "btn_default_normal.9.png", "btn_radio_off.png",
			"btn_radio_on.png", "textfield_default.9.png"};
	private HashMap<String, Image> images = new HashMap<String, Image>();
	private int numLoaded;
	
	private static ImageResources inst;

	public ImageResources() {
		for (String s: imageList) {
			Image img = new Image("images/" + s);
			RootPanel.get().add(img);
			img.setVisible(false);
			images.put(s, img);

			img.addLoadHandler(new LoadHandler() {
				public void onLoad(LoadEvent event) {
					numLoaded++;
					//Window.alert("Loaded " + numLoaded + "/" + imageList.length + " images");
					if (numLoaded == imageList.length) {
						AndroidEditor.EVENT_BUS.fireEvent(new ImagesReadyEvent());
					}
				}
			});
		}
	}
	
	public static ImageResources instance() {
		if (inst == null)
			inst = new ImageResources();
		return inst;
	}
	
	public Image get(String s) {
		return images.get(s);
	}
}
