package com.aivarsda.certpinninglib.network;

public class PinnedConnectionResponse 
{
	private int responseCode;
	private String responseMessage;
	private boolean isConTrusted;
	private String connResponse;
	
	public boolean isConTrusted() {
		return isConTrusted;
	}
	public void setConTrusted(boolean isConTrusted) {
		this.isConTrusted = isConTrusted;
	}
	public String getConnResponse() {
		return connResponse;
	}
	public void setConnResponse(String connResponse) {
		this.connResponse = connResponse;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
}
