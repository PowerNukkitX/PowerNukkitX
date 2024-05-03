package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.property.CommonPropertyMap;
import cn.nukkit.block.property.enums.BigDripleafTilt;
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
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import java.util.Objects;

import static cn.nukkit.block.property.CommonBlockProperties.*;
public class BlockBigDripleaf extends BlockFlowable implements Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(BIG_DRIPLEAF, BIG_DRIPLEAF_HEAD, BIG_DRIPLEAF_TILT, MINECRAFT_CARDINAL_DIRECTION);

    public BlockBigDripleaf() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBigDripleaf(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Big Dripleaf";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public BlockFace getBlockFace() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(getPropertyValue(MINECRAFT_CARDINAL_DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(MINECRAFT_CARDINAL_DIRECTION, CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(face));
    }

    public boolean isHead() {
        return this.getPropertyValue(BIG_DRIPLEAF_HEAD);
    }

    public void setHead(boolean isHead) {
        this.setPropertyValue(BIG_DRIPLEAF_HEAD, isHead);
    }

    public BigDripleafTilt getTilt() {
        return this.getPropertyValue(BIG_DRIPLEAF_TILT);
    }

    public boolean setTilt(BigDripleafTilt tilt) {
        BigDripleafTiltChangeEvent event = new BigDripleafTiltChangeEvent(this, this.getTilt(), tilt);
        Server.getInstance().getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;
        this.setPropertyValue(BIG_DRIPLEAF_TILT, tilt);
        return true;
    }

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
        String id = below.getId();
        if (!isValidSupportBlock(id))
            return false;

        if (id.equals(BIG_DRIPLEAF)) {
            var b = new BlockBigDripleaf();
            var bf = ((BlockBigDripleaf) below).getBlockFace();
            b.setBlockFace(bf);
            b.setHead(false);
            level.setBlock(below, b, true, false);
            setBlockFace(bf);
        } else {
            setBlockFace(player != null ? player.getHorizontalFacing().getOpposite() : BlockFace.SOUTH);
        }
        setHead(true);

        if (block instanceof BlockFlowingWater)
            level.setBlock(this, 1, block, true, false);
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public boolean onActivate(@NotNull Item item, @org.jetbrains.annotations.Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isFertilizer()) {
            Block head = this;
            Block up;
            while ((up = head.up()).getId() == BIG_DRIPLEAF)
                head = up;
            if (head.getFloorY() + 1 > level.getMaxHeight())
                return false;
            Block above = head.up();
            if (!above.isAir() && !(above instanceof BlockFlowingWater))
                return false;
            if (player != null && !player.isCreative())
                item.count--;
            level.addParticle(new BoneMealParticle(this));
            var aboveDownBlock = new BlockBigDripleaf();
            aboveDownBlock.setBlockFace(this.getBlockFace());
            level.setBlock(above.getSideVec(BlockFace.DOWN), aboveDownBlock, true, false);
            if (above instanceof BlockFlowingWater)
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
                if (up().getId().equals(BIG_DRIPLEAF)) {
                    return 0;
                }

                level.useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }

            var tilt = getTilt();
            if (tilt == BigDripleafTilt.NONE) {
                return 0;
            }

            if (level.isBlockPowered(this)) {
                setTilt(BigDripleafTilt.NONE);
                level.setBlock(this, this, true, false);
                return Level.BLOCK_UPDATE_SCHEDULED;
            }

            switch (tilt) {
                case UNSTABLE -> setTiltAndScheduleTick(BigDripleafTilt.PARTIAL_TILT);
                case PARTIAL_TILT -> setTiltAndScheduleTick(BigDripleafTilt.FULL_TILT);
                case FULL_TILT -> {
                    level.addSound(this, Sound.TILT_UP_BIG_DRIPLEAF);
                    setTilt(BigDripleafTilt.NONE);
                    level.setBlock(this, this, true, false);
                }
            }
            return Level.BLOCK_UPDATE_SCHEDULED;
        }

        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            if (!isHead())
                return 0;
            var tilt = getTilt();
            if (tilt == BigDripleafTilt.NONE)
                return 0;
            if (!level.isBlockPowered(this))
                return 0;
            if (tilt != BigDripleafTilt.UNSTABLE)
                level.addSound(this, Sound.TILT_UP_BIG_DRIPLEAF);
            setTilt(BigDripleafTilt.NONE);
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
        if (!isHead() || getTilt() != BigDripleafTilt.NONE || entity instanceof EntityProjectile) return;
        setTiltAndScheduleTick(BigDripleafTilt.UNSTABLE);
    }

    @Override
    public boolean onProjectileHit(@NotNull Entity projectile, @NotNull Position position, @NotNull Vector3 motion) {
        setTiltAndScheduleTick(BigDripleafTilt.FULL_TILT);
        return true;
    }

    @Override
    public boolean canPassThrough() {
        return !isHead() || getTilt() == BigDripleafTilt.FULL_TILT;
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
                    this.y + (getTilt() == BigDripleafTilt.PARTIAL_TILT ? 0.8125 : 0.9375),
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

    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    private boolean canSurvive() {
        return isValidSupportBlock(down().getId());
    }

    private boolean isValidSupportBlock(String id) {
        return Objects.equals(id, BIG_DRIPLEAF) ||
                Objects.equals(id, GRASS_BLOCK) ||
                Objects.equals(id, DIRT) ||
                Objects.equals(id, MYCELIUM) ||
                Objects.equals(id, PODZOL) ||
                Objects.equals(id, FARMLAND) ||
                Objects.equals(id, DIRT_WITH_ROOTS) ||
                Objects.equals(id, MOSS_BLOCK) ||
                Objects.equals(id, CLAY);
    }

    private boolean setTiltAndScheduleTick(BigDripleafTilt tilt) {
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
}
