/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.stations;

import com.aqua.stations.Station;
import junit.framework.SystemTestCase;

public abstract class StationTest extends SystemTestCase {

	protected Station station;
	protected StationTestData stationTestData;

	public void setUp() throws Exception {
		super.setUp();
		station = (Station) system.getSystemObject("station");		
		stationTestData = (StationTestData)system.getSystemObject("station_test_data");
	}

	public void tearDown() throws Exception {
		if (station != null) {
			station.close();
		}
		super.tearDown();
	}

}
