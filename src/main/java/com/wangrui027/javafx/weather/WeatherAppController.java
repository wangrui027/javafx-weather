package com.wangrui027.javafx.weather;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wangrui027.javafx.weather.model.City;
import com.wangrui027.javafx.weather.model.County;
import com.wangrui027.javafx.weather.model.Province;
import com.wangrui027.javafx.weather.model.WeatherDataModel;
import com.wangrui027.javafx.weather.util.UnirestUtils;
import com.wangrui027.javafx.weather.util.WeatherDataModelBuildUtils;
import com.wangrui027.javafx.weather.util.WeatherDataUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class WeatherAppController implements Initializable {

    private static final Logger logger = LogManager.getLogger(WeatherAppController.class);

    private static final UnirestUtils restUtils = UnirestUtils.builder().build();
    /**
     * 省份选择框
     */
    @FXML
    private ChoiceBox<String> provinceChoiceBox;

    /**
     * 城市选择框
     */
    @FXML
    private ChoiceBox<String> cityChoiceBox;

    /**
     * 区县选择框
     */
    @FXML
    private ChoiceBox<String> countryChoiceBox;

    @FXML
    private Label dataUpdateTimeLabel;

    @FXML
    private HBox weatherInfoBox;

    /**
     * 视图是否被打开
     */
    private boolean viewOpened = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Province> provinceList = WeatherDataUtils.getProvinceList();
        List<City> cityList = provinceList.get(0).getCityList();
        List<County> countyList = cityList.get(0).getCountyList();
        String provinceName = null;
        String cityName = null;
        String regionJson = restUtils.get("http://ipw.cn/api/ip/region");
        JsonObject jsonObject = JsonParser.parseString(regionJson).getAsJsonObject();
        if (jsonObject.has("Address")) {
            jsonObject = jsonObject.getAsJsonObject("Address");
            provinceName = jsonObject.get("Province").getAsString();
            cityName = jsonObject.get("City").getAsString();
        }
        if (provinceName != null) {
            for (Province province : provinceList) {
                if (provinceName.startsWith(province.getName())) {
                    cityList = province.getCityList();
                    provinceName = province.getName();
                    break;
                }
            }
        }
        if (cityName != null) {
            for (City city : cityList) {
                if (cityName.startsWith(city.getName())) {
                    countyList = city.getCountyList();
                    cityName = city.getName();
                    break;
                }
            }
        }
        for (Province province : provinceList) {
            provinceChoiceBox.getItems().add(province.getName());
        }
        for (City city : cityList) {
            cityChoiceBox.getItems().add(city.getName());
        }
        for (County county : countyList) {
            countryChoiceBox.getItems().add(county.getName());
        }
        if (provinceName != null && cityName != null) {
            provinceChoiceBox.getSelectionModel().select(provinceName);
            cityChoiceBox.getSelectionModel().select(cityName);
        } else {
            provinceChoiceBox.getSelectionModel().selectFirst();
        }
        queryWeather();
    }

    /**
     * 省份选择框被选中事件
     *
     * @param event
     */
    public void provinceChoiceBoxAction(ActionEvent event) {
        cityChoiceBox.getItems().removeAll(cityChoiceBox.getItems());
        String provinceName = provinceChoiceBox.getValue();
        List<City> cityList = WeatherDataUtils.getCityListByProvince(provinceName);
        for (City city : cityList) {
            cityChoiceBox.getItems().add(city.getName());
        }
        cityChoiceBox.getSelectionModel().selectFirst();
    }

    /**
     * 城市选择框被选中事件
     *
     * @param event
     */
    public void cityChoiceBoxAction(ActionEvent event) {
        countryChoiceBox.getItems().removeAll(countryChoiceBox.getItems());
        String cityName = cityChoiceBox.getValue();
        List<County> countyList = WeatherDataUtils.getCountryListByCity(cityName);
        for (County county : countyList) {
            countryChoiceBox.getItems().add(county.getName());
        }
        countryChoiceBox.getSelectionModel().selectFirst();
    }

    /**
     * 确定按钮响应事件
     *
     * @param event
     */
    public void countryConfirmAction(ActionEvent event) {
        queryWeather();
    }

    private void queryWeather() {
        String provinceName = provinceChoiceBox.getValue();
        String cityName = cityChoiceBox.getValue();
        String countryName = countryChoiceBox.getValue();
        if (viewOpened) {
            County county = getCountryByName(provinceName, cityName, countryName);
            if (county != null) {
                logger.info(String.format("当前选中区域【%s - %s - %s】，areaId【%s】", provinceName, cityName, county.getName(), county.getAreaId()));
                String url = String.format("http://www.weather.com.cn/weather/%s.shtml", county.getAreaId());
                String html = restUtils.get(url);
                Document doc = Jsoup.parse(html);
                String dataUpdateTime = doc.select("#update_time").val();
                dataUpdateTimeLabel.setText(dataUpdateTime);
                weatherInfoBox.getChildren().removeAll(weatherInfoBox.getChildren());
                Elements elements = doc.select("#7d>ul>li");
                for (int i = 0; i < elements.size(); i++) {
                    Element element = elements.get(i);
                    WeatherDataModel model = WeatherDataModelBuildUtils.build(element);
                    weatherInfoBox.getChildren().add(getWeatherVBox(model, i));
                }
            } else {
                dataUpdateTimeLabel.setText("数据异常，请检查【city.json】配置文件");
                logger.error(String.format("当前选中区域【%s - %s - %s】在【city.json】配置文件中未匹配到数据！", provinceName, cityName, countryName));
            }
        } else {
            viewOpened = true;
        }
    }

    private VBox getWeatherVBox(WeatherDataModel model, int index) {
        VBox vBox = new VBox();
        vBox.getStyleClass().add("weatherVBox");
        vBox.getStyleClass().add(model.getBackgroundCssClass());
        vBox.setAlignment(Pos.CENTER);
        vBox.setMinWidth(100);
        vBox.setPadding(new Insets(10, 5, 10, 5));

        Label dateLabel = new Label(model.getDate());

        Label icon1Label = new Label();
        icon1Label.setMinWidth(30);
        icon1Label.setMinHeight(30);
        icon1Label.getStyleClass().add("wea_icon");
        icon1Label.getStyleClass().add(model.getIcon1CssClass());

        Label icon2Label = new Label();
        icon2Label.setMinWidth(30);
        icon2Label.setMinHeight(30);
        icon2Label.getStyleClass().add("wea_icon");
        icon2Label.getStyleClass().add(model.getIcon2CssClass());

        Label titleLabel = new Label(model.getTitle());
        Label temperatureLabel = new Label(model.getTemperature());

        HBox winIconBox = new HBox();
        winIconBox.setAlignment(Pos.CENTER);

        Label winIcon1Label = new Label();
        winIcon1Label.setTooltip(new Tooltip(model.getWind1()));
        winIcon1Label.setMinWidth(24);
        winIcon1Label.setMinHeight(24);
        winIcon1Label.getStyleClass().add("win_icon");
        winIcon1Label.getStyleClass().add(model.getWind1CssClass());

        Label winIcon2Label = new Label();
        winIcon2Label.setTooltip(new Tooltip(model.getWind2()));
        winIcon2Label.setMinWidth(24);
        winIcon2Label.setMinHeight(24);
        winIcon2Label.getStyleClass().add("win_icon");
        winIcon2Label.getStyleClass().add(model.getWind2CssClass());

        winIconBox.getChildren().add(winIcon1Label);
        winIconBox.getChildren().add(winIcon2Label);


        Label windLevelLabel = new Label(model.getWindLevel());

        vBox.getChildren().add(dateLabel);
        vBox.getChildren().add(icon1Label);
        vBox.getChildren().add(icon2Label);
        vBox.getChildren().add(titleLabel);
        vBox.getChildren().add(temperatureLabel);
        vBox.getChildren().add(winIconBox);
        vBox.getChildren().add(windLevelLabel);
        return vBox;
    }

    private County getCountryByName(String provinceName, String cityName, String countryName) {
        List<Province> provinceList = WeatherDataUtils.getProvinceList();
        for (Province province : provinceList) {
            if (province.getName().equals(provinceName)) {
                List<City> cityList = province.getCityList();
                for (City city : cityList) {
                    if (city.getName().equals(cityName)) {
                        List<County> countyList = city.getCountyList();
                        for (County county : countyList) {
                            if (county.getName().equals(countryName)) {
                                return county;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

}
