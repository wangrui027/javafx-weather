package com.wangrui027.javafx.weather.util;

import com.wangrui027.javafx.weather.model.WeatherDataModel;
import org.jsoup.nodes.Element;

/**
 * 天气数据模型对象构建类
 */
public class WeatherDataModelBuildUtils {

    public static WeatherDataModel build(Element liEle) {
        String date = liEle.select("h1").html();
        String title = liEle.select(".wea").html();
        String temperature = liEle.select(".tem").text();
        String icon1CssClass = liEle.select("big:eq(1)").removeClass("png40").attr("class");
        String icon2CssClass = liEle.select("big:eq(2)").removeClass("png40").attr("class");
        String wind1 = liEle.select(".win>em>span:eq(0)").attr("title");
        String wind1CssClass = liEle.select(".win>em>span:eq(0)").attr("class");
        String wind2 = liEle.select(".win>em>span:eq(1)").attr("title");
        String wind2CssClass = liEle.select(".win>em>span:eq(1)").attr("class");
        String windLevel = liEle.select(".win>i").text();
        String backgroundCssClass = null;
        if (liEle.hasClass("lv1")) {
            backgroundCssClass = "lv1";
        } else if (liEle.hasClass("lv2")) {
            backgroundCssClass = "lv2";
        } else if (liEle.hasClass("lv3")) {
            backgroundCssClass = "lv3";
        } else if (liEle.hasClass("lv4")) {
            backgroundCssClass = "lv4";
        }
        return WeatherDataModel.builder()
                .date(date)
                .title(title)
                .temperature(temperature)
                .icon1CssClass(icon1CssClass)
                .icon2CssClass(icon2CssClass)
                .wind1(wind1)
                .wind1CssClass(wind1CssClass)
                .wind2(wind2)
                .wind2CssClass(wind2CssClass)
                .windLevel(windLevel)
                .backgroundCssClass(backgroundCssClass)
                .build();
    }

}
