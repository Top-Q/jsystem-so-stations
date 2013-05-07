/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.stations.linux;

import com.aqua.stations.StationTest;
import com.aqua.sysobj.conn.CliCommand;

public class AddToRoutingTable extends StationTest{

	public void testAddToRoutingTable() throws Exception {
		station.addToRoutingTable(stationTestData.getNewRoute(),"255.255.255.255",stationTestData.getNewRouteGW(),false);
		CliCommand command = new CliCommand();
		command.setCommands(new String[]{"route"});
		command.addMusts(new String[]{stationTestData.getNewRoute()});
		station.getCliSession(false).handleCliCommand("printing route file", command);
	}
	
	public void testAddToRoutingTable1() throws Exception {
		station.addToRoutingTable(stationTestData.getNewRoute(),"255.255.255.255",stationTestData.getNewRouteGW(),true);
		CliCommand command = new CliCommand();
		command.setCommands(new String[]{"route"});
		command.addMusts(new String[]{stationTestData.getNewRoute()});
		station.getCliSession(false).handleCliCommand("printing route file", command);
	}
	
	public void testAddToRoutingTable2() throws Exception {
		station.addToRoutingTable(stationTestData.getNewRoute(),"255.255.255.255",stationTestData.getNewRouteGW(),true);
		station.addToRoutingTable(stationTestData.getNewRoute(),"255.255.255.255",stationTestData.getNewRouteGW(),true);
		CliCommand command = new CliCommand();
		command.setCommands(new String[]{"route"});
		command.addMusts(new String[]{stationTestData.getNewRoute() });
		station.getCliSession(false).handleCliCommand("printing route file", command);
	}
	public void testAddToRoutingTable3() throws Exception {
		station.addToRoutingTable(stationTestData.getNewRoute(),"255.255.255.255",stationTestData.getNewRouteGW(),false);
		station.addToRoutingTable(stationTestData.getNewRoute(),"255.255.255.255",stationTestData.getNewRouteGW(),false);
		CliCommand command = new CliCommand();
		command.setCommands(new String[]{"route"});
		command.addMusts(new String[]{stationTestData.getNewRoute()});
		station.getCliSession(false).handleCliCommand("printing route file", command);
	}
	public void testAddToRoutingTable4() throws Exception {
		station.addToRoutingTable(stationTestData.getNewRoute(),"255.255.255.255",stationTestData.getNewRouteGW(),false);
		station.addToRoutingTable(stationTestData.getNewRoute(),"255.255.255.255",stationTestData.getNewRouteGW(),true);
		CliCommand command = new CliCommand();
		command.setCommands(new String[]{"route"});
		command.addMusts(new String[]{stationTestData.getNewRoute()});
		station.getCliSession(false).handleCliCommand("printing route file", command);
	}
	public void testAddToRoutingTable5() throws Exception {
		station.addToRoutingTable(stationTestData.getNewRoute(),"255.255.255.255",stationTestData.getNewRouteGW(),true);
		station.addToRoutingTable(stationTestData.getNewRoute(),"255.255.255.255",stationTestData.getNewRouteGW(),false);
		CliCommand command = new CliCommand();
		command.setCommands(new String[]{"route"});
		command.addMusts(new String[]{stationTestData.getNewRoute()});
		station.getCliSession(false).handleCliCommand("printing route file", command);
	}
	public void testAddToRoutingTable6() throws Exception {
		station.deleteFromRoutingTable(stationTestData.getNewRoute(),"255.255.255.255",stationTestData.getNewRouteGW(),true);
		station.addToRoutingTable(stationTestData.getNewRoute(),"255.255.255.255",stationTestData.getNewRouteGW(),true);
		station.addToRoutingTable(stationTestData.getNewRoute(),"255.255.255.255",stationTestData.getNewRouteGW(),false);
		CliCommand command = new CliCommand();
		command.setCommands(new String[]{"route"});
		command.addMusts(new String[]{stationTestData.getNewRoute()});
		station.getCliSession(false).handleCliCommand("printing route file", command);
	}
	
	
}
