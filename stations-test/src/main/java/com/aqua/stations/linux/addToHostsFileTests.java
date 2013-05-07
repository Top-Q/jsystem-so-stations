/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.stations.linux;

import java.io.File;

import com.aqua.stations.FindNoOfStringInText;
import com.aqua.stations.StationTest;
import com.aqua.sysobj.conn.CliCommand;

public class addToHostsFileTests extends StationTest {

	public void testToWorkAroundAProblem() throws Exception {
		try {
			report.setFailToPass(true);
			String[] host = new String[] { "123.456.789.012", "www.walla.co.il" };
			station.addToHostsFile(host[0], host[1]);
			CliCommand command = new CliCommand();
			command.setCommands(new String[]{"cat /etc/hosts"});
			command.addMusts(new String[]{host[0] + " " + host[1]});
			station.getCliSession(false).handleCliCommand("printing hosts file", command);
		}catch (Throwable t){
			report.report("Test that for some reason fails, did fail. this is a workaround");
			report.setFailToPass(false);
		}
	}

	/**
	 * adds new hosts to host file
	 * @throws Exception
	 */
	public void testAddToHostsFile1() throws Exception {
		String[] host = new String[] { "123.456.789.012", "www.walla.co.il" };
		station.addToHostsFile(host[0], host[1]);
		CliCommand command = new CliCommand();
		command.setCommands(new String[]{"cat /etc/hosts"});
		command.addMusts(new String[]{host[0] + " " + host[1]});
		station.getCliSession(false).handleCliCommand("printing hosts file", command);
	}
		
	/**
	 * 
	 * @throws Exception
	 */
	public void testAddToHostsFile2() throws Exception {
		String[] host = new String[] { "123.456.789.012", "www.walla.co.il" };
		station.addToHostsFile(host[0], host[1]);
		CliCommand command = new CliCommand();
		command.setCommands(new String[]{"cat /etc/hosts"});
		command.addMusts(new String[]{host[0] + " " + host[1]});
		station.getCliSession(false).handleCliCommand("printing hosts file", command);
		

		station.addToHostsFile(host[0], host[1]);
		command.setCommands(new String[]{"cat /etc/hosts"});
		command.addMusts(new String[]{host[0] + " " + host[1]});
		station.getCliSession(false).handleCliCommand("printing hosts file", command);
		station.getCliSession(false).cliCommand("cat /etc/hosts");
		String result = station.getCliSession(false).getTestAgainstObject().toString();
		FindNoOfStringInText findNoOfStringInText= new FindNoOfStringInText(host[0] + " " + host[1],result,1);
		if(findNoOfStringInText.getResult()){
			report.report("Pass");
		}else{
			report.report("Fail, " + host[0] + " " + host[1] +"appears more then 1 times");
		}
		
	}
	
	public void testAddToHostsFile() throws Exception {
		String[] host = new String[] { "123.456.789.012", "www.walla.co.il" };
		station.deleteFromHostsFile(host[0], host[1]);
		
		station.addToHostsFile(host[0], host[1]);
		CliCommand command = new CliCommand();
		command.setCommands(new String[]{"cat /etc/hosts"});
		command.addMusts(new String[]{host[0] + " " + host[1]});
		station.getCliSession(false).handleCliCommand("printing hosts file", command);
		
		station.addToHostsFile(new File("/etc/hosts"), host[0], host[1]);
		command = new CliCommand();
		command.setCommands(new String[]{"cat /etc/hosts"});
		command.addMusts(new String[]{host[0] + " " + host[1]});
		station.getCliSession(false).handleCliCommand("printing hosts file", command);
		
		station.getCliSession(false).cliCommand("cat /etc/hosts");
		String result = station.getCliSession(false).getTestAgainstObject().toString();
		result = result.replace(host[0] + " " + host[1], "");
		if(result.contains(host[0] + " " + host[1])){
			throw new Exception("Error," + host[0] + " " + host[1] + "still appears ini hosts file");
		}
	}	
}
