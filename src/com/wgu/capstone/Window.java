package com.wgu.capstone;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Window extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) {

        stage.setTitle("JavaFX WebView Example");

        WebView webView = new WebView();

        webView.getEngine().load("http://localhost:7001");

        VBox vBox = new VBox(webView);
        Scene scene = new Scene(vBox, 960, 600);

        stage.setScene(scene);
        stage.show();
    }
}