package com.holomatic.someip;

import android.content.Context;
import android.content.res.AssetManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.kotei.ktsomeip.struct.HMIDisplayInfo;

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

    @Test
    public void decodeClass() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.holomatic.someip", appContext.getPackageName());

        AssetManager assetManager = appContext.getAssets();
        try {
            InputStream inputStream = assetManager.open("dto/HMIDisplayInfo.holo");

            byte[] classData = new byte[ inputStream.available()];
            inputStream.read(classData);
            inputStream.close();
            ClassDecUtil decUtil = new ClassDecUtil();
            List<ClassDecUtil.FieldInfo> fieldInfos = decUtil.decodeClass(classData);

            for (ClassDecUtil.FieldInfo f : fieldInfos) {
                System.out.println(" fieldInfo: "+f);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }



    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.holomatic.someip", appContext.getPackageName());

        Class hmiDisClass = HMIDisplayInfo.class;
//        hmiDisClass.
        Field[] list = hmiDisClass.getDeclaredFields();
        Field[] list2 = hmiDisClass.getFields();


        for (Field f : list) {
            System.out.println("onCreate f: "+f.getName());
        }

        CameraManager manager = (CameraManager) appContext.getSystemService(Context.CAMERA_SERVICE);
        String[] cameraIdList = new String[0];
        try {
            cameraIdList = manager.getCameraIdList();
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }

        for (String cameraId : cameraIdList) {
            CameraCharacteristics characteristics = null;
            try {
                characteristics = manager.getCameraCharacteristics(cameraId);
            } catch (CameraAccessException e) {
                throw new RuntimeException(e);
            }
            int cameraType = characteristics.get(CameraCharacteristics.LENS_FACING);
            if (cameraType == CameraCharacteristics.LENS_FACING_FRONT) {
                // 前置摄像头ID
                System.out.println(" cameraID: "+cameraId+", front");
            } else if (cameraType == CameraCharacteristics.LENS_FACING_BACK) {
                // 后置摄像头ID
                System.out.println(" cameraID: "+cameraId+", back");
            }
        }


    }
}