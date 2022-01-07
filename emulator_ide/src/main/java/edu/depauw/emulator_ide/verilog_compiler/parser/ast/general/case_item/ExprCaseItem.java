package edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.case_item;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.list.ExpressionList;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.Statement;

public class ExprCaseItem extends CaseItem {

    private final ExpressionList expList;
    private final Statement      stat;

    public ExprCaseItem(ExpressionList expList, Statement stat) {
        super(expList.getExpression(0).getPosition());
        this.expList = expList;
        this.stat = stat;
    }

    public Expression getExpression(int index){ return expList.getExpression(index); }

    public void setExpression(int index, Expression exp){ this.expList.setExpression(index, exp); }

    public int numExpressions(){ return expList.getSize(); }

    public Statement getStatement(){ return stat; }
}
