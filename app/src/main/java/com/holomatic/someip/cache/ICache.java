package com.holomatic.someip.cache;

import com.holomatic.someip.codec.SomeIpPkgCodec;
import com.holomatic.someip.core.ISomeIpPkgDealer;

import java.util.List;

/**
 * @author 比才-贾硕哲
 * @time 14/5/2024 17:12
 * @desc
 */
public interface ICache {

    void initCache(List<CacheItem> cacheItems);

    void update(SomeIpPkgCodec.SomeIpPkg pkg);

    void setPkgSender(ISomeIpPkgDealer.ISomeIpSender sender);

    void mockPkg(short serviceId, short methodId, Object data);

}
