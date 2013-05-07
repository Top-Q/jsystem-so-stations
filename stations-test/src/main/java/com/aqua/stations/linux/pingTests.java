/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.stations.linux;

import com.aqua.stations.StationTest;

public class pingTests extends StationTest{

	String existIp = "google.com";
	String notExistIp = "nonexistingip.co.il";
	
	public void testPing1() throws Exception {
		station.ping(existIp);
	}
	
	public void testPing2() throws Exception {
		try {
			station.ping(notExistIp);
			report.report("Error: IP:" + notExistIp + " does not exist", false);
		} catch (Throwable e) {
			report.step(e.toString());
			report.report("It is OK. IP:" + notExistIp + " does not exist");
		}
	}
}
