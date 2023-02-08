package edu.depauw.emulator_ide.verilog_compiler.parser.ast;

import java.util.Collections;
import java.util.List;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.ModuleItem;

public class VerilogFile extends AstNode {
    /**
     * After a Verilog file has been preprocessed it is possible that
     * the user will end up with more then one module.
     * 
     * This is because of `include statement in verilog
     *      
     */
   public final List<ModuleDeclaration> modules;

   public VerilogFile(Position Pos, List<ModuleDeclaration> modules){
        super(Pos);
        this.modules = modules;
   }
}
