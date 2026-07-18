package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.projectile.EntityArrow;
import org.powernukkitx.entity.projectile.EntitySmallFireball;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationType;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.DoubleTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.utils.RedstoneComponent;
import org.powernukkitx.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static org.powernukkitx.block.property.CommonBlockProperties.EXPLODE_BIT;

public class BlockTnt extends BlockSolid implements RedstoneComponent, Natural {

    public static final BlockProperties PROPERTIES = new BlockProperties(TNT, EXPLODE_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTnt() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTnt(BlockState state) {
        super(state);
    }

    @Override
    public String getName() {
        return "TNT";
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getBurnChance() {
        return 15;
    }

    @Override
    public int getBurnAbility() {
        return 100;
    }

    public void prime() {
        this.prime(80);
    }

    public void prime(int fuse) {
        prime(fuse, null);
    }

    public void prime(int fuse, Entity source) {
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true);
        double mot = (new NukkitRandom()).nextFloat() * Math.PI * 2;
        CompoundTag nbt = new CompoundTag()
                .putList("Pos", new ListTag<DoubleTag>()
                        .add(new DoubleTag(this.x + 0.5))
                        .add(new DoubleTag(this.y))
                        .add(new DoubleTag(this.z + 0.5)))
                .putList("Motion", new ListTag<DoubleTag>()
                        .add(new DoubleTag(-Math.sin(mot) * 0.02))
                        .add(new DoubleTag(0.2))
                        .add(new DoubleTag(-Math.cos(mot) * 0.02)))
                .putList("Rotation", new ListTag<FloatTag>()
                        .add(new FloatTag(0f))
                        .add(new FloatTag(0f)))
                .putShort("Fuse", (short) fuse);
        Entity tnt = Entity.createEntity(Entity.TNT,
                this.getLevel().getChunk(this.getFloorX() >> 4, this.getFloorZ() >> 4),
                nbt, source
        );
        if (tnt == null) {
            return;
        }
        tnt.spawnToAll();
        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(
                source != null ? source : this, this.add(0.5, 0.5, 0.5), VibrationType.PRIME_FUSE));
    }

    @Override
    public int onUpdate(int type) {
        if (!this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
            return 0;
        }

        if ((type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) && this.isGettingPower()) {
            this.prime();
        }

        return 0;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        switch (item.getId()) {
            case Item.FLINT_AND_STEEL -> {
                item.useOn(this);
                this.prime(80, player);
                return true;
            }
            case Item.FIRE_CHARGE -> {
                if (player != null && !player.isCreative()) {
                    item.count--;
                }
                this.prime(80, player);
                return true;
            }
        }
        if (item.hasEnchantment(Enchantment.ID_FIRE_ASPECT) && item.applyEnchantments()) {
            item.useOn(this);
            this.prime(80, player);
            return true;
        }
        return false;
    }

    @Override
    public boolean onProjectileHit(@NotNull Entity projectile, @NotNull Position position, @NotNull Vector3 motion) {
        //TODO: Wither skull, ghast fireball
        if (projectile instanceof EntitySmallFireball ||
                (projectile.isOnFire() && projectile instanceof EntityArrow)) {
            prime(80, projectile);
            return true;
        }
        return false;
    }

}
