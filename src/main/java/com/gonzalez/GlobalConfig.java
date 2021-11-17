package com.gonzalez;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXStageDialog;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.BindingUtils;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleGroup;
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


    public GlobalConfig(Settings settings) {
        this.settings = settings;
    }

    public void setMfxStageDialog(MFXStageDialog mfxStageDialog) {
        this.mfxStageDialog = mfxStageDialog;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        switch (settings.getAutoClickerType()) {
            case INFINITE -> radioInfinite.setSelected(true);
            case TIME -> radioTime.setSelected(true);
            case CYCLE -> radioCycle.setSelected(true);
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
    }
}
