package com.droiddev.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("droidDev")
public interface DroidDevService extends RemoteService {
	
	public void saveFile(String fileName, String contents);
}
