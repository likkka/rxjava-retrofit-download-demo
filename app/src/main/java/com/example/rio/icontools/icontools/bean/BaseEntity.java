package com.example.rio.icontools.icontools.bean;

import java.util.List;

/**
 * Created by rio on 17-6-23.
 */

public class BaseEntity {
    private int code;
    private FlymeIconBeanEntity data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public FlymeIconBeanEntity getData() {
        return data;
    }

    public void setData(FlymeIconBeanEntity data) {
        this.data = data;
    }
}
