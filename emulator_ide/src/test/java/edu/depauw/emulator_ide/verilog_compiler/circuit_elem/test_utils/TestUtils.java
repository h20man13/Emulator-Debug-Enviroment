package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils;


import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.nodes.Register;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.lang.StringBuilder;

public class TestUtils {

	private static void setRowInputs(LinkedList<Boolean> inputs, LinkedList<Register> realInputs){
		assertTrue("Total Registers provided does not match primative input size " + inputs.size(),
			realInputs.size() == inputs.size());
		int size = inputs.size();

		for (int i = 0; i < size; i++) { realInputs.get(i).setSignal(inputs.get(i)); }

	}

	private static void checkRowOutputs(LinkedList<Boolean> outputs, LinkedList<CircuitElem> realOutputs, int row){
		assertTrue("Total Registers provided does not match primative input size " + outputs.size(),
			realOutputs.size() == outputs.size());
		int size = outputs.size();

		for (int i = 0; i < size; i++) {
			StringBuilder sb = new StringBuilder("Error: unexpected output at row: ");
			sb.append(row);
			sb.append(" index: ");
			sb.append(i);
			sb.append(" [Got => ");
			sb.append(realOutputs.get(i).getStateSignal());
			sb.append(" | Expected => ");
			sb.append(outputs.get(i));
			sb.append(']');
			assertTrue(sb.toString(), realOutputs.get(i).getStateSignal() == outputs.get(i));
		}

	}

	public static void primitiveVerify(Primitive table, Tuple inputs, Tuple outputs){
		assertTrue("Total Registers provided does not match primative input size " + table.getNumInputs(),
			table.getNumInputs() == inputs.size());
		assertTrue("Outputs provided does not match primative output size " + table.getNumOutputs(),
			table.getNumOutputs() == outputs.size());
		int rows = table.getNumRows();
		LinkedList<Register> realInputs = inputs.getList();
		LinkedList<CircuitElem> realOutputs = outputs.getList();

		for (int i = 0; i < rows; i++) {
			setRowInputs(table.getRowInputs(i), realInputs); // Set the inputs to the next vales
			checkRowOutputs(table.getRowOutputs(i), realOutputs, i); // Check if the outputs match the expected values
		}

	}
}
