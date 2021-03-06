package  edu.depauw.emulator_ide.common.debug;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

import  edu.depauw.emulator_ide.common.io.Destination;
import  edu.depauw.emulator_ide.common.debug.item.ErrorItem;
import  edu.depauw.emulator_ide.verilog_compiler.token.Position;

import java.io.StringWriter;
import java.io.Writer;

public class InfoLogTest{
    
    @Test
    public void TestInfoLog(){
	Destination destination = new Destination(new StringWriter());
	Position position = new Position(0,0);
	InfoLog log = new InfoLog(destination);
	for(int i = 0; i < 30; i++){
	    log.addItem(new ErrorItem("" + i, position));
	}
	log.printLogNoFlush();
	assertTrue(!destination.getWriter().toString().equals(""));
	destination.flush();
	destination.close();
    }   
}


