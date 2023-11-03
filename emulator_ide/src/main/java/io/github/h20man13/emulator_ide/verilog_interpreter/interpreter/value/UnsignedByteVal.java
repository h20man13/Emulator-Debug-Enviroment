package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value;

public class UnsignedByteVal extends ByteVal implements Unsigned{

    public UnsignedByteVal(byte value){
        super(value);
    }

    public UnsignedByteVal(Integer value){
        super(value);
    }

    public String toString(){
        byte value = super.byteValue();
        return Integer.toUnsignedString(value);
    }

    @Override
    public boolean isByteValue(){
        return false;
    }

    @Override
    public boolean isUnsignedByteValue(){
        return true;
    }
    
}
