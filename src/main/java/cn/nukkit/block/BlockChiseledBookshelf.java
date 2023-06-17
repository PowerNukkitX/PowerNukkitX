package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChiseledBookshelf;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBook;
import cn.nukkit.item.ItemBookEnchanted;
import cn.nukkit.item.ItemBookWritable;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockChiseledBookshelf extends BlockBookshelf implements BlockEntityHolder<BlockEntityChiseledBookshelf>, Faceable {
    public static final IntBlockProperty BOOKS_STORED = new IntBlockProperty("books_stored", false, 63);
    public static final BlockProperties PROPERTIES = new BlockProperties(CommonBlockProperties.DIRECTION, BOOKS_STORED);

    public BlockChiseledBookshelf(int meta) {
        super(meta);
    }

    public BlockChiseledBookshelf() {
        this(0);
    }

    @NotNull
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

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (player != null) {
            setBlockFace(player.getHorizontalFacing().getOpposite());
        } else {
            setBlockFace(BlockFace.SOUTH);
        }
        CompoundTag nbt = new CompoundTag();
        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }
        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }
        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null;
    }

    @NotNull
    @Override
    public Class<? extends BlockEntityChiseledBookshelf> getBlockEntityClass() {
        return BlockEntityChiseledBookshelf.class;
    }

    @NotNull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.CHISELED_BOOKSHELF;
    }

    @Since("1.20.0-r2")
    @Override
    public void onClick(@NotNull Player player, Item item, PlayerInteractEvent.Action action, BlockFace face, Vector3 clickPoint) {
        BlockFace blockFace = getBlockFace();
        if (player.getHorizontalFacing().getOpposite() == blockFace) {
            /*
             * south z==1  The lower left corner is the origin
             * east  x==1  The lower right corner is the origin
             * west  x==0  The lower left corner is the origin
             * north z==0  The lower right corner is the origin
             */
            Vector2 clickPos = switch (blockFace) {
                case NORTH -> new Vector2(1 - clickPoint.getX(), clickPoint.getY());
                case SOUTH -> new Vector2(clickPoint.getX(), clickPoint.getY());
                case WEST -> new Vector2(clickPoint.getZ(),  clickPoint.getY());
                case EAST -> new Vector2(1 -clickPoint.getZ(), clickPoint.getY());
                default -> throw new IllegalArgumentException(blockFace.toString());
            };
            int index = getRegion(clickPos);
            BlockEntityChiseledBookshelf blockEntity = this.getBlockEntity();
            if (blockEntity != null) {
                if (item instanceof ItemBook || item instanceof ItemBookEnchanted || item instanceof ItemBookWritable) {
                    Item itemClone = item.clone();
                    if (!player.isCreative()) {
                        itemClone.setCount(itemClone.getCount() - 1);
                        player.getInventory().setItemInHand(itemClone);
                    }
                    itemClone.setCount(1);

                    blockEntity.setBook(itemClone, index);
                } else if (item.isNull() && blockEntity.hasBook(index)) {
                    Item book = blockEntity.removeBook(index);
                    player.getInventory().addItem(book);
                }
                this.setPropertyValue(BOOKS_STORED, blockEntity.getBooksStoredBit());
                this.getLevel().setBlock(this, this, true);
            }
        }
    }

    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(CommonBlockProperties.DIRECTION);
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.DIRECTION, face);
    }

    private int getRegion(Vector2 clickPos) {
        if (clickPos.getX() - 0.333333 < 0) {
            return clickPos.getY() - 0.5 < 0 ? 3 : 0;
        } else if (clickPos.getX() - 0.666666 < 0) {
            return clickPos.getY() - 0.5 < 0 ? 4 : 1;
        } else {
            return clickPos.getY() - 0.5 < 0 ? 5 : 2;
        }
    }
}