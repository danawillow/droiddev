package com.droiddev.client.file;

public class OtherFile extends File {

	public OtherFile(String fileName, String path) {
		super(fileName, path);
	}

	@Override
	public int getType() {
		return File.OTHER;
	}

}
