/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.stations.linux;

import com.aqua.stations.StationTest;
import com.aqua.sysobj.conn.CliCommand;

public class deleteFromRoutingTable extends StationTest{

	public void testDeleteFromRoutingTable() throws Exception {
		station.deleteFromRoutingTable(stationTestData.getNewRoute(),"255.255.255.255",stationTestData.getNewRouteGW(),true);
		station.addToRoutingTable(stationTestData.getNewRoute(),"255.255.255.255",stationTestData.getNewRouteGW(),false);
		station.deleteFromRoutingTable(stationTestData.getNewRoute(),"255.255.255.255",stationTestData.getNewRouteGW(),false);
		CliCommand command = new CliCommand();
		command.setCommands(new String[]{"route"});
		command.addErrors(stationTestData.getNewRoute());
		station.getCliSession(false).handleCliCommand("printing route file", command);
		
		command.setCommands(new String[]{"cat /etc/rc.local"});
		command.addErrors("route add -net "+ stationTestData.getNewRoute() + " netmask " + "255.255.255.255" + " gw " + stationTestData.getNewRouteGW());
		station.getCliSession(false).handleCliCommand("printing route file", command);
	}
	
	public void testDeleteFromRoutingTable1() throws Exception {
		station.addToRoutingTable(stationTestData.getNewRoute(),"255.255.255.255",stationTestData.getNewRouteGW(),true);
		station.deleteFromRoutingTable(stationTestData.getNewRoute(),"255.255.255.255",stationTestData.getNewRouteGW(),false);
		CliCommand command = new CliCommand();
		command.setCommands(new String[]{"route"});
		command.addErrors(stationTestData.getNewRoute());
		station.getCliSession(false).handleCliCommand("printing route file", command);
		command = new CliCommand();
		command.setCommands(new String[]{"cat /etc/rc.local"});
		command.addMusts(new String[]{"route add -net "+ stationTestData.getNewRoute() + " netmask " + "255.255.255.255" + " gw " + stationTestData.getNewRouteGW()});
		station.getCliSession(false).handleCliCommand("printing route file", command);
	}
	
	public void testDeleteFromRoutingTable2() throws Exception {
		station.addToRoutingTable(stationTestData.getNewRoute(),"255.255.255.255",stationTestData.getNewRouteGW(),true);
		station.deleteFromRoutingTable(stationTestData.getNewRoute(),"255.255.255.255",stationTestData.getNewRouteGW(),true);
		CliCommand command = new CliCommand();
		command.setCommands(new String[]{"route"});
		command.addErrors(stationTestData.getNewRoute() +"  "+ stationTestData.getNewRouteGW());
		station.getCliSession(false).handleCliCommand("printing route file", command);
		
		command.setCommands(new String[]{"cat /etc/rc.local"});
		command.addErrors("route add -net "+ stationTestData.getNewRoute() + " netmask " + "255.255.255.255" + " gw " + stationTestData.getNewRouteGW());
		station.getCliSession(false).handleCliCommand("printing route file", command);
	}
}
