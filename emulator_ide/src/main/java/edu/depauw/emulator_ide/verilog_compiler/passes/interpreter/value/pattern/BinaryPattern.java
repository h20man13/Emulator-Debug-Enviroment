package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.pattern;


import java.lang.Integer;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.ByteVal;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.IntVal;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.LongVal;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.ShortVal;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.Vector;
import edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value.circuit_elem.CircuitElem;

/**
 * The VectorElement class is used to par.E a call to an array cell
 * 
 * @author Jacob Bauer
 */

public class BinaryPattern extends Pattern{

    public BinaryPattern(String pattern) { super(pattern); }

    public boolean match(LongVal value){

        String pattern = super.getPattern();
        long val = value.getValue();

        int patternLength = pattern.length();

        if(patternLength < Long.toBinaryString(val).length()){
            long shiftedValue = val >> patternLength;
            if(shiftedValue != 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt((int)i);
            if (current == 'x' || current == 'z')
                continue;
            
            long patternPieceAsLong = Long.parseLong("" + current, 2);
            long shiftedValLong = val >> patternLength - i - 1;
            long maskedVal = shiftedValLong & 0b1; // or in binary 0b111

            if(maskedVal != patternPieceAsLong)
                return false;
        }

        return true;
    }

    public boolean match(IntVal value){

        String pattern = super.getPattern();
        int val = value.getValue();

        int patternLength = pattern.length();

        if(patternLength > 8){
            int num = value.getValue();
            LongVal newVal = new LongVal((long)num);
            return match(newVal);
        }

        if(patternLength < Integer.toBinaryString(val).length()){
            int shiftedValue = val >> patternLength;
            if(shiftedValue != 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            int patternPieceAsInt = Integer.parseInt("" + current, 2);
            int shiftedValInt = val >> patternLength - i - 1;
            int maskedVal = shiftedValInt & 0b1; // or 0b111 for short

            if(maskedVal != patternPieceAsInt)
                return false;
        }

        return true;
    }

    public boolean match(ShortVal value){

        String pattern = super.getPattern();
        short val = value.getValue();

        int patternLength = pattern.length();

        if(patternLength > 4){
            short num = value.getValue();
            IntVal newVal = new IntVal(num);
            return match(newVal);
        }

        if(patternLength * 3 < Integer.toBinaryString(val).length()){
            short shiftedValue = (short)(val >> patternLength);
            if(shiftedValue != 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            short patternPieceAsShort = Short.parseShort("" + current, 2);
            short shiftedValShort = (short)(val >> patternLength - i - 1);
            short maskedVal = (short)(shiftedValShort & 0b1);

            if(maskedVal != patternPieceAsShort)
                return false;
        }

        return true;
    }

    public boolean match(ByteVal value){

        String pattern = super.getPattern();
        byte val = value.getValue();

        int patternLength = pattern.length();

        if(patternLength > 2){
            byte num = value.getValue();
            ShortVal newVal = new ShortVal(num);
            return match(newVal);
        }

        if(patternLength < Integer.toBinaryString(val).length()){
            byte shiftedValue = (byte)(val >> patternLength);
            if(shiftedValue != 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            byte patternPieceAsByte = Byte.parseByte("" + current, 2);
            byte shiftedValByte = (byte)(val >> patternLength - i - 1);
            byte maskedVal = (byte)(shiftedValByte & 0b1);

            if(maskedVal != patternPieceAsByte)
                return false;
        }

        return true;
    }

    public boolean match(CircuitElem elem){
        byte elemSignalAsByte = (byte)(elem.getStateSignal() ? 1 : 0);
        ByteVal retByte = new ByteVal(elemSignalAsByte);
        return match(retByte);
    }

    public boolean match(Vector value){

        String pattern = super.getPattern();
        int patternLength = pattern.length();

        int bitIncr = (value.getIndex1() < value.getIndex2()) ? 1 : -1;
        int binIncr = bitIncr;

        int endOverflow = value.getIndex2() + ((value.getIndex1() > value.getIndex2())? patternLength - 1 : -patternLength + 1);

        if(patternLength * 4 < value.getSize()){
            for(int i = value.getIndex1(); i != endOverflow; i+=bitIncr){
                //If the vector length is grater then the pattern length * 4 then all of the signals need to be set to false
                //The reasoning is that if one of them is set true then it is impossible for the two elements to match because the one on the right is a bigger number
                if(value.getValue(i).getStateSignal()){
                    return false;
                }
            }
        }

        for (int i = 0, vi = endOverflow; i < patternLength; i++, vi+=binIncr) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            byte patternPieceAsByte = Byte.parseByte("" + current, 2);
            boolean patternSignal = patternPieceAsByte != 0;

            if(patternSignal != value.getValue(vi).getStateSignal()){
                return false;
            }
        }

        return true;
    }
}
