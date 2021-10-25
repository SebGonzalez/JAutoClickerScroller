package com.gonzalez;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.StackedFontIcon;

import java.io.IOException;

public class Target extends Stage {

    public boolean isDragDetect = false;
    private double xOffset = 0;
    private double yOffset = 0;

    public Target(Stage stage, int number) {
        StackedFontIcon fontIcon = new StackedFontIcon();
        fontIcon.setIconCodeLiterals("far-dot-circle");
        fontIcon.setIconColor(Paint.valueOf("blue"));
        fontIcon.setIconSize(32);

        Label label = new Label("" + number);
        label.setStyle("-fx-font-size:30px; -fx-text-fill:orange;");
        fontIcon.getChildren().add(label);

        VBox vBox = new VBox(fontIcon);
        vBox.setStyle("-fx-background-color: null");

        Scene scene = new Scene(vBox);
        scene.getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());
        this.setScene(scene);
        this.setAlwaysOnTop(true);
        this.initStyle(StageStyle.TRANSPARENT);

       // this.initModality(Modality.WINDOW_MODAL);
        setDraggable(scene.getRoot());
        scene.setFill(null);
        this.initOwner(stage);
        this.show();
    }

    protected void setDraggable(Node node) {
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                isDragDetect = false;
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
            this.setX((mouseEvent.getScreenX() - xOffset));
            this.setY((mouseEvent.getScreenY() - yOffset));
        });
    }
}
