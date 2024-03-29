package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression;


import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.Environment;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.Value;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ExpressionVisitor;

/**
 * The const.expression class is used to parse constant expressions.
 * This class actually
 * just wraps an expression and during the type checking phase we will determine
 * if it is const
 * 
 * @param Jacob Bauer
 */
public class ConstantExpression extends AstNode implements Expression {
    public Expression expression; // expression determined to be const

    /**
     * The const expression constructor takes in a single expression and decorates the object to becoome a constant constructor.
     * 
     * @param Jacob Bauer
     */
    public ConstantExpression(Position start, Expression expression) {
        super(start);
        this.expression = expression;
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
        return expression.toString();
    }
}
