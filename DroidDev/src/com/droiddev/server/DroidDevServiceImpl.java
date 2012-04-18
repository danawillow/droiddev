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
			boolean printing = false;
			while ((line = reader.readLine()) != null) {
				if (line.equals("-compile:") || line.equals("BUILD SUCCESSFUL"))
					printing = true;
				else if (line.equals("-post-compile:"))
					printing = false;

				if (printing)
					s += line + "<br>";
			}
			reader.close();

			if (p.waitFor() != 0)
				return s;

			p = r.exec("/Users/Dana/Documents/android-sdk-mac_x86/platform-tools/adb install -r HelloAndroid/bin/HelloAndroid-debug.apk");
			reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = reader.readLine()) != null) {
				s += line + "<br>";
			}

			reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((line = reader.readLine()) != null)
				s += line + "<br>";

			reader.close();

			p.waitFor();
			return s;
		} catch (IOException e) {
			e.printStackTrace();
			return "Error";
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error";
		}
	}

	@Override
	public void deleteFile(String fileName) {
		File f = new File(fileName);
		f.delete();
	}

	@Override
	public void pressKey(String keyCode) {
		Runtime r = Runtime.getRuntime();
		try {
			Process p = r.exec("/usr/bin/VBoxManage controlvm Android keyboardputscancode " + keyCode);
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null)
				System.out.println(line);

			reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((line = reader.readLine()) != null)
				System.out.println(line);

			reader.close();
			p.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void startVMAndADB() {
		try {
			new ProcessBuilder("/usr/bin/VBoxHeadless", "--startvm", "Android").redirectErrorStream(true).start();
			System.out.println("launchVM");

			Process adbConnect = new ProcessBuilder("/Users/Dana/Documents/android-sdk-mac_x86/platform-tools/adb", "connect", "192.168.56.101").
					redirectErrorStream(true).start();

			System.out.println("adbConnect");

			if (adbConnect.waitFor() != 0)
				return;

			System.out.println("adb");

			new ProcessBuilder("/Users/Dana/Documents/android-sdk-mac_x86/platform-tools/adb", "shell",
					"droidvnc_x86 -k /dev/input/event1 -t /dev/input/event2").redirectErrorStream(true).start();

			System.out.println("adbVNC");

			new ProcessBuilder("/Users/Dana/Sites/IW/noVNC/utils/launch.sh", "--vnc", "192.168.56.101:5901").
			redirectErrorStream(true).start();

			System.out.println("vnc");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void closeVMAndADB() {
		try {
			new ProcessBuilder("/usr/bin/VBoxManage", "controlvm", "Android", "poweroff").redirectErrorStream(true).start();
			System.out.println("closing VM");

			new ProcessBuilder("/Users/Dana/Documents/android-sdk-mac_x86/platform-tools/adb", "disconnect", "192.168.56.101").
			redirectErrorStream(true).start();

			System.out.println("adb disconnect");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
