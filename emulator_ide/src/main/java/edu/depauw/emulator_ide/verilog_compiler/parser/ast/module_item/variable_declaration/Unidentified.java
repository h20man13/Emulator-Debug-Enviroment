package edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.AstNode;
import edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.ModuleItem;
import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ModuleVisitor;

public class Unidentified {
    public class Declaration extends AstNode implements ModuleItem{

        private final String declaration;
        
        public Declaration (Position start, String name){
            super(start);
            this.declaration = name;
        }

        public String getDeclaration(){
            return declaration;
        }

        /**
         * The ast node visitor will allow the user to pass down data through the argument
         * vector. The accept method is needed to know which visit method to run.
         * 
         * @author Jacob Bauer
         */
        public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
            return modVisitor.visit(this, argv);
        }

    }
}
