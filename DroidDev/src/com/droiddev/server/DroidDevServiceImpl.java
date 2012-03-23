package com.droiddev.server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.droiddev.client.DroidDevService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DroidDevServiceImpl extends RemoteServiceServlet implements DroidDevService {
	@Override
	public void saveFile(String fileName, String contents) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(contents);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
