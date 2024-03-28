package com.holomatic.someip;

import org.junit.Test;

import static org.junit.Assert.*;

import com.kotei.ktsomeip.struct.DistancePoint2F;
import com.kotei.ktsomeip.struct.PrkgSlotInfo;
import com.kotei.ktsomeip.struct.SignelLaneLineInfo;
import com.kotei.ktsomeip.struct.SingleLaneMarkerInfo;
import com.kotei.ktsomeip.struct.SingleObjectInfo;
import com.kotei.ktsomeip.struct.SlotPoints;

import java.util.Arrays;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    PayloadCodec codec = PayloadCodec.getInstance();

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);

        UDPChannel udpLayer = UDPChannel.getInstance();
        udpLayer.startReceive(receiver);

        TestDto testDto = new TestDto();
        testDto.setI(8);
        testDto.setIa(new int[]{1, 2, 3, 4});
        testDto.setE("test");
        testDto.setEa(new String[]{"test", "test2"});

        TestDto2 testDto2 = new TestDto2();
        testDto2.setA(11);
        testDto2.setB(false);
        testDto2.setName("nameTest");
        testDto.setTestDto2(testDto2);

        int testDto2Len = 10;
        TestDto2[] testDto2s = new TestDto2[testDto2Len];
        for (int i = 0; i < testDto2Len; i++) {
            TestDto2 testDto21 = new TestDto2();
            testDto21.setA(i);
            testDto21.setB(false);
            testDto21.setName("nameTest--" + i);
            testDto2s[i] = testDto21;
        }
        testDto.setTestDto2Arr(testDto2s);

        long start = System.currentTimeMillis();
        byte[] enCodedDataArr = codec.enCodeStruct(testDto);
        System.out.println(" enCodeCostMS:  " + (System.currentTimeMillis() - start));

        System.out.println("destArrLen: " + enCodedDataArr.length);

        // decode
        start = System.currentTimeMillis();
        TestDto decodeDto = (TestDto) codec.deCodeStruct(enCodedDataArr, testDto.getClass());
        System.out.println(" deCodeCostMS:  " + (System.currentTimeMillis() - start));

//        System.out.println("decode-dto:"+decodeDto);


        PrkgSlotInfo prkgSlotInfo = new PrkgSlotInfo();
        prkgSlotInfo.prkgSlotAttribute = 1;
        prkgSlotInfo.prkgSlotBarrierSt = 2;
        prkgSlotInfo.prkgSlotDisplayID = 3;
        prkgSlotInfo.prkgSlotID = 4;
        prkgSlotInfo.prkgSlotSelected = 5;
        prkgSlotInfo.prkgSlotType = 7;
        prkgSlotInfo.slotTheta = 8.1f;

        SlotPoints slotPoints = new SlotPoints();
        slotPoints.slotPoint0 = new DistancePoint2F();
        slotPoints.slotPoint0.dX = 1.1f;
        slotPoints.slotPoint0.dY = 2.2f;
        slotPoints.slotPoint1 = new DistancePoint2F();
        slotPoints.slotPoint1.dX = 1.1f;
        slotPoints.slotPoint1.dY = 2.2f;
        slotPoints.slotPoint2 = new DistancePoint2F();
        slotPoints.slotPoint2.dX = 1.1f;
        slotPoints.slotPoint2.dY = 2.2f;
        slotPoints.slotPoint3 = new DistancePoint2F();
        slotPoints.slotPoint3.dX = 1.1f;
        slotPoints.slotPoint3.dY = 2.2f;

        prkgSlotInfo.prkgSlotPoints = slotPoints;
        start = System.currentTimeMillis();
        byte[] encodeData2 = codec.enCodeStruct(prkgSlotInfo);
        System.out.println(" enCodeCostMS:  " + (System.currentTimeMillis() - start));
        System.out.println("prkgSlotInfo  destArrLen: " + encodeData2.length);


        start = System.currentTimeMillis();
        PrkgSlotInfo prkgSlotInfo1 = (PrkgSlotInfo) codec.deCodeStruct(encodeData2, PrkgSlotInfo.class);
        System.out.println(" deCodeCostMS:  " + (System.currentTimeMillis() - start));
        System.out.println(" srcObj:  " + GsonUtils.getInstance().toJson(prkgSlotInfo));
        System.out.println(" decodeObj:  " + GsonUtils.getInstance().toJson(prkgSlotInfo1));

        SomeIpPkgCodec.SomeIpPkg someIpPkg = new SomeIpPkgCodec.SomeIpPkg((short) 0x1002, (short) 0x1003, encodeData2);
        udpLayer.sendMsg(someIpPkg.toBytes());

        SomeIpPkgCodec.SomeIpPkg someIpPkg2 = new SomeIpPkgCodec.SomeIpPkg((short) 0x1003, (short) 0x1004, enCodedDataArr);
        udpLayer.sendMsg(someIpPkg2.toBytes());

        udpLayer.sendMsg(testdata);

        // wait udp dealer
        try {
            Thread.sleep(3000 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public static class TestDto {
        private int i = 1;
        private int[] ia = {1, 2, 3, 4, 5};
        private boolean b;
        private boolean[] ba;
        //        private long l;
//        private long[] la;
//        private short s;
//        private short[] sa;
        private String e = "hello";
        private String[] ea = {"hello1", "hello2"};
        //        private float f;
//        private float[] fa;
//        private double d;
//        private double[] da;
//        private byte by;
//        private byte[] bya;
        TestDto2 testDto2;
        TestDto2[] testDto2Arr;

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

        public int[] getIa() {
            return ia;
        }

        public void setIa(int[] ia) {
            this.ia = ia;
        }

        public String getE() {
            return e;
        }

        public void setE(String e) {
            this.e = e;
        }

        public String[] getEa() {
            return ea;
        }

        public void setEa(String[] ea) {
            this.ea = ea;
        }

        public TestDto2 getTestDto2() {
            return testDto2;
        }

        public void setTestDto2(TestDto2 testDto2) {
            this.testDto2 = testDto2;
        }

        public TestDto2[] getTestDto2Arr() {
            return testDto2Arr;
        }

        public void setTestDto2Arr(TestDto2[] testDto2Arr) {
            this.testDto2Arr = testDto2Arr;
        }

        @Override
        public String toString() {
            return "TestDto{" +
                    "i=" + i +
                    ", ia=" + Arrays.toString(ia) +
                    ", e='" + e + '\'' +
                    ", ea=" + Arrays.toString(ea) +
                    ", testDto2=" + testDto2 +
                    ", testDto2Arr=" + Arrays.toString(testDto2Arr) +
                    '}';
        }
    }


    public static class TestDto2 {
        private int a;
        private boolean b;
        private String name;

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public boolean isB() {
            return b;
        }

        public void setB(boolean b) {
            this.b = b;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "TestDto2{" +
                    "a=" + a +
                    ", b=" + b +
                    ", name='" + name + '\'' +
                    '}';
        }
    }


    UDPChannel.UDPPkgReceiver receiver = new UDPChannel.UDPPkgReceiver() {
        boolean mIsExit = false;

        public void setIsExit(boolean mIsExit) {
            this.mIsExit = mIsExit;
        }

        @Override
        public void onGotData(byte[] data, int offset, int length) {
//            System.out.println("upd-rec-onGotData: offset: " + offset + ", length: " + length /* +", data: "+Arrays.toString(Arrays.copyOfRange(data,offset,length))*/);

            SomeIpPkgCodec.SomeIpPkg someIpPkg = SomeIpPkgCodec.decode(data, offset, length);
//            System.out.println(" upd-rec-someIpPkg:  " + someIpPkg);

            switch (someIpPkg.mServiceId) {
//                case 0x1002:{
//                    long start = System.currentTimeMillis();
//                    PrkgSlotInfo prkgSlotInfo2 = (PrkgSlotInfo) codec.deCodeStruct(someIpPkg.payload, PrkgSlotInfo.class);
//                    System.out.println(" upd-rec-decodeObj2:  "+ GsonUtils.getInstance().toJson(prkgSlotInfo2));
//                    System.out.println(" upd-rec-deCodeCostMS:  "+ (System.currentTimeMillis() - start));
//                    break;
//                }
//                case 0x1003:{
//                    long start = System.currentTimeMillis();
//                    TestDto decodeDto = (TestDto) codec.deCodeStruct(someIpPkg.payload, TestDto.class);
//                    System.out.println(" de-udp-testDto-CodeCostMS:  "+ (System.currentTimeMillis() - start));
////                    System.out.println(" decode-udp-testDto:"+decodeDto);
//                    break;
//                }
                case 0x5002: {
                    System.out.println(" upd-rec-someIpPkg:  " + someIpPkg);
                    if (someIpPkg.mMethodId == (short)0x8001) {
                        long start = System.currentTimeMillis();
                        SignelLaneLineInfo[] decodeDto = (SignelLaneLineInfo[]) codec.deCodeStructArr(someIpPkg.payload, SignelLaneLineInfo.class);
                        System.out.println(" de-udp-SignelLaneLineInfo-CodeCostMS:  " + (System.currentTimeMillis() - start));
                        System.out.println(" decode-udp-SignelLaneLineInfo:" + GsonUtils.getInstance().toJson(decodeDto));
                        break;
                    }
                    break;
                }
                case 0x0: {
//                    if (someIpPkg.mMethodId == 0x00) {
//                        long start = System.currentTimeMillis();
//                        SingleObjectInfo[] decodeDtoArr = (SingleObjectInfo[]) codec.deCodeStructArr(someIpPkg.payload, SingleObjectInfo.class);
//                        System.out.println(" de-udp-SingleObjectInfo-CodeCostMS:  " + (System.currentTimeMillis() - start));
//                        System.out.println(" decode-udp-SingleObjectInfo:" + GsonUtils.getInstance().toJson(decodeDtoArr));
//
//                    }

                    break;
                }


            }
        }

        @Override
        public boolean isExit() {
            return mIsExit;
        }
    };


    private byte[] testdata = new byte[]
            {
                     (byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x06 ,(byte)0x40

                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x4c
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0xf9 ,(byte)0x02 ,(byte)0x03 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x0c ,(byte)0x42 ,(byte)0x1e ,(byte)0x99 ,(byte)0xae ,(byte)0x40 ,(byte)0xdc
                    ,(byte)0xfe ,(byte)0x48 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x08 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0xe7 ,(byte)0xfb ,(byte)0x40 ,(byte)0x48 ,(byte)0x74 ,(byte)0x6c ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xbf ,(byte)0xcf ,(byte)0x68 ,(byte)0x3f ,(byte)0xcb ,(byte)0x2f ,(byte)0xa9 ,(byte)0x40 ,(byte)0x8d ,(byte)0x6d ,(byte)0x24 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x06 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00

                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x4c
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0xfa ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x0c ,(byte)0x42 ,(byte)0x1d ,(byte)0x5f ,(byte)0xc6 ,(byte)0x40 ,(byte)0x8a
                    ,(byte)0x83 ,(byte)0xb6 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x08 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0xc0 ,(byte)0x48 ,(byte)0xc9 ,(byte)0xc3 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xd6 ,(byte)0x3b ,(byte)0x3e ,(byte)0x3f ,(byte)0xd4 ,(byte)0xad ,(byte)0xcc ,(byte)0x40 ,(byte)0x91 ,(byte)0xdc ,(byte)0x76 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x06 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00

                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x4c
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0xfe ,(byte)0x02 ,(byte)0x03 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x0c ,(byte)0x41 ,(byte)0xd2 ,(byte)0x42 ,(byte)0x9e ,(byte)0xc1 ,(byte)0x5c
                    ,(byte)0xfb ,(byte)0x5a ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x08 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x40 ,(byte)0x48 ,(byte)0x2b ,(byte)0x32 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xd4 ,(byte)0x96 ,(byte)0xf1 ,(byte)0x3f ,(byte)0xda ,(byte)0x94 ,(byte)0xe0 ,(byte)0x40 ,(byte)0x95 ,(byte)0x15 ,(byte)0xc9 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x06 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00

                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x4c
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x01 ,(byte)0x01 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x0c ,(byte)0x42 ,(byte)0x1c ,(byte)0x01 ,(byte)0xec ,(byte)0x41 ,(byte)0x6b
                    ,(byte)0x94 ,(byte)0x7d ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x08 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0xe7 ,(byte)0xfb ,(byte)0xc0 ,(byte)0x47 ,(byte)0x5b ,(byte)0x06 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xd4 ,(byte)0xad ,(byte)0x42 ,(byte)0x3f ,(byte)0xc5 ,(byte)0x91 ,(byte)0xf0 ,(byte)0x40 ,(byte)0x8c ,(byte)0xa0 ,(byte)0x3b ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x06 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x4c
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x01 ,(byte)0x02 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x0c ,(byte)0x41 ,(byte)0xd0 ,(byte)0xbb ,(byte)0x50 ,(byte)0xc1 ,(byte)0x98
                    ,(byte)0xd2 ,(byte)0xa7 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x08 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x3c ,(byte)0x64 ,(byte)0xcb ,(byte)0x73 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xd9 ,(byte)0x40 ,(byte)0xac ,(byte)0x3f ,(byte)0xc4 ,(byte)0xd3 ,(byte)0x03 ,(byte)0x40 ,(byte)0x9a ,(byte)0xd2 ,(byte)0xae ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x06 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x4c
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x01 ,(byte)0x04 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x0c ,(byte)0x41 ,(byte)0xd1 ,(byte)0x3f ,(byte)0xd6 ,(byte)0xc0 ,(byte)0xca
                    ,(byte)0xe8 ,(byte)0x34 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x08 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x41 ,(byte)0xd0 ,(byte)0xad ,(byte)0xcc ,(byte)0xc0 ,(byte)0x47 ,(byte)0xd2 ,(byte)0x8f ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xd3 ,(byte)0x28 ,(byte)0x0b ,(byte)0x3f ,(byte)0xc6 ,(byte)0xc8 ,(byte)0xcb ,(byte)0x40 ,(byte)0x93 ,(byte)0x2e ,(byte)0x89 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x06 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x4c
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x01 ,(byte)0x06 ,(byte)0x02 ,(byte)0x03 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x0c ,(byte)0x41 ,(byte)0xd2 ,(byte)0x2b ,(byte)0xad ,(byte)0xc0 ,(byte)0x60
                    ,(byte)0x07 ,(byte)0x76 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x08 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x41 ,(byte)0x6b ,(byte)0xa5 ,(byte)0x38 ,(byte)0xc0 ,(byte)0x44 ,(byte)0x49 ,(byte)0x33 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xd1 ,(byte)0xa8 ,(byte)0xd1 ,(byte)0x3f ,(byte)0xcf ,(byte)0x9c ,(byte)0x87 ,(byte)0x40 ,(byte)0x91 ,(byte)0xa5 ,(byte)0xfa ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x06 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x4c
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x01 ,(byte)0x0b ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x0c ,(byte)0x42 ,(byte)0x1c ,(byte)0x25 ,(byte)0xdc ,(byte)0x41 ,(byte)0x8a
                    ,(byte)0x31 ,(byte)0xf5 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x08 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0xc0 ,(byte)0x44 ,(byte)0x92 ,(byte)0x28 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xcb ,(byte)0x7b ,(byte)0xc7 ,(byte)0x3f ,(byte)0xc9 ,(byte)0x6d ,(byte)0x0a ,(byte)0x40 ,(byte)0x8b ,(byte)0x98 ,(byte)0xa0 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x06 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x4c
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0xfb ,(byte)0x02 ,(byte)0x03 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x0c ,(byte)0x41 ,(byte)0x20 ,(byte)0xcf ,(byte)0xfa ,(byte)0x41 ,(byte)0x00
                    ,(byte)0x97 ,(byte)0x0e ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x08 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0xe7 ,(byte)0xfb ,(byte)0xbf ,(byte)0xd0 ,(byte)0x21 ,(byte)0x8a ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xda ,(byte)0xa6 ,(byte)0x56 ,(byte)0x3f ,(byte)0xf7 ,(byte)0x63 ,(byte)0xfe ,(byte)0x40 ,(byte)0x95 ,(byte)0x0a ,(byte)0x1e ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x06 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x4c
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0xff ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x0c ,(byte)0x40 ,(byte)0xe8 ,(byte)0xf7 ,(byte)0xd6 ,(byte)0x41 ,(byte)0x00
                    ,(byte)0xc8 ,(byte)0x2a ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x08 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0xbf ,(byte)0xcf ,(byte)0x3f ,(byte)0x1b ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xda ,(byte)0xa4 ,(byte)0x44 ,(byte)0x3f ,(byte)0xcb ,(byte)0x9e ,(byte)0x55 ,(byte)0x40 ,(byte)0x8e ,(byte)0x0e ,(byte)0xc6 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x06 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x4c
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x01 ,(byte)0x0d ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xcd ,(byte)0x88 ,(byte)0x35 ,(byte)0xc0 ,(byte)0x23
                    ,(byte)0x2b ,(byte)0x5b ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x08 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x41 ,(byte)0x20 ,(byte)0xd9 ,(byte)0xa6 ,(byte)0xb9 ,(byte)0xdd ,(byte)0xcd ,(byte)0x8d ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xdd ,(byte)0x41 ,(byte)0x05 ,(byte)0x3f ,(byte)0xe3 ,(byte)0x1a ,(byte)0x02 ,(byte)0x40 ,(byte)0x99 ,(byte)0xe0 ,(byte)0xcf ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x06 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x4c
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x01 ,(byte)0x0e ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xb1 ,(byte)0xb3 ,(byte)0xd6 ,(byte)0x40 ,(byte)0x21
                    ,(byte)0x46 ,(byte)0x0a ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x08 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x41 ,(byte)0x00 ,(byte)0xac ,(byte)0xe1 ,(byte)0xbb ,(byte)0x59 ,(byte)0x4a ,(byte)0x96 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xdd ,(byte)0x10 ,(byte)0x42 ,(byte)0x3f ,(byte)0xe6 ,(byte)0x09 ,(byte)0x42 ,(byte)0x40 ,(byte)0x95 ,(byte)0x1b ,(byte)0x28 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x06 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x4c
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x01 ,(byte)0x6e ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x0c ,(byte)0x42 ,(byte)0x1d ,(byte)0x76 ,(byte)0xf6 ,(byte)0x41 ,(byte)0x12
                    ,(byte)0xe6 ,(byte)0x88 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x08 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x40 ,(byte)0x48 ,(byte)0x62 ,(byte)0x08 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xcd ,(byte)0xd3 ,(byte)0x6f ,(byte)0x3f ,(byte)0xc6 ,(byte)0x8b ,(byte)0x37 ,(byte)0x40 ,(byte)0x8d ,(byte)0x03 ,(byte)0xc0 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x06 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x4c
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x01 ,(byte)0x8f ,(byte)0x02 ,(byte)0x03 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x0c ,(byte)0x42 ,(byte)0x1c ,(byte)0xac ,(byte)0x6e ,(byte)0x3f ,(byte)0xc2
                    ,(byte)0x66 ,(byte)0xe8 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x08 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0xe7 ,(byte)0xfb ,(byte)0x40 ,(byte)0x47 ,(byte)0xb9 ,(byte)0x8b ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xd5 ,(byte)0xba ,(byte)0xc8 ,(byte)0x3f ,(byte)0xc1 ,(byte)0xbe ,(byte)0xaa ,(byte)0x40 ,(byte)0x8e ,(byte)0x4b ,(byte)0x44 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x06 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x4c
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x01 ,(byte)0x96 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x0c ,(byte)0x41 ,(byte)0xd1 ,(byte)0xb9 ,(byte)0x38 ,(byte)0xbf ,(byte)0x83
                    ,(byte)0x43 ,(byte)0xd3 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x08 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x40 ,(byte)0x47 ,(byte)0xa5 ,(byte)0xdf ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xda ,(byte)0xf8 ,(byte)0x90 ,(byte)0x3f ,(byte)0xc3 ,(byte)0x3b ,(byte)0x94 ,(byte)0x40 ,(byte)0x8e ,(byte)0x00 ,(byte)0x6a ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x06 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x4c
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x01 ,(byte)0xad ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x0c ,(byte)0x41 ,(byte)0x80 ,(byte)0x54 ,(byte)0x45 ,(byte)0xc0 ,(byte)0xc3
                    ,(byte)0xb7 ,(byte)0xa0 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x08 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x42 ,(byte)0x1d ,(byte)0x76 ,(byte)0xf6 ,(byte)0xbd ,(byte)0x04 ,(byte)0x39 ,(byte)0x10 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xd1 ,(byte)0x5c ,(byte)0x3e ,(byte)0x3f ,(byte)0xe4 ,(byte)0x62 ,(byte)0xce ,(byte)0x40 ,(byte)0x92 ,(byte)0x3f ,(byte)0x7d ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x06 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x4c
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x01 ,(byte)0xb6 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x0c ,(byte)0x42 ,(byte)0x1c ,(byte)0xe2 ,(byte)0xd0 ,(byte)0xc1 ,(byte)0xc6
                    ,(byte)0xec ,(byte)0x65 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x08 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x41 ,(byte)0x12 ,(byte)0xe6 ,(byte)0x8e ,(byte)0xc0 ,(byte)0x45 ,(byte)0x38 ,(byte)0x11 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xd0 ,(byte)0x6c ,(byte)0x4f ,(byte)0x3f ,(byte)0xd7 ,(byte)0x7a ,(byte)0x85 ,(byte)0x40 ,(byte)0x95 ,(byte)0xee ,(byte)0xc1 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x06 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x4c
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x01 ,(byte)0xbc ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x0c ,(byte)0x41 ,(byte)0xd2 ,(byte)0xc4 ,(byte)0x78 ,(byte)0xc1 ,(byte)0xc1
                    ,(byte)0xb8 ,(byte)0xb6 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x08 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0xbd ,(byte)0x14 ,(byte)0x86 ,(byte)0x8a ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xdc ,(byte)0x12 ,(byte)0x9e ,(byte)0x3f ,(byte)0xd7 ,(byte)0x7c ,(byte)0xcd ,(byte)0x40 ,(byte)0x90 ,(byte)0x89 ,(byte)0x63 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x06 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x4c
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x01 ,(byte)0xd3 ,(byte)0x02 ,(byte)0x03 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x0c ,(byte)0x41 ,(byte)0xd5 ,(byte)0xc6 ,(byte)0x23 ,(byte)0xc1 ,(byte)0x0c
                    ,(byte)0x9a ,(byte)0x07 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x08 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0xe7 ,(byte)0xfb ,(byte)0xc0 ,(byte)0x47 ,(byte)0x8b ,(byte)0x0a ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xce ,(byte)0x2d ,(byte)0x9d ,(byte)0x3f ,(byte)0xd4 ,(byte)0x90 ,(byte)0x1a ,(byte)0x40 ,(byte)0x8f ,(byte)0xbd ,(byte)0xe6 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x06 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x4c
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x02 ,(byte)0x66 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x0c ,(byte)0x41 ,(byte)0x50 ,(byte)0x5e ,(byte)0x1d ,(byte)0x40 ,(byte)0xf4
                    ,(byte)0x2e ,(byte)0xa3 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x08 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0xbf ,(byte)0xd2 ,(byte)0xb1 ,(byte)0x63 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x0c ,(byte)0x3f ,(byte)0xd4 ,(byte)0xcc ,(byte)0x88 ,(byte)0x3f ,(byte)0xc9 ,(byte)0xb8 ,(byte)0xe9 ,(byte)0x40 ,(byte)0x91 ,(byte)0xf8 ,(byte)0x59 ,(byte)0x00 ,(byte)0x00
                    ,(byte)0x00 ,(byte)0x00 ,(byte)0x06 ,(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
            };

}