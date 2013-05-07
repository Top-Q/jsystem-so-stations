/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.stations;

import com.aqua.sysobj.conn.CliApplication;
import com.aqua.sysobj.conn.CliCommand;

/**
 * Additional CliApplication methods
 * TODO should be merged with the CliApplication class
 * @author Golan Derazon
 */
public class CliApplicationExtension {

	/**
	 * Activates cli command and verifies that the command has no output.
	 * The verification is done in the following way:
	 * 1. Activates the command and gets operation output.
	 *    the output of the operation is composed of the command+ command output and the prompt.
	 * 2. Trims from the operation output the command and prompt
	 * 3. verifies that the result of the trim is an empty string.
	 */
	public static void handleCommandAndVerifyEmptyOutput(
			CliApplication application, String title, CliCommand command)
			throws Exception {
		application.handleCliCommand(title, command);
		String res = application.getTestAgainstObject().toString();
		/**
		 * In some machines (mainly linux machines) a time stamp is part of the prompt 
		 * in these cases, the method might fail although the output is
		 * empty.
		 * 
		 * Extracting the prompt should be after command activation. In most cases,
		 * the two operations (the command and extracting the prompt)
		 * will be done in the same second and the operation will succeed.
		 * 
		 */
		String prompt = getPromptLine(application);

		String commandString = cleanWhiteSpaces(command.getCommands()[0]);
		res = cleanWhiteSpaces(res);
		res = res.substring(commandString.length(), findLastIndexOfPrompt(res,prompt)).trim();
		if (!"".equals(res)) {
			throw new Exception("Error in cli command activation. Got - " + res);
		}
	}

	/**
	 * Looks for the index of the prompt in <code>res</code>.
	 * In some machine part of the prompt is a time stamp, for these cases,
	 * if prompt is not found it is cut into half until prompt is found in result.
	 */
	private static int findLastIndexOfPrompt(String res, String prompt){
		while (res.lastIndexOf(prompt) ==-1){
			prompt = prompt.substring(0,prompt.length()/2+1);
			if (prompt.length() == 1){
				throw new RuntimeException("Could not find prompt in operation results");
			}
		}		
		return res.lastIndexOf(prompt);
	}
	/**
	 * Activates Cli command and verifies that the command has no output except excepted output.
	 */
	public static void handleCommandAndVerifyEmptyOutputExcept(
			CliApplication application, String title, CliCommand command,String [] except)
			throws Exception {
		application.cliCommand("");
		application.handleCliCommand(title, command);
		String res = application.getTestAgainstObject().toString();
		String prompt = getPromptLine(application);
		String commandString = cleanWhiteSpaces(command.getCommands()[0]);
		res = cleanWhiteSpaces(res);
		res = cleanExpected(res,except);
		res = res.substring(res.lastIndexOf(commandString)+commandString.length(), res.indexOf(prompt)).trim();
		if (!"".equals(res)) {
			throw new Exception("Error in cli command activation. Got - " + res);
		}
	}
	/**
	 * Sends an empty command. The result of the empty command is the remote machine
	 * full prompt.
	 */
	private static String getPromptLine(CliApplication application)
			throws Exception {
		application.setTestAgainstObject(null);
		application.cliCommand("");
		return cleanWhiteSpaces(application.getTestAgainstObject().toString());
	}

	private static String cleanWhiteSpaces(String txt) {
		return txt.replaceAll("\\s", "");
	}
	
	private static String cleanExpected(String txt,String[] expected){
		for(int i=0; i<expected.length;i++){
			expected[i] = cleanWhiteSpaces(expected[i]);
			txt = txt.replaceAll(expected[i], "");
		}
		return txt;
	}
}
