package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityMobSpawner;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemCustomEntitySpawnEgg;
import org.powernukkitx.item.ItemSpawnEgg;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.registry.Registries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockMobSpawner extends BlockSolid implements BlockEntityHolder<BlockEntityMobSpawner> {
    public static final BlockProperties PROPERTIES = new BlockProperties(MOB_SPAWNER);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(5)
            .resistance(25)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canBePushed(false)
            .canBePulled(false)
            .canBeActivated(true)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMobSpawner() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockMobSpawner(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        CompoundTag nbt = new CompoundTag();

        if (item.hasCustomBlockData()) {
            nbt.putAll(item.getCustomBlockData());
        }

        return BlockEntityHolder.setBlockAndCreateEntity(this, false, true, nbt) != null;
    }

    @Override
    public String getName() {
        return "Monster Spawner";
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    
    public boolean setType(int networkId) {
        BlockEntity blockEntity = getLevel().getBlockEntity(this);
        if (blockEntity != null && blockEntity instanceof BlockEntityMobSpawner spawner) {
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
        if (player == null || player.isAdventure()) return false;
        int networkId = -1;

        if (item instanceof ItemSpawnEgg egg) {
            networkId = egg.getEntityNetworkId();
        } else if (item instanceof ItemCustomEntitySpawnEgg) {
            String eggId = item.getId();
            String entityId = ItemCustomEntitySpawnEgg.entityIdFromEggId(eggId);
            if (entityId != null) {
                int rid = Registries.ENTITY.getEntityNetworkId(entityId);
                if (rid != 0) networkId = rid;
            }
        }

        if (networkId <= 0) return false;

        if (setType(networkId)) {
            if (!player.isCreative()) {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
            }
            return true;
        }
        return false;
    }

    @Override
    public @NotNull Class<? extends BlockEntityMobSpawner> getBlockEntityClass() {
        return BlockEntityMobSpawner.class;
    }

    @Override
    public @NotNull String getBlockEntityType() {
        return BlockEntity.MOB_SPAWNER;
    }
}
