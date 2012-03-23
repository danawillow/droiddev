package com.droiddev.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DroidDevServiceAsync {

	void saveFile(String fileName, String contents, AsyncCallback<Void> callback);

	void build(AsyncCallback<String> callback);

}
