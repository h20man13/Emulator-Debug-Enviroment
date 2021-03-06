package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.gates;

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem.Register;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem.Wire;

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils.Primitive;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils.Tuple;

import static edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils.TestUtils.*;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class NotGateTest {
    @Test
    public void NotTest(){
    	Register input = new Register(false);

	Wire in = new Wire();
	Wire out = new Wire();

	input.setOutput(in);
	
	CircuitElem norGate = new NotGate(out, in); //put wires through a basic nor gate
	
	Primitive table = new Primitive(1, 1); //two input one output table
	table.addRow(new Tuple<Boolean>(true), new Tuple<Boolean>(false));
	table.addRow(new Tuple<Boolean>(false), new Tuple<Boolean>(true));

	primitiveVerify(table, new Tuple<Register>(input), new Tuple<CircuitElem>(out)); //check if the boolean logic table playes out correctly in the circuit created
    }
}
