package com.gonzalez;

import java.time.LocalTime;

public class Settings {

    private AutoClickerType autoClickerType;

    private int nbCycle = 10;
    private LocalTime time = LocalTime.of(0,5);

    public Settings() {
        autoClickerType = AutoClickerType.INFINITE;
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

    public enum AutoClickerType {
        INFINITE, TIME, CYCLE
    }
}
