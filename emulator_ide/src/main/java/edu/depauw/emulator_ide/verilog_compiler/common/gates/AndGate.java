package edu.depauw.emulator_ide.verilog_compiler.common.gates;

public class AndGate extends Gate{

    public AndGate(Gate output, Gate... inputs){
	super(output, inputs);
    }
    
    public void update(){
	if(outputSignal == false){
		for(Gate input : inputs){
			if(input.getSignal() == false){
				return;
			}
		}
		outputSignal = true;
		super.updateOutputs();
	} else {
		for(Gate input : inputs){
			if(input.getSignal() == false){
				outputSignal = false;
				super.updateOutputs();
				break;
			}
		}
	}
    }

    public boolean getSignal(){
	return super.getSignal();
    }
}
