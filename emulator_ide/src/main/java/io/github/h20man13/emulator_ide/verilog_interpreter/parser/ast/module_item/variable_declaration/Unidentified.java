package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration;
import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.AstNode;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.ModuleItem;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ModuleVisitor;

public class Unidentified {
    public class Declaration extends AstNode implements ModuleItem{
        public final String declaration;
        
        public Declaration (Position start, String name){
            super(start);
            this.declaration = name;
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

        @Override
        public String toString(){
            return declaration;
        }

    }
}
