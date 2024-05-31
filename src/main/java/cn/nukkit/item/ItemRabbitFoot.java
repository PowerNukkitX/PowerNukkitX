package cn.nukkit.item;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class ItemRabbitFoot extends Item {
    /**
     * @deprecated 
     */
    

    public ItemRabbitFoot() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemRabbitFoot(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemRabbitFoot(Integer meta, int count) {
        super(RABBIT_FOOT, meta, count, "Rabbit Foot");
    }
}
