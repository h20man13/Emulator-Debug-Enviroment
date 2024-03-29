package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.operation;

import java.util.List;
import io.github.H20man13.emulator_ide.common.Pointer;
import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.common.SymbolTable;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.label.LValue;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ExpressionVisitor;

/**
 * The Concatenation class is used to par.E concatenation.expressions
 * Concatenation.expressions are used to group Wires together from different nets
 * 
 * @author Jacob Bauer
 */
public class Concatenation extends AstNode implements Expression, LValue{
    public final List<Expression> circuitElementExpressionList; // list of Expressions to concatenate

    /**
     * The concatenation.expression is used to Concatenate multiple instances
     * 
     * @param expList the.expressions which to concatenate
     */
    public Concatenation(Position start, List<Expression>  circuitElementExpressionList) {
        super(start);
        this.circuitElementExpressionList =  circuitElementExpressionList;
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
    public <DataType> Pointer<DataType> getLValue(SymbolTable<Pointer<DataType>> environment){ // TODO Auto-generated method stub
    return null; }

    @Override
    public String toString(){ // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        int expressionListSize = circuitElementExpressionList.size();
        sb.append('{');
        boolean first = true;
        for(int i = 0; i < expressionListSize; i++){
            Expression exp = circuitElementExpressionList.get(i);
            sb.append(exp.toString());
            if(i < expressionListSize - 1){
                sb.append(',');
            }
        }
        sb.append('}');
        return sb.toString();
    }
}
