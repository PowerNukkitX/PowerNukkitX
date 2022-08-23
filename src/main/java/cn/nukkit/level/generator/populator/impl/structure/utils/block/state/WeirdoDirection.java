package cn.nukkit.level.generator.populator.impl.structure.utils.block.state;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

//\\ VanillaStates::WeirdoDirection
@PowerNukkitXOnly
@Since("1.19.21-r6")
public final class WeirdoDirection {

    public static final int EAST = 0b00;
    public static final int WEST = 0b01;
    public static final int SOUTH = 0b10;
    public static final int NORTH = 0b11;

    private WeirdoDirection() {

    }
}
