package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value;

import io.github.H20man13.emulator_ide.verilog_interpreter.Utils;

public class StrVal implements Value{
    
    private String value;

    public StrVal(String value){
        this.value = value;
    }

    public double realValue(){
        return Double.parseDouble(value);
    }

    public long longValue(){
        return Long.parseLong(value);
    }

    public int intValue(){
        return Integer.parseInt(value);
    }

    public short shortValue(){
        return Short.parseShort(value);
    }

    public byte byteValue(){
        return Byte.parseByte(value);
    }

    public boolean boolValue(){
        return Boolean.parseBoolean(value);
    }

    public String toString(){
        return value;
    }

    @Override
    public boolean isBoolValue(){ 
        return false; 
    }

    @Override
    public boolean isShortValue(){ 
        return false; 
    }

    @Override
    public boolean isUnsignedShortValue(){ 
        return false; 
    }

    @Override
    public boolean isByteValue(){ 
        return false; 
    }

    @Override
    public boolean isUnsignedByteValue(){ 
        return false; 
    }

    @Override
    public boolean isIntValue(){ 
        return false; 
    }

    @Override
    public boolean isUnsignedIntValue(){ 
        return false; 
    }

    @Override
    public boolean isLongValue(){ 
        return false; 
    }

    @Override
    public boolean isUnsignedLongValue(){ 
        return false; 
    }

    @Override
    public boolean isRealValue(){ 
        return false; 
    }

    @Override
    public boolean isStringValue(){ 
        return true; 
    }

    @Override
    public boolean isVector(){ 
        return false; 
    }

    @Override
    public boolean isRegister(){ 
        return false; 
    }

    @Override
    public boolean isWire(){ 
        return false; 
    }

    @Override
    public Value getShallowSlice(int startIndex, int endIndex) throws Exception{
        throw new UnsupportedOperationException("Cant perform a shallow slice operation on a String operand");
    }

    public int length(){
        return value.length();
    }

    public char charAt(int index){
        return value.charAt(index);
    }
}
