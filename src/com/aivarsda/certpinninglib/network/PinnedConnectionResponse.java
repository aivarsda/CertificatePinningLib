package com.aivarsda.certpinninglib.network;

public class PinnedConnectionResponse 
{
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
}
