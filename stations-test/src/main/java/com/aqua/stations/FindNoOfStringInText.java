/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.stations;

public class FindNoOfStringInText {

	String text;
	String expectedString;
	int expectedCounter;
	
	public FindNoOfStringInText(String text, String expectedString, int expectedCounter) {
		this.text = text;
		this.expectedString = expectedString;
		this.expectedCounter = expectedCounter;
	}
	

	/**
	 * @return true if the expected String appears expectedCounter times at the text.
	 */
	public boolean getResult(){
		if(howManyAInB(expectedString,text)==expectedCounter){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * return how many times string A appears at string B
	 * @param A
	 * @param B
	 * @return
	 */
	public int howManyAInB(String A, String B){
		int counter = 0; 
		while(B.length()>0){
			if(B.contains(A)){
				counter++;
				B.replace(A,"");
			}else{
				B="";
			}
		}
		return counter;
	}
}
