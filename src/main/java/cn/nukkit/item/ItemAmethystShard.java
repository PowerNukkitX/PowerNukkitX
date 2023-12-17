package cn.nukkit.item;


public class ItemAmethystShard extends Item {
    public ItemAmethystShard() {
        this(AMETHYST_SHARD);
    }

    public ItemAmethystShard(int id) {
        this(id, Integer.valueOf(0));
    }

    public ItemAmethystShard(Integer meta) {
        this(AMETHYST_SHARD, meta);
    }

    public ItemAmethystShard(Integer meta, int count) {
        this(AMETHYST_SHARD, meta, count);
    }

    public ItemAmethystShard(int id, Integer meta) {
        this(id, meta, 1);
    }

    public ItemAmethystShard(int id, Integer meta, int count) {
        this(id, meta, count, "Amethyst Shard");
    }

    public ItemAmethystShard(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }
}
