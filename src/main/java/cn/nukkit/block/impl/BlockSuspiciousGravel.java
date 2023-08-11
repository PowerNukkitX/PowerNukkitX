package cn.nukkit.block.impl;

import cn.nukkit.block.BlockFallableMeta;
import cn.nukkit.block.property.BlockProperties;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.IntBlockProperty;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockSuspiciousGravel extends BlockFallableMeta {
    public static final IntBlockProperty BRUSHED_PROGRESS = new IntBlockProperty("brushed_progress", false, 3);
    public static final BlockProperties PROPERTIES = new BlockProperties(CommonBlockProperties.HANGING, BRUSHED_PROGRESS);

    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSuspiciousGravel() {
    }

    public BlockSuspiciousGravel(int meta) {
        super(meta);
    }

    public int getId() {
        return SUSPICIOUS_GRAVEL;
    }

    public String getName() {
        return "Suspicious Gravel";
    }

    @Override
    public double getHardness() {
        return 0.25;
    }

    @Override
    public double getResistance() {
        return 1.25;
    }


    @Override
    protected EntityFallingBlock createFallingEntity(CompoundTag customNbt) {
        customNbt.putBoolean("BreakOnGround", true);
        return super.createFallingEntity(customNbt);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{Item.AIR_ITEM};
    }
}
