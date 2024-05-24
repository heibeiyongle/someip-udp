package com.holomatic.someip.cache;

import static com.holomatic.someip.cache.CacheImpl.CacheType.RR;
import static com.holomatic.someip.cache.CacheImpl.CacheType.Event;
import static com.holomatic.someip.cache.CacheImpl.CacheType.FF;
import static com.holomatic.someip.cache.CacheImpl.CacheType.FieldGetter;
import static com.holomatic.someip.cache.CacheImpl.CacheType.FieldNotifier;
import static com.holomatic.someip.cache.CacheImpl.CacheType.FieldSetter;
import static com.holomatic.someip.cache.CacheImpl.CacheType.None;
import static com.holomatic.someip.cache.CacheImpl.CacheType.RR;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.holomatic.holopilotparking.soa.module.holodto.HoloDefUserSettings;
import com.holomatic.holopilotparking.soa.module.holodto.HoloDefVehicleInfo;
import com.holomatic.someip.codec.PayloadCodec;
import com.holomatic.someip.codec.SomeIpPkgCodec;
import com.holomatic.someip.core.ISomeIpPkgDealer;
import com.holomatic.utils.ClassDecUtil;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.Key;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 比才-贾硕哲
 * @time 14/5/2024 17:40
 * @desc
 */
public class CacheImpl implements ICache{

    PayloadCodec mDeCode;
    PayloadCodec mEnCode;
    private static CacheImpl instance;


    private CacheImpl(Context context) {

        mEnCode = new PayloadCodec();
        mDeCode = new PayloadCodec();

        AssetManager assetManager = context.getAssets();
        try {
            String[] files = assetManager.list("dto/");
            HashMap<String, List<ClassDecUtil.FieldInfo>> mMapClassNameFields = new HashMap<>();
            mEnCode.setMapClassNameFields(mMapClassNameFields);
            mDeCode.setMapClassNameFields(mMapClassNameFields);
            for (String fileName:files) {
                InputStream inputStream = assetManager.open("dto/"+fileName);
                byte[] classData = new byte[inputStream.available()];
                inputStream.read(classData);
                inputStream.close();
                ClassDecUtil decUtil = new ClassDecUtil();
                List<ClassDecUtil.FieldInfo> fieldInfos = decUtil.decodeClass(classData);
//                    HLog.i(TAG, "HoloChannelManager put: "+fileName+", fields: "+Arrays.toString(fieldInfos.toArray(fieldInfos.toArray(new ClassDecUtil.FieldInfo[0]))));
                mMapClassNameFields.put(fileName,fieldInfos);
            }

        }catch (Exception e){
            throw new RuntimeException(e);
        }
        initClassMap();
    }

    static Context mCtx;
    public static void initCtx(Context context){
        mCtx = context;
    }

    public static CacheImpl getInstance() {
        if (instance == null) {
            synchronized (CacheImpl.class) {
                if (instance == null) {
                    instance = new CacheImpl(mCtx);
                }
            }
        }
        return instance;
    }

    HashMap<Integer,Object> cacheMap = new HashMap();

    @Override
    public void initCache(List<CacheItem> cacheItems) {
        if(cacheItems == null || cacheItems.size() == 0 ){
            return;
        }
        for (CacheItem item : cacheItems) {
            cacheMap.put(item.mCacheId,item.data);
        }
    }

    private static final String TAG = "CacheImpl";
    /**
     *
     * cacheMap
     * key objectId
     *      serviceId concat 1001 // 代表ObjectId
     *      serviceId concat 8001 // 代表OEventObjectId
     * cache
     * Object
     *
     * Field list
     * getter 5001
     * setter 6001
     * notifier 9001
     *
     * event list 800x
     *
     *
     * 找到 对应cache
     * key value
     * RR
     * setter
     * getter
     * notifier
     * field
     * event
     * 约定: 收到event,更新cache，转发event
     *
     */
    @Override
    public void update(SomeIpPkgCodec.SomeIpPkg pkg) {
        Log.i(TAG, "update: pkg: "+pkg);
        CacheType reqPkgType = getPkgType(pkg.getmMethodId());
        switch (reqPkgType) {
            case FieldSetter: {
                // set , notify
                int key = getCacheId(pkg);
                Object payloadObj = decodePayload(pkg);
                cacheMap.put(key, payloadObj);
                notify(key);
                break;
            }
            case FieldGetter: {
                int key = getCacheId(pkg);
                if(cacheMap.containsKey(key)){
                    notify(key);
                }
                break;
            }
            case Event: {
//                int key = getCacheId(pkg);
//                Object payloadObj = decodePayload(pkg);
//                cacheMap.put(key, payloadObj);
//                notify(key);
                break;
            }
            case RR:{
                Log.i(TAG, "update RR "+pkg);
                break;
            }
        }
    }

    int getCacheId(SomeIpPkgCodec.SomeIpPkg pkg){
        return getCacheId(pkg.getmServiceId(),pkg.getmMethodId());
    }

    int getCacheId( short serviceId, short methodId ){
        // res serviceId + 1001 8001

        int seed = 0x1000;
        if(((Short.toUnsignedInt(methodId) & 0xF000) >> 12) == 8){
            seed = 0x8000;
        }

        int res = (Short.toUnsignedInt(serviceId) << 16) | (Short.toUnsignedInt(methodId) & 0xF | seed);
        return res;
    }

    Object decodePayload(SomeIpPkgCodec.SomeIpPkg pkg){
        Class type = mSoaMessageIdWithStructType.get(convertSIdMIdToKey(pkg.getmServiceId(),pkg.getmMethodId()));
        if(type == null){
            RuntimeException e = new RuntimeException(" pkg not reg! "+pkg);
            Log.e(TAG, "decodePayload: ", e);
            throw e;
        }
        Object res = mDeCode.deCodeStruct(pkg.getPayload(),type);
        return res;
    }

    SomeIpPkgCodec.SomeIpPkg genPkg(int cacheId,Object object){
        short mServiceId = (short) (cacheId >> 16);
        short mMethodId = 0;
        int type = cacheId & 0xF000;
        if(type == 0x1000){
            mMethodId = (short) (0x9000 | cacheId & 0x00FF);
        } else if (type == 0x8000) {
            mMethodId = (short) (cacheId&0xFFFF);
        }
        SomeIpPkgCodec.SomeIpPkg res = new SomeIpPkgCodec.SomeIpPkg(mServiceId,mMethodId,null);
        synchronized (mEnCode){
            res.setPayload( mEnCode.enCodeStruct(object));
        }
        return res;
    }


    void notify(int cacheId){
        Log.i(TAG, "notify cacheId: "+Integer.toUnsignedString(cacheId,16));
        if(mSomeIpPkgSender != null){
            SomeIpPkgCodec.SomeIpPkg tmpPkg = genPkg(cacheId,cacheMap.get(cacheId));
            Log.i(TAG, "notify: "+tmpPkg);
            mSomeIpPkgSender.sendMsg(tmpPkg);
        }
    }



    ISomeIpPkgDealer.ISomeIpSender mSomeIpPkgSender;

    @Override
    public void setPkgSender(ISomeIpPkgDealer.ISomeIpSender sender) {
        mSomeIpPkgSender = sender;
    }

    @Override
    public void mockPkg(short serviceId, short methodId, Object data) {
        CacheType reqPkgType = getPkgType(methodId);
        Log.i(TAG, "mockPkg: reqType: "+reqPkgType+" serviceId: 0x"+Integer.toUnsignedString(Short.toUnsignedInt(serviceId),16)+", methodId: 0x"+Integer.toUnsignedString(Short.toUnsignedInt(methodId),16)+", data: "+data);
        switch (reqPkgType) {
            case FieldSetter:
            case FieldNotifier:
            {
                // set , notify
                int key = getCacheId(serviceId,methodId);
                cacheMap.put(key, data);
                notify(key);
                break;
            }
            case FieldGetter: {
                int key = getCacheId(serviceId,methodId);
                notify(key);
                break;
            }
            case Event: {
                int key = getCacheId(serviceId,methodId);
                cacheMap.put(key, data);
                notify(key);
                break;
            }
            case RR:{
                Log.i(TAG, "update RR ");
                break;
            }
        }

    }

    private CacheType getPkgType(@NonNull short methodId){
        CacheType res = CacheType.None;
        int type = (Short.toUnsignedInt(methodId)&0xF000) >> 12;
        Log.i(TAG, "getPkgType: methodId: "+Integer.toUnsignedString(Short.toUnsignedInt(methodId),16)+", type: "+type);
        switch (type){
            case 0:{
                // RR
                res =RR;
                break;
            }
            case 1:{
                //F&F
                res = FF;
                break;
            }
            case 5:{
                // Field getter
                res = FieldGetter;
                break;
            }
            case 6:{
                // Field setter
                res = FieldSetter;
                break;
            }
            case 8:{
                // event
                res = Event;
                break;
            }
            case 9:{
                // Field notifier
                res = FieldNotifier;
                break;
            }
        }
        return res;
    }

    enum CacheType{
        None,
        FF,
        RR,
        FieldGetter,
        FieldSetter,
        FieldNotifier,
        Event
    }


    /**
     *
     * getter setter notifier -> class
     *
     * event -> class
     *
     */
    HashMap<Integer,Class> mSoaMessageIdWithStructType = new HashMap<>();
    private void initClassMap(){
        SoaSpecInfoDef def = new SoaSpecInfoDef();
        Field[] fields = def.getClass().getDeclaredFields();
        try {
            for (Field f:fields) {
                SoaSpecInfo soaSpecInfo = (SoaSpecInfo) f.get(def);
                mSoaMessageIdWithStructType.put(
                        convertSIdMIdToKey(soaSpecInfo.serviceId, soaSpecInfo.methodId),
                        soaSpecInfo.structClass);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }


    }
    protected int convertSIdMIdToKey( short serviceId, short methodId ){
        int res = -1;
        res = (serviceId&0xFFFF) << 8 | (methodId&0xFFFF);
        return res;
    }







    static class SoaSpecInfoDef {
        // holo add for debug
        SoaSpecInfo specInfo60018001 = new SoaSpecInfo(0x6001, 0x8001, HoloDefVehicleInfo.class,
                true, null, Object.class);
        SoaSpecInfo specInfo500d_8006 = new SoaSpecInfo(0x6003, 0x9001, HoloDefUserSettings.class,
                true, null, Object.class);
        SoaSpecInfo specInfo503d_8001 = new SoaSpecInfo(0x6002, 0x9003, Byte.TYPE,
                true, null, Object.class);
        // todo add setter
        // set paaSt
        SoaSpecInfo specInfo6002_6001 = new SoaSpecInfo(0x6002, 0x6003, Byte.TYPE,
                true, null, Object.class);


    }


    static class SoaSpecInfo{
        short serviceId;
        short methodId;
//        Set<Integer> soaFuncNames = new HashSet<>();
        Class structClass;
        boolean isStructArray = false;

        public SoaSpecInfo(int serviceId, int methodId, Class structClass, boolean isStructArray,
                           Integer[] soaFuncNameArr,Class soaProxy) {
            this.serviceId = (short) serviceId;
            this.methodId = (short) methodId;
            this.structClass = structClass;
            this.isStructArray = isStructArray;
//            Arrays.asList(soaFuncNameArr)
//            this.soaFuncNames.addAll(Arrays.asList(soaFuncNameArr));
        }
    }
}
