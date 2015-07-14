package org.wso2.appserver.samples;

import java.sql.Timestamp;

public class ToolPurchase {
	private int tenantID;
	private String toolID;
	private Timestamp time;
	
	public int getTenantID() {
		return tenantID;
	}
	public void setTenantID(int tenantID) {
		this.tenantID = tenantID;
	}
	public String getToolID() {
		return toolID;
	}
	public void setToolID(String toolID) {
		this.toolID = toolID;
	}
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}
}
