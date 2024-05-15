package com.holomatic.someip.core;

import android.content.Context;

import com.holomatic.someip.codec.SomeIpPkgCodec;

/**
 * @author 比才-贾硕哲
 * @time 13/5/2024 17:14
 * @desc
 */
public interface ISomeIpPkgDealer {

    /**
     *
     * @param pkg
     * 根据messageId
     * 确定结构体，以及 getter / setter / RR
     *
     * CacheInfo
     *  data,
     *  methodType, Field, event, RR
     *
     * cache list
     *  HoloDefPaaFuncSt
     *  init default
     *
     * rec dealPkg
     * cmd change pkg
     *
     *
     *
     *
     *
     */

    void init(Context context);

    void onRecSomeIp(SomeIpPkgCodec.SomeIpPkg pkg);

    void setPkgSender(ISomeIpSender sender);

    void mockPkg(short serviceId, short methodId, Object data);

    interface ISomeIpSender{
        void sendMsg(SomeIpPkgCodec.SomeIpPkg pkg);
    }



}
