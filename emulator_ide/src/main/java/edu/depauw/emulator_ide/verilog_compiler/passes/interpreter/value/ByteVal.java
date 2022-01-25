package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value;

public class ByteVal implements Value{
    
    private byte value;

    public ByteVal(byte value){
        this.value = value;
    }

    public double realValue(){
        return (double)value;
    }

    public long longValue(){
        return (long)value;
    }

    public int intValue(){
        return (int)value;
    }

    public short shortValue(){
        return (short)value;
    }

    public byte byteValue(){
        return (byte)value;
    }

    public boolean boolValue(){
        return value != 0;
    }


    public String toString(){
        return Byte.toString(value);
    }
    
}
