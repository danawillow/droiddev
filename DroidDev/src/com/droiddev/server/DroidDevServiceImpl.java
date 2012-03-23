package com.droiddev.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

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

	@Override
	public String build() {
		Runtime r = Runtime.getRuntime();
		try {
			Process p = r.exec("ant debug install", null, new File("HelloAndroid"));
			String s = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				s += line + "<br>";
			}
			reader.close();
			return s;
		} catch (IOException e) {
			e.printStackTrace();
			return "Error";
		}
	}
}
