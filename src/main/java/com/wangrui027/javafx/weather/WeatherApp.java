package com.wangrui027.javafx.weather;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class WeatherApp extends BaseApp {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(this.getClass().getResource("/fxml/WeatherApp.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("一周天气预报 | Copyright © 1989-2021 wangrui027@outlook.com");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/favicon.ico")));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
