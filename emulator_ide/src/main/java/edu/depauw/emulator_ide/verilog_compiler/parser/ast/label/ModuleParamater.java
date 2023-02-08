package edu.depauw.emulator_ide.verilog_compiler.parser.ast.label;

import edu.depauw.emulator_ide.common.SymbolTable;
import edu.depauw.emulator_ide.verilog_compiler.data_structure.Pointer;

/**
 * The Identifier class is used to define an Identifier An identifier is any set of
 * characters that isnt considered a verilog keyword. This includes variable names,
 * function names, module names Etc...
 */

public interface ModuleParamater {
    public <DataType> Pointer<DataType> getModuleParamater(SymbolTable<Pointer<DataType>> environment);
}
