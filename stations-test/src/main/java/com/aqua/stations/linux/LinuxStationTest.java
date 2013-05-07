/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.stations.linux;

import java.io.File;
import java.util.ArrayList;

import jsystem.extensions.analyzers.text.FindText;
import jsystem.extensions.analyzers.text.TextNotFound;

import com.aqua.stations.CliApplicationExtension;
import com.aqua.stations.SharedFunctionalityStationTest;
import com.aqua.sysobj.conn.CliApplication;
import com.aqua.sysobj.conn.CliCommand;

public class LinuxStationTest extends SharedFunctionalityStationTest {

	public void _testChangeIp() throws Exception {
		station.setStaticIp("eth0", "192.168.17.111", "255.255.255.0");
	}

	public void testPing() throws Exception {
		String existIp = "10.0.0.201";//192.118.82.140
		String notExistIp = "10.0.0.250";//192.118.82.250
		station.ping(existIp);
		try {
			station.ping(notExistIp);
			report.report("Error: IP:" + notExistIp + " does not exist", false);
		} catch (Exception e) {
			report.step(e.toString());
			report.report("It is OK. IP:" + notExistIp + " does not exist");
		}
	}

	public void testVerifyEmptyCommand() throws Exception {
		CliCommand command = new CliCommand();
		command.setCommands(new String[] { "" });
		CliApplication app = station.getCliSession(false);
		CliApplicationExtension.handleCommandAndVerifyEmptyOutput(app, "testing empty command", command);
	}

	public void testVerifyEmptyCommandFail() throws Exception {
		CliCommand command = new CliCommand();
		command.setCommands(new String[] { "gibrish" });
		CliApplication app = station.getCliSession(false);
		try {
			CliApplicationExtension.handleCommandAndVerifyEmptyOutput(app, "testing empty command", command);
			assertTrue("An exception was not thrown as expected", false);
		} catch (Throwable t) {
			//test passes
		}
	}

	public void testGetInterfacesList() throws Exception {
		ArrayList<String> expectedInterfacesList = new ArrayList<String>();
		String[] interfaces = new String[] { "eth0" };
		for (int i = 0; i < interfaces.length; i++) {
			expectedInterfacesList.add(interfaces[i]);
		}
		ArrayList<String> v1 = new ArrayList<String>();
		v1 = station.getInterfacesList();
		if (!v1.toString().equals(expectedInterfacesList.toString())) {
			report.report("Error, expected: " + expectedInterfacesList.toString() + " actual :" + v1.toString(), false);
		} else {
			report.report("The following interfaces were found: " + v1.toString());
		}

		interfaces = new String[] { "eth0,eth1" };
		expectedInterfacesList = new ArrayList<String>();
		for (int i = 0; i < interfaces.length; i++) {
			expectedInterfacesList.add(interfaces[i]);
		}
		v1 = station.getInterfacesList();
		if (v1.toString().equals(expectedInterfacesList.toString())) {
			report.report("Error, expected: " + expectedInterfacesList.toString() + " actual :" + v1.toString(), false);
		} else {
			report.report("Pass, expected: " + expectedInterfacesList.toString() + " actual :" + v1.toString(), true);
		}
	}

	public void testAddToHostsFile() throws Exception {
		String[] host = new String[] { "123.456.789.012", "www.walla.co.il" };
		station.addToHostsFile(host[0], host[1]);
		station.getCliSession(false).cliCommand("cat /etc/hosts");
		station.getCliSession(false).getTestAgainstObject().toString();
		station.getCliSession(false).analyze(new FindText(host[0] + " " + host[1]));
		station.addToHostsFile(new File("/etc/hosts"), host[0], host[1]);
		station.getCliSession(false).cliCommand("cat /etc/hosts");
		station.getCliSession(false).getTestAgainstObject().toString();
		station.getCliSession(false).analyze(new FindText(host[0] + " " + host[1]));
	}

	public void testDeleteFromHostsFile() throws Exception {
		String[] host = new String[] { "123.456.789.012", "www.walla.co.il" };
		report.step("add new host to hosts file:" + host[0] + " " + host[1]);
		station.addToHostsFile(new File("/etc/hosts"), host[0], host[1]);
		station.getCliSession(false).cliCommand("cat /etc/hosts");
		station.getCliSession(false).analyze(new FindText(host[0] + " " + host[1]));
		report.step("delete host from hosts file:" + host[0] + " " + host[1]);
		station.deleteFromHostsFile(host[0], host[1]);
		station.getCliSession(false).cliCommand("cat /etc/hosts");
		station.getCliSession(false).analyze(new TextNotFound(host[0] + " " + host[1]));
	}
}
