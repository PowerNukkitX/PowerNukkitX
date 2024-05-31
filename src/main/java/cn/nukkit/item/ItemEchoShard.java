package cn.nukkit.item;


public class ItemEchoShard extends Item {
    /**
     * @deprecated 
     */
    
    public ItemEchoShard() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemEchoShard(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemEchoShard(Integer meta, int count) {
        super(ECHO_SHARD, meta, count, "Echo Shard");
    }
}
