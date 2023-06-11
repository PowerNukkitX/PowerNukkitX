package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.item.Item;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockChiseledBookshelf extends BlockBookshelf {
    public static final IntBlockProperty BOOKS_STORED = new IntBlockProperty("books_stored", false, 63);
    public static final BlockProperties PROPERTIES = new BlockProperties(CommonBlockProperties.DIRECTION, BOOKS_STORED);

    public BlockChiseledBookshelf(int meta) {
        super(meta);
    }

    public BlockChiseledBookshelf() {
        this(0);
    }

    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public int getId() {
        return CHISELED_BOOKSHELF;
    }

    public String getName() {
        return "Chiseled Bookshelf";
    }

    @Override
    public Item[] getDrops(Item item) {
        //todo implement all store book Drops
        return new Item[]{};
    }
}