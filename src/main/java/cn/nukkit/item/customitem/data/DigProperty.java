package cn.nukkit.item.customitem.data;

import org.cloudburstmc.nbt.NbtMap;

import javax.annotation.Nullable;


public class DigProperty {
    private NbtMap states;
    private Integer speed;

    public DigProperty() {
        this.states = NbtMap.EMPTY;
    }

    public DigProperty(NbtMap states, int speed) {
        this.states = states;
        this.speed = speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public @Nullable Integer getSpeed() {
        return speed;
    }

    public void setStates(NbtMap states) {
        this.states = states;
    }

    public NbtMap getStates() {
        return states;
    }
}
