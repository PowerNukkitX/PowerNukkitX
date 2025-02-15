package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static cn.nukkit.block.property.CommonBlockProperties.*;

public class BlockSuspiciousSand extends BlockFallable {
    public static final BlockProperties PROPERTIES = new BlockProperties(SUSPICIOUS_SAND, HANGING, BRUSHED_PROGRESS);

    public BlockSuspiciousSand() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSuspiciousSand(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public String getName() {
        return "Suspicious Sand";
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
    public void onTouch(@NotNull Vector3 vector, @NotNull Item item, @NotNull BlockFace face, float fx, float fy, float fz, @Nullable Player player, PlayerInteractEvent.@NotNull Action action) {
        int progress = getPropertyValue(CommonBlockProperties.BRUSHED_PROGRESS);
        if(progress < 3) {
            setPropertyValue(CommonBlockProperties.BRUSHED_PROGRESS, progress+1);
            getLevel().addSound(this, Sound.HIT_SUSPICIOUS_SAND);
            getLevel().setBlock(this, this);
        } else {
            getLevel().setBlock(this, BlockSand.PROPERTIES.getDefaultState().toBlock());
            getLevel().addSound(this, Sound.BREAK_SUSPICIOUS_SAND);
        }
        super.onTouch(vector, item, face, fx, fy, fz, player, action);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{Item.AIR};
    }
}