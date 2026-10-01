package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonPropertyMap;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.particle.BoneMealParticle;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.powernukkitx.block.property.CommonBlockProperties.GROWTH_1;
import static org.powernukkitx.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;

/**
 * A mushroom that grows on the side of a block. Bone meal takes it from small to full grown.
 */
public class BlockShelfMushroom extends BlockFlowable {
    public static final BlockProperties PROPERTIES = new BlockProperties(SHELF_MUSHROOM, MINECRAFT_CARDINAL_DIRECTION, GROWTH_1);

    public BlockShelfMushroom() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockShelfMushroom(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Shelf Mushroom";
    }

    /**
     * @return the face this mushroom is looking at, its support sits on the opposite side
     */
    public BlockFace getFacing() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(getPropertyValue(MINECRAFT_CARDINAL_DIRECTION));
    }

    public void setFacing(BlockFace face) {
        setPropertyValue(MINECRAFT_CARDINAL_DIRECTION, CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(face));
    }

    private boolean isSupportValid() {
        Block support = getSide(getFacing().getOpposite());
        return support.isSolid() && support.isFullBlock();
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (face.getAxis() == BlockFace.Axis.Y || !target.isSolid() || !target.isFullBlock()) {
            return false;
        }

        setFacing(face);
        return this.getLevel().setBlock(this, this);
    }

    @Override
    public int onUpdate(int type) {
        if (type != Level.BLOCK_UPDATE_NORMAL) {
            return 0;
        }

        if (!isSupportValid()) {
            this.getLevel().useBreakOn(this);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        return 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (!item.isFertilizer() || getPropertyValue(GROWTH_1) >= GROWTH_1.getMax()) {
            return false;
        }

        if (player != null && !player.isCreative()) {
            item.count--;
        }

        this.level.addParticle(new BoneMealParticle(this));
        setPropertyValue(GROWTH_1, getPropertyValue(GROWTH_1) + 1);
        this.getLevel().setBlock(this, this);
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    public boolean useDefaultFallDamage() {
        return false;
    }

    @Override
    public void onEntityFallOn(Entity entity, float fallDistance) {
        float damage = (float) Math.floor((fallDistance - 3) * 0.5f);

        if (damage > 0) {
            entity.attack(new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.FALL, damage));
        }
    }
}
