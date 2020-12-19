package edu.depauw.emulator_ide.verilog_compiler.common.gates;

import edu.depauw.emulator_ide.verilog_compiler.common.gates.*;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AndGateTest{
    @Test
    public void BasicAndTest(){
    	Gate reg1 = new Register(true);
    	Gate reg2 = new Register(false);
    	Gate andGate = new AndGate(reg1, reg2);
    	assertTrue(!andGate.getSignal());
    }
}
