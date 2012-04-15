package com.droiddev.client.file;


public class XMLFile extends File {
	private String content;

	public XMLFile(String fileName, String path) {
		super(fileName, path);
	}

	@Override
	public int getType() {
		return File.XML;
	}
	
	public void setContent(String s) {
		content = s;
	}
	
	public String getContent() {
		return content;
	}

}
