package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.gate_declaration;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.visitor.ModuleVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModItem;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.ExpressionList;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;

public abstract class GateDeclaration extends ModItem {

    private final ExpressionList exprList;

    protected GateDeclaration(ExpressionList exprList) {
        super(exprList.getExpression(0).getPosition());
        this.exprList = exprList;
    }

    protected ExpressionList getExpressionList(){ return exprList; }

    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);

}
