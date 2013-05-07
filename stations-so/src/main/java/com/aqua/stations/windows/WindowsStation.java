/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.stations.windows;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.extensions.analyzers.text.FindText;
import jsystem.extensions.analyzers.text.GetParameterValue;
import jsystem.extensions.analyzers.text.TextNotFound;
import systemobject.terminal.Prompt;

import com.aqua.stations.CliApplicationExtension;
import com.aqua.stations.IpStructure;
import com.aqua.stations.StationDefaultImpl;
import com.aqua.stations.StationsCliCommand;
import com.aqua.sysobj.conn.CliCommand;

public class WindowsStation extends StationDefaultImpl {

	private String tempDir;

	private static final String ADD_TO_ROUTING_TABLE = "ROUTE ADD {0} mask {1} {2}";
	
	private static final String DELETE_FROM_ROUTING_TABLE = "ROUTE DELETE {0}";
	
	private static final String SET_STATIC_IP = "netsh interface ip set address name=\"{0}\" static {1} {2}";

	private static final String FIND_ADAPTER = "{0}/devcon find pci\\*";

	private static final String RESTART_ADAPTER = "{0}/devcon restart =net ''{1}";
	private static final String ENABLE_ADAPTER = "{0}/devcon enable =net ''{1}";
	private static final String DISABLE_ADAPTER = "{0}/devcon disable =net ''{1}";
	private static final String FIND_ADAPTER_REGEXP = "\\n([^:]*?):.*?({0}).*?";
	
	/**
	 *     RegKey data types    
	 */
	public enum RegKeyDataTypes{
		REG_SZ,
		REG_DWORD,
		REG_NONE,
		REG_MULTI_SZ,
		REG_BINARY,
		REG_EXPAND_SZ,
		REG_DWORD_BIG_ENDIAN,
		REG_DWORD_LITTLE_ENDIAN,
	}
	

	public WindowsStation() {
		super(null, null, null, null);
	}

    public WindowsStation(String host, String protocol, String user, String password,Prompt[] additionalPrompts) {
    	super(host, protocol, user, password,additionalPrompts);
    }

	public WindowsStation(String host, String protocol, String user,
			String password) {
		super(host, protocol, user, password);
	}

	public void init() throws Exception {
		super.init();
	}

	
	protected String getOperatingSystem() {
		return "windows";
	}

	public void deleteDirectory(String directoryName) throws Exception {
		initCli();
		String command = "rmdir /S /Q " + directoryName;
		StationsCliCommand cliCommand = new StationsCliCommand(command);
		CliApplicationExtension.handleCommandAndVerifyEmptyOutputExcept(getCliApplication(), 
				"Deleting directory" + directoryName, cliCommand, new String[]{"The system cannot find the file specified."});
	}

	public void deleteFile(String fileName) throws Exception {
		initCli();
		String command = "del /f /Q " + fileName;
		StationsCliCommand cliCommand = new StationsCliCommand(command);
		CliApplicationExtension.handleCommandAndVerifyEmptyOutputExcept(getCliApplication(), 
				"Deleting file" + fileName, cliCommand, new String[]{"Could Not Find(.*)"+new File(fileName).getName()});
	}

	public void resetAdapter(String adapterPhisicalName) throws Exception {
		initCli();
		InputStream stream = getClass().getResourceAsStream(
				"/com/aqua/stations/windows/devcon/i386/devcon.exe");
		getFileTransfer().copyFileFromLocalMachineToRemoteMachine(stream,
				new File(getTempDirectory(), "devcon.exe"));
		String command = MessageFormat.format(FIND_ADAPTER, getTempDirectory(),
				getHost());
		getCliApplication().cliCommand(command);
		String regExp = MessageFormat.format(FIND_ADAPTER_REGEXP,
				adapterPhisicalName);
		GetParameterValue val = new GetParameterValue(regExp);
		getCliApplication().analyze(val);

		String id  = val.getValue();
		String restart = MessageFormat.format(RESTART_ADAPTER,getTempDirectory(),id);
		getCliApplication().cliCommand(restart);
		FindText findText = new FindText("device(s) restarted");
		getCliApplication().analyze(findText);
	}
	
	
	public void enableAdapter(String adapterPhisicalName) throws Exception{
		initCli();
		InputStream stream = getClass().getResourceAsStream(
				"/com/aqua/stations/windows/devcon/i386/devcon.exe");
		getFileTransfer().copyFileFromLocalMachineToRemoteMachine(stream,
				new File(getTempDirectory(), "devcon.exe"));
		String command = MessageFormat.format(FIND_ADAPTER, getTempDirectory(),
				getHost());
		getCliApplication().cliCommand(command);
		String regExp = MessageFormat.format(FIND_ADAPTER_REGEXP,
				adapterPhisicalName);
		GetParameterValue val = new GetParameterValue(regExp);
		getCliApplication().analyze(val);

		String id  = val.getValue();
		String enable = MessageFormat.format(ENABLE_ADAPTER,getTempDirectory(),id);
		getCliApplication().cliCommand(enable);
		FindText findText = new FindText("device(s) enabled");
		getCliApplication().analyze(findText);
	}
	

	public void diableAdapter(String adapterPhisicalName) throws Exception{
		initCli();
		InputStream stream = getClass().getResourceAsStream(
				"/com/aqua/stations/windows/devcon/i386/devcon.exe");
		getFileTransfer().copyFileFromLocalMachineToRemoteMachine(stream,
				new File(getTempDirectory(), "devcon.exe"));
		String command = MessageFormat.format(FIND_ADAPTER, getTempDirectory(),
				getHost());
		getCliApplication().cliCommand(command);
		String regExp = MessageFormat.format(FIND_ADAPTER_REGEXP,
				adapterPhisicalName);
		GetParameterValue val = new GetParameterValue(regExp);
		getCliApplication().analyze(val);

		String id  = val.getValue();
		String enable = MessageFormat.format(DISABLE_ADAPTER,getTempDirectory(),id);
		getCliApplication().cliCommand(enable);
		FindText findText = new FindText("device(s) disabled");
		getCliApplication().analyze(findText);
	}	
	

	public void setStaticIp(String interfaceName, String ip, String subnetMask)
			throws Exception {
		initCli();
		String command = MessageFormat.format(SET_STATIC_IP, interfaceName, ip,
				subnetMask);
		getCliApplication().cliCommand(command, 1000 * 60 * 2);
		FindText findText = new FindText("Ok.");
		getCliApplication().analyze(findText);
	}

	public String getTempDirectory() throws Exception {
		if (tempDir == null) {
			initCli();
			String command = "echo %TEMP%";
			getCliApplication().cliCommand(command);
			String res = getCliApplication().getTestAgainstObject().toString();
			int indexOfCommand = res.indexOf(command) + command.length();
			tempDir = res.substring(indexOfCommand,
					res.indexOf('\r', indexOfCommand + 1)).trim();
		}
		return tempDir;
	}

	public void ping(String ip) throws Exception {
		initCli();
		String command = "ping " + ip;
		getCliApplication().cliCommand(command);
		TextNotFound textNotFound = new TextNotFound("100% loss");
		getCliApplication().analyze(textNotFound);

		textNotFound = new TextNotFound("unreachable");
		getCliApplication().analyze(textNotFound);

	}

	public ArrayList<String> getInterfacesList() throws Exception {
		initCli();
		String command = "ipconfig";
		getCliApplication().cliCommand(command);
		String res = getCliApplication().getTestAgainstObject().toString();
		ArrayList<String> interfaces = new ArrayList<String>();
		Pattern p = Pattern.compile("Ethernet adapter(.*):.*[\\n\\r]");
		Matcher m = p.matcher(res);
		while (m.find()) {
			if (!m.group(1).trim().contains(":")) {
				interfaces.add(m.group(1).trim());
			}
		}
		return interfaces;
	}

	public void addToRoutingTable(String ip, String subnetMask,
			String interfaceName, boolean persistent) throws Exception {
		initCli();
		String command = MessageFormat.format(ADD_TO_ROUTING_TABLE, ip, subnetMask,
				interfaceName);
		if (persistent) {
			command += " -p";
		}
		StationsCliCommand cmd = new StationsCliCommand(command);
		CliApplicationExtension.handleCommandAndVerifyEmptyOutput(
				getCliApplication(), "adding to the routing table", cmd);
	}

	public void deleteFromRoutingTable(String ip, String subnetMask,
			String interfaceName) throws Exception {
		initCli();
		String command = MessageFormat.format(DELETE_FROM_ROUTING_TABLE, ip,
				subnetMask, interfaceName);
		StationsCliCommand cmd = new StationsCliCommand(command);
		CliApplicationExtension.handleCommandAndVerifyEmptyOutputExcept(
					getCliApplication(), "deleting from the routing table",
					cmd, new String[] { "The route specified was not found." });
	}

	public void deleteFromHostsFile(String oldHostIp, String oldHostName)
			throws Exception {
		initCli();
		String oldHost = oldHostIp + " " + oldHostName;
		File tmpHosts = new File("tmp");
		File tmpHosts1 = new File("tmp1");
		copyFileFromRemoteMachineToLocalMachine(new File(
				"\\WINDOWS\\system32\\drivers\\etc\\hosts"), tmpHosts);
		BufferedReader in = new BufferedReader(new FileReader(tmpHosts));
		BufferedWriter out = new BufferedWriter(new FileWriter(tmpHosts1));
		try {
			String line = new String();
			while ((line = in.readLine()) != null) {
				if (!line.startsWith(oldHost)) {
					out.write(line);
					out.newLine();
				}
			}
		}finally {
			out.close();
			in.close();
		}
		copyFileFromLocalMachineToRemoteMachine(tmpHosts1, new File(
				"\\WINDOWS\\system32\\drivers\\etc\\hosts"));
	}
	
	public void printHostsFile() throws Exception{
		printHostsFile("type %SystemRoot%\\system32\\drivers\\etc\\hosts");	
	}
	
	public void printHostsFile(String hostFile) throws Exception{
		initCli();
		String command = "type " + hostFile;
		getCliApplication().cliCommand(command);	
	}
	


	public void addToHostsFile(String newHostName, String newHostIp)
			throws Exception {
		addToHostsFile(new File("\\WINDOWS\\system32\\drivers\\etc\\hosts"),
				newHostName, newHostIp);
	}

	public void printRoutingTable() throws Exception{
		initCli();
		String command = "route print";
		getCliApplication().cliCommand(command);
	}

	public ArrayList<IpStructure> getIps(String interfaceName)
			throws Exception {
		ArrayList<IpStructure> ips = new ArrayList<IpStructure>();
		ArrayList<String> interfaces = new ArrayList<String>();
		initCli();
		String command = "ipconfig";
		getCliApplication().cliCommand(command);
		String res = getCliApplication().getTestAgainstObject().toString();

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

		res = res.replaceAll("\\s", "");
		Pattern p = Pattern.compile("IPAddress.*:(.*)Subnet.*:(.*)Default");
		Matcher m = p.matcher(res);
		while (m.find()) {
			ips.add(new IpStructure(m.group(1),m.group(2)));
		}
		report.report("IPs list for " + interfaceName);
		for (int i = 0; i < ips.size(); i++) {
			report.report(ips.get(i).toString());
		}
		return ips;
	}
	
	public boolean isFileExist(File directoryPath,String fileName) throws Exception{		
		
		String dirCommand = "dir "+directoryPath;
		 
		initCli();		
		getCliApplication().cliCommand(dirCommand);
		String result = (String)getCliApplication().getTestAgainstObject();
		if(result.contains(fileName)){
			return true;
		}else{
			return false;
		}			
	}
	

	
	public void changeTelnetServerMaxConnections(int maxConnections) throws Exception{
		updateRegistry("HKLM\\SOFTWARE\\Microsoft\\TelnetServer\\1.0", "MaxConnections", RegKeyDataTypes.REG_DWORD, "0x0000000"+maxConnections);
		updateRegistry("HKLM\\SOFTWARE\\Microsoft\\TelnetServer\\Defaults", "MaxConnections", RegKeyDataTypes.REG_DWORD, "0x0000000"+maxConnections);
	}
	
	/**
	 * 
	 * @param regPath - the registry path of the reg key  . example:  "HKLM\\SOFTWARE\\Microsoft\\TelnetServer\\1.0"	   
	 * @param valueName -  The value name, under the selected Key, to add 
	 * @param type - RegKey data types 
	 * @param data - The data to assign to the registry ValueName being added 
	 * @throws Exception
	 */	
	
	public void updateRegistry(String regPath,String valueName,RegKeyDataTypes regKeyDataTypes,String data) throws Exception{
		
		CliCommand regCommand = new CliCommand();
		Prompt valuePrompt = new Prompt();
		Prompt completed = new Prompt();		
		
			
		valuePrompt.setAddEnter(true);
		valuePrompt.setDontWaitForScrollEnd(true);
		valuePrompt.setPrompt("Value "+valueName);
		valuePrompt.setStringToSend("Y");

		completed.setCommandEnd(true);
		completed.setDontWaitForScrollEnd(true);
		completed.setPrompt("completed");
		
		initCli();
		regCommand.setCommands(new String [] {"REG ADD "+regPath+" /v "+ valueName+" /t "+regKeyDataTypes.name()+" /d "+data});
		regCommand.setPrompts(new Prompt[]{valuePrompt,completed});
		getCliApplication().handleCliCommand("Value "+valueName, regCommand);
		getCliApplication().close();
	}
}
