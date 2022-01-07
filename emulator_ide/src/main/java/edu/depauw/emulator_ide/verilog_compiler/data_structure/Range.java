package edu.depauw.emulator_ide.verilog_compiler.data_structure;


import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ExpressionVisitor;
import java.lang.Long;

/**
 * The VectorElement class is used to par.E a call to an array cell
 * 
 * @author Jacob Bauer
 */

public class Range {

    private final long lowerBound;
    private final long upperBound;

    /**
     * The Range is a data structure to verify case statement numbers
     * 
     * @param index1 min index of the array
     * @param index2 max index of the array
     */

    public Range(long lowerBound, long upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public boolean inRange(long value){ return lowerBound <= value && value <= upperBound; }

    public String toString(){
        return "Range is: " + Long.toString(lowerBound, 2) + " <=  value  <= " + Long.toString(upperBound, 2);
    }

}
