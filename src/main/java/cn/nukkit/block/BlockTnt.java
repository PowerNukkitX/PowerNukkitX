package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntitySmallFireball;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.RedstoneComponent;
import cn.nukkit.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static cn.nukkit.block.property.CommonBlockProperties.ALLOW_UNDERWATER_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.EXPLODE_BIT;

public class BlockTnt extends BlockSolid implements RedstoneComponent {

    public static final BlockProperties PROPERTIES = new BlockProperties(TNT,
            EXPLODE_BIT, ALLOW_UNDERWATER_BIT);

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
                        .add(new FloatTag(0))
                        .add(new FloatTag(0)))
                .putShort("Fuse", fuse);
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
        if (!this.level.getServer().getSettings().levelSettings().enableRedstone()) {
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
