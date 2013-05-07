/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.stations.linux;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.extensions.analyzers.text.FindText;
import jsystem.extensions.analyzers.text.TextNotFound;
import jsystem.utils.FileUtils;
import systemobject.terminal.Prompt;

import com.aqua.stations.CliApplicationExtension;
import com.aqua.stations.IpStructure;
import com.aqua.stations.StationDefaultImpl;
import com.aqua.stations.StationsCliCommand;

public class LinuxStation extends StationDefaultImpl {
	
	private static final String ADD_TO_ROUTING_TABLE = "route add -net {0} netmask {1} gw {2} ";
	
	private static final String DELETE_FROM_ROUTING_TABLE = "route del -net {0} netmask {1}  ";
	
	private static final String SET_STATIC_IP =	"ifconfig {0} {1} netmask {2}";
	
	private static final String RESTART_ADAPTER = "ifconfig {0} down; ifconfig {0} up ";

    public LinuxStation() {
    	super(null, null, null, null);
    }

    public LinuxStation(String host, String protocol, String user, String password,Prompt[] additionalPrompts) {
    	super(host, protocol, user, password,additionalPrompts);
    }

    public LinuxStation(String host, String protocol, String user, String password) {
    	super(host, protocol, user, password);
    }

    @Override
    protected String getOperatingSystem() {
    	return "linux";
    }

    public void deleteDirectory(String directoryName) throws Exception {
    	internalDeleteFile(directoryName);
    }

    public void deleteFile(String fileName) throws Exception {
    	internalDeleteFile(fileName);
    }

	public void resetAdapter(String adapterPhisicalName) throws Exception {
		resetAdapter(adapterPhisicalName, false);
	}

	public void resetAdapter(String adapterPhisicalName, boolean machineIP) throws Exception {
		initCli();
		try {
			getCliApplication().cliCommand("");
			String command = MessageFormat.format(RESTART_ADAPTER,adapterPhisicalName);
			StationsCliCommand cmd = new StationsCliCommand(command);
			CliApplicationExtension.handleCommandAndVerifyEmptyOutput(getCliApplication(),"reset adapter", cmd );
		}finally {
			if(!machineIP){
				getCliApplication().conn.cli.close();
			}
		}
	}

	public void setStaticIp(String interfaceName, String ip, String subnetMask) throws Exception {
		setStaticIp(interfaceName, ip, subnetMask, false);
	}
	
	public void setStaticIp(String interfaceName, String ip, String subnetMask, boolean machineIP) throws Exception {
		initCli();
		try{
			getCliApplication().cliCommand("");
			String command = MessageFormat.format(SET_STATIC_IP,interfaceName,ip,subnetMask);
			StationsCliCommand cmd = new StationsCliCommand(command);
			CliApplicationExtension.handleCommandAndVerifyEmptyOutput(getCliApplication(),"Setting static IP", cmd );
		}finally{
			if(!machineIP){
				getCliApplication().conn.cli.close();
			}
		}
	}

    private void internalDeleteFile(String fileName) throws Exception {
    	initCli();
    	fileName = FileUtils.replaceSeparator(fileName);
    	getCliApplication().cliCommand("");
    	String command = "rm -f -r " + fileName;
		StationsCliCommand cmd = new StationsCliCommand(command);
		CliApplicationExtension.handleCommandAndVerifyEmptyOutput(getCliApplication(),"internal delete file", cmd );  	
    }

    /**
     * TODO Implement - current implementation simply returns the home directory.
     */
    public String getTempDirectory() throws Exception {
    	return ".";
    }

	public void ping(String ip) throws Exception {
		initCli();
		String command = "ping -c 5 " +ip;
		getCliApplication().cliCommand(command);
		FindText findText = new FindText("5 packets transmitted");
		getCliApplication().analyze(findText);
		TextNotFound textNotFound = new TextNotFound("100% packet loss");
		FindText isTextFound = new FindText("100% packet loss", true);
		getCliApplication().analyze(isTextFound);
		if (isTextFound.getCounter() != null) {
			throw new Exception("100% packet loss");
		}
		getCliApplication().analyze(textNotFound);
	}

	public ArrayList<String> getInterfacesList() throws Exception {
		initCli();
		String command = "ifconfig";
		getCliApplication().cliCommand(command);
	   	String res = getCliApplication().getTestAgainstObject().toString();
	   	res = res.substring(0,res.indexOf("lo        Link encap"));
	   	ArrayList<String> interfaces = new ArrayList<String>();
	   	Pattern p = Pattern.compile("(.*)Link encap.*[\\n\\r]");
	   	Matcher m = p.matcher(res);
	   	while(m.find()){
	   		if(!m.group(1).trim().contains(":")){
	   			interfaces.add(m.group(1).trim());
	   		}
	   	}
	   	report.report("interfacses list: " + interfaces.toString());
		return interfaces;	
	}

	public ArrayList<IpStructure> getIps(String interfaceName)
			throws Exception {
		ArrayList<IpStructure> ips = new ArrayList<IpStructure>();
		ArrayList<String> interfaces = new ArrayList<String>();
		initCli();
		String command = "ifconfig";
		getCliApplication().cliCommand(command);
		String res = getCliApplication().getTestAgainstObject().toString();

		if (res.contains("lo        Link encap")) {
			res = res.substring(0, res.indexOf("lo        Link encap"));
		}
		interfaces = getInterfacesList();
		if (interfaces.indexOf(interfaceName) == -1) {
			throw new Exception("unknown interface");
		}
		if (interfaces.indexOf(interfaceName) < interfaces.size() - 1) {
			res = res.substring(res.indexOf(interfaceName), res
					.indexOf((String) interfaces.get(interfaces
							.indexOf(interfaceName) + 1)));
		} else {
			res = res.substring(res.indexOf(interfaceName));
		}

		Pattern p = Pattern.compile("inet addr:(.*)Bcast.*Mask:(.*)[\\n\\r]");
		Matcher m = p.matcher(res);
		while (m.find()) {
			ips.add(new IpStructure(m.group(1).trim(),m.group(2).trim()));
		}
	   	report.report("ips list for " +interfaceName +  " interface " + ips.toString());
		return ips;
	}

	public void addToRoutingTable(String ip, String subnetMask,
			String gw, boolean persistent) throws Exception {
		initCli();
		getCliApplication().cliCommand("");
		addToRoutingTable(ip,subnetMask,gw);
		if (persistent) {
			addPersistentRoute(ip,subnetMask,gw);
		} 
	}
	
	public void addToRoutingTable(String ip, String subnetMask,
			String gw)throws Exception{
		initCli();
		
		String command = MessageFormat.format(ADD_TO_ROUTING_TABLE, ip, subnetMask,
				gw);
		StationsCliCommand cmd = new StationsCliCommand(command);
		CliApplicationExtension.handleCommandAndVerifyEmptyOutputExcept(
				getCliApplication(), "adding to routing table", cmd,
				new String[] { "SIOCADDRT: File exists" });
	}
	
	public void deleteFromRoutingTable(String ip, String subnetMask,
			String gw) throws Exception {
		initCli();
		String command = MessageFormat.format(DELETE_FROM_ROUTING_TABLE, ip,
				subnetMask, gw);
		StationsCliCommand cmd = new StationsCliCommand(command);
		CliApplicationExtension.handleCommandAndVerifyEmptyOutputExcept(
				getCliApplication(), "deleting from the routing table",
				cmd, new String[] { "SIOCDELRT: No such process" });
	}
	
	public void printRoutingTable() throws Exception{
		initCli();
		String command = "route";
		getCliApplication().cliCommand(command);
	}
		
	public void printHostsFile() throws Exception{
		printHostsFile("/usr/etc/hosts");
	}
	
	public void printHostsFile(String hostsFile) throws Exception{
		initCli();
		String command = "cat" + hostsFile;
		getCliApplication().cliCommand(command);
	}

	public void deleteFromHostsFile(String oldHostIp, String oldHostName)
			throws Exception {
		initCli();
		String oldHost = oldHostIp + " " + oldHostName;
		File tmpHosts = new File("tmp");
		File tmpHosts1 = new File("tmp1");
		copyFileFromRemoteMachineToLocalMachine(new File("/etc/hosts"),
				tmpHosts);
		BufferedReader in = new BufferedReader(new FileReader(tmpHosts));
		BufferedWriter out = new BufferedWriter(new FileWriter(tmpHosts1));
		String line = new String();
		while ((line = in.readLine()) != null) {
			if (!line.startsWith(oldHost)) {
				out.write(line);
				out.newLine();
			}
		}
		out.close();
		in.close();
		copyFileFromLocalMachineToRemoteMachine(tmpHosts1, new File(
				"/etc/hosts"));
	}

	public void addToHostsFile(String newHostIp, String newHostName)
			throws Exception {
		addToHostsFile(new File("/etc/hosts"), newHostIp, newHostName);
	}

	private void addPersistentRoute(String ip, String subnetMask,String gw) throws Exception {
		initCli();
		String newRoute = "route add -net "+ ip + " netmask " + subnetMask + " gw " + gw ;
		boolean exist = false;
		File tmpHosts = new File("tmp");
		File tmpHosts1 = new File("tmp1");
		try{
			copyFileFromRemoteMachineToLocalMachine(new File("/etc/rc.local"), tmpHosts);
		}catch(Exception e){
			report.report("This is the first persistent route");
		}
		BufferedReader in = new BufferedReader(new FileReader(tmpHosts));
		BufferedWriter out = new BufferedWriter(new FileWriter(tmpHosts1));
		try {
			String line = new String();
			while ((line = in.readLine()) != null) {
				if (line.startsWith(newRoute)) {
					exist = true;
				}
				out.write(line);
				out.newLine();
			}
			if (!exist) {
				out.write(newRoute);
				report.report("new route was added to hosts file: " + newRoute);
			} else {
				report.report("hosts: " + newRoute + "already exist");
			}
		}finally{
			out.close();
			in.close();
		}
		copyFileFromLocalMachineToRemoteMachine(tmpHosts1, new File("/etc/rc.local"));
	}

	public void deleteFromRoutingTable(String ip, String subnetMask,String gw,boolean persistent)throws Exception {
		initCli();
		if(persistent){
			deletePersistentRoute(ip, subnetMask, gw);
		}
		deleteFromRoutingTable(ip,  subnetMask, gw);
	
	}
	
	private void deletePersistentRoute(String ip, String subnetMask,String gw)throws Exception{
		initCli();
		String oldPersistentRoute = "route add -net "+ ip + " netmask " + subnetMask + " gw " + gw ; 
		File tmpHosts = new File("tmp");
		File tmpHosts1 = new File("tmp1");
		copyFileFromRemoteMachineToLocalMachine(new File("/etc/rc.local"),tmpHosts);
		BufferedReader in = new BufferedReader(new FileReader(tmpHosts));
		BufferedWriter out = new BufferedWriter(new FileWriter(tmpHosts1));
		try {
			String line = new String();
			while ((line = in.readLine()) != null) {
				if (!line.startsWith(oldPersistentRoute)) {
					out.write(line);
					out.newLine();
				}
			}
		}finally{
			out.close();
			in.close();
		}
		copyFileFromLocalMachineToRemoteMachine(tmpHosts1, new File("/etc/rc.local"));
	}
	
	public void compareConfig(String fileName1, String fileName2) throws Exception {   	
		initCli();
		String promptLine = getPromptLine();
		String cmdStr = "diff " + fileName1 + " " + fileName2; 
		getCliApplication().cliCommand(cmdStr);
		String res = getCliApplication().getTestAgainstObject().toString();
		res = res.substring(cmdStr.length(),res.indexOf(promptLine)).trim();
		report.step(res);
	}
	
	public void killProcess(int pid) throws Exception {
		initCli();
		String promptLine = getPromptLine();
		String cmdStr = "kill -9 " + pid;
		getCliApplication().cliCommand(cmdStr);
		String res = getCliApplication().getTestAgainstObject().toString();
		res = res.substring(cmdStr.length(),res.indexOf(promptLine)).trim();
		report.step(res);
	}
	
	public void killProcess(String process) throws Exception {
		initCli();
		String promptLine = getPromptLine();
		String cmdStr = "kill -9 " + process;
		getCliApplication().cliCommand(cmdStr);
		String res = getCliApplication().getTestAgainstObject().toString();
		res = res.substring(cmdStr.length(),res.indexOf(promptLine)).trim();
		report.step(res);
	}
	
	public void ctrl_C() throws Exception {
		initCli();
		String promptLine = getPromptLine();
		String cmdStr = "\u0003";
		getCliApplication().cliCommand(cmdStr);
		String res = getCliApplication().getTestAgainstObject().toString();
		res = res.substring(cmdStr.length(),res.indexOf(promptLine)).trim();
		report.step(res);
	}
	
	public boolean isFileExist(File directoryPath,String fileName) throws Exception{
		String path = directoryPath.getPath().replace("\\", "/");
		
		String lsCommand = "ls "+path;
		 
		initCli();		
		getCliApplication().cliCommand(lsCommand);
		String result = (String)getCliApplication().getTestAgainstObject();
		if(result.contains(fileName)){
			return true;
		}else{
			return false;
		}			
	}

}
