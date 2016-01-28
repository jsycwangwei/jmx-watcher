package com.focustech.jmx.exceptions;

public class SampleException {
	
	private String msg;
	private String appName;
	private String host;
	
	public SampleException(String appName, String host, String msg){
		this.appName = appName;
		this.host = host;
		this.msg = msg;
	}
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		return result.append("App:").append(appName).append(";Host:").append(host).append("[").append(msg).append("]").toString();
	}
	
}
