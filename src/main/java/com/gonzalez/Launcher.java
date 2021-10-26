package com.gonzalez;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Launcher extends Application {

    private static Scene scene;
    private static Stage stage;
    public boolean isDragDetect = false;
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML private VBox vboxHidableButton;
    @FXML private Button play;
    @FXML private Button plus;
    @FXML private Button minus;
    @FXML private Button settings;
    @FXML private Button expand;

    static List<Target> listTarget = new ArrayList<>();
    private boolean startAutoCliker = false;

    public static void main(String[] args) {
        Application.launch(Launcher.class, args);
    }

    @Override
    public void start(Stage stage) {
        FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("views/home.fxml"));

        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Launcher.stage = stage;
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());
        //scene.setFill(new Color(0.22,0.22,0.22,0.5));
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
        addTarget();
    }

    @FXML
    public void initialize() {
        setDraggable(expand);

        play.setOnMousePressed(event -> {
            if (!startAutoCliker) {
                onPlayPressed();
            } else {
                onLeftPressed();
            }
        });

        plus.setOnMousePressed(event -> {
            addTarget();
            if(listTarget.size() == 1) {
                minus.setDisable(false);
            }
        });
        minus.setOnMousePressed(event -> {
            removeTarget();
            if(listTarget.size() == 0) {
                minus.setDisable(true);
            }
        });
    }

    private void onPlayPressed() {
        startAutoCliker = true;
        ((FontIcon) play.getGraphic()).setIconLiteral("fas-pause");

        for(Target target: listTarget) {
            target.setVisible(false);
        }

        Thread t = new Thread(() -> {
            Robot robot = null;
            try {
                robot = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
            }
            while(true) {
                for(Target target : listTarget) {
                    if(!startAutoCliker) break;

                    robot.mouseMove((int)target.getX() + 24, (int)target.getY() + 24);
                    sleep(100);
                    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    sleep(100);
                    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

                    target.playAnimationClick();
                    sleep(target.getTimeToWait());
                }
            }
        });
        t.start();

    }

    private void onLeftPressed() {
        startAutoCliker = false;
        ((FontIcon) play.getGraphic()).setIconLiteral("fas-play");

        for(Target target: listTarget) {
            target.setVisible(true);
        }
    }

    protected void setDraggable(Node node) {
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (isDragDetect) {
                    isDragDetect = false;
                } else {
                    onExpandPressed();
                }
            }
        });

        node.addEventHandler(MouseEvent.MOUSE_EXITED, event -> isDragDetect = false);

        node.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                xOffset = mouseEvent.getSceneX();
                yOffset = mouseEvent.getSceneY();
            }
        });

        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
            isDragDetect = true;
            stage.setX((mouseEvent.getScreenX() - xOffset));
            stage.setY((mouseEvent.getScreenY() - yOffset));
        });
    }

    private void onExpandPressed() {
        if (vboxHidableButton.isVisible()) {
            vboxHidableButton.setVisible(false);
            vboxHidableButton.setManaged(false);
        } else {
            vboxHidableButton.setVisible(true);
            vboxHidableButton.setManaged(true);
        }
    }

    private void addTarget() {
        Target target = new Target(stage, listTarget.size()+1);
        listTarget.add(target);
    }

    private void removeTarget() {
        listTarget.get(listTarget.size()-1).close();
        listTarget.remove(listTarget.size()-1);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
