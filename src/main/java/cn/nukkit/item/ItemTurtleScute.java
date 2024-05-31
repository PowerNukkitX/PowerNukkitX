package cn.nukkit.item;


public class ItemTurtleScute extends Item {
    /**
     * @deprecated 
     */
    
    public ItemTurtleScute() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemTurtleScute(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemTurtleScute(Integer meta, int count) {
        super(TURTLE_SCUTE, meta, count, "Turtle Scute");
    }
}
