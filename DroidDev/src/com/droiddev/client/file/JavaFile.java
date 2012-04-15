package com.droiddev.client.file;


public class JavaFile extends File {
	private String content;
	
	public JavaFile(String fileName, String path) {
		super(fileName, path);
	}

	@Override
	public int getType() {
		return File.JAVA;
	}
	
	public void setContent(String s) {
		content = s;
	}
	
	public String getContent() {
		return content;
	}
}
