package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.procedure_declaration;

import java.util.List;
import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.ModuleItem;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.Statement;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ModuleVisitor;

public class FunctionDeclaration extends ProcedureDeclaration {
    public final ModuleItem      functionName;

    public FunctionDeclaration(Position start, ModuleItem functionName, List<ModuleItem> paramaters, Statement stat) {
        super(start, paramaters, stat);
        this.functionName = functionName;
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
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("function ");
        sb.append(functionName.toString());
        sb.append('\n');
        sb.append(super.toString());
        sb.append("endfunction\n");
        return sb.toString();
    }

}
