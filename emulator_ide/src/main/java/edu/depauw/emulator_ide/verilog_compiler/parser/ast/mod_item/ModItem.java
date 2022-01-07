package edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item;


import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ModuleVisitor;
import edu.depauw.emulator_ide.common.Position;

public abstract class ModItem extends AstNode {

    protected ModItem(Position position) { super(position); }

    /**
     * The ast node visitor will allow the user to pass down data through the argument
     * vector. The accept method is needed to know which visit method to run.
     * 
     * @author Jacob Bauer
     */
    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);

}
