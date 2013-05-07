/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.stations;

import jsystem.framework.system.SystemObjectImpl;

public class StationTestData extends SystemObjectImpl {

	private String interfaceName;
	private String newRoute;
	private String newRouteGW;
	
	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getNewRoute() {
		return newRoute;
	}

	public void setNewRoute(String newRoute) {
		this.newRoute = newRoute;
	}

	public String getNewRouteGW() {
		return newRouteGW;
	}

	public void setNewRouteGW(String newRouteGW) {
		this.newRouteGW = newRouteGW;
	}
}
