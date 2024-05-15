package com.holomatic.someip.core;

import android.content.Context;

import com.holomatic.someip.cache.CacheImpl;
import com.holomatic.someip.codec.SomeIpPkgCodec;

/**
 * @author 比才-贾硕哲
 * @time 15/5/2024 15:52
 * @desc
 */
public class SomeIpPkgDealer implements ISomeIpPkgDealer{
    CacheImpl cacheImpl ;
    @Override
    public void init(Context context) {
        CacheImpl.initCtx(context);
        cacheImpl = CacheImpl.getInstance();

        // init default cache

    }

    @Override
    public void onRecSomeIp(SomeIpPkgCodec.SomeIpPkg pkg) {
        cacheImpl.update(pkg);
    }

    @Override
    public void setPkgSender(ISomeIpSender sender) {
        cacheImpl.setPkgSender(sender);
    }

    @Override
    public void mockPkg(short serviceId, short methodId, Object data) {
        cacheImpl.mockPkg(serviceId,methodId,data);
    }
}
