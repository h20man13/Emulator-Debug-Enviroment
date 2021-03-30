package edu.depauw.emulator_ide.verilog_compiler.visitor;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;

import java.util.ArrayList;

public class TypeCheckerFunctionData{

    private final TypeCheckerVariableData  returnType;
    private ArrayList<TypeCheckerVariableData> expressionTypes;
    private final Position position;

    public TypeCheckerFunctionData(TypeCheckerVariableData type, Position position){
	this.position = position;
	this.type = type;
	this.expressionTypes = new ArrayList<>();
    }
    
    public void addParameterType(TypeCheckerVariableData parType){
	expressionTypes.add(parType);
    }

    public int numParamaterTypes(){
	return expressionTypes.size();
    }

    public TypeCheckerVariableData getReturnType(){
	return returnType;
    }
    
    public Position getPosition(){
	return this.position;
    }
    
    
}
