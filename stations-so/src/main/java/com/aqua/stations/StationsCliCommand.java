/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.stations;

import com.aqua.sysobj.conn.CliCommand;

public class StationsCliCommand extends CliCommand {
	
	public StationsCliCommand(String[] command){
		super();
		setCommands(command);
		addErrors("parse error");
		addErrors("Network is down");
		addErrors("No such file or directory");
		addErrors("command not found");
		addErrors("Invalid or unknown ");
	}
	
	public StationsCliCommand(String command){
		this(new String[]{command});
	}

}