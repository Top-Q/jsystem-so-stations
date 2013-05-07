/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.stations;

import systemobject.terminal.Cli;
import systemobject.terminal.Prompt;
import com.aqua.stations.linux.LinuxStation;
import com.aqua.stations.windows.WindowsStation;
import com.aqua.sysobj.conn.CliFactory;
import com.aqua.sysobj.conn.CliFactory.EnumOperatinSystem;

/**
 * Factory class for creation of {@link Cli} instance.
 * 
 *  @author Golan Derazon
 *  @category please note that the Stations project (and this factory
 *            class) is an incubation project.
 */
public class StationsFactory  {
	
	public static final String OPERATING_SYSTEM_WINDOWS = CliFactory.OPERATING_SYSTEM_WINDOWS;
	public static final String OPERATING_SYSTEM_LINUX = CliFactory.OPERATING_SYSTEM_LINUX;
		
	/**
	 * 
	 * @param host
	 * @param operatingSystem
	 * @param protocol
	 * @param user
	 * @param password
	 * @param additionalPrompts
	 * @return
	 * @throws Exception
	 */
	public static StationDefaultImpl createStation(String host,String operatingSystem,String protocol,String user,String password,Prompt[] additionalPrompts) throws Exception {
		StationDefaultImpl impl = null;

		if (operatingSystem.equals(OPERATING_SYSTEM_WINDOWS)){
			impl = new WindowsStation(host,protocol,user,password,additionalPrompts);
		}else
		if (operatingSystem.equals(OPERATING_SYSTEM_LINUX)){
			impl = new LinuxStation(host,protocol,user,password,additionalPrompts);
		}
		return impl;
	}	

	public static StationDefaultImpl createStation(String host, EnumOperatinSystem operatingSystem, String protocol,String user,String password,Prompt[] additionalPrompts) throws Exception {		
		return createStation(host, operatingSystem.toString().toLowerCase(), protocol, user, password, additionalPrompts);
	}	

}
