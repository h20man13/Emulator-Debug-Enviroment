package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.circuitry;

import io.github.H20man13.emulator_ide.verilog_interpreter.Utils;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.VectorVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.nodes.gates.NotGate;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.circuit_elem.web.WireVal;

public class RippleCarrySubtractor {
    public RippleCarrySubtractor(WireVal CarryOut, VectorVal Output, VectorVal Input1, VectorVal Input2) throws Exception{
        if(Input1.getSize() == Input2.getSize()){
            int Start1 = Input1.getStart();
        int Start2 = Input2.getStart();
        int StartOut = Output.getStart();

        int End1 = Input1.getEnd();
        int End2 = Input2.getEnd();

        WireVal Elem1 = new WireVal();
        Elem1.assignInput(Input1.getValue(Start1));

        WireVal Elem2 = new WireVal();
        Elem2.assignInput(Input2.getValue(Start2));

        WireVal ElemOutput = new WireVal();
        ElemOutput.addOutput(Output.getValue(StartOut));

        WireVal CarryOutInner = new WireVal();
        new HalfAdder(CarryOutInner, CarryOut, Elem1, Elem2);
        
        Start1++;
        Start2++;
        StartOut++;
        while(Start1 != End1 && Start2 != End2){
            Elem1 = new WireVal();
            Elem1.assignInput(Input1.getValue(Start1));

            Elem2 = new WireVal();
            Elem2.assignInput(Input2.getValue(Start2));
            
            WireVal Elem2Real = new WireVal();
            new NotGate(Elem2Real, Elem2);

            Elem2 = Elem2Real;

            ElemOutput = new WireVal();
            ElemOutput.addOutput(Output.getValue(StartOut));

            WireVal CarryIn = CarryOutInner;
            CarryOutInner = new WireVal();
            new FullAdder(CarryOutInner, ElemOutput, Elem1, Elem2, CarryIn);
            
            Start1++;
            Start2++;
            StartOut++;
        }

        CarryOut.assignInput(CarryOutInner);
        } else {
            Utils.errorAndExit("Error: Cannot create a ripple caryy adder with Vectors of Two different size!!!");
        }
    }
}
