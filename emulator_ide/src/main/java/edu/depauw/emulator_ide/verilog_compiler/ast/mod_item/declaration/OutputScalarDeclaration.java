package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration;

import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;
import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModItem;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.ConstantExpression;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.IdentifierList;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Identifier;

public class OutputScalarDeclaration extends ScalarDeclaration{

    private final IdentifierList identList;
    
    public OutputScalarDeclaration(IdentifierList identList){
	super(identList.getIdentifier(0).getPosition());
	this.identList = identList;
    }

    public Identifier getIdentifier(int index){
	return identList.getIdentifier(index);
    }

    public int numIdentifiers(){
	return this.identList.getSize();
    }

    public <ModVisitType, StatVisitType, ExprVisitType> ModVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor){
	return astNodeVisitor.visit(this);
    }
    
}
