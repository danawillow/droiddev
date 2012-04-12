package com.droiddev.client;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("droidDev")
public interface DroidDevService extends RemoteService {
	
	public static final String HOME_CODE = "E0 5B E0 DB";
	public static final String MENU_CODE = "E0 5D E0 DD";
	public static final String BACK_CODE = "01 81";
	
	public void saveFile(HashMap<String, String> fileContents);
	
	public String build();
	
	public void deleteFile(String fileName);
	
	public void pressKey(String keyCode);
}
