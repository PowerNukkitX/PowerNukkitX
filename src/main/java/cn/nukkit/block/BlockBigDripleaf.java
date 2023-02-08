package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.*;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.block.BigDripleafTiltChangeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static cn.nukkit.block.BlockBigDripleaf.Tilt.*;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockBigDripleaf extends BlockFlowable implements Faceable {

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final BlockProperty<Tilt> TILT = new ArrayBlockProperty<>("big_dripleaf_tilt", false, new Tilt[]{Tilt.NONE, PARTIAL_TILT, Tilt.FULL_TILT, Tilt.UNSTABLE});

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final BooleanBlockProperty HEAD = new BooleanBlockProperty("big_dripleaf_head", false);

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final BlockProperties PROPERTIES = new BlockProperties(CommonBlockProperties.DIRECTION, TILT, HEAD);

    protected BlockBigDripleaf() {
        super(0);
    }

    @Override
    public String getName() {
        return "Big Dripleaf";
    }

    @Override
    public int getId() {
        return BlockID.BIG_DRIPLEAF;
    }

    @Since("1.6.0.0-PNX")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(CommonBlockProperties.DIRECTION);
    }

    @Since("1.6.0.0-PNX")
    @PowerNukkitOnly
    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.DIRECTION, face);
    }

    public boolean isHead() {
        return this.getBooleanValue(HEAD);
    }

    public void setHead(boolean isHead) {
        this.setBooleanValue(HEAD, isHead);
    }

    public Tilt getTilt() {
        return this.getPropertyValue(TILT);
    }

    public boolean setTilt(Tilt tilt) {
        BigDripleafTiltChangeEvent event = new BigDripleafTiltChangeEvent(this, this.getTilt(), tilt);
        Server.getInstance().getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;
        this.setPropertyValue(TILT, tilt);
        return true;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_NONE;
    }

    @Override
    public boolean canBeActivated() {
        return true;
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
    public int getBurnChance() {
        return 15;
    }

    @Override
    public int getBurnAbility() {
        return 100;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        Block below = block.down();
        int id = below.getId();
        if (!isValidSupportBlock(id))
            return false;

        if (id == BIG_DRIPLEAF) {
            var b = new BlockBigDripleaf();
            var bf = ((BlockBigDripleaf) below).getBlockFace();
            b.setBlockFace(bf);
            b.setHead(false);
            level.setBlock(below, b, true, false);
            setHead(true);
            setBlockFace(bf);
        } else {
            setBlockFace(player.getHorizontalFacing().getOpposite());
            setHead(true);
        }

        if (block instanceof BlockWater)
            level.setBlock(this, 1, block, true, false);
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public boolean onActivate(@NotNull Item item, @org.jetbrains.annotations.Nullable Player player) {
        if (item.isFertilizer()) {
            Block head = this;
            Block up;
            while ((up = head.up()).getId() == BIG_DRIPLEAF)
                head = up;
            if (head.getFloorY() + 1 >= level.getMaxHeight())
                return false;
            Block above = head.up();
            if (!(above.getId() == AIR) && !(above instanceof BlockWater))
                return false;
            if (player != null && !player.isCreative())
                item.count--;
            level.addParticle(new BoneMealParticle(this));
            var aboveDownBlock = new BlockBigDripleaf();
            aboveDownBlock.setBlockFace(this.getBlockFace());
            level.setBlock(above.getSideVec(BlockFace.DOWN), aboveDownBlock, true, false);
            if (above instanceof BlockWater)
                level.setBlock(above, 1, above, true, false);
            var aboveBock = new BlockBigDripleaf();
            aboveBock.setBlockFace(this.getBlockFace());
            aboveBock.setHead(true);
            level.setBlock(above, aboveBock, true);
            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            level.scheduleUpdate(this, 1);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!canSurvive()) {
                level.useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }

            if (!isHead()) {
                if (up().getId() == BIG_DRIPLEAF) {
                    return 0;
                }

                level.useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }

            var tilt = getTilt();
            if (tilt == Tilt.NONE) {
                return 0;
            }

            if (level.isBlockPowered(this)) {
                setTilt(Tilt.NONE);
                level.setBlock(this, this, true, false);
                return Level.BLOCK_UPDATE_SCHEDULED;
            }

            switch (tilt) {
                case UNSTABLE -> setTiltAndScheduleTick(PARTIAL_TILT);
                case PARTIAL_TILT -> setTiltAndScheduleTick(FULL_TILT);
                case FULL_TILT -> {
                    level.addSound(this, Sound.TILT_UP_BIG_DRIPLEAF);
                    setTilt(NONE);
                    level.setBlock(this, this, true, false);
                }
            }
            return Level.BLOCK_UPDATE_SCHEDULED;
        }

        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            if (!isHead())
                return 0;
            var tilt = getTilt();
            if (tilt == NONE)
                return 0;
            if (!level.isBlockPowered(this))
                return 0;
            if (tilt != UNSTABLE)
                level.addSound(this, Sound.TILT_UP_BIG_DRIPLEAF);
            setTilt(NONE);
            level.setBlock(this, this, true, false);

            level.cancelSheduledUpdate(this, this);
            return Level.BLOCK_UPDATE_SCHEDULED;
        }

        return 0;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (!isHead() || getTilt() != NONE || entity instanceof EntityProjectile) return;
        setTiltAndScheduleTick(UNSTABLE);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{new BlockSmallDripleaf().toItem()};
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean onProjectileHit(@NotNull Entity projectile, @NotNull Position position, @NotNull Vector3 motion) {
        setTiltAndScheduleTick(FULL_TILT);
        return true;
    }

    @Override
    public boolean canPassThrough() {
        return !isHead() || getTilt() == FULL_TILT;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        if (!isHead()) {
            //杆没有碰撞箱
//            var face = this.getBlockFace().getOpposite();
            return /*new SimpleAxisAlignedBB(
                    0.3125,
                    0,
                    0.3125,
                    0.6875,
                    1,
                    0.6875)
                    .offset(
                            this.x + face.getXOffset() * 0.1875,
                            this.y,
                            this.z + face.getZOffset() * 0.1875
                    );*/null;
        } else {
            return new SimpleAxisAlignedBB(
                    this.x,
                    this.y + 0.6875,
                    this.z,
                    this.x + 1,
                    this.y + (getTilt() == PARTIAL_TILT ? 0.8125 : 0.9375),
                    this.z + 1
            );
        }
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        var bb = getBoundingBox();
        //使方块碰撞检测箱的maxY向上取整，使当实体站在方块上面的时候可以触发碰撞
        if (isHead())
            bb.setMaxY(Math.ceil(bb.getMaxY()));
        return bb;
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }

    private boolean canSurvive() {
        return isValidSupportBlock(down().getId());
    }

    private boolean isValidSupportBlock(int id) {
        return id == BIG_DRIPLEAF || id == GRASS || id == DIRT || id == MYCELIUM || id == PODZOL || id == FARMLAND || id == DIRT_WITH_ROOTS || id == MOSS_BLOCK || id == CLAY_BLOCK;
    }

    private boolean setTiltAndScheduleTick(Tilt tilt) {
        if (!setTilt(tilt))
            return false;
        level.setBlock(this, this, true, false);

        switch (tilt) {
            case NONE -> level.scheduleUpdate(this, 1);
            case UNSTABLE -> {
                level.scheduleUpdate(this, 15);
                return true;
            }
            case PARTIAL_TILT -> level.scheduleUpdate(this, 15);
            case FULL_TILT -> level.scheduleUpdate(this, 100);
        }

        level.addSound(this, Sound.TILT_DOWN_BIG_DRIPLEAF);
        return true;
    }

    public enum Tilt {
        NONE,
        UNSTABLE,
        PARTIAL_TILT,
        FULL_TILT
    }
}
