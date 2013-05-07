/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.stations;

import java.io.File;
import java.util.ArrayList;

import jsystem.extensions.analyzers.text.FindText;
import jsystem.extensions.analyzers.text.TextNotFound;

public abstract class SharedFunctionalityStationTest extends StationTest {


	/**
	 */
	public void _testChangeIp() throws Exception {
		station.setStaticIp("TestLan_no", "10.0.2.112", "255.255.255.0");

	}

	/**
	 * It is difficult to verify that adapter was restarting. I'm assuming that
	 * if an exception was not thrown, every thing is Okay.
	 */
	public void _testResetAdapter() throws Exception {
		station.resetAdapter("PRO/1000 PT Desktop Adapter");
	}

	public void testCreateDirectory() throws Exception {
		station.createDirectory("aquatest");
	}

	public void testDeleteDirectory() throws Exception {
		station.createDirectory("aquatest");
		station.deleteDirectory("aquatest");
		station.deleteDirectory("aquatest");
	}

	public void testDeleteFile() throws Exception {
		File f = new File("aquatest/test.class");
		station.createDirectory("aquatest");
		station.copyFileFromLocalMachineToRemoteMachine(getClass()
				.getResourceAsStream("/com/aqua/stations/Station.class"), f);
		station.deleteFile(f.getPath());
		station.deleteFile(f.getPath());
	}

	public void testCopyFileFromLocalMachineToRemoteMachine() throws Exception {
		File f2 = new File("testi.pka");
		f2.delete();
		assertTrue(!f2.exists());
		File f = new File("aquatest/test.class");
		station.createDirectory("aquatest");
		station.copyFileFromLocalMachineToRemoteMachine(getClass()
				.getResourceAsStream("/com/aqua/stations/Station.class"), f);
		station.copyFileFromRemoteMachineToLocalMachine(f, f2);
		station.close();
		assertTrue(f2.exists());
		Thread.sleep(5000);
	}

	public void testGetInterfacesList() throws Exception {
		ArrayList<String> v1 = new ArrayList<String>();
		v1 = station.getInterfacesList();
		report.report(v1.toString());
	}

	public void testGetIps() throws Exception {
		String interfaceName = stationTestData.getInterfaceName();
		IpStructure expectedIp = new IpStructure(station.getCliSession(false).conn.cli.getHost(), "255.255.255.0");
		ArrayList<IpStructure> v1 = station.getIps(interfaceName);
		assertTrue(v1.size() > 0);
		for (IpStructure struct:v1){
			if (struct.equals(expectedIp)){
				return;
			}
		}
		throw new Exception("Did not find expected ip in ip list");
	}
	
	public void testAddToRoutingTablePersistent() throws Exception {
		station.deleteFromRoutingTable(stationTestData.getNewRoute(), "255.255.255.255","");
		station.addToRoutingTable(stationTestData.getNewRoute(), "255.255.255.255",	stationTestData.getNewRouteGW(),true);

	}

	public void testAddToRoutingTableTwice1() throws Exception {
		station.deleteFromRoutingTable(stationTestData.getNewRoute(), "255.255.255.255","");
		station.addToRoutingTable(stationTestData.getNewRoute(), "255.255.255.255",	stationTestData.getNewRouteGW(),true);
		station.addToRoutingTable(stationTestData.getNewRoute(), "255.255.255.255",	stationTestData.getNewRouteGW(),false);
	}

	public void testAddToRoutingTableNotPersistent() throws Exception {
		station.deleteFromRoutingTable(stationTestData.getNewRoute(), "255.255.255.255","");
		station.addToRoutingTable(stationTestData.getNewRoute(), "255.255.255.255",	stationTestData.getNewRouteGW(),false);
	}

	public void testAddToRoutingTablePersistentTwice() throws Exception {
		station.deleteFromRoutingTable(stationTestData.getNewRoute(), "255.255.255.255","");
		station.addToRoutingTable(stationTestData.getNewRoute(), "255.255.255.255",	stationTestData.getNewRouteGW(),true);
		station.addToRoutingTable(stationTestData.getNewRoute(), "255.255.255.255",	stationTestData.getNewRouteGW(),true);
	}

	public void testDeleteFromRoutingTable() throws Exception {
		station.addToRoutingTable(stationTestData.getNewRoute(), "255.255.255.255",	stationTestData.getNewRouteGW(),true);
		station.deleteFromRoutingTable(stationTestData.getNewRoute(), "255.255.255.255","");

		station.addToRoutingTable(stationTestData.getNewRoute(), "255.255.255.255",	stationTestData.getNewRouteGW(),false);
		station.deleteFromRoutingTable(stationTestData.getNewRoute(), "255.255.255.255","");

		station.addToRoutingTable(stationTestData.getNewRoute(), "255.255.255.255",	stationTestData.getNewRouteGW(),false);
		station.deleteFromRoutingTable(stationTestData.getNewRoute(), "255.255.255.255","");
		station.deleteFromRoutingTable(stationTestData.getNewRoute(), "255.255.255.255","");

		station.addToRoutingTable(stationTestData.getNewRoute(), "255.255.255.255",	stationTestData.getNewRouteGW(),true);
		station.deleteFromRoutingTable(stationTestData.getNewRoute(), "255.255.255.255","");
		station.deleteFromRoutingTable(stationTestData.getNewRoute(), "255.255.255.255","");
	}

	
	public void testGetIpsWithDummyInterfaceName() {
		String  interfaceName = "dummyName";
		try {
			station.getIps(interfaceName);
			report.report("Error: intreface " + interfaceName + " does not exist", false);
		} catch (Exception e) {
			report.step(e.toString());
			report.report("It is OK. intreface " + interfaceName + " does not exist");
		}
	}

	public void testPing() throws Exception {
		station.ping("google.com");
	}

	public void testAddToRoutingTable() throws Exception {
		station.deleteFromRoutingTable(stationTestData.getNewRoute(), "255.255.255.255","");
		station.addToRoutingTable(stationTestData.getNewRoute(), "255.255.255.255",	stationTestData.getNewRouteGW(),true);
		station.deleteFromRoutingTable(stationTestData.getNewRoute(), "255.255.255.255","");
		station.addToRoutingTable(stationTestData.getNewRoute(), "255.255.255.255",	stationTestData.getNewRouteGW(),false);
		station.deleteFromRoutingTable(stationTestData.getNewRoute(), "255.255.255.255","");
		station.addToRoutingTable(stationTestData.getNewRoute(), "255.255.255.255",	stationTestData.getNewRouteGW(),true);
		station.addToRoutingTable(stationTestData.getNewRoute(), "255.255.255.255",	stationTestData.getNewRouteGW(),true);
		try {
			station.deleteFromRoutingTable(stationTestData.getNewRoute(), "255.255.255.255","");
			station.addToRoutingTable(stationTestData.getNewRoute(), "255.255.255.255",	stationTestData.getNewRouteGW(),true);
			station.addToRoutingTable(stationTestData.getNewRoute(), "255.255.255.255",	stationTestData.getNewRouteGW(),false);
		} catch (Exception e) {
			report.report(e.toString());
			report.report("It is OK. ");
		}
	}

	public void testAddToHostsFile() throws Exception {
		String[] host = new String[] { "127.0.0.1", "myhost" };
		station.deleteFromHostsFile(host[0], host[1]);
		station.close();
		station.addToHostsFile(host[0], host[1]);
		station.close();
		try {
			station.getCliSession(false).cliCommand("type %SystemRoot%\\system32\\drivers\\etc\\hosts");
			station.getCliSession(false).getTestAgainstObject().toString();
			station.getCliSession(false).analyze(new FindText(host[0] + " " + host[1]));
			station.ping("myhost");
		}finally {
			station.deleteFromHostsFile(host[0], host[1]);
			station.close();
		}
	}

	public void testDeleteFromHostsFile() throws Exception {
		String[] host = new String[] { "123.456.789.012", "www.walla.co.il" };
		report.step("delete host from hosts file:" + host[0] + " " + host[1]);
		station.deleteFromHostsFile(host[0], host[1]);
		station.getCliSession(false).cliCommand("type %SystemRoot%\\system32\\drivers\\etc\\hosts");
		station.getCliSession(false).analyze(new TextNotFound(host[0] + " " + host[1]));
	}
}
