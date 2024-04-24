package com.holomatic.someip;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author 比才-贾硕哲
 * @time 22/4/2024 20:13
 * @desc
 */
public class ClassDecUtil {

    ByteBuffer buffer;
    int tagIndex = 0;
    public ArrayList<FieldInfo> decodeClass(byte[] data ){

        buffer = ByteBuffer.allocate(data.length);
        buffer.put(data);
        buffer.flip();

        skipBuffer(4+2+2);
        readConstantPool();
        skipBuffer(2*3);

//        byte[] testArr = new byte[16];
//        buffer.get(testArr);
//        System.out.println(" [interfaceCnt .. ] testArr: "+Arrays.toString(testArr));

        int interfaceCnt = Short.toUnsignedInt(buffer.getShort());
//        System.out.println(" interfaceCnt: "+interfaceCnt);
        skipBuffer(2*interfaceCnt);
        readFieldInfos();
        // print info
        return fieldList;
    }



    private void readConstantPool(){
        // constant item
        tagIndex = 0;
        int cpCnt = Short.toUnsignedInt(buffer.getShort());
        tagList = new TagInfo[cpCnt];
        while (tagIndex < cpCnt - 1){
            // loop
            tagIndex ++;

            byte tag = buffer.get();
//            System.out.println("tag:["+tagIndex+"] "+tag);

            switch (tag){
                case 7:{
                    // class
    //                CONSTANT_Class_info {
    //                    u1 tag;
    //                    u2 name_index;
    //                }
                    skipBuffer(2);
                    break;
                }
                case 9:{ // field
    //                CONSTANT_Fieldref_info {
    //                    u1 tag;
    //                    u2 class_index;
    //                    u2 name_and_type_index;
    //                }
                    skipBuffer(4);
                    break;
                }
                case 10:{ // method
    //                CONSTANT_Methodref_info {
    //                    u1 tag;
    //                    u2 class_index;
    //                    u2 name_and_type_index;
    //                }
                    skipBuffer(4);
                    break;
                }
                case 11:{// interface
    //                CONSTANT_InterfaceMethodref_info {
    //                    u1 tag;
    //                    u2 class_index;
    //                    u2 name_and_type_index;
    //                }
                    skipBuffer(4);
                    break;
                }
                case 8:{ // string
    //                CONSTANT_String_info {
    //                    u1 tag;
    //                    u2 string_index;
    //                }
                    skipBuffer(2);
                    break;
                }
                case 3:{ // interger
    //                CONSTANT_Integer_info {
    //                    u1 tag;
    //                    u4 bytes;
    //                }
                    skipBuffer(4);
                    break;
                }
                case 4:{ // float
    //                CONSTANT_Float_info {
    //                    u1 tag;
    //                    u4 bytes;
    //                }
                    skipBuffer(4);
                    break;
                }
                case 5:{ // long
    //                CONSTANT_Long_info {
    //                    u1 tag;
    //                    u4 high_bytes;
    //                    u4 low_bytes;
    //                }
                    skipBuffer(8);
                    break;
                }
                case 6:{ // double

    //                CONSTANT_Double_info {
    //                    u1 tag;
    //                    u4 high_bytes;
    //                    u4 low_bytes;
    //                }
                    skipBuffer(8);
                    break;
                }
                case 12:{ // nameAndType
    //                CONSTANT_NameAndType_info {
    //                    u1 tag;
    //                    u2 name_index;
    //                    u2 descriptor_index;
    //                }
                    skipBuffer(4);
                    break;
                }
                case 1:{ // utf8
    //                CONSTANT_Utf8_info {
    //                    u1 tag;
    //                    u2 length;
    //                    u1 bytes[length];
    //                }
                    readUnt8Tag();
                    break;
                }
                case 15:{ // methodHandle
    //                CONSTANT_MethodHandle_info {
    //                    u1 tag;
    //                    u1 reference_kind;
    //                    u2 reference_index;
    //                }
                    skipBuffer(3);
                    break;
                }
                case 16:{//methodType
    //                CONSTANT_MethodType_info {
    //                    u1 tag;
    //                    u2 descriptor_index;
    //                }
                    skipBuffer(2);
                    break;
                }
                case 18:{ // invokeDynamic
    //                CONSTANT_InvokeDynamic_info {
    //                    u1 tag;
    //                    u2 bootstrap_method_attr_index;
    //                    u2 name_and_type_index;
    //                }
                    skipBuffer(4);
                    break;
                }
            }

        }
    }

    ArrayList<FieldInfo> fieldList = new ArrayList();
    private void readFieldInfos(){
        /**
        field_info {
            u2 access_flags;
            u2 name_index;
            u2 descriptor_index; // type
            u2 attributes_count;
            attribute_info attributes[attributes_count];
        }

         attribute_info {
         u2 attribute_name_index;
         u4 attribute_length;
         u1 info[attribute_length];
         }

         */

        int fieldCnt = Short.toUnsignedInt(buffer.getShort());
        int fieldIndex = 0;
        while (fieldIndex < fieldCnt){
            FieldInfo fieldInfo = new FieldInfo();
            fieldInfo.accessFlags = Short.toUnsignedInt(buffer.getShort());
            fieldInfo.nameIndex = Short.toUnsignedInt(buffer.getShort());
            fieldInfo.nameStr = tagList[fieldInfo.nameIndex].utf8Tag;
            fieldList.add(fieldInfo);
            // skip attr
            buffer.getShort(); // desc
            int attrCnt = Short.toUnsignedInt(buffer.getShort());// attr cnt
//            System.out.println(" attrCnt: "+attrCnt );
            // loop attr info
            int attrIndex = 0;
            while (attrIndex < attrCnt){
                buffer.getShort();
                int attrinfoLen = buffer.getInt();
                skipBuffer(attrinfoLen);
            }
            fieldIndex++;
        }
    }

    static class FieldInfo{
        int accessFlags;
        int nameIndex;
        String nameStr;

        @Override
        public String toString() {
            return "FieldInfo{" +
                    "accessFlags=" + accessFlags +
                    ", nameIndex=" + nameIndex +
                    ", nameStr='" + nameStr + '\'' +
                    '}';
        }
        /* desc attr skipped  */
    }


    private void skipBuffer(int size){
        buffer.position(buffer.position()+size);
    }


    TagInfo[] tagList = null;
    private void readUnt8Tag(){
//                CONSTANT_Utf8_info {
//                    u1 tag;
//                    u2 length;
//                    u1 bytes[length];
//                }
        int length = Short.toUnsignedInt(buffer.getShort());
        byte[] data = new byte[length];
        buffer.get(data);
        TagInfo tmpTag = TagInfo.genUtf8Tag(data);
        tagList[tagIndex] = tmpTag;
//        System.out.println(" tag["+tagIndex+"]: "+tmpTag);
    }

    public static class TagInfo{
        byte tag;
        byte[] data;
        String utf8Tag;

        @Override
        public String toString() {
            return "TagInfo{" +
                    "tag=" + tag +
                    ", data=" + Arrays.toString(data) +
                    ", utf8Tag='" + utf8Tag + '\'' +
                    '}';
        }

        public static TagInfo genUtf8Tag(byte[] bytes){
            TagInfo tagInfo = new TagInfo();
            tagInfo.tag = 0x1;
            tagInfo.utf8Tag = new String(bytes);
            return tagInfo;
        }

        public static TagInfo genCommTag(byte tag, byte[] data){
            TagInfo tagInfo = new TagInfo();
            tagInfo.tag = tag;
            tagInfo.data = data;
            return tagInfo;
        }
    }




}
