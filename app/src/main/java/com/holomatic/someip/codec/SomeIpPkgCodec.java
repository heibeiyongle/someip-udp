package com.holomatic.someip.codec;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author 比才-贾硕哲
 * @time 26/3/2024 15:11
 * @desc
 */
public class SomeIpPkgCodec {

    /*
    encode
    serviceID  msgID
    length
    req_client_id req_session_id
    protocol_ver interface_ver
    msgType return_code

    payload

    decode
    */


    public static SomeIpPkg decode(byte[] data,int offset, int length){
        SomeIpPkg someIpPkg = new SomeIpPkg(data,offset,length);
        return someIpPkg;
    }


    public static byte[] encode(short serviceId, short methodId, byte[] payload){
        SomeIpPkg someIpPkg = new SomeIpPkg(serviceId,methodId,payload);
        return someIpPkg.toBytes();
    }



    public static class SomeIpPkg{
        private short mServiceId, mMethodId;
        private int mLength;
        private short reqClientId, reqSessionId;
        private byte protocolVer, interfaceVer;
        private byte msgType, returnCode;

        private byte[] payload;

        public SomeIpPkg(){
        }
        public static final int headSize = 4*4;
        ByteBuffer mSmIpHeadByteBuf = ByteBuffer.allocate(headSize);
        public SomeIpPkg(byte[] data,int offset, int length){

            mSmIpHeadByteBuf.clear();
            mSmIpHeadByteBuf.put(Arrays.copyOfRange(data,offset,mSmIpHeadByteBuf.capacity()));
            mSmIpHeadByteBuf.flip();
            mServiceId = mSmIpHeadByteBuf.getShort();
            mMethodId = mSmIpHeadByteBuf.getShort();
            mLength = mSmIpHeadByteBuf.getInt();
            reqClientId = mSmIpHeadByteBuf.getShort();
            reqSessionId = mSmIpHeadByteBuf.getShort();
            protocolVer = mSmIpHeadByteBuf.get();
            interfaceVer = mSmIpHeadByteBuf.get();
            msgType = mSmIpHeadByteBuf.get();
            returnCode = mSmIpHeadByteBuf.get();
            payload = Arrays.copyOfRange(data,mSmIpHeadByteBuf.capacity(),length);
        }

        public SomeIpPkg(short serviceId, short methodId, byte[] payload){
            this.mServiceId = serviceId;
            this.mMethodId = methodId;
            if(payload != null){
                this.payload = payload;
                this.mLength = payload.length + 2*4;
            }
        }

        public void setReqClientId(short reqClientId) {
            this.reqClientId = reqClientId;
        }

        public void setReqSessionId(short reqSessionId) {
            this.reqSessionId = reqSessionId;
        }

        public byte[] getPayload() {
            return payload;
        }

//        public void setPayload(byte[] payload,int offset, int mLength) {
//        }

        public void setPayload(byte[] payload) {
            this.payload = payload;
            if(payload != null){
                this.mLength = payload.length + 2*4;
            }else {
                this.mLength = 0;
            }
        }

        public short getmServiceId() {
            return mServiceId;
        }

        public short getmMethodId() {
            return mMethodId;
        }

        public short getReqSessionId() {
            return reqSessionId;
        }

        public byte[] toBytes(){
            int payloadLen = payload == null? 0:payload.length;
            byte[] res = new byte[payloadLen+headSize];
            mSmIpHeadByteBuf.clear();
            mSmIpHeadByteBuf.putShort(mServiceId);
            mSmIpHeadByteBuf.putShort(mMethodId);
            mSmIpHeadByteBuf.putInt(mLength);
            mSmIpHeadByteBuf.putShort(reqClientId);
            mSmIpHeadByteBuf.putShort(reqSessionId);
            mSmIpHeadByteBuf.put(protocolVer);
            mSmIpHeadByteBuf.put(interfaceVer);
            mSmIpHeadByteBuf.put(msgType);
            mSmIpHeadByteBuf.put(returnCode);
            mSmIpHeadByteBuf.flip();
            mSmIpHeadByteBuf.get(res,0,headSize);
            if(payload != null){
                System.arraycopy(payload,0,res,headSize,payload.length);
            }
            return res;
        }

        @Override
        public String toString() {
            return "SomeIpPkg{" +
                    "mServiceId= 0x" + Integer.toUnsignedString(Short.toUnsignedInt(mServiceId),16) +
                    ", mMethodId= 0x" + Integer.toUnsignedString(Short.toUnsignedInt(mMethodId),16) +
                    ", mLength=" + mLength +
                    ", reqClientId=" + reqClientId +
                    ", reqSessionId=" + reqSessionId +
                    ", protocolVer=" + protocolVer +
                    ", interfaceVer=" + interfaceVer +
                    ", msgType=" + msgType +
                    ", returnCode=" + returnCode +
//                    ", payload=" + Arrays.toString(payload) +
                    ", payload-len=" + (payload == null? 0 : payload.length) +
                    ", mSmIpHeadByteBuf=" + mSmIpHeadByteBuf +
                    '}';
        }
    }







}
