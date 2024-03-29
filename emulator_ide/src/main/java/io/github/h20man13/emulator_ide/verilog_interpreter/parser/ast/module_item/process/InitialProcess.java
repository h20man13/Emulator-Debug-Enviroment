package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.process;

import java.util.concurrent.Semaphore;
import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.Interpreter;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.Statement;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ModuleVisitor;

public class InitialProcess extends ProcessBase{

    public InitialProcess(Position start, Statement statement) {
        super(start, statement);
    }

    public void executeProcess(Interpreter interpreter, Semaphore semaphore) throws Exception{
        interpreter.interpretShallowStatement(this.statement);
        semaphore.release();
    }

    /**
     * The ast node visitor will allow the user to pass down data through the argument
     * vector. The accept method is needed to know which visit method to run.
     * 
     * @author Jacob Bauer
     */
    public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
        return modVisitor.visit(this, argv);
    }

    @Override
    public String toString(){ // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        sb.append("intitial\n");
        sb.append(statement.toString());
        sb.append('\n');
        sb.append("end\n");
        return sb.toString();
    }
}
