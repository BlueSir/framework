package com.sohu.smc.jdbc;

import java.io.Serializable;
import java.util.Date;

public class TblClientInstall implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 4162346274843850216L;

    /**
     * This attribute maps to the column id in the tbl_client_install table.
     */
    protected int id;

    /**
     * This attribute maps to the column client_id in the tbl_client_install table.
     */
    protected int clientId;

    /**
     * This attribute maps to the column product_id in the tbl_client_install table.
     */
    protected int productId;

    /**
     * This attribute maps to the column channel_id in the tbl_client_install table.
     */
    protected int channelId;

    /**
     * This attribute maps to the column imei in the tbl_client_install table.
     */
    protected String imei;

    /**
     * This attribute maps to the column version in the tbl_client_install table.
     */
    protected String version;

    /**
     * This attribute maps to the column is_active in the tbl_client_install table.
     */
    protected short isActive;

    /**
     * This attribute maps to the column active_time in the tbl_client_install table.
     */
    protected Date activeTime = new Date();

    /**
     * This attribute maps to the column install_time in the tbl_client_install table.
     */
    protected Date installTime = new Date();

    /**
     * This attribute maps to the column curr_channel_id in the tbl_client_install table.
     */
    protected int currChannelId;

    /**
     * This attribute maps to the column api_version in the tbl_client_install table.
     */
    protected int apiVersion;

    /**
     * This attribute maps to the column active_channel_id in the tbl_client_install table.
     */
    protected int activeChannelId;

    /**
     * This attribute maps to the column ctime in the tbl_client_install table.
     */
    protected Date ctime;

    /**
     * This attribute maps to the column mtime in the tbl_client_install table.
     */
    protected Date mtime;

    /**
     * Method 'TblClientInstall'
     */
    public TblClientInstall() {
    }

    public TblClientInstall(int clientId, int productId, int channelId,
                            String imei, String version, short isActive,
                            int currChannelId, int apiVersion) {
        super();
        this.clientId = clientId;
        this.productId = productId;
        this.channelId = channelId;
        this.imei = imei;
        this.version = version;
        this.isActive = isActive;
        if (isActive == 1) {
            this.activeChannelId = channelId;
            this.activeTime = new Date();
        }
        this.currChannelId = currChannelId;
        this.apiVersion = apiVersion;
        this.installTime = new Date();
        this.ctime = new Date();
        this.mtime = new Date();
    }

    /**
     * Method 'getId'
     *
     * @return int
     */
    public int getId() {
        return id;
    }

    /**
     * Method 'setId'
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Method 'getClientId'
     *
     * @return int
     */
    public int getClientId() {
        return clientId;
    }

    /**
     * Method 'setClientId'
     *
     * @param clientId
     */
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    /**
     * Method 'getProductId'
     *
     * @return int
     */
    public int getProductId() {
        return productId;
    }

    /**
     * Method 'setProductId'
     *
     * @param productId
     */
    public void setProductId(int productId) {
        this.productId = productId;
    }

    /**
     * Method 'getChannelId'
     *
     * @return int
     */
    public int getChannelId() {
        return channelId;
    }

    /**
     * Method 'setChannelId'
     *
     * @param channelId
     */
    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    /**
     * Method 'getImei'
     *
     * @return String
     */
    public String getImei() {
        return imei;
    }

    /**
     * Method 'setImei'
     *
     * @param imei
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Method 'getVersion'
     *
     * @return String
     */
    public String getVersion() {
        return version;
    }

    /**
     * Method 'setVersion'
     *
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Method 'getIsActive'
     *
     * @return short
     */
    public short getIsActive() {
        return isActive;
    }

    /**
     * Method 'setIsActive'
     *
     * @param isActive
     */
    public void setIsActive(short isActive) {
        this.isActive = isActive;
    }

    /**
     * Method 'getActiveTime'
     *
     * @return Date
     */
    public Date getActiveTime() {
        return activeTime;
    }

    /**
     * Method 'setActiveTime'
     *
     * @param activeTime
     */
    public void setActiveTime(Date activeTime) {
        this.activeTime = activeTime;
    }

    /**
     * Method 'getInstallTime'
     *
     * @return Date
     */
    public Date getInstallTime() {
        return installTime;
    }

    /**
     * Method 'setInstallTime'
     *
     * @param installTime
     */
    public void setInstallTime(Date installTime) {
        this.installTime = installTime;
    }

    /**
     * Method 'getCurrChannelId'
     *
     * @return int
     */
    public int getCurrChannelId() {
        return currChannelId;
    }

    /**
     * Method 'setCurrChannelId'
     *
     * @param currChannelId
     */
    public void setCurrChannelId(int currChannelId) {
        this.currChannelId = currChannelId;
    }

    /**
     * Method 'getApiVersion'
     *
     * @return int
     */
    public int getApiVersion() {
        return apiVersion;
    }

    /**
     * Method 'setApiVersion'
     *
     * @param apiVersion
     */
    public void setApiVersion(int apiVersion) {
        this.apiVersion = apiVersion;
    }

    /**
     * Method 'getActiveChannelId'
     *
     * @return int
     */
    public int getActiveChannelId() {
        return activeChannelId;
    }

    /**
     * Method 'setActiveChannelId'
     *
     * @param activeChannelId
     */
    public void setActiveChannelId(int activeChannelId) {
        this.activeChannelId = activeChannelId;
    }

    /**
     * Method 'getCtime'
     *
     * @return Date
     */
    public Date getCtime() {
        return ctime;
    }

    /**
     * Method 'setCtime'
     *
     * @param ctime
     */
    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    /**
     * Method 'getMtime'
     *
     * @return Date
     */
    public Date getMtime() {
        return mtime;
    }

    /**
     * Method 'setMtime'
     *
     * @param mtime
     */
    public void setMtime(Date mtime) {
        this.mtime = mtime;
    }

    /**
     * Method 'equals'
     *
     * @param _other
     * @return boolean
     */
    public boolean equals(Object _other) {
        if (_other == null) {
            return false;
        }

        if (_other == this) {
            return true;
        }

        if (!(_other instanceof TblClientInstall)) {
            return false;
        }

        final TblClientInstall _cast = (TblClientInstall) _other;
        if (id != _cast.id) {
            return false;
        }

        if (clientId != _cast.clientId) {
            return false;
        }

        if (productId != _cast.productId) {
            return false;
        }

        if (channelId != _cast.channelId) {
            return false;
        }

        if (imei == null ? _cast.imei != imei : !imei.equals(_cast.imei)) {
            return false;
        }

        if (version == null ? _cast.version != version : !version.equals(_cast.version)) {
            return false;
        }

        if (isActive != _cast.isActive) {
            return false;
        }

        if (activeTime == null ? _cast.activeTime != activeTime : !activeTime.equals(_cast.activeTime)) {
            return false;
        }

        if (installTime == null ? _cast.installTime != installTime : !installTime.equals(_cast.installTime)) {
            return false;
        }

        if (currChannelId != _cast.currChannelId) {
            return false;
        }

        if (apiVersion != _cast.apiVersion) {
            return false;
        }

        if (activeChannelId != _cast.activeChannelId) {
            return false;
        }

        if (ctime == null ? _cast.ctime != ctime : !ctime.equals(_cast.ctime)) {
            return false;
        }

        if (mtime == null ? _cast.mtime != mtime : !mtime.equals(_cast.mtime)) {
            return false;
        }

        return true;
    }

    /**
     * Method 'hashCode'
     *
     * @return int
     */
    public int hashCode() {
        int _hashCode = 0;
        _hashCode = 29 * _hashCode + id;
        _hashCode = 29 * _hashCode + clientId;
        _hashCode = 29 * _hashCode + productId;
        _hashCode = 29 * _hashCode + channelId;
        if (imei != null) {
            _hashCode = 29 * _hashCode + imei.hashCode();
        }

        if (version != null) {
            _hashCode = 29 * _hashCode + version.hashCode();
        }

        _hashCode = 29 * _hashCode + (int) isActive;
        if (activeTime != null) {
            _hashCode = 29 * _hashCode + activeTime.hashCode();
        }

        if (installTime != null) {
            _hashCode = 29 * _hashCode + installTime.hashCode();
        }

        _hashCode = 29 * _hashCode + currChannelId;
        _hashCode = 29 * _hashCode + apiVersion;
        _hashCode = 29 * _hashCode + activeChannelId;
        if (ctime != null) {
            _hashCode = 29 * _hashCode + ctime.hashCode();
        }

        if (mtime != null) {
            _hashCode = 29 * _hashCode + mtime.hashCode();
        }

        return _hashCode;
    }

    /**
     * Method 'toString'
     *
     * @return String
     */
    public String toString() {
        StringBuffer ret = new StringBuffer();
        ret.append("com.sohu.smc.service.user.dto.TblClientInstall: ");
        ret.append("id=" + id);
        ret.append(", clientId=" + clientId);
        ret.append(", productId=" + productId);
        ret.append(", channelId=" + channelId);
        ret.append(", imei=" + imei);
        ret.append(", version=" + version);
        ret.append(", isActive=" + isActive);
        ret.append(", activeTime=" + activeTime);
        ret.append(", installTime=" + installTime);
        ret.append(", currChannelId=" + currChannelId);
        ret.append(", apiVersion=" + apiVersion);
        ret.append(", activeChannelId=" + activeChannelId);
        ret.append(", ctime=" + ctime);
        ret.append(", mtime=" + mtime);
        return ret.toString();
    }

}
