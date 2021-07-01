package com.wangrui027.javafx.weather.model;

import lombok.Data;

/**
 * 区县
 */
@Data
public class County {

    /**
     * 区县名称
     */
    private String name;

    /**
     * 区域id
     */
    private String areaId;

    /**
     * 区县所属城市
     */
    private City city;

}
