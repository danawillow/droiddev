package com.droiddev.client.file;

public class Folder extends File {

	public Folder(String fileName, String path) {
		super(fileName, path);
	}

	@Override
	public int getType() {
		return File.FOLDER;
	}

}
