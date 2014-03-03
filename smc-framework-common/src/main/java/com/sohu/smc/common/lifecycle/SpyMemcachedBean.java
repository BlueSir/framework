/**
 * $Id: SpyMemcachedBean.java 3576 2012-07-05 08:58:54Z qinqidong $
 * All Rights Reserved.
 */
package com.sohu.smc.common.lifecycle;

import org.springframework.beans.factory.FactoryBean;

/**
 * Memcache池配置
 *
 * @author <a href="mailto:minni@sohu-inc.com">NiMin</a>
 * @version 1.0 2012-07-03 17:31:31
 */
public class SpyMemcachedBean implements FactoryBean {

    public Object getObject() throws Exception {
        return this;
    }

    @SuppressWarnings("unchecked")
    public Class getObjectType() {
        return SpyMemcachedBean.class;
    }

    public boolean isSingleton() {
        return true;
    }

    /**
     * 标识是否需要用双实例来实现故障转移，默认为false
     */
    private boolean failover = false;

    /**
     * 地址列表
     */
    private String masterAddress;

    /**
     * 另一组地址列表（如果不需双实例，slave值为空）
     */
    private String slaveAddress;

    private String zkConn;

    public String getZkConn() {
        return zkConn;
    }

    public void setZkConn(String zkConn) {
        this.zkConn = zkConn;
    }

    /**
     * @return the failover
     */
    public boolean isFailover() {
        return failover;
    }

    /**
     * @param failover the failover to set
     */
    public void setFailover(boolean failover) {
        this.failover = failover;
    }

    /**
     * @return the masterAddress
     */
    public String getMasterAddress() {
        return masterAddress;
    }

    /**
     * @param masterAddress the masterAddress to set
     */
    public void setMasterAddress(String masterAddress) {
        this.masterAddress = masterAddress;
    }

    /**
     * @return the slaveAddress
     */
    public String getSlaveAddress() {
        return slaveAddress;
    }

    /**
     * @param slaveAddress the slaveAddress to set
     */
    public void setSlaveAddress(String slaveAddress) {
        this.slaveAddress = slaveAddress;
    }


    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SpyMemcachedBean [failover=" + failover + ", masterAddress=" + masterAddress + ", slaveAddress=" + slaveAddress + "]";
    }

}
