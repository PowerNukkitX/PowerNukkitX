package cn.nukkit.entity.data;

public enum PlayerFlag {
    SLEEP(1),
    DEAD(2);

    private final int value;

    PlayerFlag(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
