package cn.nukkit.item;


public class ItemTurtleScute extends Item {
    public ItemTurtleScute() {
        this(0, 1);
    }

    public ItemTurtleScute(Integer meta) {
        this(meta, 1);
    }

    public ItemTurtleScute(Integer meta, int count) {
        super(TURTLE_SCUTE, meta, count, "Turtle Scute");
    }
}
