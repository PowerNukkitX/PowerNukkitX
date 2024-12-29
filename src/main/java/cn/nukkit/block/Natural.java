package cn.nukkit.block;

public interface Natural {

    default boolean canBePickedUp() {
        return true;
    }

}
