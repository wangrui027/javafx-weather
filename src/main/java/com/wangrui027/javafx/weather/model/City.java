package com.wangrui027.javafx.weather.model;

import lombok.Data;

import java.util.List;

/**
 * 城市
 */
@Data
public class City {

    /**
     * 城市名称
     */
    private String name;

    /**
     * 城市下辖区县
     */
    private List<County> countyList;

    /**
     * 城市所属省份
     */
    private Province province;

}
