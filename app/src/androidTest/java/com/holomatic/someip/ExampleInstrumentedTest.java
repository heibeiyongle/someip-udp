package com.holomatic.someip;

import android.content.Context;
import android.content.res.AssetManager;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.holomatic.holopilotparking.soa.module.holodto.HoloDefUserSettings;
import com.holomatic.holopilotparking.soa.module.holodto.HoloDefVehicleInfo;
import com.holomatic.someip.api.SomeIpEngine;
import com.holomatic.someip.cache.CacheImpl;
import com.holomatic.someip.codec.SomeIpPkgCodec;
import com.holomatic.utils.ClassDecUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    SomeIpEngine engine;
    @Test
    public void startEngine() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.holomatic.someip", appContext.getPackageName());

        engine = SomeIpEngine.getInstance();
        engine.init(appContext);
        engine.startEngine();

        byte speed = 0;
        while (true){

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            HoloDefVehicleInfo holoDefVehicleInfo = new HoloDefVehicleInfo();
            holoDefVehicleInfo.Vehicle_Speed =speed++ ;
            engine.mockPkg((short) 0x6001,(short) 0x8001,holoDefVehicleInfo);

        }

    }



    @Test
    public void setEvent() {
        // Context of the app under test.
        // event
//        CacheImpl.SoaSpecInfo specInfo60018001 = new CacheImpl.SoaSpecInfo(0x6001, 0x8001, HoloDefVehicleInfo.class,
//                true, null, Object.class);

//        SoaSpecInfo specInfo60018001 = new SoaSpecInfo( 0x6001, 0x8001, HoloDefVehicleInfo.class,
//                true,new Integer[]{ SOAFunctionName.INSTRUMENT_VEHICLE_INFO}, Object.class);
//        SoaSpecInfo specInfo500d_8006 = new SoaSpecInfo( 0x6003, 0x9001, HoloDefUserSettings.class,
//                true,new Integer[]{ SOAFunctionName.USER_SETTINGS}, Object.class);
//        SoaSpecInfo specInfo503d_8001 = new SoaSpecInfo( 0x6002, 0x9003, Byte.TYPE,
//                true,new Integer[]{ SOAFunctionName.UDP_PAA_FUNC_ST}, Object.class);
        HoloDefVehicleInfo holoDefVehicleInfo = new HoloDefVehicleInfo();
        holoDefVehicleInfo.Vehicle_Speed =1 ;
        engine.mockPkg((short) 0x6001,(short) 0x8001,holoDefVehicleInfo);
    }
}