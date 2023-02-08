package cn.nukkit.item.customitem.data;

import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nullable;

public class DigProperty {
    private CompoundTag states;
    private Integer speed;

    public DigProperty() {
        this.states = new CompoundTag();
    }

    public DigProperty(CompoundTag states, int speed) {
        this.states = states;
        this.speed = speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Nullable
    public Integer getSpeed() {
        return speed;
    }

    public void setStates(CompoundTag states) {
        this.states = states;
    }

    public CompoundTag getStates() {
        return states;
    }
}
