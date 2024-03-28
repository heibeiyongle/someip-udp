package com.holomatic.someip;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author 比才-贾硕哲
 * @time 27/3/2024 15:49
 * @desc
 */
public class UDPChannel {

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

    // 陆逊-PC
//    public static final String RemoteServerHost = "10.1.1.112";
    public static final String RemoteServerHost = "127.0.0.1";
    public static final int RemoteServerPort = 9990;

    public void sendMsg(byte[] data){
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress serverAddressInetAddress = InetAddress.getByName(RemoteServerHost);
            /* data 的最大长度为65507 byte */
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, serverAddressInetAddress, RemoteServerPort);
            clientSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static final int LocalServerPort = 9990;
    public void startReceive(UDPPkgReceiver receiver){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try (DatagramSocket serverSocket = new DatagramSocket(LocalServerPort)) {
                    System.out.println("UDP Server is listening on port " + LocalServerPort);

                    byte[] receiveBuffer = new byte[1024*1024];
                    while (!receiver.isExit()) {
                        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                        serverSocket.receive(receivePacket);
                        receiver.onGotData(receivePacket.getData(),0,receivePacket.getLength());
//                        System.out.println("Received from client: " + receivePacket.getLength());
                        // 可以在这里添加代码来处理接收到的数据
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    public interface UDPPkgReceiver{
        void onGotData(byte[] data,int offset,int length);
        boolean isExit();
    }


}