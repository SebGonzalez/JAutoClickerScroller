package com.gonzalez;

import com.gonzalez.serialize.ConfigurationList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalTime;

public class Settings {
    private String currentConfigurationName;
    private ConfigurationList configurationList;
    private AutoClickerType autoClickerType;

    private int nbCycle = 10;
    private LocalTime time = LocalTime.of(0,5);

    public Settings() {
        autoClickerType = AutoClickerType.INFINITE;
        loadConfigurationList();
    }

    public String getCurrentConfigurationName() {
        return currentConfigurationName;
    }

    public void setCurrentConfigurationName(String currentConfigurationName) {
        this.currentConfigurationName = currentConfigurationName;
    }

    public ConfigurationList getConfigurationList() {
        return configurationList;
    }

    public AutoClickerType getAutoClickerType() {
        return autoClickerType;
    }

    public void setAutoClickerType(AutoClickerType autoClickerType) {
        this.autoClickerType = autoClickerType;
    }

    public int getNbCycle() {
        return nbCycle;
    }

    public void setNbCycle(int nbCycle) {
        this.nbCycle = nbCycle;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    private void loadConfigurationList() {
        File file = new File(ConfigurationList.baseDir, ConfigurationList.BASE_CONFIG_SAVE_FILE);

        if(file.exists()) {
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(new FileInputStream(file));
                configurationList = (ConfigurationList) ois.readObject();
                System.out.println("YEAH : " + configurationList.getAllConfigurationsName().size());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if(configurationList == null) {
            configurationList = new ConfigurationList();
        }
    }

    public enum AutoClickerType {
        INFINITE, TIME, CYCLE
    }
}
