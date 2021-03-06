package edu.depauw.emulator_ide.verilog_compiler.token;

import java.lang.String;

public class Position{
    private final int lineNumber; //line number of the character
    private final int linePosition; //horizontal position of the character

    /**
     * The position constructoe is used to keep track of positions
     *@param linenummber
     *@param linePosition
     *@author Jacob Bauer
     */
    public Position(int lineNumber, int linePosition){
	this.lineNumber = lineNumber;
	this.linePosition = linePosition;
    }

    /**
     * Displays the position object as a String
     *@author Jacob Bauer
     */
    @Override
    public String toString(){
	return "Line number: " + lineNumber + " Position: " + linePosition; 
    }
}
