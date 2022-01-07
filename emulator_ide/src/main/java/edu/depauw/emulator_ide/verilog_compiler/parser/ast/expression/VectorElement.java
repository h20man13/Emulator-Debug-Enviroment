package edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression;


import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ExpressionVisitor;

/**
 * The VectorElement class is used to par.E a call to an array cell
 * 
 * @author Jacob Bauer
 */

public class VectorElement extends Expression {

    private Expression       index1; // initial index to grap from the array
    private final Identifier ident;  // name of the array

    /**
     * The VectorElement constructor takes an identifier with up to twoindex to s Ecify the sub
     * array that is desired
     * 
     * @param ident  name of the array
     * @param index1 min index of the array
     * @param index2 max index of the array
     */

    public VectorElement(Identifier ident, Expression index1) {
        super(ident.getPosition());
        this.index1 = index1;
        this.ident = ident;
    }

    /**
     * The name of the array
     * 
     * @param none
     */
    public Identifier getIdentifier(){ return ident; }

    /**
     * Returns the starting index
     * 
     * @param none
     */
    public Expression getExpression(){ return index1; }

    /**
     * Returns the starting index
     * 
     * @param none
     */
    public void setExpression(Expression exp){ this.index1 = exp; }

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
