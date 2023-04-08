package edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement._case_.item;


import java.util.List;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_interpreter.visitor_passes.visitor.StatementVisitor;

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
}