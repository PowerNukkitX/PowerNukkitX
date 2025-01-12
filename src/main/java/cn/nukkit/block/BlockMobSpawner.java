package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityMobSpawner;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSpawnEgg;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockMobSpawner extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(MOB_SPAWNER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMobSpawner() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockMobSpawner(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Monster Spawner";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public double getResistance() {
        return 25;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public  boolean canBePulled() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    public boolean setType(int networkId) {
        BlockEntity blockEntity = getLevel().getBlockEntity(this);
        if(blockEntity != null && blockEntity instanceof BlockEntityMobSpawner spawner) {
            spawner.setSpawnEntityType(networkId);
        } else {
            if (blockEntity != null) {
                blockEntity.close();
            }
            CompoundTag nbt = new CompoundTag()
                    .putString(BlockEntityMobSpawner.TAG_ID, BlockEntity.MOB_SPAWNER)
                    .putInt(BlockEntityMobSpawner.TAG_ENTITY_ID, networkId)
                    .putInt(BlockEntityMobSpawner.TAG_X, (int) x)
                    .putInt(BlockEntityMobSpawner.TAG_Y, (int) y)
                    .putInt(BlockEntityMobSpawner.TAG_Z, (int) z);

            BlockEntityMobSpawner entitySpawner = new BlockEntityMobSpawner(getLevel().getChunk((int) x >> 4, (int) z >> 4), nbt);
            entitySpawner.spawnToAll();
        }
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if(!(item instanceof ItemSpawnEgg egg)) return false;
        if(player == null) return false;
        if (player.isAdventure()) return false;
        if(setType(egg.getEntityNetworkId())) {
            if (!player.isCreative()) {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
            }
            return true;
        }
        return false;
    }
}
