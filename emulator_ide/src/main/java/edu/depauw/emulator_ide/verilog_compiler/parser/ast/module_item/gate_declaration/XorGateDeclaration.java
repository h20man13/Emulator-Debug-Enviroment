package edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.gate_declaration;


import java.util.List;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.visitor.ModuleVisitor;

public class XorGateDeclaration extends GateDeclaration< List<Expression> > {
    public XorGateDeclaration(Position start, List<Expression> inputList) { super(start, inputList); }

    /**
     * The ast node visitor will allow the user to pass down data through the argument
     * vector. The accept method is needed to know which visit method to run.
     * 
     * @author Jacob Bauer
     */
    public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
        return modVisitor.visit(this, argv);
    }

}
