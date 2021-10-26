package com.gonzalez;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStageDialog;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.enums.ButtonType;
import io.github.palexdev.materialfx.controls.enums.DialogType;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyComboBox;
import io.github.palexdev.materialfx.utils.BindingUtils;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.StackedFontIcon;

import java.util.List;

public class Target extends Stage {

    public boolean isDragDetect = false;
    private double xOffset = 0;
    private double yOffset = 0;

    private int number;
    private int timeToWait = 2000;

    Timeline timeline;
    StackedFontIcon fontIcon;
    private Circle circle;

    MFXStageDialog mfxStageDialog;

    public Target(Stage stage, int number) {
        this.number = number;

        fontIcon = new StackedFontIcon();
        fontIcon.setIconCodeLiterals("far-dot-circle");
        fontIcon.setIconColor(Paint.valueOf("blue"));
        fontIcon.setIconSize(48);
        fontIcon.setStyle("-fx-background-color: null");

        Label label = new Label("" + number);
        label.setStyle("-fx-font-size:42px; -fx-text-fill:orange;");
        fontIcon.getChildren().add(label);

        circle = new Circle(0, Color.web("grey", 0.5));
        fontIcon.getChildren().add(circle);
        //circle.setVisible(false);

        VBox vBox = new VBox(fontIcon);
        vBox.setStyle("-fx-background-color: null");
        vBox.setPickOnBounds(true);

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

        Duration cycleDuration = Duration.millis(300);
        timeline = new Timeline(
                new KeyFrame(cycleDuration,
                        new KeyValue(circle.radiusProperty() ,24, Interpolator.EASE_BOTH))
        );
        Timeline timeline2 = new Timeline(
                new KeyFrame(cycleDuration,
                        new KeyValue(circle.radiusProperty() ,0, Interpolator.EASE_BOTH))
        );
        timeline.setOnFinished(event-> timeline2.play());
    }

    public int getTimeToWait() {
        return timeToWait;
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
            } else if(mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                openTargetConfig();
            }
        });

        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
            isDragDetect = true;
            this.setX((mouseEvent.getScreenX() - xOffset));
            this.setY((mouseEvent.getScreenY() - yOffset));
        });
    }

    public void setVisible(boolean visible) {
        for(int i=0; i<fontIcon.getChildren().size()-1; i++) {
            fontIcon.getChildren().get(i).setVisible(visible);
        }
    }

    public void playAnimationClick() {
        circle.setVisible(true);
        timeline.play();
    }
    private void openTargetConfig() {
        if(mfxStageDialog == null) {
            mfxStageDialog = new MFXStageDialog(DialogType.GENERIC, "Modifier la cible " + number, "Le délai avant de faire le prochain clic");
            mfxStageDialog.setOwner(this);

            MFXTextField mfxTextfield = new MFXTextField("" + timeToWait);
            mfxTextfield.setValidated(true);
            mfxTextfield.getValidator().add(
                    BindingUtils.toProperty(mfxTextfield.textProperty().length().greaterThan(0)),
                    "Le champ ne peut pas être vide"
            );

            mfxTextfield.getValidator().add(BindingUtils.toProperty(
                            Bindings.createBooleanBinding(() -> mfxTextfield.getText().matches("\\d*"), mfxTextfield.textProperty())),
                    "La valeur doit contenir seulement des chiffres"
            );


            MFXLegacyComboBox<String> unit = new MFXLegacyComboBox<>();
            unit.setItems(FXCollections.observableArrayList(List.of("milliseconde", "seconde", "minute")));
            unit.getSelectionModel().selectFirst();

            HBox timeBox = new HBox(20, mfxTextfield, unit);

            MFXButton cancel = new MFXButton("Annuler");
            MFXButton ok = new MFXButton("Ok");

            cancel.setButtonType(ButtonType.RAISED);
            ok.setButtonType(ButtonType.RAISED);

            cancel.setOnAction(event -> mfxStageDialog.close());
            ok.setOnAction(event -> {
                timeToWait = switch (unit.getSelectionModel().getSelectedItem()) {
                    case "milliseconde" -> Integer.parseInt(mfxTextfield.getText());
                    case "seconde" -> Integer.parseInt(mfxTextfield.getText()) * 1000;
                    case "minute" -> Integer.parseInt(mfxTextfield.getText()) * 1000 * 60;
                    default -> 2000;
                };

                mfxStageDialog.close();
            });

            ok.disableProperty().bind(mfxTextfield.getValidator().validProperty().not());

            HBox box = new HBox(20, cancel, ok);
            box.setAlignment(Pos.CENTER);
            box.setPadding(new Insets(20, 5, 20, 5));

            VBox vBox = new VBox(10, timeBox, box);

            mfxStageDialog.getDialog().setActions(new HBox(vBox));
            mfxStageDialog.setCenterInOwner(true);
        }

        mfxStageDialog.show();
    }
}
