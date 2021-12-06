package edu.depauw.emulator_ide.verilog_compiler.ast.reg_value;


import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Identifier;
import edu.depauw.emulator_ide.verilog_compiler.visitor.RegValueVisitor;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;

/**
 * The vectorslice class is used to parse a call to an array cell
 * 
 * @author Jacob Bauer
 */

public class OutputRegScalarIdent extends RegValue {

    private final Identifier ident; // name of the array

    /**
     * The VectorCall constructor takes an identifier with up to twoindex to specify the sub
     * array that is desired
     * 
     * @param ident  name of the array
     * @param index1 min index of the array
     * @param index2 max index of the array
     */
    public OutputRegScalarIdent(Identifier ident) {
        super(ident.getPosition());
        this.ident = ident;
    }

    /**
     * The name of the array
     * 
     * @param none
     */
    public Identifier getIdentifier(){ return ident; }

    /**
     * The accept method is used to visit VectorCalls
     * 
     * @param RegValueVisitor the visitor object visiting the unary operation
     */

    public <RegValVisitType> RegValVisitType accept(RegValueVisitor<RegValVisitType> RegValueVisitor, Object... argv){
        return RegValueVisitor.visit(this, argv);
    }
}
