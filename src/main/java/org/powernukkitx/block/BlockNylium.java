package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.ObjectNyliumVegetation;
import org.powernukkitx.level.particle.BoneMealParticle;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;


public abstract class BlockNylium extends BlockSolid implements Natural {
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0.4)
            .resistance(0.4)
            .toolType(ItemTool.TYPE_PICKAXE)
            .burnChance(0)
            .burnAbility(0)
            .canBeActivated(true)
            .isFertilizable(true)
            .build();
    public BlockNylium(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockNylium(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM && !up().isTransparent()) {
            level.setBlock(this, Block.get(NETHERRACK), false);
            return type;
        }
        return 0;
    }

    
    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        Block up = up();
        if (item.isNull() || !item.isFertilizer() || !up.isAir()) {
            return false;
        }

        if (player != null && !player.isCreative()) {
            item.count--;
        }

        grow();

        level.addParticle(new BoneMealParticle(up));

        return true;
    }

    public boolean grow() {
        BlockManager blockManager = new BlockManager(this.level);
        ObjectNyliumVegetation.growVegetation(blockManager, this, new NukkitRandom());
        blockManager.applySubChunkUpdate();
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{Item.get(NETHERRACK)};
        }
        return Item.EMPTY_ARRAY;
    }

    }
