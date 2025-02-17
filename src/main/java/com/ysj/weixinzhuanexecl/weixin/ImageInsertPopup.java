//package com.ysj.weixinzhuanexecl.weixin;
//
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.TextArea;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.VBox;
//import javafx.stage.FileChooser;
//import javafx.stage.Stage;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ImageInsertPopup extends Application {
//
//    @Override
//    public void start(Stage primaryStage) {
//        try {
//            Parent root = FXMLLoader.load(getClass().getResource("C:\\Users\\sideyu\\IdeaProjects\\weixinzhuanExecl\\src\\main\\resources\\pathName.fxml"));
//
//            TextArea textArea = new TextArea();
//
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif"));
//
//            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(primaryStage);
//            if (selectedFiles!= null &&!selectedFiles.isEmpty()) {
//                for (File file : selectedFiles) {
//                    textArea.appendText("图片来自: " + file.getAbsolutePath() + "\n");
//                }
//            }
//
//            root.getChildrenUnmodifiable().add(textArea);
//
//            Scene scene = new Scene(root);
//            primaryStage.setScene(scene);
//            primaryStage.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}