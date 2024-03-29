package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression;


import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ExpressionVisitor;

/**
 * The port connection class is used for module instantiations
 * 
 * @author Jacob Bauer
 */
public class PortConnection extends AstNode implements Expression {

    public final String connectingTo;// name of the port connection
    public final Expression connectingFrom;  // what the port connection connects to

    /**
     * The port connection constructor takes in an identifier
     * 
     * @param ident          name of the port connecting to
     * @param exp.expression representing what is being connected
     */
    public PortConnection(Position start, String connectingTo, Expression connectingFrom) {
        super(start);
        this.connectingTo= connectingTo;
        this.connectingFrom = connectingFrom;
    }

    /**
     * The accept method will make it so the visitor interface will work
     * 
     * @param astNodeVisitor the visitor object we want to use to visit another member of a
     *                       class
     */
    public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv){
        return exprVisitor.visit(this, argv);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append('.');
        sb.append(connectingTo);
        sb.append('(');
        sb.append(connectingFrom.toString());
        sb.append(')');
        return sb.toString();
    }

}
