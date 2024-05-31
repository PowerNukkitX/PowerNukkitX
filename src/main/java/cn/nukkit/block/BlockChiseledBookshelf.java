package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChiseledBookshelf;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBook;
import cn.nukkit.item.ItemBookWritable;
import cn.nukkit.item.ItemEnchantedBook;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static cn.nukkit.block.property.CommonBlockProperties.BOOKS_STORED;


public class BlockChiseledBookshelf extends BlockBookshelf implements BlockEntityHolder<BlockEntityChiseledBookshelf>, Faceable {
    public static final BlockProperties $1 = new BlockProperties(CHISELED_BOOKSHELF, BOOKS_STORED, CommonBlockProperties.DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockChiseledBookshelf() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockChiseledBookshelf(BlockState blockstate) {
        super(blockstate);
    }
    /**
     * @deprecated 
     */
    

    public String getName() {
        return "Chiseled Bookshelf";
    }

    @Override
    public Item[] getDrops(Item item) {
        BlockEntityChiseledBookshelf $2 = this.getBlockEntity();
        if (blockEntity != null) {
            return blockEntity.getItems();
        }
        return Item.EMPTY_ARRAY;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (player != null) {
            setBlockFace(player.getHorizontalFacing().getOpposite());
        } else {
            setBlockFace(BlockFace.SOUTH);
        }
        CompoundTag $3 = new CompoundTag();
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

    @Override
    @NotNull
    public Class<? extends BlockEntityChiseledBookshelf> getBlockEntityClass() {
        return BlockEntityChiseledBookshelf.class;
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String getBlockEntityType() {
        return BlockEntity.CHISELED_BOOKSHELF;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onTouch(@NotNull Vector3 vector, @NotNull Item item, @NotNull BlockFace face, float fx, float fy, float fz, @Nullable Player player, PlayerInteractEvent.@NotNull Action action) {
        if(action== PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK){
            BlockFace $4 = getBlockFace();
            assert player != null;
            if (player.getHorizontalFacing().getOpposite() == blockFace) {
                /*
                 * south $5==1  The lower left corner is the origin
                 * east  $6==1  The lower right corner is the origin
                 * west  $7==0  The lower left corner is the origin
                 * north $8==0  The lower right corner is the origin
                 */
                Vector2 $9 = switch (blockFace) {
                    case NORTH -> new Vector2(1 - fx, fy);
                    case SOUTH -> new Vector2(fx, fy);
                    case WEST -> new Vector2(fz, fy);
                    case EAST -> new Vector2(1 - fz, fy);
                    default -> throw new IllegalArgumentException(blockFace.toString());
                };
                int $10 = getRegion(clickPos);
                BlockEntityChiseledBookshelf $11 = this.getBlockEntity();
                if (blockEntity != null) {
                    if (blockEntity.hasBook(index)) {
                        Item $12 = blockEntity.removeBook(index);
                        player.getInventory().addItem(book);
                    } else if (item instanceof ItemBook || item instanceof ItemEnchantedBook || item instanceof ItemBookWritable) {
                        Item $13 = item.clone();
                        if (!player.isCreative()) {
                            itemClone.setCount(itemClone.getCount() - 1);
                            player.getInventory().setItemInHand(itemClone);
                        }
                        itemClone.setCount(1);
                        blockEntity.setBook(itemClone, index);
                    }
                    this.setPropertyValue(BOOKS_STORED, blockEntity.getBooksStoredBit());
                    this.getLevel().setBlock(this, this, true);
                }
            }
        }
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getPropertyValue(CommonBlockProperties.DIRECTION));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.DIRECTION, face.getHorizontalIndex());
    }

    
    /**
     * @deprecated 
     */
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