package io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.procedure_declaration;

import java.util.List;
import io.github.h20man13.emulator_ide.common.Position;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.ModuleItem;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.statement.Statement;
import io.github.h20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ModuleVisitor;

public abstract class ProcedureDeclaration extends AstNode implements ModuleItem {
    public final List<ModuleItem> paramaters;
    public final Statement        stat;

    public ProcedureDeclaration(Position start, List<ModuleItem> paramaters, Statement stat) {
        super(start);
        this.paramaters = paramaters;
        this.stat = stat;
    }

    /**
     * The ast node visitor will allow the user to pass down data through the argument
     * vector. The accept method is needed to know which visit method to run.
     * 
     * @author Jacob Bauer
     */
    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);

}