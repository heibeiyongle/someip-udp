package com.holomatic.someip.channel;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * @author 比才-贾硕哲
 * @time 27/3/2024 15:49
 * @desc
 */
public class UDPChannel {

    private static final String TAG = "UDPChannel";
    private static UDPChannel instance;

    public static UDPChannel getInstance(){
        if (instance == null) {
            synchronized (UDPChannel.class){
                if(instance == null){
                    instance = new UDPChannel();
                }
            }
        }
        return instance;
    }

    public void setHostInfo(int localReceiverPort){
        LocalServerPort = localReceiverPort;
    }

    // 陆逊-PC
//    public static final String RemoteServerHost = "10.1.1.112";
    public int LocalServerPort = 9990;
    private int LocalSenderPort = 0;

    public void sendMsg( byte[] data,String remoteHost, int remotePort){
        try (DatagramSocket clientSocket = new DatagramSocket(LocalSenderPort)) {
            InetAddress serverAddressInetAddress = InetAddress.getByName(remoteHost);
            /* data 的最大长度为65507 byte */
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, serverAddressInetAddress, remotePort);
            clientSocket.send(sendPacket);
            LocalSenderPort = clientSocket.getLocalPort();
            Log.i(TAG, "sendMsg src: localhost:"+clientSocket.getLocalPort()+" --> dest: "+remoteHost+":"+remotePort+" data.length: "+data.length+", data: "+ Arrays.toString(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void startReceive(UDPPkgReceiver receiver){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try (DatagramSocket serverSocket = new DatagramSocket(LocalServerPort)) {
                    Log.i(TAG, " UDP Server is listening on port " + LocalServerPort);
                    byte[] receiveBuffer = new byte[1024*1024];
                    while (!receiver.isExit()) {
                        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                        serverSocket.receive(receivePacket);
                        receiver.onGotData(receivePacket.getAddress().getHostAddress(),receivePacket.getData(),0,receivePacket.getLength());
//                        HLog.i(TAG, " Received from client: " + receivePacket.getLength());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    public interface UDPPkgReceiver{
        void onGotData(String srcHost, byte[] data,int offset,int length);
        boolean isExit();
    }


}
