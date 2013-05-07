/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.stations;

import java.io.File;

import java.io.InputStream;

import java.util.ArrayList;

import jsystem.framework.analyzer.Analyzer;

import jsystem.framework.system.SystemObject;

import com.aqua.sysobj.conn.CliApplication;

	/** 
	 * Defines the methods that should 
 	 */
	public interface Station extends SystemObject, Analyzer {

	/**
	 * Copies stream to remote machine.<br>
	 * Destination can be either absolute destination path or relative to
	 * client's
	 * user directory.<br>
	 * Examples:<br>
	 * 1. Absolute destination: new File("C:/automation/test.txt"). Will copy
	 * the stream to c:/automation/test.txt
	 * on the remote machine. If folder automation doesn't exist the operation
	 * will fail.<br>
	 * 2. Relative destination: new File("test.txt"). Will copy the stream to
	 * user_dir/test.txt.<br>
	 * <br> 
	 * @see TFTPRemoteClient#copyFileFromLocalMachineToRemoteClient(String,
	 *      String)
	 */
	public void copyFileFromLocalMachineToRemoteMachine(InputStream source,
			File destination) throws Exception;

	/**
	 * Copies file to remote machine.
	 * @see #copyFileFromLocalMachineToRemoteMachine(InputStream, File)
	 */
	public void copyFileFromLocalMachineToRemoteMachine(File source,
			File destination) throws Exception;

	/**
	 * Copies file from remote machine to this machine<br>
	 * <br> 
	 * Source can be either absolute destination path or relative to client's 
	 * user directory.<br> 
	 * Examples:<br> 
	 * 1. Absolute destination: new File("C:/automation/test.txt"). Will copy
	 * the stream to c:/automation/test.txt 
	 * on the remote machine. If folder automation doesn't exist the operation
	 * will fail.<br>
	 * 
	 * 2. Relative destination: new File("test.txt"). Will copy the stream to
	 * user_dir/test.txt.<br> 
	 * Destination can be either absolute destination path or relative to
	 * current dir<br> 
	 * <br> 
	 * @see TFTPRemoteClient#copyFileFromRemoteClientToLocalMachine(String,
	 *      String)
	 */
	public void copyFileFromRemoteMachineToLocalMachine(File source,
			File destination) throws Exception;

	/** 
	 * Method not implemented yet 
	 */
	public void copyDirectoryFromRemoteMachineToLocalMachine(File source,
			File destination) throws Exception;

	/** 
	 * Method not implemented yet 
	 */
	public void copyDirectoryFromLocalMachineToRemoteMachine(File source,
			File destination) throws Exception;

	/**
	 * 
	 */
	public CliApplication getCliSession(boolean newSession) throws Exception;

	/** 
	 * Creates a new directory.
	 * @param directoryName - path+name of the directory. If the directory already exists,nothing happens.
	 * @throws Exception
	 */
	public void createDirectory(String directoryName) throws Exception;

	/** 
	 * Deletes a directory 
	 * directoryName - path+name of the directory. It also deletes sub
	 * directories. 
	 * @param directoryName 
	 * @throws Exception 
	 */
	public void deleteDirectory(String directoryName) throws Exception;

	/**
	 * Deletes a file
	 * @param fileName - path+name of the filename. It also deletes sub directories.
	 * @throws Exception
	 */
	public void deleteFile(String fileName) throws Exception;

	/**
	 * reset an adapter.
	 * @param adapterPhisicalName
	 * @throws Exception
	 */
	public void resetAdapter(String adapterPhisicalName) throws Exception;

	/**
	 * set static IP.
	 * @param interfaceName - The interface which its IP is going to be changed.
	 * @param ip - The new IP for the interface
	 * @param subnetMask - The subnt mask for this IP
	 * @throws Exception
	 */
	public void setStaticIp(String interfaceName, String ip, String subnetMask)
			throws Exception;

	/**
	 * Sends ping from the remote machine to another machine with this ip.
	 */
	public void ping(String ip) throws Exception;

	/**
	 * Returns a list of the remote machine's interfaces
	 * It does not return the lo interface.
	 */
	public ArrayList<String> getInterfacesList() throws Exception;

	/**
	 * Returns a list of the IPs of an interface
	 */
	public ArrayList<IpStructure> getIps(String interfaceName) throws Exception;

	/**
	 * Adds new route to the routing table.
	 * @param ip - the new destination
	 * @param subnetMask - subnet mask for this IP destination
	 * @param interfaceName - The interface that gets the new route
	 * @param persistent - if false, an exception will be throwed if this routing exists.
	 * @throws Exception
	 */
	public void addToRoutingTable(String ip, String subnetMask,
			String interfaceName, boolean persistent) throws Exception;

	/**
	 * Deletes a route to the routing table.
	 * @param ip - the destination
	 * @param subnetMask - subnet mask for this IP destination
	 * @param interfaceName - deletes the route from this interface
	 * @param persistent - if false, an exception will be throwed if this routing does not exists.
	 * @throws Exception
	 */
	public void deleteFromRoutingTable(String ip, String subnetMask,
			String interfaceName) throws Exception;

	public void deleteFromRoutingTable(String ip, String subnetMask,
			String interfaceName,boolean persistent) throws Exception;

	
	/**
	 * Adds new host to the hosts file
	 * @param newHost - The new host which is going to be added
	 * @throws Exception
	 */
	public void addToHostsFile(String newHostIp, String newHostName)
			throws Exception;

	/**
	 * Adds new host to the hosts file 
	 * @param hostsFile - Path and name to the hosts file
	 * @param newHost - The new host which is going to be added
	 * @throws Exception 
	 */
	public void addToHostsFile(File remoteHostsFile, String newHostIp,String newHostName) throws Exception;

	/**
	 * Deletes a host from the hosts file 
	 * @param oldHostIp 
	 * @param oldHostName 
	 * @throws Exception
	 */
	public void deleteFromHostsFile(String oldHostIp, String oldHostName)
			throws Exception;

	/**
	 * Check whether a file exist on remote host
	 * @param directoryPath
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public boolean isFileExist(File directoryPath,String fileName) throws Exception;
}