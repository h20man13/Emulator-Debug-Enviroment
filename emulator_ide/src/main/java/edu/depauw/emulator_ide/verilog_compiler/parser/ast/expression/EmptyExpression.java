package edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression;

import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.Environment;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.Value;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ExpressionVisitor;
import edu.depauw.emulator_ide.common.Position;

/**
 * The empty.expression class is designed as a place holder to par.E an empty expression
 * 
 * @author Jacob Bauer
 */
public class EmptyExpression extends AstNode implements Expression {

    /**
     * The empty.expression constructor only takes a position then it pas Es that up to the
     * Expression constructor
     * 
     * @param position Position of the empty.expression
     */

    public EmptyExpression(Position start) { super(start); }

    /**
     * The accept method will make it so the visitor interface will work
     * 
     * @param astNodeVisitor the visitor object we want to use to visit another member of a
     *                       class
     */
    public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv){
        return exprVisitor.visit(this, argv);
    }

    public Value interpret(Environment environment){
        return null;
    }
}
