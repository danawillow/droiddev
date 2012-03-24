package com.droiddev.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.droiddev.client.DroidDevService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DroidDevServiceImpl extends RemoteServiceServlet implements DroidDevService {
	@Override
	public void saveFile(HashMap<String, String> fileContents) {
		BufferedWriter writer;
		for (String fileName: fileContents.keySet()) {
			try {
				writer = new BufferedWriter(new FileWriter(fileName));
				writer.write(fileContents.get(fileName));
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String build() {
		Runtime r = Runtime.getRuntime();
		try {
			Process p = r.exec("ant debug", null, new File("HelloAndroid"));
			String s = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				s += line + "<br>";
			}
			reader.close();
			
			if (p.exitValue() != 0)
				return s;
			
			p = r.exec("/Users/Dana/Documents/android-sdk-mac_x86/platform-tools/adb -r install HelloAndroid/HelloAndroid-debug.apk");
			reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
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
