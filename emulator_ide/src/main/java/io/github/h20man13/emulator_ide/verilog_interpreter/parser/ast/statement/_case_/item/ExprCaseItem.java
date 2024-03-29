package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement._case_.item;


import java.util.List;
import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.statement.Statement;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.StatementVisitor;

public class ExprCaseItem extends CaseItem {

    public final List<Expression> expList;

    public ExprCaseItem(Position start, List<Expression> expList, Statement stat) {
        super(start, stat);
        this.expList = expList;
    }

    /**
     * The accept method makes it possible so that nodes know which visitor object to call
     * the visit method from. This is needed because the Visitor method is an interface not
     * a class. All of the classes implementing ASTnode visitor will not have the required
     * dependencies.
     * 
     * @author Jacob Bauer
     */
    public <StatVisitType> StatVisitType accept(StatementVisitor<StatVisitType> statVisitor, Object... argv){
        return statVisitor.visit(this, argv);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        int exprListSize = expList.size();
        for(int i = 0; i < exprListSize; i++){
            Expression exp = expList.get(i);
            sb.append(exp.toString());
            if(i < exprListSize - 1){
                sb.append(", ");
            }
        }
        sb.append(" : ");
        sb.append(super.toString());
        return sb.toString();
    }
}
