package com.holomatic.someip.codec;


import com.holomatic.utils.ClassDecUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author 比才-贾硕哲
 * @time 22/3/2024 11:31
 * @desc
 */
public class PayloadCodec {

    public PayloadCodec(){
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
        Class template = srcDto.getClass();
        if(template.isArray()){
            enCodeArray(mByteBuffer,srcDto);
        }else if(isPrimitive(template)){
            enCodePrimitive(mByteBuffer,srcDto);
        }else {
            enCodeStruct(mByteBuffer,template);
        }
        mByteBuffer.flip();
        int dateLen = mByteBuffer.remaining();
        byte[] res = new byte[dateLen];
        mByteBuffer.get(res);
        return res;
    }


    public Object deCodeStruct(byte[] data, Class template){
        mByteBuffer.clear();
        mByteBuffer.put(data);
        mByteBuffer.flip();
        if(template.isArray()){
            return deCodeArray(mByteBuffer,template.getComponentType());
        }else if(isPrimitive(template)){
            return decodePrimitive(mByteBuffer,template);
        }else {
            return deCodeStruct(mByteBuffer,template);
        }
    }

//    private Object deCodeStructArr(byte[] data, Class template){
//        mByteBuffer.clear();
//        mByteBuffer.put(data);
//        mByteBuffer.flip();
//        return deCodeArray(mByteBuffer,template);
//    }
//
//    private Object deCodeStruct(byte[] data,int offset, int length, Class template){
//        mByteBuffer.clear();
//        mByteBuffer.put(data,offset,length);
//        mByteBuffer.flip();
//        return deCodeStruct(mByteBuffer,template);
//    }

    private boolean isPrimitive(Class type){
        if(type.equals(Boolean.class) || type.equals(boolean.class)){
            return true;
        }else if(type.equals(Byte.class) || type.equals(byte.class)){
            return true;
        }else if(type.equals(Character.class) || type.equals(char.class)){ //
            return true;
        }else if(type.equals(Short.class) || type.equals(short.class)){
            return true;
        }else if(type.equals(Integer.class) || type.equals(int.class)){
            return true;
        }else if(type.equals(Long.class) || type.equals(long.class)){
            return true;
        }else if(type.equals(Float.class) || type.equals(float.class)){
            return true;
        }else if(type.equals(Double.class) || type.equals(double.class)){
            return true;
        }
        return false;
    }


    private void enCodePrimitive(ByteBuffer bufDest,Object srcDto){
        Class type = srcDto.getClass();
        if(type.equals(Boolean.class) || type.equals(boolean.class)){
            enCodeBaseType(bufDest,(byte) srcDto);
        }else if(type.equals(Byte.class) || type.equals(byte.class)){
            enCodeBaseType(bufDest,(byte) srcDto);
        }else if(type.equals(Character.class) || type.equals(char.class)){ //
            enCodeBaseType(bufDest,(byte) srcDto);
        }else if(type.equals(Short.class) || type.equals(short.class)){
            enCodeBaseType(bufDest,(short) srcDto);
        }else if(type.equals(Integer.class) || type.equals(int.class)){
            enCodeBaseType(bufDest,(int)srcDto);
        }else if(type.equals(Long.class) || type.equals(long.class)){
            enCodeBaseType(bufDest,(long)srcDto);
        }else if(type.equals(Float.class) || type.equals(float.class)){
            enCodeBaseType(bufDest,(float)srcDto);
        }else if(type.equals(Double.class) || type.equals(double.class)){
            enCodeBaseType(bufDest,(double) srcDto);
        }
    }


    private void enCodeStruct(ByteBuffer bufDest, Object srcDto){
        bufDest.putInt(0);
        int dataOffset = bufDest.position();
        Class template = srcDto.getClass();
        Field[] fields = getOrderedFields(template);
        for (Field fieldItem: fields) {
            fieldItem.setAccessible(true);
            try {
                Class type = fieldItem.getType();
                if(type.isPrimitive()){
                    Object value = fieldItem.get(srcDto);
                    enCodePrimitive(bufDest,value);
//                    if(type.equals(boolean.class)){
//                        enCodeBaseType(bufDest,fieldItem.getBoolean(srcDto));
//                    }else if(type.equals(byte.class)){
//                        enCodeBaseType(bufDest,fieldItem.getByte(srcDto));
//                    }else if(type.equals(char.class)){
//                        enCodeBaseType(bufDest,fieldItem.getByte(srcDto));
//                    }else if(type.equals(short.class)){
//                        enCodeBaseType(bufDest,fieldItem.getShort(srcDto));
//                    }else if(type.equals(int.class)){
//                        enCodeBaseType(bufDest,fieldItem.getInt(srcDto));
//                    }else if(type.equals(long.class)){
//                        enCodeBaseType(bufDest,fieldItem.getLong(srcDto));
//                    }else if(type.equals(float.class)){
//                        enCodeBaseType(bufDest,fieldItem.getFloat(srcDto));
//                    }else if(type.equals(double.class)){
//                        enCodeBaseType(bufDest,fieldItem.getDouble(srcDto));
//                    }
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

    public void setMapClassNameFields(HashMap<String, List<ClassDecUtil.FieldInfo>> mMapClassNameFields) {
        this.mMapClassNameFields = mMapClassNameFields;
    }

    HashMap<String, List<ClassDecUtil.FieldInfo>> mMapClassNameFields = null;

    private Field[] getOrderedFields(Class template){
        Field[] tmpFields = template.getDeclaredFields();
        List<ClassDecUtil.FieldInfo> targetSortFieldList = mMapClassNameFields.get(template.getSimpleName());

        if(targetSortFieldList == null){
            throw new RuntimeException(" someIP struct-define not found! struct: "+template.getSimpleName());
        }

        if(targetSortFieldList.size() != tmpFields.length){
            throw new RuntimeException(" someIP struct-define field-length-missMatch! struct: "+template.getSimpleName());
        }

        Field[] fields = new Field[tmpFields.length];
        for (int i=0; i < fields.length; i++) {
            ClassDecUtil.FieldInfo fieldInfo = targetSortFieldList.get(i);
            try {
                fields[i] = template.getDeclaredField(fieldInfo.nameStr);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        return fields;
    }

    private Object deCodeStruct(ByteBuffer bufDest, Class template){
//        bufDest.order(ByteOrder.BIG_ENDIAN);
//        HLog.i(TAG, "deCodeStruct class: "+template.getName()+" bufDest:"+bufDest );
        int payloadLen = bufDest.getInt();
        Object res;
        try {
            Constructor constructor = template.getConstructor();
            res = constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Field[] fields = getOrderedFields(template);
        for (Field fieldItem: fields) {
            fieldItem.setAccessible(true);
//            HLog.i(TAG, "deCodeStruct filed: "+fieldItem.getName()+", buffer: "+bufDest);
            try {
                Class type = fieldItem.getType();
                // todo 如果payload 仅为基本类型，或基本类型arr
                if(type.isPrimitive()){
                    Object tmpObj = decodePrimitive(bufDest,type);
                    fieldItem.set(res,tmpObj);
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

    private Object decodePrimitive(ByteBuffer bufDest, Class type){
        Object res = null;
        if(type.equals(boolean.class) || type.equals(Boolean.class)){
            res = bufDest.get() == 1?true:false;
        }else if(type.equals(byte.class) || type.equals(Byte.class)){
            res = bufDest.get();
        }else if(type.equals(char.class) || type.equals(Character.class)){
            res = bufDest.get();
        }else if(type.equals(short.class) || type.equals(Short.class)){
            res = bufDest.getShort();
        }else if(type.equals(int.class) || type.equals(Integer.class)){
            res = bufDest.getInt();
        }else if(type.equals(long.class) || type.equals(Long.class)){
            res = bufDest.getLong();
        }else if(type.equals(float.class) || type.equals(Float.class)){
            res = bufDest.getFloat();
        }else if(type.equals(double.class) || type.equals(Double.class)){
            res = bufDest.getDouble();
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

    private static final String TAG = "HoloChannelManager";
    int i = 0;
    private Object deCodeArray(ByteBuffer buffer, Class itemType){
//        HLog.i(TAG, "deCodeArray buffer: "+buffer);
        int payloadLen = buffer.getInt();
//        HLog.i(TAG, "deCodeArray payloadLen: "+payloadLen+", buffer: "+buffer+", itemType: "+itemType);
        if(payloadLen == 0 ){
            return null;
        }
//        if (payloadLen != 0 && buffer.remaining() == 0){
//            throw new RuntimeException("------------------------------");
//        }

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
