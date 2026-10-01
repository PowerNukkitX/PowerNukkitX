package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.projectile.EntityArrow;
import org.powernukkitx.entity.projectile.EntityThrownTrident;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.MovingObjectPosition;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.redstone.circuit.CircuitSystem;
import org.powernukkitx.level.redstone.circuit.components.BaseCircuitComponent;
import org.powernukkitx.level.redstone.circuit.components.ProducerComponent;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.BlockFace.Axis;
import org.powernukkitx.math.NukkitMath;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author joserobjr
 */
public class BlockTarget extends BlockSolid implements RedstoneComponent {
    public static final BlockProperties PROPERTIES = new BlockProperties(TARGET);
    private static final int NON_DIRECTIONAL_PRODUCER = 6;
    private static final float TARGET_FACE_RADIUS = 0.70710677f;

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
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace face) {
        return getActivePower();
    }

    private int getActivePower() {
        CircuitSystem circuitSystem = this.level.getCircuitSystem();
        BaseCircuitComponent component = circuitSystem.getBaseComponent(this);

        if (component == null) {
            circuitSystem.setupPoweredBlock(this);
            return 0;
        }

        return component instanceof ProducerComponent ? component.getStrength() : 0;
    }

    public boolean activatePower(int power) {
        return activatePower(power, 4 * 2);
    }

    public boolean activatePower(int power, int ticks) {
        Level level = getLevel();
        if (power <= 0 || ticks <= 0) {
            return deactivatePower();
        }

        if (!level.getServer().getSettings().gameplaySettings().enableRedstone()) return false;

        int previous = getActivePower();
        CircuitSystem circuitSystem = level.getCircuitSystem();
        circuitSystem.removeComponent(this);
        circuitSystem.setupProducer(this, NON_DIRECTIONAL_PRODUCER, power);
        int current = circuitSystem.getStrength(this);
        level.cancelScheduledUpdate(this, this);
        level.scheduleUpdate(this, ticks);

        if (previous != current) {
            updateAroundRedstone();
        }

        return true;
    }

    public boolean deactivatePower() {
        CircuitSystem circuitSystem = this.level.getCircuitSystem();

        BaseCircuitComponent component = circuitSystem.getBaseComponent(this);
        if (!(component instanceof ProducerComponent)) return false;

        int previous = component.getStrength();
        circuitSystem.removeComponent(this);
        circuitSystem.setupPoweredBlock(this);
        int current = getActivePower();

        if (previous != current && this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
            updateAroundRedstone();
        }

        return true;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) return false;
        this.level.getCircuitSystem().setupPoweredBlock(this);
        return true;
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
        int ticks = projectile instanceof EntityArrow || projectile instanceof EntityThrownTrident ? 20 : 8;
        MovingObjectPosition intercept = getBoundingBox().calculateIntercept(position, position.add(motion.multiply(2)));
        if (intercept == null) return false;

        BlockFace faceHit = intercept.getFaceHit();
        if (faceHit == null) return false;

        Vector3 hitVector = intercept.hitVector.subtract(x, y, z);
        List<Axis> axes = new ArrayList<>(Arrays.asList(Axis.values()));

        axes.remove(faceHit.getAxis());

        float firstOffset = (float) hitVector.getAxis(axes.get(0)) - 0.5f;
        float secondOffset = (float) hitVector.getAxis(axes.get(1)) - 0.5f;
        float distance = (float) Math.sqrt(firstOffset * firstOffset + secondOffset * secondOffset);
        int power = NukkitMath.clamp((int) ((TARGET_FACE_RADIUS - distance) * 16.0f / TARGET_FACE_RADIUS), 1, 15);

        if (activatePower(power, ticks)) {
            this.level.updateAroundObserver(this);
        }

        return true;
    }

    @Override
    public void afterRemoval(Block newBlock, boolean update) {
        int previous =this.level.getCircuitSystem().getStrength(this);
        this.level.getCircuitSystem().removeComponent(this);

        if (previous != 0 && this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
            updateAroundRedstone();
        }

        super.afterRemoval(newBlock, update);
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
