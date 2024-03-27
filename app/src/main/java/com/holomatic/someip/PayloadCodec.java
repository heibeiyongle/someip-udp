package com.holomatic.someip;


import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author 比才-贾硕哲
 * @time 22/3/2024 11:31
 * @desc
 */
public class PayloadCodec {

    private PayloadCodec(){
        mByteBuffer = ByteBuffer.allocate(mBufferSize);
        mByteBuffer.order(ByteOrder.BIG_ENDIAN);
    }

    private static PayloadCodec instance;

    public static PayloadCodec getInstance(){
        if (instance == null) {
            synchronized (PayloadCodec.class){
                if(instance == null){
                    instance = new PayloadCodec();
                }
            }
        }
        return instance;
    }
    private static final int defaultBufferSize = 1024*1024*2;

    private int mBufferSize = defaultBufferSize;
    ByteBuffer mByteBuffer;

    public void updateBufferSize(int bufferSize){
        mBufferSize = bufferSize;
    }

    public byte[] enCodeStruct(Object srcDto){
        if (mBufferSize != mByteBuffer.capacity()) {
            mByteBuffer = ByteBuffer.allocate(mBufferSize);
        }
        mByteBuffer.clear();
        enCodeStruct(mByteBuffer,srcDto);
        mByteBuffer.flip();
        int dateLen = mByteBuffer.remaining();
        byte[] res = new byte[dateLen];
        mByteBuffer.get(res);
        return res;
    }

    public Object deCodeStruct(byte[] data, Class template){
        return deCodeStruct(data,0,data.length,template);
    }


    public Object deCodeStruct(byte[] data,int offset, int length, Class template){
        mByteBuffer.clear();
        mByteBuffer.put(data,offset,length);
        mByteBuffer.flip();
        return deCodeStruct(mByteBuffer,template);
    }


    private void enCodeStruct(ByteBuffer bufDest, Object srcDto){
        bufDest.putInt(0);
        int dataOffset = bufDest.position();
        Class template = srcDto.getClass();
        Field[] fields = template.getDeclaredFields();
        for (Field fieldItem: fields) {
            fieldItem.setAccessible(true);
            try {
                Class type = fieldItem.getType();
                if(type.isPrimitive()){
                    if(type.equals(boolean.class)){
                        enCodeBaseType(bufDest,fieldItem.getBoolean(srcDto));
                    }else if(type.equals(byte.class)){
                        enCodeBaseType(bufDest,fieldItem.getByte(srcDto));
                    }else if(type.equals(char.class)){
                        enCodeBaseType(bufDest,fieldItem.getByte(srcDto));
                    }else if(type.equals(short.class)){
                        enCodeBaseType(bufDest,fieldItem.getShort(srcDto));
                    }else if(type.equals(int.class)){
                        enCodeBaseType(bufDest,fieldItem.getInt(srcDto));
                    }else if(type.equals(long.class)){
                        enCodeBaseType(bufDest,fieldItem.getLong(srcDto));
                    }else if(type.equals(float.class)){
                        enCodeBaseType(bufDest,fieldItem.getFloat(srcDto));
                    }else if(type.equals(double.class)){
                        enCodeBaseType(bufDest,fieldItem.getDouble(srcDto));
                    }
                }else if(String.class.equals(type)){
                    enCodeString(bufDest, (String) fieldItem.get(srcDto));
                }else if(type.isArray()){
                    enCodeArray(bufDest,fieldItem.get(srcDto));
                }else {
                    /* 此处重点关注*/
                    enCodeStruct(bufDest,fieldItem.get(srcDto));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }

        int len = bufDest.position() - dataOffset;
        bufDest.putInt(dataOffset -4, len);

    }



    // big end
    private void enCodeBaseType(ByteBuffer buffer, byte value){
        buffer.put(value);
    }

    private void enCodeBaseType(ByteBuffer buffer, boolean value){
        buffer.put((byte) (value?1:0));
    }

    private void enCodeBaseType(ByteBuffer buffer, short value){
        buffer.putShort(value);
    }

    private void enCodeBaseType(ByteBuffer buffer, int value){
        buffer.putInt(value);
    }

    private void enCodeBaseType(ByteBuffer buffer, long value){
        buffer.putLong(value);
    }
    private void enCodeBaseType(ByteBuffer buffer, float value){
        buffer.putFloat(value);
    }
    private void enCodeBaseType(ByteBuffer buffer, double value){
        buffer.putDouble(value);
    }
    private void enCodeString(ByteBuffer buffer, String value){
        buffer.putInt(0);
        int pos = buffer.position();
        buffer.put( value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        int length = buffer.position() - pos;
        buffer.putInt(pos-4,length);
    }

    private void enCodeArray(ByteBuffer buffer, Object array){
        buffer.putInt(0);//站位
        int dataOffset = buffer.position();
        if (array instanceof Object[] && !(array instanceof String[])) {
            for (Object item: (Object[]) array) {
                enCodeStruct(buffer,item);
            }
        } else if (array instanceof boolean[]) {
            for (boolean item: (boolean[]) array) {
                enCodeBaseType(buffer,item);
            }
        } else if (array instanceof byte[]) {
            buffer.put((byte[]) array);
        } else if (array instanceof char[]) {
            buffer.put((byte[]) array);
        } else if (array instanceof double[]) {
            for (double item: (double[]) array) {
                enCodeBaseType(buffer,item);
            }
        } else if (array instanceof float[]) {
            for (float item: (float[]) array) {
                enCodeBaseType(buffer,item);
            }
        } else if (array instanceof int[]) {
            for (int item: (int[]) array) {
                enCodeBaseType(buffer,item);
            }
        } else if (array instanceof long[]) {
            for (long item: (long[]) array) {
                enCodeBaseType(buffer,item);
            }
        } else if (array instanceof short[]) {
            for (short item: (short[]) array) {
                enCodeBaseType(buffer,item);
            }
        } else if (array instanceof String[]) {
            for (String item: (String[]) array) {
                enCodeString(buffer,item);
            }
        }
        int dataLen = buffer.position() - dataOffset;
        buffer.putInt(dataOffset - 4,dataLen);
    }



    private Object deCodeStruct(ByteBuffer bufDest, Class template){
        bufDest.order(ByteOrder.BIG_ENDIAN);
        int payloadLen = bufDest.getInt();
        Object res;
        try {
            Constructor constructor = template.getConstructor();
            res = constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Field[] fields = template.getDeclaredFields();
        for (Field fieldItem: fields) {
            fieldItem.setAccessible(true);
//            System.out.println("filed: "+fieldItem.getName());
            try {
                Class type = fieldItem.getType();
                if(type.isPrimitive()){
                    if(type.equals(boolean.class)){
                        fieldItem.setBoolean(res,bufDest.get() == 1?true:false);
                    }else if(type.equals(byte.class)){
                        fieldItem.setByte(res,bufDest.get());
                    }else if(type.equals(char.class)){
                        fieldItem.setChar(res,(char)(bufDest.get()));
                    }else if(type.equals(short.class)){
                        fieldItem.setShort(res,bufDest.getShort());
                    }else if(type.equals(int.class)){
                        fieldItem.setInt(res,bufDest.getInt());
                    }else if(type.equals(long.class)){
                        fieldItem.setLong(res,bufDest.getLong());
                    }else if(type.equals(float.class)){
                        fieldItem.setFloat(res,bufDest.getFloat());
                    }else if(type.equals(double.class)){
                        fieldItem.setDouble(res,bufDest.getDouble());
                    }
                }else if(String.class.equals(type)){
                    fieldItem.set(res,deCodeString(bufDest));
                }else if(type.isArray()){
                    Object tmp = deCodeArray(bufDest,type.getComponentType());
                    fieldItem.set(res,tmp);
                }else {
                    /* 此处重点关注*/
                    fieldItem.set(res,deCodeStruct(bufDest,type));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
//            System.out.println(" decode-ed: "+res);
        }
        return res;
    }



    private String deCodeString(ByteBuffer buffer ){
        int payloadLen = buffer.getInt();
        byte[] tmp = new byte[payloadLen];
        buffer.get(tmp);
        String res = new String(tmp,java.nio.charset.StandardCharsets.UTF_8);
//        System.out.println("deCodeString: arr: "+Arrays.toString(tmp)+", string: "+res);
        return res;
    }

    private Object deCodeArray(ByteBuffer buffer, Class itemType){
        int payloadLen = buffer.getInt();
        if(payloadLen == 0){
            return null;
        }
        Object res;
        if (boolean.class.equals(itemType)) {
            //
            boolean[] tmpResArr = new boolean[payloadLen];
            int toSetIndex = 0;
            while ( toSetIndex < tmpResArr.length ){
                tmpResArr[toSetIndex] = buffer.get() == 1? true: false;
                toSetIndex ++;
            }
            res = tmpResArr;
        } else if (byte.class.equals(itemType)) {
            //
            byte[] tmpResArr = new byte[payloadLen];
            buffer.get(tmpResArr);
            res = tmpResArr;
        } else if (char.class.equals(itemType)) {
            //
            byte[] tmpResArr = new byte[payloadLen];
            buffer.get(tmpResArr);
            res = tmpResArr;
        } else if (short.class.equals(itemType)) {
            //
            short[] tmpResArr = new short[payloadLen/2];
            int toSetIndex = 0;
            while ( toSetIndex < tmpResArr.length ){
                tmpResArr[toSetIndex] = buffer.getShort();
                toSetIndex ++;
            }
            res = tmpResArr;
        } else if (int.class.equals(itemType)) {
            //
            int[] tmpResArr = new int[payloadLen/4];
            int toSetIndex = 0;
            while ( toSetIndex < tmpResArr.length ){
                tmpResArr[toSetIndex] = buffer.getInt();
                toSetIndex ++;
            }
            res = tmpResArr;
        } else if (float.class.equals(itemType)) {
            //
            float[] tmpResArr = new float[payloadLen/4];
            int toSetIndex = 0;
            while ( toSetIndex < tmpResArr.length ){
                tmpResArr[toSetIndex] = buffer.getFloat();
                toSetIndex ++;
            }
            res = tmpResArr;
        } else if (double.class.equals(itemType)) {
            //
            double[] tmpResArr = new double[payloadLen/8];
            int toSetIndex = 0;
            while ( toSetIndex < tmpResArr.length ){
                tmpResArr[toSetIndex] = buffer.getDouble();
                toSetIndex ++;
            }
            res = tmpResArr;
        } else if (long.class.equals(itemType)) {
            //
            long[] tmpResArr = new long[payloadLen/8];
            int toSetIndex = 0;
            while ( toSetIndex < tmpResArr.length ){
                tmpResArr[toSetIndex] = buffer.getLong();
                toSetIndex ++;
            }
            res = tmpResArr;
        } else if (String.class.equals(itemType)) {
            //
            int startPos = buffer.position();
            ArrayList<String> tmpRes = new ArrayList<>();
            while (buffer.position() < startPos + payloadLen){
                tmpRes.add(deCodeString(buffer));
            }
            Object[] tmpArr4Type = (Object[]) Array.newInstance(itemType,0);
            res = tmpRes.toArray(tmpArr4Type);
        }else {
            int startPos = buffer.position();
            ArrayList<Object> tmpRes = new ArrayList<>();
            while (buffer.position() < startPos + payloadLen){
                tmpRes.add(deCodeStruct(buffer,itemType));
            }
            Object[] tmpArr4Type = (Object[]) Array.newInstance(itemType,0);
            res = tmpRes.toArray(tmpArr4Type);
        }
        return res;
    }

}
