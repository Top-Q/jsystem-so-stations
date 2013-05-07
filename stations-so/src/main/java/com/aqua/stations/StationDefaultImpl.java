/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.stations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;

import systemobject.terminal.Prompt;
import jsystem.framework.system.SystemObjectImpl;
import com.aqua.filetransfer.ftp.FTPFileTransfer;
import com.aqua.sysobj.conn.CliApplication;
import com.aqua.sysobj.conn.CliFactory;

public abstract class StationDefaultImpl extends SystemObjectImpl implements Station {

	private FTPFileTransfer fileTransfer;
	private CliApplication cliApplication;
	
	private String host;
	private String cliUser;
	private String cliPassword;
	private String protocol = "telnet";
	private Prompt[] additionalPrompts;
	private String ftpUser;
	private String ftpPassword;
	private String localHostExternalName;

	protected StationDefaultImpl(String host,String protocol,String user,String password,Prompt[] additionalPrompts){
		this(host,protocol,user,password);
		this.additionalPrompts = additionalPrompts;
	}

	protected StationDefaultImpl(String host,String protocol,String user,String password){
		super();
		setHost(host);
		setCliProtocol(protocol);
		setCliUser(user);
		setCliPassword(password);
	}
		
	public void init() throws Exception{
		super.init();
	}
	
	public void createDirectory(String directoryName) throws Exception {
		initCli();
		String promptLine = getPromptLine();
		String command= "mkdir "+directoryName;
		getCliApplication().cliCommand(command);
		String res = getCliApplication().getTestAgainstObject().toString();
		res = res.substring(command.length(),res.indexOf(promptLine)).trim();
		if (res.toLowerCase().indexOf("exists") > -1){
			report.report("Directory already exists: " + directoryName);
			return;
		}
		if (!"".equals(res)){
			throw new Exception("Error in directory creation. Directory name= " +directoryName + ". "+ getCliApplication().getTestAgainstObject().toString());
		}
	}
	
	public void copyFileFromLocalMachineToRemoteMachine(InputStream source,File destination) throws Exception{
		getFileTransfer().copyFileFromLocalMachineToRemoteMachine(source, destination);
	}

	public void copyFileFromLocalMachineToRemoteMachine(File source,File destination) throws Exception{
		getFileTransfer().copyFileFromLocalMachineToRemoteMachine(source, destination);
	}

	public void copyFileFromRemoteMachineToLocalMachine(File source,File destination) throws Exception{
		getFileTransfer().copyFileFromRemoteMachineToLocalMachine(source, destination);
	}

	public void copyDirectoryFromRemoteMachineToLocalMachine(File source,File destination) throws Exception{
		throw new Exception("Method not implemented yet");
	}

	public void copyDirectoryFromLocalMachineToRemoteMachine(File source,File destination) throws Exception{
		throw new Exception("Method not implemented yet");
	}

	public void closeStationSession() throws Exception {
		if (getCliApplication()!=null && getCliApplication().conn.cli.isConnected()){
			getCliApplication().conn.cli.disconnect();
		}
		closeFileTransferSession();
	}
	
	public void closeFileTransferSession() throws Exception {
		if (fileTransfer != null) {
			fileTransfer.closeFileTransferSession();
		}
	}
	
	public void close(){
		try {
			closeStationSession();
		} catch (Exception e) {
			report.report("Failed clossing session",e);
		}
		super.close();
	}

	protected abstract String getOperatingSystem();
	
	public void deleteFromRoutingTable(String ip, String subnetMask, String interfaceName,boolean persistent) throws Exception{
		deleteFromRoutingTable(ip, subnetMask, interfaceName);
	}

	protected void initCli() throws Exception{
		if (getCliApplication() == null){
			cliApplication = CliFactory.createCliApplication(getHost(),getOperatingSystem(), getCliProtocol(), getCliUser(), getCliPassword(), additionalPrompts);
			cliApplication.cliCommand("");
		}
		if (!getCliApplication().conn.cli.isConnected()){
			getCliApplication().conn.cli.connect();
		}
	}

	protected FTPFileTransfer getFileTransfer() throws Exception {
		if (fileTransfer == null){
			fileTransfer = createFileTransferObject();
		}
		return fileTransfer;
	}

	private FTPFileTransfer createFileTransferObject() throws Exception {
		FTPFileTransfer newFileTransfer;
		newFileTransfer = new FTPFileTransfer(getHost(),getCliUser(),getCliPassword());
		newFileTransfer.setProtocol(getCliProtocol());
		if (getFtpUser() != null){
			newFileTransfer.setFtpUserName(getFtpUser());			
		}
		if (getFtpPassword()!= null){
			newFileTransfer.setFtpPassword(getFtpPassword());			
		}
		if (getLocalHostExternalName() != null){
			newFileTransfer.setLocalHostExternalName(getLocalHostExternalName());
		}
		newFileTransfer.setOperatingSystem(getOperatingSystem());
		newFileTransfer.init();
		return newFileTransfer;
	}
	
	public String getCliPassword() {
		return cliPassword;
	}	
	public void setCliPassword(String cliPassword) {
		this.cliPassword = cliPassword;
	}
	public String getCliUser() {
		return cliUser;
	}
	public void setCliUser(String cliUser) {
		this.cliUser = cliUser;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getFtpPassword() {
		return ftpPassword;
	}
	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}	
	public String getFtpUser() {
		return ftpUser;
	}	
	public void setFtpUser(String ftpUser) {
		this.ftpUser = ftpUser;
	}
	public void setCliProtocol(String protocol){
		this.protocol = protocol;
	}
	public String getCliProtocol(){
		return protocol;
	}
	
	public CliApplication getCliSession(boolean newSession) throws Exception {
		if (newSession){
			return CliFactory.createCliApplication(getHost(),getOperatingSystem(), getCliProtocol(), getCliUser(), getCliPassword(), null);
		}else {
			initCli();
			return getCliApplication();
		}
	}

	public String getLocalHostExternalName() {
		return localHostExternalName;
	}

	public void setLocalHostExternalName(String ftpServerHostName) {
		this.localHostExternalName = ftpServerHostName;
	}

	protected CliApplication getCliApplication() throws Exception{
		return cliApplication;
	}
	
	protected String getPromptLine() throws Exception{
		getCliApplication().cliCommand("");
		getCliApplication().cliCommand("");
		return getCliApplication().getTestAgainstObject().toString().trim();
	}

	protected void changeDirectory(String directoryName) throws Exception {
		String command= "cd "+directoryName;
		getCliApplication().cliCommand(command);
		String res = getCliApplication().getTestAgainstObject().toString();
		if (res.toLowerCase().indexOf(directoryName.toLowerCase()) == -1){
			throw new Exception("Failed changing directory. Directory name= " +directoryName + ". "+ getCliApplication().getTestAgainstObject().toString());
		}
	};	
	
	public abstract String getTempDirectory() throws Exception;
	
	public void addToHostsFile(File remoteHostsFile, String newHostIp,
			String newHostName) throws Exception {

		initCli();
		getCliApplication().cliCommand("");

		boolean exist = false;
		String newHost = newHostIp + " " + newHostName;
		File tmpHosts = new File("tmp");
		File tmpHosts1 = new File("tmp1");
		copyFileFromRemoteMachineToLocalMachine(new File(remoteHostsFile
				.getPath()), tmpHosts);
		BufferedReader in = new BufferedReader(new FileReader(tmpHosts));
		BufferedWriter out = new BufferedWriter(new FileWriter(tmpHosts1));
		String line = new String();
		while ((line = in.readLine()) != null) {
			if (line.startsWith(newHost)) {
				exist = true;
			}
			out.write(line);
			out.newLine();
		}
		if (!exist) {
			out.write(newHost);
			report.report("new host was added to hosts file: " + newHost);
		} else {
			report.report("hosts: " + newHost + "already exist");
		}

		out.close();
		in.close();
		copyFileFromLocalMachineToRemoteMachine(tmpHosts1, new File(
				remoteHostsFile.getPath()));
	}

}
