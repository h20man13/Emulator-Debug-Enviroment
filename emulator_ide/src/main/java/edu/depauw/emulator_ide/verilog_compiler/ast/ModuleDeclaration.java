package edu.depauw.emulator_ide.verilog_compiler.ast;

import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Identifier;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.ModItem;

import java.util.ArrayList;
    
public class ModuleDeclaration extends AstNode{

    private final Identifier ident;
    private final IdentifierList identList;
    private final ModItemList modItemList;
    
    public ModuleDeclaration(Identifier ident){
	super(ident.getPosition());
	this.ident = ident;
	this.identList = new IdentifierList(new ArrayList<>());
	this.modItemList = new ModItemList(new ArrayList<>());
    }

    public ModuleDeclaration(Identifier ident, IdentifierList identList){
	super(ident.getPosition());
	this.ident = ident;
	this.identList = identList;
	this.modItemList = new ModItemList(new ArrayList<>());
    }

    public ModuleDeclaration(Identifier ident, ModItemList modItemList){
	super(ident.getPosition());
	this.ident = ident;
	this.identList = new IdentifierList(new ArrayList<>());
	this.modItemList = modItemList;
    }

    public ModuleDeclaration(Identifier ident, IdentifierList identList, ModItemList modItemList){
	super(ident.getPosition());
	this.ident = ident;
	this.identList = identList;
	this.modItemList = modItemList;
    }

    public Identifier getModuleName(){
	return ident;
    }

    public Identifier getParameter(int index){
	return identList.getIdentifier(index);
    }

    public int  numParameters(){
	return identList.getSize();
    }

    public ModItem getModItem(int index){
	return modItemList.getModItem(index);
    }

    public int numModItems(){
	return modItemList.getSize();
    }
}
