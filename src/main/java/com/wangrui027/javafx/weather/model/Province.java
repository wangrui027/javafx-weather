package com.wangrui027.javafx.weather.model;

import lombok.Data;

import java.util.List;

/**
 * 省份
 */
@Data
public class Province {

    /**
     * 省份名称
     */
    private String name;

    /**
     * 省份下辖城市
     */
    private List<City> cityList;

}
