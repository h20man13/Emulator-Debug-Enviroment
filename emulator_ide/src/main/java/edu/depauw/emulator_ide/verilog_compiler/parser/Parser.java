package edu.depauw.emulator_ide.verilog_compiler.parser;


import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.common.debug.ErrorLog;
import edu.depauw.emulator_ide.common.debug.item.ErrorItem;
import edu.depauw.emulator_ide.verilog_compiler.token.Token;
import edu.depauw.emulator_ide.verilog_compiler.data_structure.Context;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.case_item.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.general.list.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item.declaration.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.mod_item.gate_declaration.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.reg_value.*;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.statement.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {

	private final List<Token>               lexedTokens;
	private final ErrorLog                  errorLog;
	private Context context;

	/**
	 * This is the consturctor to the parser class
	 * 
	 * @param tokenArray array of token objects passed into this by the lexer
	 * @param errorLog   errorLog to print error messages
	 */

	public Parser(List<Token> tokens, ErrorLog errorLog) {
		this.context = Context.getContext();

		this.lexedTokens = Lexer.filterWhiteSpace(tokens);
		
		this.errorLog = errorLog;
	}

	private boolean willMatch(Token.Type... types){
		for(int i = 0; i < types.length; i++){
			if(willMatch(types[i])){
				return true;
			}
		}
		return false;
	}

	private boolean willMatch(Token.Type type){
		if (lexedTokens.isEmpty()) return false;

		return lexedTokens.get(0).getTokenType() == type;
	}

	private void errorAndExit(String message){
		errorAndExit(message, null);
	}

	private void errorAndExit(String message, Position position){
		errorLog.addItem(new ErrorItem(message, position));
		errorLog.printLog();
		System.exit(1);
	}

	private Token skip(){
		if (!lexedTokens.isEmpty()) return lexedTokens.remove(0);

		errorAndExit("Unexpected end of file while skipping token");
		return null;
	}

	private boolean skipIfYummy(Token.Type type){
		if (!willMatch(type)) return false;

		skip();
		return true;
	}

	private Token peek(){
		if (!lexedTokens.isEmpty()) return lexedTokens.get(0);

		errorAndExit("Unexpected end of file while peeking at token");
		return null;
	}

	private enum STRATEGY{REPAIR, SKIP, EXIT}

	private Token match(Token.Type type){
		return match(type, STRATEGY.EXIT);
	}

	private Token match(Token.Type type, STRATEGY strategy){

		if (lexedTokens.isEmpty()) { // if token isnt matched then
			errorAndExit("Unexpected end of file while matching " + type);
			return null;
		} else if (willMatch(type)) { // return the token if it is matched
			return skip();
		} else {
			Token matched = peek();

			ErrorItem errorItem = new ErrorItem("Token of type " + type + " expected but token of type " + matched.getTokenType() + " found ", matched.getPosition());
			errorLog.addItem(errorItem);
			//Depending on the error strategy exit the program, skip tokens, or repair a token
			if(strategy == STRATEGY.SKIP){
				while(!willMatch(type, Token.Type.EOF))
					skip();
				return null;
			} else if(strategy == STRATEGY.EXIT){
				errorLog.printLog();
				System.exit(1);
				return null;
			} else {
				return null;
			}
		}

	}

	public ModuleDeclaration parseAST(){
		ModuleDeclaration modDec = parseModuleDeclaration();
		return modDec;
	}

	/**
	 * Below is the code for dealing with parsing Module Declarations
	 * 
	 * @author Jacob Bauer
	 */

	// ModuleDeclaration -> MODULE IDENT ( ModuleDeclarationList ) ; ModItemList ENDMODULE
	private ModuleDeclaration parseModuleDeclaration(){
		match(Token.Type.MODULE);
		Identifier ident = parseIdentifier();

		if (skipIfYummy(Token.Type.LPAR)) {

			DeclarationList declList;

			if (willMatch(Token.Type.RPAR))
				declList = new DeclarationList(new ArrayList<>());
			else
				declList = parseModuleParDeclarationList();

			match(Token.Type.RPAR, STRATEGY.REPAIR);
			match(Token.Type.SEMI, STRATEGY.REPAIR);

			if (skipIfYummy(Token.Type.ENDMODULE)) return new ModuleDeclaration(ident, declList);

			ModItemList modList = parseModItemList();
			match(Token.Type.ENDMODULE, STRATEGY.REPAIR);
			return new ModuleDeclaration(ident, declList, modList);

		} else {
			match(Token.Type.SEMI, STRATEGY.REPAIR);

			if (skipIfYummy(Token.Type.ENDMODULE)) return new ModuleDeclaration(ident);

			ModItemList modList = parseModItemList();
			match(Token.Type.ENDMODULE, STRATEGY.REPAIR);
			return new ModuleDeclaration(ident, modList);
		}

	}

	/**
	 * Below is all of the code for parsing Module Items.
	 * 
	 * @author Jacob Bauer
	 */

	// ModuleDeclarationList -> ModuleParam ModuleDeclarationListRest
	// ModuleDeclarationListRest -> , ModuleParam ModuleDeclarationListRest | NULL
	private DeclarationList parseModuleParDeclarationList(){
		ArrayList<Declaration> declList = new ArrayList<>();

		do {
			Declaration decl = parseModuleParDeclaration();
			declList.add(decl);
		} while(skipIfYummy(Token.Type.COMMA));

		return new DeclarationList(declList);
	}

	// ModuleDeclaration -> OutputDeclaration | InputDeclaration | OutputWireDeclaration |
	// OutputRegDeclaration | InputWireDeclaration
	private Declaration parseModuleParDeclaration(){

		if (skipIfYummy(Token.Type.INPUT)) {

			skipIfYummy(Token.Type.WIRE);

			if (skipIfYummy(Token.Type.LBRACK)) {

				ConstantExpression exp1 = parseConstantExpression();
				match(Token.Type.COLON, STRATEGY.REPAIR);
				ConstantExpression exp2 = parseConstantExpression();
				match(Token.Type.RBRACK, STRATEGY.REPAIR);

				Identifier ident = parseIdentifier();

				ArrayList<Identifier> identList = new ArrayList<>();
				identList.add(ident);

				IdentifierList list = new IdentifierList(identList);

				return new InputWireVectorDeclaration(exp1, exp2, list);
			} else {
				Identifier ident = parseIdentifier();
				ArrayList<Identifier> identList = new ArrayList<>();
				identList.add(ident);

				return new InputWireScalarDeclaration(new IdentifierList(identList));
			}

		} else if (skipIfYummy(Token.Type.OUTPUT)) {

			if (skipIfYummy(Token.Type.REG)) {

				if (skipIfYummy(Token.Type.LBRACK)) {

					ConstantExpression exp1 = parseConstantExpression();
					match(Token.Type.COLON, STRATEGY.REPAIR);
					ConstantExpression exp2 = parseConstantExpression();
					match(Token.Type.RBRACK, STRATEGY.REPAIR);

					Identifier ident = parseIdentifier();

					ArrayList<RegValue> regValList = new ArrayList<>();
					regValList.add(new RegVectorIdent(ident));

					return new OutputRegVectorDeclaration(exp1, exp2, new RegValueList(regValList));
				} else {
					Identifier ident = parseIdentifier();
					ArrayList<RegValue> regList = new ArrayList<>();
					regList.add(new RegScalarIdent(ident));
					return new OutputRegScalarDeclaration(new RegValueList(regList));
				}

			} else if (willMatch(Token.Type.WIRE)) {
				skip();

				if (willMatch(Token.Type.LBRACK)) {
					skip();
					ConstantExpression exp1 = parseConstantExpression();
					match(Token.Type.COLON, STRATEGY.REPAIR);
					ConstantExpression exp2 = parseConstantExpression();
					match(Token.Type.RBRACK, STRATEGY.REPAIR);
					Identifier ident = parseIdentifier();
					ArrayList<Identifier> identList = new ArrayList<>();
					identList.add(ident);
					return new OutputWireVectorDeclaration(exp1, exp2, new IdentifierList(identList));
				} else {
					Identifier ident = parseIdentifier();
					ArrayList<Identifier> identList = new ArrayList<>();
					identList.add(ident);
					return new OutputWireScalarDeclaration(new IdentifierList(identList));
				}

			} else {

				if (willMatch(Token.Type.LBRACK)) {
					skip();
					ConstantExpression exp1 = parseConstantExpression();
					match(Token.Type.COLON, STRATEGY.REPAIR);
					ConstantExpression exp2 = parseConstantExpression();
					match(Token.Type.RBRACK, STRATEGY.REPAIR);
					Identifier ident = parseIdentifier();
					ArrayList<Identifier> identList = new ArrayList<>();
					identList.add(ident);
					return new OutputWireVectorDeclaration(exp1, exp2, new IdentifierList(identList));
				} else {
					Identifier ident = parseIdentifier();
					ArrayList<Identifier> identList = new ArrayList<>();
					identList.add(ident);
					return new OutputWireScalarDeclaration(new IdentifierList(identList));
				}

			}

		} else {
			Identifier ident = parseIdentifier();
			return new UnidentifiedDeclaration(ident);
		}

	}

	// ModItem -> Function | Task | IntegerDeclaration | RealDeclaration | OutputDeclaration
	// | InitialDeclaration | AllwaysDeclaration | RegDeclaration | ContinuousAssignment |
	// ModuleInstantiation | GateDeclaration
	private ModItem parseModItem(){

		if (willMatch(Token.Type.FUNCTION)) return parseFunctionDeclaration();
		else if (willMatch(Token.Type.TASK)) return parseTaskDeclaration();
		else if (willMatch(Token.Type.INTEGER)) return parseIntegerDeclaration();
		else if (willMatch(Token.Type.REAL)) return parseRealDeclaration();
		else if (willMatch(Token.Type.INITIAL)) return parseInitialStatement();
		else if (willMatch(Token.Type.ALLWAYS)) return parseAllwaysStatement();
		else if (willMatch(Token.Type.REG)) return parseRegDeclaration();
		else if (willMatch(Token.Type.WIRE)) return parseWireDeclaration();
		else if (willMatch(Token.Type.ASSIGN)) return parseContinuousAssignment();
		else if (willMatch(Token.Type.IDENT)) return parseModInstantiation();
		else if (willMatch(Token.Type.OUTPUT)) {
			
			skip();

			if (willMatch(Token.Type.WIRE)) {
				return parseOutputWireDeclaration();
			} else if (willMatch(Token.Type.REG)) {
				return parseOutputRegDeclaration();
			} else {
				return parseOutputDeclaration();
			}

		} else if (willMatch(Token.Type.INPUT)) {
			
			skip();

			if (willMatch(Token.Type.WIRE)) {
				return parseInputWireDeclaration();
			} else {
				return parseInputDeclaration();
			}

		} else if (willMatch(Token.Type.ANDGATE, Token.Type.ORGATE, Token.Type.NANDGATE, Token.Type.NORGATE, Token.Type.NOTGATE, Token.Type.XNORGATE, Token.Type.XORGATE)){
			return parseGateDeclaration();
	    } else {
			Token matched = peek();
			errorAndExit("Unexpected ModItem token of type " + matched.getTokenType() + " and lexeme " + matched.getLexeme() + " found", matched.getPosition());
			return null;
		}

	}

	// ModItemList -> ModItem ModItemListRest | NULL
	// ModItemListRest -> ModItem ModItemListRest | NULL
	private ModItemList parseModItemList(){
		List<ModItem> modList = new ArrayList<>();

		while(!willMatch(Token.Type.ENDMODULE)) {
			ModItem modItem = parseModItem();
			modList.add(modItem);
		}

		return new ModItemList(modList);
	}

	// Function -> Function FunctionName DeclarationList Statement ENDFUNCTION
	private ModItem parseFunctionDeclaration(){
		match(Token.Type.FUNCTION);
		Declaration decl = parseFunctionName();
		match(Token.Type.SEMI);
		DeclarationList declList = parseDeclarationList(true);
		Statement stat = parseStatement();
		match(Token.Type.ENDFUNCTION);
		return new FunctionDeclaration(decl, declList, stat);
	}

	// FunctionName -> (REG | REG [ : ] | INTEGER | REAL) IDENT | UNIDENTIFIED
	private Declaration parseFunctionName(){

		if (willMatch(Token.Type.REG)) {
			skip();

			if (willMatch(Token.Type.LBRACK)) {
				skip();
				ConstantExpression exp1 = parseConstantExpression();
				match(Token.Type.COLON);
				ConstantExpression exp2 = parseConstantExpression();
				match(Token.Type.RBRACK);
				Identifier ident = parseIdentifier();
				ArrayList<RegValue> exprList = new ArrayList<>();
				exprList.add(new RegVectorIdent(ident));
				return new RegVectorDeclaration(exp1, exp2, new RegValueList(exprList));
			} else {
				Identifier ident = parseIdentifier();
				ArrayList<RegValue> expList = new ArrayList<>();
				expList.add(new RegScalarIdent(ident));
				return new RegScalarDeclaration(new RegValueList(expList));
			}

		} else if (willMatch(Token.Type.INTEGER)) {
			skip();
			Identifier ident = parseIdentifier();
			ArrayList<RegValue> exprList = new ArrayList<>();
			exprList.add(new IntegerIdent(ident));
			return new IntegerDeclaration(new RegValueList(exprList));
		} else if (willMatch(Token.Type.REAL)) {
			skip();
			Identifier ident = parseIdentifier();
			ArrayList<Identifier> identList = new ArrayList<>();
			identList.add(ident);
			return new RealDeclaration(new IdentifierList(identList));
		} else if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);
			Identifier ident = parseIdentifier();
			ArrayList<RegValue> exprList = new ArrayList<>();
			exprList.add(new RegVectorIdent(ident));
			return new RegVectorDeclaration(exp1, exp2, new RegValueList(exprList));
		} else {
			Identifier ident = parseIdentifier();
			ArrayList<RegValue> expList = new ArrayList<>();
			expList.add(new RegScalarIdent(ident));
			return new RegScalarDeclaration(new RegValueList(expList));
		}

	}

	// Task -> TASK IDENT ; DeclarationList StatementOrNull ENDTASK
	private ModItem parseTaskDeclaration(){
		match(Token.Type.TASK);
		Identifier ident = parseIdentifier();
		match(Token.Type.SEMI);
		DeclarationList declList = parseDeclarationList(false);
		Statement stat = parseStatementOrNull();
		match(Token.Type.ENDTASK);
		return new TaskDeclaration(ident, declList, stat);
	}

	// Declaration -> IntegerDeclaration | WireDeclaration | RealDeclaration |
	// RegDeclaration | OutputDeclaration | InputDeclaration
	private Declaration parseDeclaration(){

		if (willMatch(Token.Type.INTEGER)) {
			return parseIntegerDeclaration();
		} else if (willMatch(Token.Type.REAL)) {
			return parseRealDeclaration();
		} else if (willMatch(Token.Type.WIRE)) {
			return parseWireDeclaration();
		} else if (willMatch(Token.Type.REG)) {
			return parseRegDeclaration();
		} else if (willMatch(Token.Type.INPUT)) {
			skip();

			if (willMatch(Token.Type.WIRE)) {
				return parseInputWireDeclaration();
			} else if (willMatch(Token.Type.REG)) {
				return parseInputRegDeclaration();
			} else {
				return parseInputDeclaration();
			}

		} else if (willMatch(Token.Type.OUTPUT)) {
			skip();

			if (willMatch(Token.Type.REG)) {
				return parseOutputRegDeclaration();
			} else if (willMatch(Token.Type.WIRE)) {
				return parseOutputWireDeclaration();
			} else {
				return parseOutputDeclaration();
			}

		} else {
			Token matched = peek();
			errorLog.addItem(new ErrorItem("Unexpected Declaration token of type " + matched.getTokenType() + " and lexeme "
				+ matched.getLexeme() + " found", matched.getPosition()));
			errorLog.printLog();
			System.exit(1);
			return null;
		}

	}

	// DeclarationList -> NULL | Declaration DeclarationListRest
	// DeclarationListRest -> Declaration DeclarationListRest | NULL
	private DeclarationList parseDeclarationList(boolean atLeastOne){
		List<Declaration> declList = new ArrayList<>();

		if (atLeastOne) { declList.add(parseDeclaration()); }

		while(willMatch(Token.Type.INTEGER) || willMatch(Token.Type.REAL) || willMatch(Token.Type.WIRE)
			|| willMatch(Token.Type.REG) || willMatch(Token.Type.INPUT) || willMatch(Token.Type.OUTPUT)) {
			declList.add(parseDeclaration());
		}

		return new DeclarationList(declList);
	}

	// AllwaysStatement -> Allways Statement
	private ModItem parseAllwaysStatement(){
		match(Token.Type.ALLWAYS);
		Statement stat = parseStatement();
		return new AllwaysStatement(stat);
	}

	// InitialStatement -> Initial Statement
	private ModItem parseInitialStatement(){
		match(Token.Type.INITIAL);
		Statement stat = parseStatement();
		return new InitialStatement(stat);
	}

	// ContinuousAssignment -> ASSIGN AssignmentList ;
	private ModItem parseContinuousAssignment(){
		match(Token.Type.ASSIGN);
		AssignmentList assignList = parseAssignmentList();
		match(Token.Type.SEMI);
		return new ContinuousAssignment(assignList);
	}

	// RegDeclaration -> REG RegValueList ; | REG [ ConstExpression : ConstExpression ]
	// RegValueList ;
	private Declaration parseRegDeclaration(){
		match(Token.Type.REG);

		if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);
			RegValueList identList = parseRegVectorValueList();
			match(Token.Type.SEMI);
			return new RegVectorDeclaration(exp1, exp2, identList);
		} else {
			RegValueList identList = parseRegScalarValueList();
			match(Token.Type.SEMI);
			return new RegScalarDeclaration(identList);
		}

	}

	// OutputRegDeclaration -> OUTPUT REG RegValueList ; | OUTPUT REG [ ConstExpression :
	// ConstExpression ] RegValueList ;
	private Declaration parseOutputRegDeclaration(){
		match(Token.Type.REG);

		if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);
			RegValueList identList = parseOutputRegVectorValueList();
			match(Token.Type.SEMI);
			return new OutputRegVectorDeclaration(exp1, exp2, identList);
		} else {
			RegValueList identList = parseOutputRegScalarValueList();
			match(Token.Type.SEMI);
			return new OutputRegScalarDeclaration(identList);
		}

	}

	// WireDeclaration -> WIRE IdentifierList ; | WIRE [ ConstExpression : ConstExpression ]
	// IdentifierList ;
	private Declaration parseWireDeclaration(){
		match(Token.Type.WIRE);

		if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);
			IdentifierList identList = parseIdentifierList();
			match(Token.Type.SEMI);
			return new WireVectorDeclaration(exp1, exp2, identList);
		} else {
			IdentifierList identList = parseIdentifierList();
			match(Token.Type.SEMI);
			return new WireScalarDeclaration(identList);
		}

	}

	// OutputWireDeclaration -> INPUT WIRE IdentifierList ; | INPUT WIRE [ ConstExpression :
	// ConstExpression ] IdentifierList ;
	private Declaration parseOutputWireDeclaration(){
		match(Token.Type.WIRE);

		if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);
			IdentifierList identList = parseIdentifierList();
			match(Token.Type.SEMI);
			return new OutputWireVectorDeclaration(exp1, exp2, identList);
		} else {
			IdentifierList identList = parseIdentifierList();
			match(Token.Type.SEMI);
			return new OutputWireScalarDeclaration(identList);
		}

	}

	// InputWireDeclaration -> INPUT WIRE IdentifierList ; | INPUT WIRE [ ConstExpression :
	// ConstExpression ] IdentifierList ;
	private Declaration parseInputWireDeclaration(){
		match(Token.Type.WIRE);

		if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);
			IdentifierList identList = parseIdentifierList();
			match(Token.Type.SEMI);
			return new InputWireVectorDeclaration(exp1, exp2, identList);
		} else {
			IdentifierList identList = parseIdentifierList();
			match(Token.Type.SEMI);
			return new InputWireScalarDeclaration(identList);
		}

	}

	// InputWireDeclaration -> INPUT REG IdentifierList ; | INPUT REG [ ConstExpression :
	// ConstExpression ] IdentifierList ;
	private Declaration parseInputRegDeclaration(){
		match(Token.Type.REG);

		if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);
			IdentifierList identList = parseIdentifierList();
			match(Token.Type.SEMI);
			return new InputRegVectorDeclaration(exp1, exp2, identList);
		} else {
			IdentifierList identList = parseIdentifierList();
			match(Token.Type.SEMI);
			return new InputRegScalarDeclaration(identList);
		}

	}

	// OutputRegDeclaration -> INPUT IdentifierList ; | INPUT [ ConstExpression :
	// ConstExpression ] IdentifierList ;
	private Declaration parseInputDeclaration(){

		if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);
			IdentifierList identList = parseIdentifierList();
			match(Token.Type.SEMI);
			return new InputWireVectorDeclaration(exp1, exp2, identList);
		} else {
			IdentifierList identList = parseIdentifierList();
			match(Token.Type.SEMI);
			return new InputWireScalarDeclaration(identList);
		}

	}

	// OutputDeclaration -> OUTPUT IdentifierList ; | OUTPUT [ ConstExpression :
	// ConstExpression ] IdentifierList ;
	private Declaration parseOutputDeclaration(){

		if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);
			IdentifierList identList = parseIdentifierList();
			match(Token.Type.SEMI);
			return new OutputWireVectorDeclaration(exp1, exp2, identList);
		} else {
			IdentifierList identList = parseIdentifierList();
			match(Token.Type.SEMI);
			return new OutputWireScalarDeclaration(identList);
		}

	}

	// RealDeclaration -> REAL IdentifierList ;
	private Declaration parseRealDeclaration(){
		match(Token.Type.REAL);
		IdentifierList identList = parseIdentifierList();
		match(Token.Type.SEMI);
		return new RealDeclaration(identList);
	}

	// IntegerDeclaration -> INTEGER IdentifierList ;
	private Declaration parseIntegerDeclaration(){
		match(Token.Type.INTEGER);
		RegValueList identList = parseIntegerValueList();
		match(Token.Type.SEMI);
		return new IntegerDeclaration(identList);
	}

	// GateDeclaration -> GATYPE ( ExpressionList );
	private ModItem parseGateDeclaration(){

		if (willMatch(Token.Type.ORGATE)) {
			skip();
			match(Token.Type.LPAR);
			ExpressionList expList = parseExpressionList();
			match(Token.Type.RPAR);
			match(Token.Type.SEMI);
			return new OrGateDeclaration(expList);
		} else if (willMatch(Token.Type.ANDGATE)) {
			skip();
			match(Token.Type.LPAR);
			ExpressionList expList = parseExpressionList();
			match(Token.Type.RPAR);
			match(Token.Type.SEMI);
			return new AndGateDeclaration(expList);
		} else if (willMatch(Token.Type.NANDGATE)) {
			skip();
			match(Token.Type.LPAR);
			ExpressionList expList = parseExpressionList();
			match(Token.Type.RPAR);
			match(Token.Type.SEMI);
			return new NandGateDeclaration(expList);
		} else if (willMatch(Token.Type.NORGATE)) {
			skip();
			match(Token.Type.LPAR);
			ExpressionList expList = parseExpressionList();
			match(Token.Type.RPAR);
			match(Token.Type.SEMI);
			return new NorGateDeclaration(expList);
		} else if (willMatch(Token.Type.XORGATE)) {
			skip();
			match(Token.Type.LPAR);
			ExpressionList expList = parseExpressionList();
			match(Token.Type.RPAR);
			match(Token.Type.SEMI);
			return new XorGateDeclaration(expList);
		} else if (willMatch(Token.Type.XNORGATE)) {
			skip();
			match(Token.Type.LPAR);
			ExpressionList expList = parseExpressionList();
			match(Token.Type.RPAR);
			match(Token.Type.SEMI);
			return new XnorGateDeclaration(expList);
		} else if (willMatch(Token.Type.NOTGATE)) {
			match(Token.Type.NOTGATE);
			match(Token.Type.LPAR);
			ExpressionList expList = parseExpressionList();
			match(Token.Type.RPAR);
			match(Token.Type.SEMI);
			return new NotGateDeclaration(expList);
		} else {
			Token matched = peek();
			errorLog.addItem(new ErrorItem("Unexpected GateDeclaration token of type " + matched.getTokenType() + " and lexeme "
				+ matched.getLexeme() + " found", matched.getPosition()));
			errorLog.printLog();
			System.exit(1);
			return null;
		}

	}

	// ModInstantiation -> IDENT ModuleInstanceList
	private ModItem parseModInstantiation(){
		Identifier ident = parseIdentifier();
		ModInstanceList modList = parseModInstanceList();
		match(Token.Type.SEMI);
		return new ModInstantiation(ident, modList);
	}

	// ModInstanceList -> ModInstance ModInstanceListRest
	// ModInstance -> , ModInstance ModInstanceListRest | null
	private ModInstanceList parseModInstanceList(){
		List<ModInstance> modList = new ArrayList<>();
		ModInstance inst = parseModInstance();
		modList.add(inst);

		while(willMatch(Token.Type.COMMA)) {
			skip();
			inst = parseModInstance();
			modList.add(inst);
		}

		return new ModInstanceList(modList);
	}

	// ModInstance -> IDENT ( ExpressionList )
	private ModInstance parseModInstance(){
		Identifier ident = parseIdentifier();
		match(Token.Type.LPAR);
		ExpressionList expList;

		if (willMatch(Token.Type.DOT)) {
			expList = parsePortConnectionList();
		} else {
			expList = parseExpressionOrNullList();
		}

		match(Token.Type.RPAR);
		return new ModInstance(ident, expList);
	}

	/**
	 * Below is the code for parsing statements aswell as CaseItems
	 * 
	 * @author Jacob Bauer
	 */

	// Statement -> IfStatement | CaseXStatement | CaseStatement | CaseZStatement |
	// ForeverStatement | RepeatStatement | WhileStatement | ForStatement | WaitStatement |
	// SeqBlock | NonBlockAssign | ContinuousAssign | BlockAssign | NONBlockAssign |
	// TaskCall
	public Statement parseStatement(){

		if (willMatch(Token.Type.IF)) return parseIfStatement();
		else if (willMatch(Token.Type.CASE)) return parseCaseStatement();
		else if (willMatch(Token.Type.CASEZ)) return parseCaseZStatement();
		else if (willMatch(Token.Type.CASEX)) return parseCaseXStatement();
		else if (willMatch(Token.Type.FOREVER)) return parseForeverStatement();
		else if (willMatch(Token.Type.REPEAT)) return parseRepeatStatement();
		else if (willMatch(Token.Type.WHILE)) return parseWhileStatement();
		else if (willMatch(Token.Type.FOR)) return parseForStatement();
		else if (willMatch(Token.Type.WAIT)) return parseWaitStatement();
		else if (willMatch(Token.Type.BEGIN)) return parseSeqBlock();
		else if (willMatch(Token.Type.ASSIGN)) {
			skip();
			Statement stat = parseAssignment();
			match(Token.Type.SEMI);
			return stat;
		} else if (willMatch(Token.Type.DOLLAR)) { // system tasks
			skip();
			Identifier ident = parseIdentifier();

			if (willMatch(Token.Type.SEMI)) {
				skip();
				return new SystemTaskStatement(ident);
			} else {
				match(Token.Type.LPAR);

				if (willMatch(Token.Type.RPAR)) {
					skip();
					match(Token.Type.SEMI);
					return new SystemTaskStatement(ident);
				} else {
					ExpressionList expList = parseExpressionList();
					match(Token.Type.RPAR);
					match(Token.Type.SEMI);
					return new SystemTaskStatement(ident, expList);
				}

			}

		} else if (willMatch(Token.Type.LCURL)) {
			Expression concat = parseConcatenation();

			if (willMatch(Token.Type.EQ1)) { // it is a blocking assignment
				skip();
				Expression exp = parseExpression();
				Statement stat = new BlockAssign(concat, exp);
				match(Token.Type.SEMI);
				return stat;
			} else { // it is a non blocking assignment
				match(Token.Type.LE);
				Expression exp = parseExpression();
				Statement stat = new NonBlockAssign(concat, exp);
				match(Token.Type.SEMI);
				return stat;
			}

		} else { // lvalue or task_enable
			Identifier ident = parseIdentifier();

			if (willMatch(Token.Type.LPAR)) {
				skip();

				if (willMatch(Token.Type.RPAR)) {
					skip();
					match(Token.Type.SEMI);
					return new TaskStatement(ident);
				} else {
					ExpressionList expList = parseExpressionList();
					match(Token.Type.RPAR);
					match(Token.Type.SEMI);
					return new TaskStatement(ident, expList);
				}

			} else if (willMatch(Token.Type.SEMI)) {
				skip();
				return new TaskStatement(ident);
			} else if (willMatch(Token.Type.LBRACK)) { // It must be an assignment
				skip();
				Expression exp1 = parseExpression();

				if (willMatch(Token.Type.RBRACK)) {
					skip();
					Expression vec = new VectorElement(ident, exp1);

					if (willMatch(Token.Type.EQ1)) { // it is a blocking assignment
						skip();
						Expression exp = parseExpression();
						Statement stat = new BlockAssign(vec, exp);
						match(Token.Type.SEMI);
						return stat;
					} else { // it is a non blocking assignment
						match(Token.Type.LE);
						Expression exp = parseExpression();
						Statement stat = new NonBlockAssign(vec, exp);
						match(Token.Type.SEMI);
						return stat;
					}

				} else {
					match(Token.Type.COLON);
					ConstantExpression exp2 = parseConstantExpression();
					match(Token.Type.RBRACK);
					Expression vec = new VectorSlice(ident, new ConstantExpression(exp1), exp2);

					if (willMatch(Token.Type.EQ1)) { // it is a blocking assignment
						skip();
						Expression exp = parseExpression();
						Statement stat = new BlockAssign(vec, exp);
						match(Token.Type.SEMI);
						return stat;
					} else { // it is a non blocking assignment
						match(Token.Type.LE);
						Expression exp = parseExpression();
						Statement stat = new NonBlockAssign(vec, exp);
						match(Token.Type.SEMI);
						return stat;
					}

				}

			} else if (willMatch(Token.Type.EQ1)) { // it is a blocking assignment
				skip();
				Expression exp = parseExpression();
				Statement stat = new BlockAssign(ident, exp);
				match(Token.Type.SEMI);
				return stat;
			} else if (willMatch(Token.Type.LE)) { // it is a non blocking assignment
				skip();
				Expression exp = parseExpression();
				Statement stat = new NonBlockAssign(ident, exp);
				match(Token.Type.SEMI);
				return stat;
			} else {
				Token matched = peek();
				errorAndExit("Unexpected Statement token of type " + matched.getTokenType() + " and lexeme "
				+ matched.getLexeme() + " found", matched.getPosition());
				return null;
			}

		}

	}

	// StatementOrNull -> {Statement | NULL} ;
	private Statement parseStatementOrNull(){

		if (willMatch(Token.Type.SEMI)) {
			Token sem = skip();
			return new EmptyStatement(sem.getPosition());
		} else {
			return parseStatement();
		}

	}

	// StatementList -> Statement StatementList | NULL
	private StatementList parseStatementList(){
		List<Statement> statList = new ArrayList<>();

		if (!willMatch(Token.Type.END)) {

			do {
				Statement stat = parseStatement();
				statList.add(stat);
			} while(!willMatch(Token.Type.END));

		}

		return new StatementList(statList);
	}

	// CaseItemList -> CaseItemList CaseItem
	private CaseItemList parseCaseItemList(){
		List<CaseItem> caseList = new ArrayList<>();
		CaseItem item = parseCaseItem();
		caseList.add(item);

		while(!willMatch(Token.Type.ENDCASE)) {
			item = parseCaseItem();
			caseList.add(item);
		}

		return new CaseItemList(caseList);
	}

	// CaseItem -> DEFAULT : Statement | DEFAULT Statement | ExpressionList : Statement
	private CaseItem parseCaseItem(){

		if (willMatch(Token.Type.DEFAULT)) {
			skip();

			if (willMatch(Token.Type.COLON)) { skip(); }

			Statement stat = parseStatementOrNull();
			return new DefCaseItem(stat);
		} else {
			ExpressionList expList = parseExpressionList();
			match(Token.Type.COLON, STRATEGY.REPAIR);
			Statement stat = parseStatementOrNull();
			return new ExprCaseItem(expList, stat);
		}

	}

	// IfStatement -> IF ( expression ) StatementOrNull
	// IfElseStatement -> IF ( expression ) StatementOrNull ELSE StatementOrNull
	private Statement parseIfStatement(){
		match(Token.Type.IF, STRATEGY.REPAIR);
		match(Token.Type.LPAR, STRATEGY.REPAIR);
		Expression expr = parseExpression();
		match(Token.Type.RPAR, STRATEGY.REPAIR);
		Statement stat = parseStatementOrNull();

		if (willMatch(Token.Type.ELSE)) {
			skip();
			Statement stat2 = parseStatementOrNull();
			return new IfElseStatement(expr, stat, stat2);
		} else {
			return new IfStatement(expr, stat);
		}

	}

	// ForStatement -> FOR ( Assignment ; Expression ; Assignment ) Statement
	private Statement parseForStatement(){
		match(Token.Type.FOR, STRATEGY.REPAIR);
		match(Token.Type.LPAR, STRATEGY.REPAIR);
		Assignment init = parseAssignment();
		match(Token.Type.SEMI, STRATEGY.REPAIR);
		Expression expr = parseExpression();
		match(Token.Type.SEMI, STRATEGY.REPAIR);
		Assignment change = parseAssignment();
		match(Token.Type.RPAR, STRATEGY.REPAIR);
		Statement stat = parseStatement();
		return new ForStatement(init, expr, change, stat);
	}

	// Assignment -> LValue = Expression
	private Assignment parseAssignment(){
		Expression exp = parseLValue();
		match(Token.Type.EQ1, STRATEGY.REPAIR);
		Expression exp1 = parseExpression();
		return new Assignment(exp, exp1);
	}

	// AssignmentList -> Assignment AssignmentListRest
	// AssignmentListRest -> , Assignment AssignmentListRest | NULL
	private AssignmentList parseAssignmentList(){
		List<Assignment> assignList = new ArrayList<>();
		assignList.add(parseAssignment());

		while(willMatch(Token.Type.COMMA)) {
			skip();
			assignList.add(parseAssignment());
		}

		return new AssignmentList(assignList);
	}

	// CaseStatement -> CASE ( Expression ) CaseItemList ENDCASE
	private Statement parseCaseStatement(){
		match(Token.Type.CASE, STRATEGY.REPAIR);
		match(Token.Type.LPAR, STRATEGY.REPAIR);
		Expression exp = parseExpression();
		match(Token.Type.RPAR, STRATEGY.REPAIR);
		CaseItemList caseList = parseCaseItemList();
		match(Token.Type.ENDCASE, STRATEGY.REPAIR);
		return new CaseStatement(exp, caseList);
	}

	// CaseZStatement -> CASEZ ( Expression ) CaseItemList ENDCASE
	private Statement parseCaseZStatement(){
		match(Token.Type.CASEZ, STRATEGY.REPAIR);
		match(Token.Type.LPAR, STRATEGY.REPAIR);
		Expression exp = parseExpression();
		match(Token.Type.RPAR, STRATEGY.REPAIR);
		CaseItemList caseList = parseCaseItemList();
		match(Token.Type.ENDCASE, STRATEGY.REPAIR);
		return new CaseZStatement(exp, caseList);
	}

	// CaseXStatement -> CASEX ( Expression ) CaseItemList ENDCASE
	private Statement parseCaseXStatement(){
		match(Token.Type.CASEX, STRATEGY.REPAIR);
		match(Token.Type.LPAR, STRATEGY.REPAIR);
		Expression exp = parseExpression();
		match(Token.Type.RPAR, STRATEGY.REPAIR);
		CaseItemList caseList = parseCaseItemList();
		match(Token.Type.ENDCASE, STRATEGY.REPAIR);
		return new CaseXStatement(exp, caseList);
	}

	// ForeverStatement -> FOREVER Statement
	private Statement parseForeverStatement(){
		match(Token.Type.FOREVER, STRATEGY.REPAIR);
		Statement stat = parseStatement();
		return new ForeverStatement(stat);
	}

	// RepeatStatement -> REPEAT Statement
	private Statement parseRepeatStatement(){
		match(Token.Type.REPEAT, STRATEGY.REPAIR);
		match(Token.Type.LPAR, STRATEGY.REPAIR);
		Expression exp = parseExpression();
		match(Token.Type.RPAR, STRATEGY.REPAIR);
		Statement stat = parseStatement();
		return new RepeatStatement(exp, stat);
	}

	// WhileStatement -> WHILE Statement
	private Statement parseWhileStatement(){
		match(Token.Type.WHILE, STRATEGY.REPAIR);
		match(Token.Type.LPAR, STRATEGY.REPAIR);
		Expression exp = parseExpression();
		match(Token.Type.RPAR, STRATEGY.REPAIR);
		Statement stat = parseStatement();
		return new WhileStatement(exp, stat);
	}

	// WaitStatement -> WAIT Statement
	private Statement parseWaitStatement(){
		match(Token.Type.WAIT, STRATEGY.REPAIR);
		match(Token.Type.LPAR, STRATEGY.REPAIR);
		Expression exp = parseExpression();
		match(Token.Type.RPAR, STRATEGY.REPAIR);
		Statement stat = parseStatementOrNull();
		return new WaitStatement(exp, stat);
	}

	// SeqBlock -> BEGIN StatementList END
	private Statement parseSeqBlock(){
		match(Token.Type.BEGIN, STRATEGY.REPAIR);
		StatementList statList = parseStatementList();
		match(Token.Type.END, STRATEGY.REPAIR);
		return new SeqBlockStatement(statList);
	}

	/**
	 * Below is the code for parsing expressions for the verilog lanuage. The recursive
	 * decent takes into account all operator precedence and it can also be used to parse
	 * strings. There are no booleans in Verilog so having a boolean would not make a bunch
	 * of sense.
	 * 
	 * @author Jacob Bauer
	 */

	// Expression -> STRING | LOR_Expression

	public Expression parseExpression(){

		if (willMatch(Token.Type.STRING)) {
			Token string = skip();
			return new StrValue(string);
		} else {
			Expression expression = parseLOR_Expression();

			if (willMatch(Token.Type.QUEST)) {
				skip();
				Expression left = parseExpression();
				match(Token.Type.COLON);
				Expression right = parseExpression();
				expression = new TernaryOperation(expression, left, right);
			}

			return expression;
		}

	}

	// ExpressionOrNull -> Expression | NULL (ex: a, b, ,d)
	// This is mainly used for Module Items
	private Expression parseExpressionOrNull(){

		if (willMatch(Token.Type.COMMA)) {
			Token comma = peek();
			return new EmptyExpression(comma.getPosition());
		} else {
			return parseExpression();
		}

	}

	// lvalue -> IDENT | IDENT [ Expression ] | IDENT [ Expression : Expression ] |
	// Concatenation
	private Expression parseLValue(){

		if (willMatch(Token.Type.LCURL)) {
			return parseConcatenation();
		} else {
			Token ident = match(Token.Type.IDENT);

			if (willMatch(Token.Type.LBRACK)) {
				skip();
				Expression exp = parseExpression();

				if (willMatch(Token.Type.RBRACK)) {
					skip();
					return new VectorElement(new Identifier(ident), exp);
				} else {
					match(Token.Type.COLON);
					ConstantExpression exp2 = parseConstantExpression();
					match(Token.Type.RBRACK);
					return new VectorSlice(new Identifier(ident), new ConstantExpression(exp), exp2);
				}

			} else {
				return new Identifier(ident);
			}

		}

	}

	// RegIntegerValue -> IDENT [ ConstExpr : ConstExpr ] | IDENT
	private RegValue parseIntegerValue(){
		Token ident = match(Token.Type.IDENT);

		if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);
			return new IntegerArray(new Identifier(ident), exp1, exp2);
		} else {
			return new IntegerIdent(new Identifier(ident));
		}

	}

	// RegVectorValue -> IDENT [ ConstExpr : ConstExpr ] | IDENT
	private RegValue parseRegVectorValue(){
		Token ident = match(Token.Type.IDENT);

		if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);
			return new RegVectorArray(new Identifier(ident), exp1, exp2);
		} else {
			return new RegVectorIdent(new Identifier(ident));
		}

	}

	// RegVectorValue -> IDENT [ ConstExpr : ConstExpr ] | IDENT
	private RegValue parseOutputRegVectorValue(){
		Token ident = match(Token.Type.IDENT);

		if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);
			return new OutputRegVectorArray(new Identifier(ident), exp1, exp2);
		} else {
			return new OutputRegVectorIdent(new Identifier(ident));
		}

	}

	// RegScalarValue -> IDENT [ ConstExpr : ConstExpr ] | IDENT
	private RegValue parseRegScalarValue(){
		Token ident = match(Token.Type.IDENT);

		if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);
			return new RegScalarArray(new Identifier(ident), exp1, exp2);
		} else {
			return new RegScalarIdent(new Identifier(ident));
		}

	}

	// RegScalarValue -> IDENT [ ConstExpr : ConstExpr ] | IDENT
	private RegValue parseOutputRegScalarValue(){
		Token ident = match(Token.Type.IDENT);

		if (willMatch(Token.Type.LBRACK)) {
			skip();
			ConstantExpression exp1 = parseConstantExpression();
			match(Token.Type.COLON);
			ConstantExpression exp2 = parseConstantExpression();
			match(Token.Type.RBRACK);
			return new OutputRegScalarArray(new Identifier(ident), exp1, exp2);
		} else {
			return new OutputRegScalarIdent(new Identifier(ident));
		}

	}

	// RegValueList -> RegValue RegValueRest
	// RegValueRest -> , RegValue RegValueRest | NULL
	private RegValueList parseIntegerValueList(){
		List<RegValue> expList = new ArrayList<>();

		expList.add(parseIntegerValue());

		while(willMatch(Token.Type.COMMA)) {
			skip();
			RegValue exp = parseIntegerValue();
			expList.add(exp);
		}

		return new RegValueList(expList);
	}

	private RegValueList parseRegScalarValueList(){
		List<RegValue> expList = new ArrayList<>();

		expList.add(parseRegScalarValue());

		while(willMatch(Token.Type.COMMA)) {
			skip();
			RegValue exp = parseRegScalarValue();
			expList.add(exp);
		}

		return new RegValueList(expList);
	}

	private RegValueList parseOutputRegScalarValueList(){
		List<RegValue> expList = new ArrayList<>();

		expList.add(parseRegScalarValue());

		while(willMatch(Token.Type.COMMA)) {
			skip();
			RegValue exp = parseOutputRegScalarValue();
			expList.add(exp);
		}

		return new RegValueList(expList);
	}

	private RegValueList parseOutputRegVectorValueList(){
		List<RegValue> expList = new ArrayList<>();

		expList.add(parseRegVectorValue());

		while(willMatch(Token.Type.COMMA)) {
			skip();
			RegValue exp = parseOutputRegVectorValue();
			expList.add(exp);
		}

		return new RegValueList(expList);
	}

	private RegValueList parseRegVectorValueList(){
		List<RegValue> expList = new ArrayList<>();

		expList.add(parseRegVectorValue());

		while(willMatch(Token.Type.COMMA)) {
			skip();
			RegValue exp = parseRegVectorValue();
			expList.add(exp);
		}

		return new RegValueList(expList);
	}

	// ConstantExpression -> expression
	private ConstantExpression parseConstantExpression(){
		Expression constant = parseExpression();
		return new ConstantExpression(constant);
	}

	// ExpressionList -> Expression ExpressionListRest
	// ExpressionListRest -> , Expression ExpressionListRest | null
	private ExpressionList parseExpressionList(){
		List<Expression> expList = new ArrayList<>();
		expList.add(parseExpression());

		while(willMatch(Token.Type.COMMA)) {
			skip();
			Expression exp = parseExpression();
			expList.add(exp);
		}

		return new ExpressionList(expList);
	}

	// ExpressionOrNullList -> ExpressionOrNull ExpressionOrNullListRest
	// ExpressionOrNullListRest -> , ExpressionOrNull ExpressionOrNullListRest
	private ExpressionList parseExpressionOrNullList(){
		List<Expression> expList = new ArrayList<>();

		Expression exp = parseExpressionOrNull();
		expList.add(exp);

		while(willMatch(Token.Type.COMMA)) {
			skip();
			exp = parseExpressionOrNull();
			expList.add(exp);
		}

		return new ExpressionList(expList);
	}

	// PortConnectionList -> PortConnection PortConnectionListRest
	// PortConnectionListRest -> , PortConenction
	private ExpressionList parsePortConnectionList(){
		List<Expression> expList = new ArrayList<>();

		Expression exp = parsePortConnection();
		expList.add(exp);

		while(willMatch(Token.Type.COMMA)) {
			skip();
			exp = parsePortConnection();
			expList.add(exp);
		}

		return new ExpressionList(expList);
	}

	// PortConnection -> . IDENT ( Expression )
	private Expression parsePortConnection(){
		match(Token.Type.DOT, STRATEGY.REPAIR);
		Identifier ident = parseIdentifier();
		match(Token.Type.LPAR, STRATEGY.REPAIR);
		Expression exp = parseExpression();
		match(Token.Type.RPAR, STRATEGY.REPAIR);
		return new PortConnection(ident, exp);
	}

	// LOR_Expression -> LAND_Expression BinOp LAND_Expression
	private Expression parseLOR_Expression(){
		Expression left = parseLAND_Expression();

		while(willMatch(Token.Type.LOR)) {
			Token opToken = skip();
			Token.Type opType = opToken.getTokenType();
			Expression right = parseLAND_Expression();
			left = new BinaryOperation(left, BinaryOperation.tokentoBinOp(opType), right);
		}

		return left;
	}

	// LAND_Expression -> BOR_Expression BinOp BOR_Expression
	private Expression parseLAND_Expression(){
		Expression left = parseBOR_Expression();

		while(willMatch(Token.Type.LAND)) {
			Token opToken = skip();
			Token.Type opType = opToken.getTokenType();
			Expression right = parseBOR_Expression();
			left = new BinaryOperation(left,  BinaryOperation.tokentoBinOp(opType), right);
		}

		return left;
	}

	// BOR_Expression -> BXOR_Expression BinOp BXOR_Expression
	private Expression parseBOR_Expression(){
		Expression left = parseBXOR_Expression();

		while(willMatch(Token.Type.BOR, Token.Type.BNOR)) {
			Token opToken = skip();
			Token.Type opType = opToken.getTokenType();
			Expression right = parseBXOR_Expression();
			left = new BinaryOperation(left,  BinaryOperation.tokentoBinOp(opType), right);
		}

		return left;
	}

	// BXOR_Expression -> BAND_Expression BinOp BAND_Expression
	private Expression parseBXOR_Expression(){
		Expression left = parseBAND_Expression();

		while(willMatch(Token.Type.BXOR, Token.Type.BXNOR)) {
			Token opToken = skip();
			Token.Type opType = opToken.getTokenType();
			Expression right = parseBAND_Expression();
			left = new BinaryOperation(left, BinaryOperation.tokentoBinOp(opType), right);
		}

		return left;
	}

	// BAND_Expression -> NE_Expression BinOp NE_Expression
	private Expression parseBAND_Expression(){
		Expression left = parseNE_Expression();

		while(willMatch(Token.Type.BNAND, Token.Type.BAND)) {
			Token opToken = skip();
			Token.Type opType = opToken.getTokenType();
			Expression right = parseNE_Expression();
			left = new BinaryOperation(left, BinaryOperation.tokentoBinOp(opType), right);
		}

		return left;
	}

	// NE_Expression -> REL_Expression BinOp REL_Expression
	private Expression parseNE_Expression(){
		Expression left = parseREL_Expression();

		while(willMatch(Token.Type.NE1, Token.Type.NE2, Token.Type.EQ2, Token.Type.EQ3)) {
			Token opToken = skip();
			Token.Type opType = opToken.getTokenType();
			Expression right = parseREL_Expression();
			left = new BinaryOperation(left, BinaryOperation.tokentoBinOp(opType), right);
		}

		return left;
	}

	// REL_Expression -> SHIFT_Expression BinOp SHIFT_Expression
	private Expression parseREL_Expression(){
		Expression left = parseSHIFT_Expression();

		if(willMatch(Token.Type.GE, Token.Type.GT, Token.Type.LT, Token.Type.LE)) {
			Token opToken = skip();
			Token.Type opType = opToken.getTokenType();
			Expression right = parseSHIFT_Expression();
			left = new BinaryOperation(left, BinaryOperation.tokentoBinOp(opType), right);
		}

		return left;
	}

	// SHIFT_Expression -> BIN_Expression BinOp BIN_Expression
	private Expression parseSHIFT_Expression(){
		Expression left = parseBIN_Expression();

		while(willMatch(Token.Type.LSHIFT, Token.Type.RSHIFT)) {
			Token opToken = skip();
			Token.Type opType = opToken.getTokenType();
			Expression right = parseBIN_Expression();
			left = new BinaryOperation(left, BinaryOperation.tokentoBinOp(opType), right);
		}

		return left;
	}

	// BIN_Expression -> MULT_Expression BinOp MULT_Expression
	private Expression parseBIN_Expression(){
		Expression left = parseMULT_Expression();

		while(willMatch(Token.Type.PLUS, Token.Type.MINUS)) {
			Token opToken = skip();
			Token.Type opType = opToken.getTokenType();
			Expression right = parseMULT_Expression();
			left = new BinaryOperation(left, BinaryOperation.tokentoBinOp(opType), right);
		}

		return left;
	}

	// MULT_Expression -> UNARY_Expression BinOp UNARY_Expression
	private Expression parseMULT_Expression(){
		Expression left = parseUNARY_Expression();

		while(willMatch(Token.Type.TIMES, Token.Type.MOD, Token.Type.DIV)) {
			Token opToken = skip();
			Token.Type opType = opToken.getTokenType();
			Expression right = parseUNARY_Expression();
			left = new BinaryOperation(left, BinaryOperation.tokentoBinOp(opType), right);
		}

		return left;
	}

	// UNARY_Expression -> UnOp Primary | Primary
	private Expression parseUNARY_Expression(){

		if (willMatch(Token.Type.PLUS, Token.Type.MINUS, Token.Type.BNEG, Token.Type.LNEG, Token.Type.BAND, Token.Type.BNAND, Token.Type.BOR, Token.Type.BXOR, Token.Type.BXNOR)) {
			Token op = skip();
			Token.Type opType = op.getTokenType();
			Expression right = parsePrimary();
			return new UnaryOperation(UnaryOperation.tokenToUnaryOp(opType), right);
		} else {
			return parsePrimary();
		}

	}

	// Primary -> NumValue | IDENT | Concatenation | SystemCall | ( Expression ) |
	// MACROIDENT
	private Expression parsePrimary(){

		if (willMatch(Token.Type.NUM)) {
			return parseNumValue();
		} else if (willMatch(Token.Type.IDENT)) {
			Token identToken = skip();

			if (willMatch(Token.Type.LBRACK)) {
				skip();
				Identifier ident = parseIdentifier(identToken);
				Expression index1 = parseExpression();

				if (willMatch(Token.Type.COLON)) {
					skip();
					ConstantExpression index2 = parseConstantExpression();
					match(Token.Type.RBRACK);
					return new VectorSlice(ident, new ConstantExpression(index1), index2);
				} else {
					match(Token.Type.RBRACK);
					return new VectorElement(ident, index1);
				}

			} else if (willMatch(Token.Type.LPAR)) {
				skip();
				Identifier ident = parseIdentifier(identToken);

				if (!willMatch(Token.Type.RPAR)) {
					ExpressionList expList = parseExpressionList();
					match(Token.Type.RPAR);
					return new FunctionCall(ident, expList);
				} else {
					match(Token.Type.RPAR);
					return new FunctionCall(ident);
				}

			} else {
				Identifier ident = new Identifier(identToken);
				return ident;
			}

		} else if (willMatch(Token.Type.LCURL)) {
			return parseConcatenation();
		} else if (willMatch(Token.Type.LPAR)) {
			skip();
			Expression exp = parseExpression();
			match(Token.Type.RPAR);
			return exp;
		} else if (willMatch(Token.Type.DOLLAR)) {
			return parseSystemCall();
		} else {
			Token matched = peek();
			errorLog.addItem(new ErrorItem("Unexpected Primary Expression token of type " + matched.getTokenType()
				+ " and lexeme " + matched.getLexeme() + " found", matched.getPosition()));
			errorLog.printLog();
			System.exit(1);
			return null;
		}

	}

	// SystemCall -> $ IDENT ( ExpressionList )
	private Expression parseSystemCall(){
		match(Token.Type.DOLLAR);
		Token identToken = match(Token.Type.IDENT);
		Identifier ident = new Identifier(identToken);

		if (willMatch(Token.Type.LPAR)) {
			skip();
			ExpressionList expList = parseExpressionList();
			match(Token.Type.RPAR);
			return new SystemFunctionCall(ident, expList);
		} else {
			return new SystemFunctionCall(ident);
		}

	}

	// Concatenation -> { ExpressionList }
	private Expression parseConcatenation(){
		match(Token.Type.LCURL);
		ExpressionList expList = parseExpressionList();
		match(Token.Type.RCURL);
		return new Concatenation(expList);
	}

	// Identifier -> IDENT
	private Identifier parseIdentifier(Token identToken){ return new Identifier(identToken); }

	// Identifier -> IDENT
	private Identifier parseIdentifier(){
		Token ident = match(Token.Type.IDENT);
		return new Identifier(ident);
	}

	// IdentifierList -> Identifier IdentifierListRest
	// IdentifierListRest -> , Identifier IdentifierListRest | NULL
	private IdentifierList parseIdentifierList(){
		List<Identifier> identList = new ArrayList<>();

		identList.add(parseIdentifier());

		while(willMatch(Token.Type.COMMA)) {
			skip();
			identList.add(parseIdentifier());
		}

		return new IdentifierList(identList);
	}

	// NumValue -> NUM
	private NumValue parseNumValue(){
		Token numToken = match(Token.Type.NUM);
		return new NumValue(numToken);
	}

}
