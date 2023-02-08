package edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item;

import edu.depauw.emulator_ide.verilog_compiler.interpreter.Environment;
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.visitor.ModuleVisitor;

public interface ModuleItem {
    /**
     * The ast node visitor will allow the user to pass down data through the argument
     * vector. The accept method is needed to know which visit method to run.
     * 
     * @author Jacob Bauer
     */
    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);
}
