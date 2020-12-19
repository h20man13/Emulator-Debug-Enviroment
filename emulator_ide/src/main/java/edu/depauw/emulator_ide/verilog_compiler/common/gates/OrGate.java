package edu.depauw.emulator_ide.verilog_compiler.common.gates;

public class OrGate extends Gate{

    public OrGate(CurcuitElem output, CircuitElem input1, CircuitElem input2, CurcuitElem optionalInputs){
	super(output, input1, input2, optionalInputs);
    }
    
    public void update(){
	if(outputSignal == false){
	    for(CircuitElem input : inputs){
		if(input.getSignal() == true){
		    outputSignal = true;
		    super.updateOutputs();
		    break;
		}
	    }	
	} else {
	    for(CircuitElem input : inputs){
		if(input.getSignal() == true){
		    return;
		}
	    }
	    outputSignal = false;
	    super.updateOutputs();
	}
    }

    public boolean getSignal(){
	return super.getSignal();
    }
}