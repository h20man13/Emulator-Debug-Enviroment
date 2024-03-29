package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration;

import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ModuleVisitor;

public class Input {
    public final class Wire{
        public class Vector extends VectorDeclaration{
            public Vector(Expression exp1, Expression exp2){
                super(exp1, exp2);
            }
            public class Ident extends IdentDeclaration implements VectorDeclarationInterface{

                public Ident(Position start, String label){
                    super(start, label);
                }

                public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
                    return modVisitor.visit(this, argv);
                }

                @Override
                public Expression GetIndex1(){ 
                    return vectorIndex1;// TODO Auto-generated method stub
                }

                @Override
                public Expression GetIndex2(){ // TODO Auto-generated method stub
                    return vectorIndex2; 
                }

                @Override
                public String toString(){
                    StringBuilder sb = new StringBuilder();
                    sb.append("input wire [");
                    sb.append(GetIndex1().toString());
                    sb.append(":");
                    sb.append(GetIndex2().toString());
                    sb.append("] ");
                    sb.append(super.toString());
                    return sb.toString();
                }
            }
        }
        public final class Scalar{
            public class Ident extends IdentDeclaration{

                public Ident(Position start, String label){
                    super(start, label);
                }

                public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
                    return modVisitor.visit(this, argv);
                }

                @Override
                public String toString(){
                    StringBuilder sb = new StringBuilder();
                    sb.append("input wire ");
                    sb.append(super.toString());
                    return sb.toString();
                }
            }
        }
    }
    public final class Reg {
        public final class Vector extends VectorDeclaration{
            public Vector(Expression exp1, Expression exp2){
                super(exp1, exp2);
            }
            public class Ident extends IdentDeclaration implements VectorDeclarationInterface{
                public final String annotationLexeme;
                public Ident(Position start, String label){
                    super(start, label);
                    this.annotationLexeme = null;
                }

                public Ident(Position start, String annotationLexeme, String label){
                    super(start, label);
                    this.annotationLexeme = annotationLexeme;
                }

                public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
                    return modVisitor.visit(this, argv);
                }

                @Override
                public Expression GetIndex1(){ 
                    // TODO Auto-generated method stub
                    return vectorIndex1; 
                }

                @Override
                public Expression GetIndex2(){ // TODO Auto-generated method stub
                    return vectorIndex2; 
                }

                @Override
                public String toString(){
                    StringBuilder sb = new StringBuilder();
                    sb.append("input reg [");
                    sb.append(GetIndex1().toString());
                    sb.append(":");
                    sb.append(GetIndex2().toString());
                    sb.append("] ");
                    sb.append(super.toString());
                    return sb.toString();
                }
            }
        }
        public final class Scalar {
            public final class Ident extends IdentDeclaration{
                public final String annotationLexeme;

                public Ident(Position start, String annotationLexeme, String lexeme){
                    super(start, lexeme);
                    this.annotationLexeme = annotationLexeme;
                }

                public Ident(Position start, String lexeme){
                    super(start, lexeme);
                    this.annotationLexeme = null;
                }
        
                public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
                    return modVisitor.visit(this, argv);
                }

                @Override
                public String toString(){
                    StringBuilder sb = new StringBuilder();
                    sb.append("input reg ");
                    sb.append(super.toString());
                    return sb.toString();
                }
            }
        }
    }
}