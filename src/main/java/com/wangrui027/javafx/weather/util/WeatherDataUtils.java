package com.wangrui027.javafx.weather.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wangrui027.javafx.weather.model.City;
import com.wangrui027.javafx.weather.model.County;
import com.wangrui027.javafx.weather.model.Province;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 天气数据工具类
 */
public class WeatherDataUtils {

    private static final List<Province> provinceList = new ArrayList<>();

    /**
     * 获取所有的省份数据
     *
     * @return
     */
    public static List<Province> getProvinceList() {
        if (provinceList.isEmpty()) {
            JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(WeatherDataUtils.class.getResourceAsStream("/json/city.json"))).getAsJsonObject();
            Set<String> provinceNameSet = jsonObject.keySet();
            for (String provinceName : provinceNameSet) {
                Province province = new Province();
                province.setName(provinceName);
                JsonObject cityListObject = jsonObject.getAsJsonObject(provinceName);
                Set<String> cityNameSet = cityListObject.keySet();
                List<City> cityList = new ArrayList<>();
                province.setCityList(cityList);
                for (String cityName : cityNameSet) {
                    City city = new City();
                    city.setName(cityName);
                    city.setProvince(province);
                    cityList.add(city);
                    JsonObject countryListObject = cityListObject.getAsJsonObject(cityName);
                    Set<String> countryNameSet = countryListObject.keySet();
                    List<County> countyList = new ArrayList<>();
                    city.setCountyList(countyList);
                    for (String countryName : countryNameSet) {
                        JsonObject countryObject = countryListObject.getAsJsonObject(countryName);
                        County county = new County();
                        county.setName(countryObject.get("NAMECN").getAsString());
                        county.setAreaId(countryObject.get("AREAID").getAsString());
                        county.setCity(city);
                        countyList.add(county);
                    }
                }
                provinceList.add(province);
            }
        }
        return provinceList;
    }

    /**
     * 根据省份获取城市列表
     *
     * @param provinceName
     * @return
     */
    public static List<City> getCityListByProvince(String provinceName) {
        for (Province province : provinceList) {
            if (province.getName().equals(provinceName)) {
                return province.getCityList();
            }
        }
        return new ArrayList<>();
    }

    /**
     * 根据城市获取区县列表
     *
     * @param cityName
     * @return
     */
    public static List<County> getCountryListByCity(String cityName) {
        for (Province province : provinceList) {
            List<City> cityList = province.getCityList();
            for (City city : cityList) {
                if (city.getName().equals(cityName)) {
                    return city.getCountyList();
                }
            }
        }
        return new ArrayList<>();
    }
}
