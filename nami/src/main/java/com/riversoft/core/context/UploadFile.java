package com.riversoft.core.context;

public class UploadFile {

	/**
	 * 文件名
	 */
	private String name;

	/**
	 * 字节流
	 */
	private byte[] value;

	public UploadFile(String name, byte[] value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}
}
