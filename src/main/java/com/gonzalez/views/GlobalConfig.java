package com.gonzalez.views;

import com.gonzalez.Launcher;
import com.gonzalez.Settings;
import com.gonzalez.serialize.Configuration;
import com.gonzalez.serialize.Target;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXStageDialog;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.enums.ButtonType;
import io.github.palexdev.materialfx.controls.enums.DialogType;
import io.github.palexdev.materialfx.utils.BindingUtils;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jfxtras.scene.control.LocalTimeTextField;

import java.net.URL;
import java.util.ResourceBundle;

public class GlobalConfig implements Initializable {

    private Settings settings;

    private MFXStageDialog mfxStageDialog;
    @FXML MFXButton cancel;
    @FXML MFXButton save;

    @FXML LocalTimeTextField timeField;
    @FXML MFXTextField numberCycle;

    @FXML ToggleGroup group;
    @FXML MFXRadioButton radioInfinite;
    @FXML MFXRadioButton radioTime;
    @FXML MFXRadioButton radioCycle;

    @FXML MFXButton configLoaderButton;
    @FXML MFXButton configSaverButton;


    public GlobalConfig(Settings settings) {
        this.settings = settings;
    }

    public void setMfxStageDialog(MFXStageDialog mfxStageDialog) {
        this.mfxStageDialog = mfxStageDialog;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        switch (settings.getAutoClickerType()) {
            case INFINITE : radioInfinite.setSelected(true); break;
            case TIME : radioTime.setSelected(true); break;
            case CYCLE : radioCycle.setSelected(true); break;
        }
        timeField.setLocalTime(settings.getTime());
        numberCycle.setText("" + settings.getNbCycle());
        numberCycle.setValidated(true);

        numberCycle.getValidator().add(BindingUtils.toProperty(
                        Bindings.createBooleanBinding(() -> numberCycle.getText().matches("\\d*"), numberCycle.textProperty())),
                "La valeur doit contenir seulement des chiffres"
        );

        numberCycle.getValidator().add(
                BindingUtils.toProperty(numberCycle.textProperty().length().greaterThan(0)),
                "Le champ ne peut pas être vide"
        );

        save.disableProperty().bind(numberCycle.getValidator().validProperty().not());

        cancel.setOnMousePressed(event -> mfxStageDialog.close());

        save.setOnMousePressed(event -> {
            if(radioInfinite.isSelected()) settings.setAutoClickerType(Settings.AutoClickerType.INFINITE);

            else if(radioTime.isSelected()) {
                settings.setTime(timeField.getLocalTime());
                settings.setAutoClickerType(Settings.AutoClickerType.TIME);
            }
            else if(radioCycle.isSelected()) {
                settings.setNbCycle(Integer.parseInt(numberCycle.getText()));
                settings.setAutoClickerType(Settings.AutoClickerType.CYCLE);
            }

            mfxStageDialog.close();
        });

        configSaverButton.setOnMousePressed(event -> {
            MFXStageDialog nameChooserDialog = new MFXStageDialog(DialogType.GENERIC, "Sauvegarder votre configuration", "Entrez le nom de la configuration");
            nameChooserDialog.setOwner(mfxStageDialog.getDialog().getScene().getWindow());

            MFXTextField mfxTextfield = new MFXTextField();
            mfxTextfield.setValidated(true);
            mfxTextfield.setPrefWidth(200);
            mfxTextfield.setText(settings.getCurrentConfigurationName());
            mfxTextfield.getValidator().add(
                    BindingUtils.toProperty(mfxTextfield.textProperty().length().greaterThan(0)),
                    "Le champ ne peut pas être vide"
            );

            MFXButton cancel = new MFXButton("Annuler");
            MFXButton ok = new MFXButton("Ok");
            ok.disableProperty().bind(mfxTextfield.getValidator().validProperty().not());

            cancel.setButtonType(ButtonType.RAISED);
            ok.setButtonType(ButtonType.RAISED);

            cancel.setOnAction(event2 -> nameChooserDialog.close());
            ok.setOnAction(event2 -> {
                Configuration configuration = new Configuration(mfxTextfield.getText());
                for(TargetStage targetStage : MainView.listTarget) {
                    configuration.addTarget(new Target(targetStage.getNumber(), targetStage.getX(), targetStage.getY()));
                }
                configuration.setName(mfxTextfield.getText());
                settings.getConfigurationList().addConfiguration(configuration);
                settings.getConfigurationList().saveAll();

                nameChooserDialog.close();
            });

            HBox hBox = new HBox(30, cancel, ok);
            hBox.setPadding(new Insets(30,20,0,20));
            hBox.setAlignment(Pos.CENTER);
            hBox.setPrefWidth(500);

            VBox vBox = new VBox(mfxTextfield, hBox);
            vBox.setPadding(new Insets(20,0,40,20));

            nameChooserDialog.getDialog().setActions(new HBox(vBox));
            nameChooserDialog.setCenterInOwner(true);
            nameChooserDialog.showAndWait();

        });

        configLoaderButton.setOnMousePressed(event -> {
            for(TargetStage target : MainView.listTarget) {
                target.close();
            }
            MainView.listTarget.clear();

            MFXStageDialog configChooserDialog = new MFXStageDialog(DialogType.GENERIC, "Chargement de votre configuration", "");
            configChooserDialog.getDialog().getStylesheets().add(Launcher.class.getResource("css/style.css").toExternalForm());
            configChooserDialog.setOwner(mfxStageDialog.getDialog().getScene().getWindow());

            ListView<String> configurationListView = new ListView<>();
            configurationListView.setItems(FXCollections.observableArrayList(settings.getConfigurationList().getAllConfigurationsName()));

            MFXButton cancel = new MFXButton("Annuler");
            cancel.getStyleClass().add("settingButton");
            MFXButton ok = new MFXButton("Ok");
            ok.getStyleClass().add("settingButton");

            cancel.setButtonType(ButtonType.RAISED);
            ok.setButtonType(ButtonType.RAISED);

            cancel.setOnAction(event2 -> configChooserDialog.close());
            ok.setOnAction(event2 -> {
                settings.setCurrentConfigurationName(configurationListView.getSelectionModel().getSelectedItem());
                Configuration configuration = settings.getConfigurationList().getConfigurationByName(configurationListView.getSelectionModel().getSelectedItem());
                for(Target target : configuration.getTargetList()) {
                    TargetStage targetStage = new TargetStage(MainView.stage, target.getNumber());
                    targetStage.setX(target.getX());
                    targetStage.setY(target.getY());
                    MainView.listTarget.add(targetStage);
                }

                configChooserDialog.close();
            });

            HBox hBox = new HBox(15, cancel, ok);
            hBox.setStyle("-fx-padding: 20;");

            configChooserDialog.getDialog().setActions(new HBox(new VBox(configurationListView, hBox)));
            configChooserDialog.setCenterInOwner(true);
            configChooserDialog.showAndWait();
        });
    }
}
