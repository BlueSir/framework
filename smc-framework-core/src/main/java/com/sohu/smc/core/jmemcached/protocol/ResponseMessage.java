package com.sohu.smc.core.jmemcached.protocol;

import com.sohu.smc.core.jmemcached.CacheElement;
import com.sohu.smc.core.jmemcached.MemcachedHandler;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Represents the response to a command.
 */
public final class ResponseMessage<CACHE_ELEMENT extends CacheElement> implements Serializable {

    public ResponseMessage(CommandMessage cmd) {
        this.cmd = cmd;
    }

    public CommandMessage<CACHE_ELEMENT> cmd;
    public CACHE_ELEMENT[] elements;
    public MemcachedHandler.StoreResponse response;
    public Map<String, Set<String>> stats;
    public String version;
    public MemcachedHandler.DeleteResponse deleteResponse;
    public Integer incrDecrResponse;
    public boolean flushSuccess;

    public ResponseMessage<CACHE_ELEMENT> withElements(CACHE_ELEMENT[] elements) {
        this.elements = elements;
        return this;
    }

    public ResponseMessage<CACHE_ELEMENT> withResponse(MemcachedHandler.StoreResponse response) {
        this.response = response;
        return this;
    }

    public ResponseMessage<CACHE_ELEMENT> withDeleteResponse(MemcachedHandler.DeleteResponse deleteResponse) {
        this.deleteResponse = deleteResponse;
        return this;
    }

    public ResponseMessage<CACHE_ELEMENT> withIncrDecrResponse(Integer incrDecrResp) {
        this.incrDecrResponse = incrDecrResp;

        return this;
    }

    public ResponseMessage<CACHE_ELEMENT> withStatResponse(Map<String, Set<String>> stats) {
        this.stats = stats;

        return this;
    }

    public ResponseMessage<CACHE_ELEMENT> withFlushResponse(boolean success) {
        this.flushSuccess = success;

        return this;
    }
}
