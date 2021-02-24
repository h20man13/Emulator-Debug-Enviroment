package edu.depauw.emulator_ide.verilog_compiler.ast.statement;

import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;

public class ForeverStatement extends Statement{
    
    private final Statement stat; //statement
    
    public ForeverStatement(Statement stat){
	super(stat.getPosition());
	this.stat = stat;
    }

    public Statement getStatement(){
	return stat;
    }
}