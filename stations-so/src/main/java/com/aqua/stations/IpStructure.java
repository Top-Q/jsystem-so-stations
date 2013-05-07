/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.stations;

/** 
 * This Class includes the ip and the net mask addresses.
 * The constructor gets a string in the following format d.d.d.d/d.d.d.d
 * (ip/mask) 
 */
public class IpStructure extends Object {

	private String ip="", mask="";

	
	public IpStructure(String ip, String mask){
		this.ip = ip;
		this.mask = mask;
	}

	public String toString() {
		return getIp() + "/" + getMask();
	}
	
	public  int hashCode(){
		return (getIp()+getMask()).hashCode();
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof IpStructure)){
			return false;
		}
		IpStructure comapreTo = (IpStructure)obj;
		return getIp().equals(comapreTo.getIp()) && getMask().equals(comapreTo.getMask());
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}
}