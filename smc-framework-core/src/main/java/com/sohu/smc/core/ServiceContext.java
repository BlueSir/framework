package com.sohu.smc.core;

import com.sohu.smc.core.admin.LifeCycleStat;
import com.sohu.smc.core.bundles.BundleUsage;

/**
 * bundle context
 * User: shijinkui
 * Date: 12-9-3
 * Time: 下午6:17
 * To change this template use File | Settings | File Templates.
 */
public class ServiceContext {
    private LifeCycleStat stat;
    private BundleUsage usage;

    public String getUsage() {
        //todo
        return "";
    }

    public LifeCycleStat getStat() {
        return stat;
    }

    public void setStat(LifeCycleStat stat) {
        this.stat = stat;
    }
}
