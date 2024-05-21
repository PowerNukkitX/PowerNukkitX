package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityTarget;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntitySmallFireball;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Axis;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author joserobjr
 */
public class BlockTarget extends BlockTransparent implements RedstoneComponent, BlockEntityHolder<BlockEntityTarget> {

    public static final BlockProperties PROPERTIES = new BlockProperties(TARGET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTarget() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTarget(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Target";
    }

    @Override
    @NotNull public Class<? extends BlockEntityTarget> getBlockEntityClass() {
        return BlockEntityTarget.class;
    }

    @Override
    @NotNull public String getBlockEntityType() {
        return BlockEntity.TARGET;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace face) {
        BlockEntityTarget target = getBlockEntity();
        return target == null? 0 : target.getActivePower();
    }

    public boolean activatePower(int power) {
        return activatePower(power, 4 * 2);
    }

    public boolean activatePower(int power, int ticks) {
        Level level = getLevel();
        if (power <= 0 || ticks <= 0) {
            return deactivatePower();
        }

        if (!level.getServer().getSettings().levelSettings().enableRedstone()) {
            return false;
        }

        BlockEntityTarget target = getOrCreateBlockEntity();
        int previous = target.getActivePower();
        level.cancelSheduledUpdate(this, this);
        level.scheduleUpdate(this, ticks);
        target.setActivePower(power);
        if (previous != power) {
            updateAroundRedstone();
        }
        return true;
    }

    public boolean deactivatePower() {
        BlockEntityTarget target = getBlockEntity();
        if (target != null) {
            int currentPower = target.getActivePower();
            target.setActivePower(0);
            target.close();
            if (currentPower != 0 && level.getServer().getSettings().levelSettings().enableRedstone()) {
                updateAroundRedstone();
            }
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            deactivatePower();
            return type;
        }
        return 0;
    }

    @Override
    public boolean onProjectileHit(@NotNull Entity projectile, @NotNull Position position, @NotNull Vector3 motion) {
        int ticks = 8;
        if (projectile instanceof EntityArrow || projectile instanceof EntityThrownTrident || projectile instanceof EntitySmallFireball) {
            ticks = 20;
        }

        MovingObjectPosition intercept = calculateIntercept(position, position.add(motion.multiply(2)));
        if (intercept == null) {
            return false;
        }

        BlockFace faceHit = intercept.getFaceHit();
        if (faceHit == null) {
            return false;
        }

        Vector3 hitVector = intercept.hitVector.subtract(x*2, y*2, z*2);
        List<Axis> axes = new ArrayList<>(Arrays.asList(Axis.values()));
        axes.remove(faceHit.getAxis());
        
        double[] coords = new double[] { hitVector.getAxis(axes.get(0)), hitVector.getAxis(axes.get(1)) };

        for (int i = 0; i < 2 ; i++) {
            if (coords[i] == 0.5) {
                coords[i] = 1;
            } else if (coords[i] <= 0 || coords[i] >= 1) {
                coords[i] = 0;
            } else if (coords[i] < 0.5) {
                coords[i] *= 2;
            } else {
                coords[i] = (coords[i] / (-0.5)) + 2;
            }
        }

        double scale = (coords[0] + coords[1]) / 2;
        activatePower(NukkitMath.ceilDouble(16 * scale), ticks);
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 0.5;
    }

    @Override
    public int getBurnAbility() {
        return 15;
    }

    @Override
    public int getBurnChance() {
        return 0;
    }
}
