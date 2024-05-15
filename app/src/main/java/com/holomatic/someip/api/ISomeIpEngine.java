package com.holomatic.someip.api;

import android.content.Context;

import com.holomatic.someip.cache.CacheImpl;
import com.holomatic.someip.codec.SomeIpPkgCodec;

/**
 * @author 比才-贾硕哲
 * @time 13/5/2024 17:03
 * @desc
 */
public interface ISomeIpEngine {

    void init(Context context);

    void startEngine();

    void stopEngine();

    void mockPkg(short serviceId, short methodId, Object data);

}
