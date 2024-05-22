package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemRedstone;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Objects;

import static cn.nukkit.block.property.CommonBlockProperties.REDSTONE_SIGNAL;


/**
 * @author Angelic47 (Nukkit Project)
 */
public class BlockRedstoneWire extends BlockFlowable implements RedstoneComponent {
    public static final BlockProperties PROPERTIES = new BlockProperties(REDSTONE_WIRE, REDSTONE_SIGNAL);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedstoneWire() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedstoneWire(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Redstone Wire";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (!canBePlacedOn(block.down())) {
            return false;
        }

        if (this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            this.getLevel().setBlock(block, this, true);

            this.updateSurroundingRedstone(true);

            Position pos = getLocation();

            for (BlockFace blockFace : Plane.VERTICAL) {
                RedstoneComponent.updateAroundRedstone(pos.getSide(blockFace), blockFace.getOpposite());
            }

            for (BlockFace blockFace : Plane.VERTICAL) {
                this.updateAround(pos.getSide(blockFace), blockFace.getOpposite());
            }

            for (BlockFace blockFace : Plane.HORIZONTAL) {
                Position p = pos.getSide(blockFace);

                if (this.level.getBlock(p).isNormalBlock()) {
                    this.updateAround(p.getSide(BlockFace.UP), BlockFace.DOWN);
                } else {
                    this.updateAround(p.getSide(BlockFace.DOWN), BlockFace.UP);
                }
            }
        } else {
            this.getLevel().setBlock(block, this, true, true);
        }
        return true;
    }

    //Update the neighbor's block of the pos location as well as the neighbor's neighbor's block
    private void updateAround(Position pos, BlockFace face) {
        if (this.level.getBlock(pos).getId().equals(Block.REDSTONE_WIRE)) {
            updateAroundRedstone(face);

            for (BlockFace side : BlockFace.values()) {
                RedstoneComponent.updateAroundRedstone(pos.getSide(side), side.getOpposite());
            }
        }
    }

    private void updateSurroundingRedstone(boolean force) {
        Vector3 pos = this.getLocation();

        int meta = this.getRedStoneSignal();
        int maxStrength = meta;
        int power = this.getIndirectPower();

        if (power > 0 && power > maxStrength - 1) {
            maxStrength = power;
        }

        int strength = 0;

        for (BlockFace face : Plane.HORIZONTAL) {
            Vector3 v = pos.getSide(face);

            if (v.getX() == this.getX() && v.getZ() == this.getZ()) {
                continue;
            }

            strength = this.getMaxCurrentStrength(v, strength);

            if (this.getMaxCurrentStrength(v.up(), strength) > strength && !this.level.getBlock(pos.up()).isNormalBlock()) {
                strength = this.getMaxCurrentStrength(v.up(), strength);
            }
            if (this.getMaxCurrentStrength(v.down(), strength) > strength && !this.level.getBlock(v).isNormalBlock()) {
                strength = this.getMaxCurrentStrength(v.down(), strength);
            }
        }

        if (strength > maxStrength) {
            maxStrength = strength - 1;
        } else if (maxStrength > 0) {
            --maxStrength;
        } else {
            maxStrength = 0;
        }

        if (power > maxStrength - 1) {
            maxStrength = power;
        } else if (power < maxStrength && strength <= maxStrength) {
            maxStrength = Math.max(power, strength - 1);
        }

        if (meta != maxStrength) {
            this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, meta, maxStrength));

            this.setRedStoneSignal(maxStrength);
            this.level.setBlock(this, this, false, true);

            updateAllAroundRedstone();
        } else if (force) {
            for (BlockFace face : BlockFace.values()) {
                RedstoneComponent.updateAroundRedstone(getSide(face), face.getOpposite());
            }
        }
    }

    private int getMaxCurrentStrength(Vector3 pos, int maxStrength) {
        if (!Objects.equals(this.level.getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()), this.getId())) {
            return maxStrength;
        } else {
            int strength = this.level.getBlockStateAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()).getPropertyValue(REDSTONE_SIGNAL);
            return Math.max(strength, maxStrength);
        }
    }

    @Override
    public boolean onBreak(Item item) {
        Block air = Block.get(BlockID.AIR);
        this.getLevel().setBlock(this, air, true, true);

        Position pos = getLocation();

        if (this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            this.updateSurroundingRedstone(false);
            this.getLevel().setBlock(this, air, true, true);

            for (BlockFace blockFace : BlockFace.values()) {
                RedstoneComponent.updateAroundRedstone(pos.getSide(blockFace));
            }

            for (BlockFace blockFace : Plane.HORIZONTAL) {
                Position p = pos.getSide(blockFace);

                if (this.level.getBlock(p).isNormalBlock()) {
                    this.updateAround(p.getSide(BlockFace.UP), BlockFace.DOWN);
                } else {
                    this.updateAround(p.getSide(BlockFace.DOWN), BlockFace.UP);
                }
            }
        }
        return true;
    }

    @Override
    public Item toItem() {
        return new ItemRedstone();
    }

    @Override
    public int onUpdate(int type) {
        if (type != Level.BLOCK_UPDATE_NORMAL && type != Level.BLOCK_UPDATE_REDSTONE) {
            return 0;
        }

        if (!this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            return 0;
        }

        // Redstone event
        RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
        getLevel().getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return 0;
        }

        if (type == Level.BLOCK_UPDATE_NORMAL && !this.canBePlacedOn(this.down())) {
            this.getLevel().useBreakOn(this);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        this.updateSurroundingRedstone(false);

        return Level.BLOCK_UPDATE_NORMAL;
    }

    public boolean canBePlacedOn(Block support) {
        return support.isSolid(BlockFace.UP);
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return this.isPowerSource() ? getWeakPower(side) : 0;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        if (!this.isPowerSource()) {
            return 0;
        } else {
            int power = this.getRedStoneSignal();

            if (power == 0) {
                return 0;
            } else if (side == BlockFace.UP) {
                return power;
            } else {
                EnumSet<BlockFace> faces = EnumSet.noneOf(BlockFace.class);

                for (BlockFace face : Plane.HORIZONTAL) {
                    if (this.isPowerSourceAt(face)) {
                        faces.add(face);
                    }
                }

                if (side.getAxis().isHorizontal() && faces.isEmpty()) {
                    return power;
                } else if (faces.contains(side) && !faces.contains(side.rotateYCCW()) && !faces.contains(side.rotateY())) {
                    return power;
                } else {
                    return 0;
                }
            }
        }
    }

    private boolean isPowerSourceAt(BlockFace side) {
        Vector3 pos = getLocation();
        Vector3 v = pos.getSide(side);
        Block block = this.level.getBlock(v);
        boolean flag = block.isNormalBlock();
        boolean flag1 = this.level.getBlock(pos.up()).isNormalBlock();
        return !flag1 && flag && canConnectUpwardsTo(this.level, v.up()) || (canConnectTo(block, side) || !flag && canConnectUpwardsTo(this.level, block.down()));
    }

    protected static boolean canConnectUpwardsTo(Level level, Vector3 pos) {
        return canConnectTo(level.getBlock(pos), null);
    }

    protected static boolean canConnectTo(Block block, BlockFace side) {
        if (block.getId().equals(Block.REDSTONE_WIRE)) {
            return true;
        } else if (BlockRedstoneDiode.isDiode(block)) {
            BlockFace face = ((BlockRedstoneDiode) block).getFacing();
            return face == side || face.getOpposite() == side;
        } else {
            return block.isPowerSource() && side != null;
        }
    }
    ///

    @Override
    public boolean isPowerSource() {
        return getPropertyValue(REDSTONE_SIGNAL) > 0;
    }

    public int getRedStoneSignal() {
        return getPropertyValue(REDSTONE_SIGNAL);
    }

    public void setRedStoneSignal(int signal) {
        setPropertyValue(REDSTONE_SIGNAL, signal);
    }

    private int getIndirectPower() {
        int power = 0;
        Vector3 pos = getLocation();

        for (BlockFace face : BlockFace.values()) {
            int blockPower = this.getIndirectPower(pos.getSide(face), face);

            if (blockPower >= 15) {
                return 15;
            }

            if (blockPower > power) {
                power = blockPower;
            }
        }

        return power;
    }

    private int getIndirectPower(Vector3 pos, BlockFace face) {
        Block block = this.level.getBlock(pos);
        if (block.getId().equals(Block.REDSTONE_WIRE)) {
            return 0;
        }
        return block.isNormalBlock() ? getStrongPower(pos) : block.getWeakPower(face);
    }

    private int getStrongPower(Vector3 pos) {
        int i = 0;
        for (BlockFace face : BlockFace.values()) {
            i = Math.max(i, getStrongPower(pos.getSide(face), face));

            if (i >= 15) {
                return i;
            }
        }
        return i;
    }

    private int getStrongPower(Vector3 pos, BlockFace direction) {
        Block block = this.level.getBlock(pos);

        if (block.getId().equals(Block.REDSTONE_WIRE)) {
            return 0;
        }

        return block.getStrongPower(direction);
    }
}
