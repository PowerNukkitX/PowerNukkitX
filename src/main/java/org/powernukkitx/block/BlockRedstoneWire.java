package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.event.block.BlockRedstoneEvent;
import org.powernukkitx.event.redstone.RedstoneUpdateEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemRedstone;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.BlockFace.Plane;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Objects;

import static org.powernukkitx.block.property.CommonBlockProperties.REDSTONE_SIGNAL;


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

        if (this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
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

            for (BlockFace side : BlockFace.getValues()) {
                RedstoneComponent.updateAroundRedstone(pos.getSide(side), side.getOpposite());
            }
        }
    }

    private void updateSurroundingRedstone(boolean force) {
        int meta = this.getRedStoneSignal();
        int maxStrength = meta;
        int power = this.getIndirectPower();

        if (power > 0 && power > maxStrength - 1) {
            maxStrength = power;
        }

        int strength = 0;

        int baseX = this.getFloorX();
        int baseY = this.getFloorY();
        int baseZ = this.getFloorZ();
        // Loop invariant: the block above this wire does not depend on the face being examined,
        // but it used to be fetched - Vector3 and Block both - once per horizontal face.
        boolean coveredFromAbove = this.level.getBlock(baseX, baseY + 1, baseZ).isNormalBlock();

        for (BlockFace face : Plane.HORIZONTAL) {
            int adjacentX = baseX + face.getXOffset();
            int adjacentZ = baseZ + face.getZOffset();

            strength = this.getMaxCurrentStrength(adjacentX, baseY, adjacentZ, strength);

            // getMaxCurrentStrength is a pure read and strength does not move between the guard and
            // the assignment, so each probe is resolved once instead of twice.
            Block adjacentBlock = null;

            // Upward propagation do not allow to power from a wire UP and to the side when the wire is over a top slab
            int upStrength = this.getMaxCurrentStrength(adjacentX, baseY + 1, adjacentZ, strength);
            if (upStrength > strength && !coveredFromAbove) {
                adjacentBlock = this.level.getBlock(adjacentX, baseY, adjacentZ);
                if (!(adjacentBlock instanceof BlockSlab slab && slab.isOnTop())) {
                    strength = upStrength;
                }
            }

            // Downward propagation allows to pull power from a wire DOWN and to the side even if the wire is over a top slab
            int downStrength = this.getMaxCurrentStrength(adjacentX, baseY - 1, adjacentZ, strength);
            if (downStrength > strength) {
                if (adjacentBlock == null) {
                    adjacentBlock = this.level.getBlock(adjacentX, baseY, adjacentZ);
                }
                if (!adjacentBlock.isNormalBlock()) {
                    strength = downStrength;
                }
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
            for (BlockFace face : BlockFace.getValues()) {
                RedstoneComponent.updateAroundRedstone(getSide(face), face.getOpposite());
            }
        }
    }

    private int getMaxCurrentStrength(Vector3 pos, int maxStrength) {
        return getMaxCurrentStrength(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), maxStrength);
    }

    /**
     * Reads the signal of a neighbouring wire. Takes coordinates rather than a {@link Vector3} so
     * the callers that probe the positions above and below a neighbour do not have to allocate one
     * per probe.
     */
    private int getMaxCurrentStrength(int x, int y, int z, int maxStrength) {
        BlockState state = this.level.getBlockStateAt(x, y, z);
        if (!Objects.equals(state.getIdentifier(), this.getId())) {
            return maxStrength;
        }
        return Math.max(state.getPropertyValue(REDSTONE_SIGNAL), maxStrength);
    }

    @Override
    public boolean onBreak(Item item) {
        Block air = Block.get(BlockID.AIR);
        this.getLevel().setBlock(this, air, true, true);

        Position pos = getLocation();

        if (this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
            this.updateSurroundingRedstone(false);
            this.getLevel().setBlock(this, air, true, true);

            for (BlockFace blockFace : BlockFace.getValues()) {
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

        if (!this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
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
        int baseX = getFloorX();
        int baseY = getFloorY();
        int baseZ = getFloorZ();

        for (BlockFace face : BlockFace.getValues()) {
            int blockPower = this.getIndirectPower(
                    baseX + face.getXOffset(), baseY + face.getYOffset(), baseZ + face.getZOffset(), face);

            if (blockPower >= 15) {
                return 15;
            }

            if (blockPower > power) {
                power = blockPower;
            }
        }

        return power;
    }

    /**
     * Resolves how much power a neighbour feeds into this wire.
     * <p>
     * Takes coordinates instead of a {@link Vector3}: a wire mesh walks six neighbours per wire and,
     * for every neighbour that turns out to be a normal block, six more. Every one of those steps
     * used to allocate a position and a {@link Block}, and a wire neighbour - the common case inside
     * a mesh - contributes nothing, which the state alone can settle.
     */
    private int getIndirectPower(int x, int y, int z, BlockFace face) {
        if (isWireAt(x, y, z)) {
            return 0;
        }
        Block block = this.level.getBlock(x, y, z);
        return block.isNormalBlock() ? getStrongPower(x, y, z) : block.getWeakPower(face);
    }

    private int getStrongPower(int x, int y, int z) {
        int i = 0;
        for (BlockFace face : BlockFace.getValues()) {
            i = Math.max(i, getStrongPower(
                    x + face.getXOffset(), y + face.getYOffset(), z + face.getZOffset(), face));

            if (i >= 15) {
                return i;
            }
        }
        return i;
    }

    private int getStrongPower(int x, int y, int z, BlockFace direction) {
        if (isWireAt(x, y, z)) {
            return 0;
        }
        return this.level.getBlock(x, y, z).getStrongPower(direction);
    }

    private boolean isWireAt(int x, int y, int z) {
        return Objects.equals(this.level.getBlockStateAt(x, y, z).getIdentifier(), Block.REDSTONE_WIRE);
    }
}