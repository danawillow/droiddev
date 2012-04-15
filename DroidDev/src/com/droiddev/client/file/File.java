package com.droiddev.client.file;

public abstract class File {
	public static final int XML = 0;
	public static final int JAVA = 1;
	public static final int FOLDER = 2;
	public static final int OTHER = 3;
	
	private String fileName;
	private String path;
	
	public File(String fileName, String path) {
		this.fileName = fileName;
		this.path = path;
	}
	
	public abstract int getType();
	
	public String getFileName() {
		return fileName;
	}
	
	public String getPath() {
		return path;
	}
}
