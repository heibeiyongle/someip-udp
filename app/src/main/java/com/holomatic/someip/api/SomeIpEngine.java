package com.holomatic.someip.api;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.holomatic.someip.channel.UDPChannel;
import com.holomatic.someip.codec.PayloadCodec;
import com.holomatic.someip.codec.SomeIpPkgCodec;
import com.holomatic.someip.core.ISomeIpPkgDealer;
import com.holomatic.someip.core.SomeIpPkgDealer;
import com.holomatic.utils.ClassDecUtil;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author 比才-贾硕哲
 * @time 15/5/2024 15:56
 * @desc
 */
public class SomeIpEngine implements ISomeIpEngine{
    Context mCtx;
    Handler mHandler;
    UDPChannel mUdpChannel;
    ISomeIpPkgDealer mPkgDealer;

    private static SomeIpEngine instance;

    private SomeIpEngine() {
    }

    public static SomeIpEngine getInstance() {
        if (instance == null) {
            synchronized (SomeIpEngine.class) {
                if (instance == null) {
                    instance = new SomeIpEngine();
                }
            }
        }
        return instance;
    }

    @Override
    public void init(Context context) {
        Log.i(TAG, "init: ");
        mCtx = context;
        HandlerThread thread = new HandlerThread("someIpEngine");
        thread.start();
        mHandler = new Handler(thread.getLooper());
        mUdpChannel = UDPChannel.getInstance();
        mPkgDealer = new SomeIpPkgDealer();
        mPkgDealer.init(context);
        mPkgDealer.setPkgSender(mSomeIpSender);
    }

    @Override
    public void startEngine() {
        Log.i(TAG, "startEngine: ");
        mUdpChannel.setHostInfo(9989);
        mUdpChannel.startReceive(udpPkgReceiver);
    }


    // start UDP rec
    UDPChannel.UDPPkgReceiver udpPkgReceiver = new UDPChannel.UDPPkgReceiver() {
        @Override
        public void onGotData(String srcHost, byte[] data, int offset, int length) {
            SomeIpPkgCodec.SomeIpPkg someIpPkg = SomeIpPkgCodec.decode(data, offset, length);
//            if(someIpPkg.getmServiceId() == (short) 0x6001 && someIpPkg.getmMethodId() == (short) 0x8001){
//                HLog.i(TAG, "onGotData someIpPkg: "+someIpPkg);
//                HLog.i(TAG, "onGotData someIpPkg-payload: "+Arrays.toString(someIpPkg.getPayload()));
//            }

            // deal heart beat
            if(someIpPkg.getmServiceId() == mHeartBeat.getmServiceId()){
                onGotHeartBeat(someIpPkg);
                remoteHost = srcHost;
                return;
            }

            mPkgDealer.onRecSomeIp(someIpPkg);
//            if(someIpPkg.getmServiceId() == (short) 0x5005 && someIpPkg.getmMethodId() == (short) 0x8001){
//                HLog.i(TAG, "onGotData end ");
//            }
//            HLog.i(TAG, "upd-RX: ServiceId: 0x" + Integer.toUnsignedString(Short.toUnsignedInt(someIpPkg.getmServiceId()), 16)+
//                    "MethodId: 0x" + Integer.toUnsignedString(Short.toUnsignedInt(someIpPkg.getmMethodId()), 16)
//            );
        }

        @Override
        public boolean isExit() {
            return false;
        }
    };

    ISomeIpPkgDealer.ISomeIpSender mSomeIpSender = new ISomeIpPkgDealer.ISomeIpSender(){

        @Override
        public void sendMsg(SomeIpPkgCodec.SomeIpPkg pkg) {
            sendUpd(pkg.toBytes());
        }
    };

    private String remoteHost;
    private int remotePort = 9999;


    private void sendUpd(byte[] data){
        Log.i(TAG, "sendUpd: ");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "sendUpd: inner remoteHost:"+remoteHost);
                if(remoteHost != null){
                    mUdpChannel.sendMsg(data,remoteHost,remotePort);
                }
            }
        });
    }

    private static final String TAG = "SomeIpEngine";
    SomeIpPkgCodec.SomeIpPkg mHeartBeat = new SomeIpPkgCodec.SomeIpPkg((short) 0x61ff,(short) 0x10ff,new byte[4]);
    private void onGotHeartBeat(SomeIpPkgCodec.SomeIpPkg someIpPkg) {
        Log.i(TAG, "onGotSomeIpData heartBeat-payload: " + Arrays.toString(someIpPkg.getPayload()) + ", pkg：" + someIpPkg);
        int tmpIndex = bytesToInt(someIpPkg.getPayload());
        mHeartBeat.setPayload(intToBytes(tmpIndex));
        sendUpd(mHeartBeat.toBytes());
    }
    public static int bytesToInt(byte[] bytes) {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res += (bytes[i] & 0xFF) << (8 * i);
        }
        return res;
    }
    public static byte[] intToBytes(int value) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[3 - i] = (byte) (value >> (i * 8));
        }
        return bytes;
    }



    @Override
    public void stopEngine() {

    }

    @Override
    public void mockPkg(short serviceId, short methodId, Object data) {
        mPkgDealer.mockPkg(serviceId,methodId,data);
    }

    /**
     *
     * 网络层
     *
     * init
     * handler init
     *
     *
     * start rec
     *
     * cache client host port
     *
     *
     *
     *
     *
     */

}
