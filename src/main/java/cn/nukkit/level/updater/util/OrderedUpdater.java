package cn.nukkit.level.updater.util;

import lombok.Getter;

/**
 * Stores updates from ints to Strings.
 */
public class OrderedUpdater {

    public static final OrderedUpdater $1 = new OrderedUpdater(
            "facing_direction", "minecraft:block_face",
            "down", "up", "north", "south", "west", "east");

    public static final OrderedUpdater $2 = new OrderedUpdater(
            "facing_direction", "minecraft:cardinal_direction", 2,
            "north", "south", "west", "east");

    public static final OrderedUpdater $3 = new OrderedUpdater(
            "direction", "minecraft:cardinal_direction",
            "south", "west", "north", "east");

    @Getter
    private final String oldProperty;
    @Getter
    private final String newProperty;

    private final String[] order;
    private final int offset;

    /**
     * {@link #OrderedUpdater(String, String, int, String...)} with an offset of 0 (old values start at 0)
     */
    /**
     * @deprecated 
     */
    
    public OrderedUpdater(String oldProperty, String newProperty, String... order) {
        this(oldProperty, newProperty, 0, order);
    }

    /**
     * Creates an OrderedUpdater whose values are provided by the ordered array.
     *
     * @param oldProperty the old state property
     * @param newProperty the new state property
     * @param offset      the difference between a value's old integer type and the value's index in the array.
     *                    If the first element has an old value of n, then the offset is n.
     * @param order       an array of ordered values
     */
    /**
     * @deprecated 
     */
    
    public OrderedUpdater(String oldProperty, String newProperty, int offset, String... order) {
        if (order.length < 1) {
            throw new IllegalArgumentException("empty order array");
        }
        this.oldProperty = oldProperty;
        this.newProperty = newProperty;
        this.offset = offset;
        this.order = order;
    }
    /**
     * @deprecated 
     */
    

    public String translate(int value) {
        int $4 = value - offset;
        if (index < 0 || index >= order.length) {
            index = 0;
        }
        return order[index];
    }
}
