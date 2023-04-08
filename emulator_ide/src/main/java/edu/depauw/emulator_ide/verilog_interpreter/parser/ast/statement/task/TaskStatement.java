package edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement.task;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_interpreter.visitor_passes.visitor.StatementVisitor;
import java.util.List;

public class TaskStatement extends AstNode implements Statement {
    public final String     taskName;
    public final List<Expression> argumentList;

    public TaskStatement(Position start, String taskName, List<Expression> argumentList) {
        super(start);
        this.taskName = taskName;
        this.argumentList = argumentList;
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