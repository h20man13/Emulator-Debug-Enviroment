package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter;

import io.github.H20man13.emulator_ide._interface.Machine;
import io.github.H20man13.emulator_ide.common.Pointer;
import io.github.H20man13.emulator_ide.common.debug.ErrorLog;
import io.github.H20man13.emulator_ide.verilog_interpreter.OpUtil;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.IntVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.LongVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.Value;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.ede.EdeMemVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.ede.EdeRegVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.ede.EdeStatVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.function_call.SystemFunctionCall;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.label.Element;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.label.Identifier;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.label.Slice;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.assignment.BlockingAssignment;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.task.SystemTaskStatement;

public class EdeInterpreter extends VerilogInterpreter {
    private Machine guiInstance;
    private String standardOutputPane;
    private String standardInputPane;
    
    public EdeInterpreter(ErrorLog errLog, Machine guiInstance, String standardOutputPane, String standardInputPane){
        super(errLog);
        this.guiInstance = guiInstance;
        this.standardOutputPane = standardOutputPane;
        this.standardInputPane = standardInputPane;
    }

    protected Value interpretSystemFunctionCall(SystemFunctionCall call) throws Exception{
        String identifier = call.functionName;
        if(identifier.equals("getRegister")){
            Expression regExp = call.argumentList.get(0);
            Value regName = interpretShallowExpression(regExp);

            if(regName.isStringValue()){
                return new LongVal(this.guiInstance.getRegisterValue(regName.toString()));
            } else {
                return new LongVal(this.guiInstance.getRegisterValue(regName.intValue()));
            }
        } else if(identifier.equals("getStatus")){
            Expression statusNameExp = call.argumentList.get(0);
            Value statusName = interpretShallowExpression(statusNameExp);

            return new LongVal(this.guiInstance.getStatusValue(statusName.toString()));
        } else if(identifier.equals("getMemory")){
            Expression memoryAddressExp = call.argumentList.get(0);
            Value memAddressVal = interpretShallowExpression(memoryAddressExp);
            return new LongVal(this.guiInstance.getMemoryValue(memAddressVal.intValue()));
        } else {
            return super.interpretSystemFunctionCall(call);
        }
    }

    protected IntVal interpretSystemTaskCall(SystemTaskStatement stat) throws Exception{
        String identifier = stat.taskName;

        if(identifier.equals("display")){
           if (stat.argumentList.size() >= 2) {
               Value fString = interpretShallowExpression(stat.argumentList.get(0));

               Object[] Params = new Object[stat.argumentList.size() - 1];
               for(int paramIndex = 0, i = 1; i < stat.argumentList.size(); i++, paramIndex++){
                    Value  fData = interpretShallowExpression(stat.argumentList.get(i));
                    Object rawValue = OpUtil.getRawValue(fData);
                    Params[paramIndex] = rawValue;
               }
               

               String formattedString = String.format(fString.toString(), Params);
               guiInstance.appendIoText(standardOutputPane, formattedString + "\r\n");
           } else if (stat.argumentList.size() == 1) {
               Value data = interpretShallowExpression(stat.argumentList.get(0));
               guiInstance.appendIoText(standardOutputPane, data.toString() + "\r\n");
           } else {
               OpUtil.errorAndExit("Unknown number of print arguments in " + stat.taskName, stat.position);
           }
        } else if(identifier.equals("setRegister")){
            if(stat.argumentList.size() != 2){
               OpUtil.errorAndExit("Error: Invalid amount of Arguments for Set Register...\nExpected 2 but found " + stat.argumentList.size()); 
            } else {
                Expression registerNameExp = stat.argumentList.get(0);
                Expression registerValueExp = stat.argumentList.get(1);

                Value registerNameVal = interpretShallowExpression(registerNameExp);
                Value registerValueVal = interpretShallowExpression(registerValueExp);

                if(registerNameVal.isStringValue()){
                    guiInstance.setRegisterValue(registerNameVal.toString(), registerValueVal.longValue());
                } else {
                    guiInstance.setRegisterValue(registerNameVal.intValue(), registerValueVal.longValue());
                }
            }
        } else if(identifier.equals("setStatus")){
            if(stat.argumentList.size() != 2){
                OpUtil.errorAndExit("Error: Invalid amount of arguments for Status...\nExpected 2 but found " + stat.argumentList.size());
            }

            Expression statusNameExp = stat.argumentList.get(0);
            Expression statusValueExp = stat.argumentList.get(1);

            Value statusNameVal = interpretShallowExpression(statusNameExp);
            Value statusValueVal = interpretShallowExpression(statusValueExp);

            guiInstance.setStatusValue(statusNameVal.toString(), statusValueVal.longValue());
        } else if(identifier.equals("setMemory")){
            if(stat.argumentList.size() != 2){
                OpUtil.errorAndExit("Error: Invalid amount of aruments for settingMemory Address...\nExpected 2 but found " + stat.argumentList.size());
            }

            Expression memAddressExp = stat.argumentList.get(0);
            Expression memValExp = stat.argumentList.get(1);

            Value memAddressVal = interpretShallowExpression(memAddressExp);
            Value memValVal = interpretShallowExpression(memValExp);

            
            guiInstance.setMemoryValue(memAddressVal.intValue(), memValVal.longValue());
        } else {
            return super.interpretSystemTaskCall(stat);
        }

        return OpUtil.success();
    }

    protected IntVal interpretShallowBlockingAssingment(BlockingAssignment assign) throws Exception{

        if(assign.leftHandSide instanceof Element){
            Element leftHandSide = (Element)assign.leftHandSide;
            Pointer<Value> val = environment.lookupVariable(leftHandSide.labelIdentifier);
            Value deref = val.deRefrence();
            if(deref instanceof EdeMemVal){
                EdeMemVal memory = (EdeMemVal)deref;
                Value rightHandSideValue = interpretShallowExpression(assign.rightHandSide);
                Value indexValue = interpretShallowExpression(leftHandSide.index1);
                memory.setElemAtIndex(indexValue.intValue(), rightHandSideValue.intValue());
                return OpUtil.success();
            } else if(deref instanceof EdeRegVal){
                EdeRegVal register = (EdeRegVal)deref;
                Value rightHandSideValue = interpretShallowExpression(assign.rightHandSide);
                Value indexValue = interpretShallowExpression(leftHandSide.index1);
                register.setBitAtIndex(indexValue.intValue(), rightHandSideValue.intValue());
                return OpUtil.success();
            }
        } else if(assign.leftHandSide instanceof Slice){
            Slice leftHandSide = (Slice)assign.leftHandSide;
            Pointer<Value> val = environment.lookupVariable(leftHandSide.labelIdentifier);
            Value deref = val.deRefrence();
            if(deref instanceof EdeRegVal){
                EdeRegVal register = (EdeRegVal)deref;
                Value rightHandSideValue = interpretShallowExpression(assign.rightHandSide);
                Value index1Value = interpretShallowExpression(leftHandSide.index1);
                Value index2Value = interpretShallowExpression(leftHandSide.index2);
                register.setBitsAtIndex(index1Value.intValue(), index2Value.intValue(), rightHandSideValue.intValue());
                return OpUtil.success();
            }
        } else if(assign.leftHandSide instanceof Identifier){
            Identifier leftHandSide = (Identifier)assign.leftHandSide;
            Pointer<Value> val = environment.lookupVariable(leftHandSide.labelIdentifier);
            Value deref = val.deRefrence();
            if(deref instanceof EdeStatVal){
                EdeStatVal status = (EdeStatVal)deref;
                Value rightHandSide = interpretShallowExpression(assign.rightHandSide);
                status.setStatusValue(rightHandSide.intValue());
                return OpUtil.success();
            } else if(deref instanceof EdeRegVal){
                EdeRegVal reg = (EdeRegVal)deref;
                Value rightHandSide = interpretShallowExpression(assign.rightHandSide);
                reg.setAllBits(rightHandSide.intValue());
                return OpUtil.success();
            }
        }

        return super.interpretShallowBlockingAssignment(assign);
    }
}
