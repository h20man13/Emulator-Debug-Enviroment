package edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression;


import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.list.ExpressionList;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ExpressionVisitor;
import java.util.List;
import java.util.ArrayList;

/**
 * The FunctionCall is used to call functions Functions are different than tasks because
 * they have a return value
 * 
 * @author Jacob Bauer
 */

public class FunctionCall extends Expression {

    private final Identifier     functionName; // name of the function
    private final ExpressionList expList;      // parameter Expressions

    /**
     * The FunctionCall constructor takes two arguments:
     * 
     * @param functionName name of the function
     */
    public FunctionCall(Identifier functionName) {
        super(functionName.getPosition());
        this.functionName = functionName;
        this.expList = new ExpressionList(new ArrayList<>());
    }

    public FunctionCall(Identifier functionName, ExpressionList expList) {
        super(functionName.getPosition());
        this.functionName = functionName;
        this.expList = expList;
    }

    /**
     * This function returns an identifier representing the function name
     * 
     * @param none
     */
    public Identifier getFunctionName(){ return this.functionName; }

    /**
     * This function returns an.expression from the expression list at the s Ecified index
     * 
     * @param index of the.expression
     */
    public Expression getExpression(int index){ return expList.getExpression(index); }

    /**
     * This function returns an.expression from the expression list at the s Ecified index
     * 
     * @param index of the.expression
     */
    public void setExpression(int index, Expression exp){ this.expList.setExpression(index, exp); }

    /**
     * This function returns the.expression List size
     * 
     * @param none
     */
    public int numExpressions(){ return expList.getSize(); }

    /**
     * The accept method will make it so the visitor interface will work
     * 
     * @param astNodeVisitor the visitor object we want to use to visit another member of a
     *                       class
     */
    public <ExprVisitType> ExprVisitType accept(ExpressionVisitor<ExprVisitType> exprVisitor, Object... argv){
        return exprVisitor.visit(this, argv);
    }
}
