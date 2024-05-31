package cn.nukkit.item;

public class ItemPrismarineShard extends Item {
    /**
     * @deprecated 
     */
    

    public ItemPrismarineShard() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemPrismarineShard(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemPrismarineShard(Integer meta, int count) {
        super(PRISMARINE_SHARD, 0, count, "Prismarine Shard");
    }
}
