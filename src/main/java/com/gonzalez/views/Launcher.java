package com.gonzalez.views;

import com.gonzalez.Settings;
import io.github.palexdev.materialfx.controls.MFXDialog;
import io.github.palexdev.materialfx.controls.MFXStageDialog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Launcher extends Application {

    private Settings globalSettings = new Settings();

    private static Scene scene;
    public static Stage stage;
    public boolean isDragDetect = false;
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML private VBox vboxHidableButton;
    @FXML private Button play;
    @FXML private Button plus;
    @FXML private Button minus;
    @FXML private Button settings;
    @FXML private Button expand;

    public static List<TargetStage> listTarget = new ArrayList<>();
    private boolean startAutoCliker = false;

    public static void main(String[] args) {
        Application.launch(Launcher.class, args);
    }

    @Override
    public void start(Stage stage) {
        Launcher.stage = stage;
        FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("home.fxml"));

        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("../css/style.css").toExternalForm());

        //scene.setFill(new Color(0.22,0.22,0.22,0.5));
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();


        ((Launcher) loader.getController()).addTarget(listTarget.size() + 1);
        ((Launcher) loader.getController()).addListener();
    }

    @FXML
    public void initialize() {
        setDraggable(expand);
    }

    private void addListener() {
        play.setOnMousePressed(event -> {
            if (!startAutoCliker) {
                onPlayPressed();
            } else {
                onLeftPressed();
            }
        });

        plus.setOnMousePressed(event -> {
            addTarget(listTarget.size() + 1);
            if (listTarget.size() == 1) {
                minus.setDisable(false);
            }
        });
        minus.setOnMousePressed(event -> {
            removeTarget();
            if (listTarget.size() == 0) {
                minus.setDisable(true);
            }
        });

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.S) {
                onLeftPressed();
            }
        });

        settings.setOnMousePressed(event -> onSettingPressed());
    }

    private void onSettingPressed() {
        MFXDialog infoDialog;
        MFXStageDialog stageDialog;

        GlobalConfig globalConfig = new GlobalConfig(globalSettings);
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("SettingDialog.fxml"));
            loader.setControllerFactory(controller -> globalConfig);
            infoDialog = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        stageDialog = new MFXStageDialog(infoDialog);
        stageDialog.setScrimBackground(true);
        stageDialog.setOwner(stage);
        stageDialog.setModality(Modality.APPLICATION_MODAL);

        stageDialog.show();

        globalConfig.setMfxStageDialog(stageDialog);
    }

    private void onPlayPressed() {
        startAutoCliker = true;
        ((FontIcon) play.getGraphic()).setIconLiteral("fas-pause");

        for (TargetStage target : listTarget) {
            target.setVisible(false);
        }

        Thread t = new Thread(() -> {
            Robot robot = null;
            try {
                robot = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
            }

            switch (globalSettings.getAutoClickerType()) {
                case INFINITE -> startInfiniteAutoClicker(robot);
                case TIME -> startTimeAutoClicker(robot);
                case CYCLE -> startCycleAutoClicker(robot);
            }

        });
        t.start();
    }

    private void startInfiniteAutoClicker(Robot robot) {
        while (startAutoCliker) {
            executeAutoClickerOnAllTargetOnce(robot);
        }
        System.out.println("fini");
    }

    private void startTimeAutoClicker(Robot robot) {
        LocalTime stopLocalTime = LocalTime.now().plusHours(globalSettings.getTime().getHour()).plusMinutes(globalSettings.getTime().getMinute());

        while (startAutoCliker && LocalTime.now().isBefore(stopLocalTime)) {
            executeAutoClickerOnAllTargetOnce(robot);
        }

        if(startAutoCliker) {
            onLeftPressed();
        }
    }

    private void startCycleAutoClicker(Robot robot) {
        int nbCycle = 0;
        while (startAutoCliker && nbCycle <= globalSettings.getNbCycle()) {
            executeAutoClickerOnAllTargetOnce(robot);
            nbCycle++;
        }

        if(startAutoCliker) {
            onLeftPressed();
        }
    }

    private void executeAutoClickerOnAllTargetOnce(Robot robot) {
        for (TargetStage target : listTarget) {
            if (!startAutoCliker) break;

            robot.mouseMove((int) target.getX() + 24, (int) target.getY() + 24);
            sleep(100);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            sleep(100);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

            target.playAnimationClick();
            Platform.runLater(() -> scene.getWindow().requestFocus());
            sleep(target.getTimeToWait());
        }
    }

    private void onLeftPressed() {
        startAutoCliker = false;
        ((FontIcon) play.getGraphic()).setIconLiteral("fas-play");

        for (TargetStage target : listTarget) {
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

    private void addTarget(int number) {
        TargetStage target = new TargetStage(stage, number);
        listTarget.add(target);
    }

    private void removeTarget() {
        listTarget.get(listTarget.size() - 1).close();
        listTarget.remove(listTarget.size() - 1);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
