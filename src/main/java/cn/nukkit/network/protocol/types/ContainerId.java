package cn.nukkit.network.protocol.types;


import lombok.experimental.UtilityClass;

@UtilityClass
public class ContainerId {
    public static final int DROP_CONTENTS = -100;

    public static final int BEACON = -24;
    public static final int TRADING_OUTPUT = -23;
    public static final int TRADING_USE_INPUTS = -22;
    public static final int TRADING_INPUT_2 = -21;
    public static final int TRADING_INPUT_1 = -20;

    public static final int ENCHANT_OUTPUT = -17;
    public static final int ENCHANT_MATERIAL = -16;
    public static final int ENCHANT_INPUT = -15;

    public static final int ANVIL_OUTPUT = -13;
    public static final int ANVIL_RESULT = -12;
    public static final int ANVIL_MATERIAL = -11;
    public static final int CONTAINER_INPUT = -10;

    public static final int CRAFTING_USE_INGREDIENT = -5;
    public static final int CRAFTING_RESULT = -4;
    public static final int CRAFTING_REMOVE_INGREDIENT = -3;
    public static final int CRAFTING_ADD_INGREDIENT = -2;
    public static final int NONE = -1;
    public static final int INVENTORY = 0;
    public static final int FIRST = 1;
    public static final int LAST = 100;

    public static final int OFFHAND = 119;
    public static final int ARMOR = 120;
    public static final int HOTBAR = 122;
    public static final int FIXED_INVENTORY = 123;
    public static final int UI = 124;
}
