package edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration;

import edu.depauw.emulator_ide.verilog_compiler.parser.ast.expression.Expression;

public interface VectorDeclarationInterface {
    Expression GetIndex1();
    Expression GetIndex2();
}
