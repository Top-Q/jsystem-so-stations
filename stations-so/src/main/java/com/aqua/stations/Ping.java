/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.stations;

import java.io.File;

import jsystem.framework.report.ReporterHelper;
import jsystem.framework.system.SystemObjectImpl;

import org.apache.commons.io.FileUtils;

import com.aqua.sysobj.conn.CliCommand;
import com.aqua.sysobj.conn.CliConnectionImpl;
import com.aqua.sysobj.conn.LinuxDefaultCliConnection;
import com.aqua.sysobj.conn.WindowsDefaultCliConnection;

/**
 * 
 * @author Haim.Meirovich
 * 
 * Ping class represent Station(windows/Linux) that handle ping operations .
 *  
 * @param cliConnection : holds the connection to the station that going
 * 		                  to send ping commands.  				  	    
 */
public class Ping extends SystemObjectImpl{
	
	public CliConnectionImpl cliConnection;	
	private boolean pinging = false;

	public Ping() {
		super();
	}

	public Ping(CliConnectionImpl cliConnection){		
		this.cliConnection = cliConnection; 
	}
	
	/**
	 *  
	 * @param host :     The source ip address of the station that willing to send ping requests  
	 * @param protocol : ssh/telent  - connectivity protocol  
	 * @param user :     The user of the station that willing to send ping requests
	 * @param password:  The password of the station that willing to send ping requests
	 * @param coonnect : true -  will create the connection in the contructor .
	 *                   false - will create the connection in one of the functions. 
	 * @throws Exception 
	 */
	public Ping(String host, String protocol, String user, String password, String OS, boolean initConnection) throws Exception{
		if(OS.equals("linux")){
			cliConnection = new LinuxDefaultCliConnection();
		}else{
			cliConnection = new WindowsDefaultCliConnection();
		}
		
		cliConnection.setHost(host);
		cliConnection.setProtocol(protocol);
		cliConnection.setUser(user);
		cliConnection.setPassword(password);
		
		if(initConnection){
			cliConnection.setMaxIdleTime(1800000);
			cliConnection.init();			
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception{	
		super.init();
		cliConnection.setMaxIdleTime(1800000);
		cliConnection.init();		
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public boolean isConnected() throws Exception{
		return cliConnection.isConnected();
	}
	
	/**
	 * close the connection 
	 */	
	public void close(){
		cliConnection.disconnect();
	}
	
	/**
	 * Ping destination IP 4 times with buffer length = 32 without saving results to file.
	 * 
	 * @param ip			Destination IP address
	 * @param retries		Number of ping retries
	 * @return number of successful retries
	 * @throws Exception
	 */
	public int ping(String ip) throws Exception {
		return ping(ip, 4, 32, null);
	}
	
	/**
	 * Ping destination IP retries times with buffer length = 32 without saving results to file.
	 * 
	 * @param ip			Destination IP address
	 * @param retries		Number of ping retries
	 * @return number of successful retries
	 * @throws Exception
	 */
	public int ping(String ip, int retries) throws Exception {
		return ping(ip, retries, 32, null);
	}
	
	/**
	 * Ping destination IP retries times with buffer length = packetSize without saving results to file.
	 * 
	 * @param ip			Destination IP address
	 * @param retries		Number of ping retries
	 * @param packetSize	Buffer (payload) size
	 * @return number of successful retries
	 * @throws Exception
	 */
	public int ping(String ip, int retries, int packetSize) throws Exception {
		return ping(ip, retries, packetSize, null);
	}
	
	/**
	 * Ping destination IP retries times with buffer length = packetSize and save results to pathToSave.
	 * 
	 * @param ip			Destination IP address
	 * @param retries		Number of ping retries
	 * @param packetSize	Buffer (payload) size
	 * @param pathToSave	Path to output file where ping results will be saved
	 * @return number of successful retries
	 * @throws Exception
	 */
	public int ping(String ip, int retries, int packetSize, String pathToSave) throws Exception {		
		
		if (!isConnected()) {
			init();
		}

		// Build command
		String strCommand = "";
		if (cliConnection instanceof WindowsDefaultCliConnection) {
			// Build ping Windows command "ping ip -l count -n size"  
			strCommand = "ping " + ip + " -n " + retries + " -l " + packetSize;						
		} else if (cliConnection instanceof LinuxDefaultCliConnection) {
			// Build ping Linux command "ping ip -c count -s size"  
			strCommand = "ping " + ip + " -c " + retries + " -s " + packetSize;
		} else {
			throw new Exception("Unsupported CLI connection - " + cliConnection.toString());
		}

		// Perform command
		CliCommand command = new CliCommand(strCommand);
		command.setTimeout(1000000000);
		cliConnection.handleCliCommand("", command);			
		String res = cliConnection.getTestAgainstObject().toString();
		setTestAgainstObject(res);

		if (pathToSave != null) {
			FileUtils.writeStringToFile(new File(pathToSave), res);
			ReporterHelper.copyFileToReporterAndAddLink(report, new File(pathToSave), "Ping Result File = " + pathToSave);			
		}

		// Extract number of successful results using the PingAnalyzer
		PingAnalyzer pa = new PingAnalyzer(100);
		analyze(pa, true);
		return pa.getReceived();

	}	
	
	/**
	 * Infinity ping: this function commit ping until the user stops it.
	 * In windows ,such ping require the attribute "-t" ,
	 * in Linux such ping is the default ping.
	 * The ping output saved on the remote computer that execute the ping request, 
	 * the output will not appear on the command line window during the run.    
	 * Note that this command does not set the testAgainstObject so the PingAnalyzer can't be used to analyze
	 * the output.
	 * @param ip : ping target   
	 * @param filePath: path to save the output file
	 * @throws Exception
	 */
	public void constantPing(String ip,String filePath) throws Exception{
		if (!isConnected()){
			init();
		}		
		
		if (cliConnection instanceof WindowsDefaultCliConnection) {
			CliCommand command = new CliCommand();
			command.setCommands(new String[]{"ping " + ip +" -t >"+filePath});
			command.setTimeout(1000);			
			cliConnection.command(command);
			pinging = true;			
		}else if(cliConnection instanceof LinuxDefaultCliConnection){
			CliCommand command = new CliCommand();
			command.setCommands(new String[]{"ping " + ip +">"+filePath});
			command.setTimeout(1000);			
			cliConnection.command(command);
			pinging = true;
		}
	}	

	/**
	 * <b>Create to work in cooperation with the method constantPing(). </b>  
	 * Stops only the method constantPing()
	 * The method is close the connection that close the ping process 
	 * and not commit Control-C . 
	 *  
	 * @throws Exception
	 */
	public void stopConstantPing() throws Exception{		
		pinging = false;		
		close();
	}
	
	/**
	 * Check whether constantPing sending ping requests . 
	 * <b>Its not checking if the ping target replay or not ! </b> 
	 * @return
	 * @throws Exception
	 */
	public synchronized boolean isConstantPingigng() throws Exception {
		if (!pinging){
			return false;
		}
		CliCommand cmd =  new CliCommand();
		cmd.setAddEnter(false);
		cmd.setCommands(new String[]{""});
		cmd.setTimeout(1000);		
		cliConnection.command(cmd);
		pinging = cmd.isFailed(); 
		return pinging;
	}
}
