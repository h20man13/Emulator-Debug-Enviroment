package edu.depauw.emulator_ide.verilog_compiler.parser.ast.label;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.common.SymbolTable;
import edu.depauw.emulator_ide.verilog_compiler.data_structure.Pointer;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.visitor_passes.visitor.ExpressionVisitor;

/**
 * The VectorElement class is used to par.E a call to an array cell
 * 
 * @author Jacob Bauer
 */

public class Element extends Label {

    public final Expression       index1; // initial index to grap from the array

    /**
     * The VectorElement constructor takes an identifier with up to twoindex to s Ecify the sub
     * array that is desired
     * 
     * @param ident  name of the array
     * @param index1 min index of the array
     * @param index2 max index of the array
     */

    public Element(Position start, String ident, Expression index1) {
        super(start, ident);
        this.index1 = index1;
    }

    public <DataType> Pointer<DataType> getLValue(SymbolTable<Pointer<DataType>> table){
        return null;
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
}
