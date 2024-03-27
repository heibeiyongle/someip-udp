package com.holomatic.someip;

import org.junit.Test;

import static org.junit.Assert.*;

import com.kotei.ktsomeip.struct.DistancePoint2F;
import com.kotei.ktsomeip.struct.PrkgSlotInfo;
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

        UDPLayer udpLayer = UDPLayer.getInstance();
        udpLayer.startReceive(receiver);

        TestDto testDto = new TestDto();
        testDto.setI(8);
        testDto.setIa(new int[]{1,2,3,4});
        testDto.setE("test");
        testDto.setEa(new String[]{"test","test2"});

        TestDto2 testDto2 = new TestDto2();
        testDto2.setA(11);
        testDto2.setB(false);
        testDto2.setName("nameTest");
        testDto.setTestDto2(testDto2);

        int testDto2Len = 1000;
        TestDto2[] testDto2s = new TestDto2[testDto2Len];
        for (int i = 0; i < testDto2Len; i++) {
            TestDto2 testDto21 = new TestDto2();
            testDto21.setA(i);
            testDto21.setB(false);
            testDto21.setName("nameTest--"+i);
            testDto2s[i] = testDto21;
        }
            testDto.setTestDto2Arr(testDto2s);

        long start = System.currentTimeMillis();
        byte[] enCodedDataArr = codec.enCodeStruct(testDto);
        System.out.println(" enCodeCostMS:  "+ (System.currentTimeMillis() - start));

        System.out.println("destArrLen: "+ enCodedDataArr.length);

        // decode
        start = System.currentTimeMillis();
        TestDto decodeDto = (TestDto) codec.deCodeStruct(enCodedDataArr,testDto.getClass());
        System.out.println(" deCodeCostMS:  "+ (System.currentTimeMillis() - start));

        System.out.println("decode-dto:"+decodeDto);


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
        byte[] encodeData2 = codec.enCodeStruct( prkgSlotInfo);
        System.out.println(" enCodeCostMS:  "+ (System.currentTimeMillis() - start));
        System.out.println("prkgSlotInfo  destArrLen: "+ encodeData2.length);




        start = System.currentTimeMillis();
        PrkgSlotInfo prkgSlotInfo1 = (PrkgSlotInfo) codec.deCodeStruct(encodeData2, PrkgSlotInfo.class);
        System.out.println(" deCodeCostMS:  "+ (System.currentTimeMillis() - start));
        System.out.println(" srcObj:  "+ GsonUtils.getInstance().toJson(prkgSlotInfo));
        System.out.println(" decodeObj:  "+ GsonUtils.getInstance().toJson(prkgSlotInfo1));

        SomeIpPkgCodec.SomeIpPkg someIpPkg = new SomeIpPkgCodec.SomeIpPkg((short) 0x1002,(short) 0x1003,encodeData2);

        udpLayer.sendMsg(someIpPkg.toBytes());



        // wait udp dealer
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



    public static class TestDto {
        private int i=1;
        private int[] ia= {1,2,3,4,5};
        private boolean b;
        private boolean[] ba;
        //        private long l;
//        private long[] la;
//        private short s;
//        private short[] sa;
        private String e = "hello";
        private String[] ea = {"hello1","hello2"};
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



    public static class TestDto2{
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


    UDPLayer.UDPPkgReceiver receiver = new UDPLayer.UDPPkgReceiver() {
        boolean mIsExit = false;

        public void setIsExit(boolean mIsExit) {
            this.mIsExit = mIsExit;
        }

        @Override
        public void onGotData(byte[] data, int offset, int length) {
            System.out.println("upd-rec-onGotData: offset: "+offset+", length: "+length+", data: "+Arrays.toString(Arrays.copyOfRange(data,offset,length)));

            SomeIpPkgCodec.SomeIpPkg someIpPkg = SomeIpPkgCodec.decode(data,offset,length);
            System.out.println(" upd-rec-someIpPkg:  "+ someIpPkg);

            switch (someIpPkg.mServiceId){
                case 0x1002:{
                    long start = System.currentTimeMillis();
                    PrkgSlotInfo prkgSlotInfo2 = (PrkgSlotInfo) codec.deCodeStruct(someIpPkg.payload, PrkgSlotInfo.class);
                    System.out.println(" upd-rec-decodeObj2:  "+ GsonUtils.getInstance().toJson(prkgSlotInfo2));
                    System.out.println(" upd-rec-deCodeCostMS:  "+ (System.currentTimeMillis() - start));
                }
            }
        }

        @Override
        public boolean isExit() {
            return mIsExit;
        }
    };


}