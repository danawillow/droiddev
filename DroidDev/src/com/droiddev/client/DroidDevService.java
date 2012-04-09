package com.droiddev.client;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("droidDev")
public interface DroidDevService extends RemoteService {
	
	public void saveFile(HashMap<String, String> fileContents);
	
	public String build();
	
	public void deleteFile(String fileName);
}
