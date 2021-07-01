package com.wangrui027.javafx.weather.model;

import lombok.Builder;
import lombok.Getter;

/**
 * 天气数据模型
 */
@Builder
@Getter
public class WeatherDataModel {

    /**
     * 天气日期，如：30日（今天）、1日（明天）
     */
    private String date;

    /**
     * 天气标题，如：雷阵雨转阴、雷阵雨
     */
    private String title;

    /**
     * 温度信息，如：32/23℃、30/22℃
     */
    private String temperature;

    /**
     * 天气1图标类，如：png40 d04
     */
    private String icon1CssClass;

    /**
     * 天气2图标类，如：png40 n02
     */
    private String icon2CssClass;

    /**
     * 风向1，如：东南风
     */
    private String wind1;

    /**
     * 风向1图标类：SE
     */
    private String wind1CssClass;

    /**
     * 风向1，如：东风
     */
    private String wind2;

    /**
     * 风向2图标类：E
     */
    private String wind2CssClass;

    /**
     * 风力级别，如：<3级
     */
    private String windLevel;

    /**
     * 背景色CSS类
     */
    private String backgroundCssClass;

}
