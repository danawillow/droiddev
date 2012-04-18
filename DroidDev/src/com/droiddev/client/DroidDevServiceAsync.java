package com.droiddev.client;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DroidDevServiceAsync {

	void saveFile(HashMap<String, String> fileContents,
			AsyncCallback<Void> callback);

	void build(AsyncCallback<String> callback);

	void deleteFile(String fileName, AsyncCallback<Void> callback);

	void pressKey(String keyCode, AsyncCallback<Void> callback);

	void startVMAndADB(AsyncCallback<Void> callback);

	void closeVMAndADB(AsyncCallback<Void> callback);

}
