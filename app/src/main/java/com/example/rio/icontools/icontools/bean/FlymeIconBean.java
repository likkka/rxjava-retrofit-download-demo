package com.example.rio.icontools.icontools.bean;

/**
 * Created by huangminzhi on 17-6-20.
 * 和数据服务器对接的字段
 * {"code":"000000",
 * "data":
 * {"values":
 * [{"_id":"594c7a555fe64e1ba8632c03",
 * "appName":"即刻","iconL":"http://i4.res.meizu.com/fileserver/app_icon/7359/1176fc69ae85480598a41ce9a4359412.png",
 * "iconM":"http://i4.res.meizu.com/fileserver/app_icon/7359/1176fc69ae85480598a41ce9a4359412.png",
 * "iconM":"http://i4.res.meizu.com/fileserver/app_icon/7359/1176fc69ae85480598a41ce9a4359412.png",
 * "oIconL":"http://i3.res.meizu.com/fileserver/app_icon/7984/c85a4e6daa86414a9a18ad3230ec6744.png",
 * "oIconM":"http://i3.res.meizu.com/fileserver/app_icon/7984/c85a4e6daa86414a9a18ad3230ec6744.png",
 * "oIconS":"http://i3.res.meizu.com/fileserver/app_icon/7984/c85a4e6daa86414a9a18ad3230ec6744.png",
 * "packageName":"com.ruguoapp.jike","sIconL":"http://i3.res.meizu.com/fileserver/app_icon/7984/c85a4e6daa86414a9a18ad3230ec6744.png",
 * "sIconM":"http://i3.res.meizu.com/fileserver/app_icon/7984/c85a4e6daa86414a9a18ad3230ec6744.png",
 * "sIconS":"http://i3.res.meizu.com/fileserver/app_icon/7984/c85a4e6daa86414a9a18ad3230ec6744.png",
 * "createAt":1498184277547,
 * "updateAt":1498184277547,
 * "accPer":{"R":{},"W":{}}}]},
 * "extra":{"count":1,"maxNum":50,"nowPageNum":0},"message":"OK"}
 */

public class FlymeIconBean {
    private String packageName;
    private String iconL, iconM, iconS;
    private String sIconL, sIconM, sIconS;
    private long updateAt;

    public String getIconL() {
        return iconL;
    }

    public void setIconL(String iconL) {
        this.iconL = iconL;
    }

    public String getIconM() {
        return iconM;
    }

    public void setIconM(String iconM) {
        this.iconM = iconM;
    }

    public String getIconS() {
        return iconS;
    }

    public void setIconS(String iconS) {
        this.iconS = iconS;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getsIconL() {
        return sIconL;
    }

    public void setsIconL(String sIconL) {
        this.sIconL = sIconL;
    }

    public String getsIconM() {
        return sIconM;
    }

    public void setsIconM(String sIconM) {
        this.sIconM = sIconM;
    }

    public String getsIconS() {
        return sIconS;
    }

    public void setsIconS(String sIconS) {
        this.sIconS = sIconS;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        return "FlymeIconBean{" +
                "packageName='" + packageName + '\'' +
                ", iconL='" + iconL + '\'' +
                ", iconM='" + iconM + '\'' +
                ", iconS='" + iconS + '\'' +
                ", sIconL='" + sIconL + '\'' +
                ", sIconM='" + sIconM + '\'' +
                ", sIconS='" + sIconS + '\'' +
                ", updateAt=" + updateAt +
                '}';
    }
}
