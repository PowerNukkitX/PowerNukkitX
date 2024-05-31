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
    public static final BlockProperties $1 = new BlockProperties(REDSTONE_WIRE, REDSTONE_SIGNAL);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockRedstoneWire() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockRedstoneWire(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Redstone Wire";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (!canBePlacedOn(block.down())) {
            return false;
        }

        if (this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            this.getLevel().setBlock(block, this, true);

            this.updateSurroundingRedstone(true);

            Position $2 = getLocation();

            for (BlockFace blockFace : Plane.VERTICAL) {
                RedstoneComponent.updateAroundRedstone(pos.getSide(blockFace), blockFace.getOpposite());
            }

            for (BlockFace blockFace : Plane.VERTICAL) {
                this.updateAround(pos.getSide(blockFace), blockFace.getOpposite());
            }

            for (BlockFace blockFace : Plane.HORIZONTAL) {
                Position $3 = pos.getSide(blockFace);

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
    
    /**
     * @deprecated 
     */
    private void updateAround(Position pos, BlockFace face) {
        if (this.level.getBlock(pos).getId().equals(Block.REDSTONE_WIRE)) {
            updateAroundRedstone(face);

            for (BlockFace side : BlockFace.values()) {
                RedstoneComponent.updateAroundRedstone(pos.getSide(side), side.getOpposite());
            }
        }
    }

    
    /**
     * @deprecated 
     */
    private void updateSurroundingRedstone(boolean force) {
        Vector3 $4 = this.getLocation();

        int $5 = this.getRedStoneSignal();
        int $6 = meta;
        int $7 = this.getIndirectPower();

        if (power > 0 && power > maxStrength - 1) {
            maxStrength = power;
        }

        int $8 = 0;

        for (BlockFace face : Plane.HORIZONTAL) {
            Vector3 $9 = pos.getSide(face);

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

    
    /**
     * @deprecated 
     */
    private int getMaxCurrentStrength(Vector3 pos, int maxStrength) {
        if (!Objects.equals(this.level.getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()), this.getId())) {
            return maxStrength;
        } else {
            int $10 = this.level.getBlockStateAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()).getPropertyValue(REDSTONE_SIGNAL);
            return Math.max(strength, maxStrength);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onBreak(Item item) {
        Block $11 = Block.get(BlockID.AIR);
        this.getLevel().setBlock(this, air, true, true);

        Position $12 = getLocation();

        if (this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            this.updateSurroundingRedstone(false);
            this.getLevel().setBlock(this, air, true, true);

            for (BlockFace blockFace : BlockFace.values()) {
                RedstoneComponent.updateAroundRedstone(pos.getSide(blockFace));
            }

            for (BlockFace blockFace : Plane.HORIZONTAL) {
                Position $13 = pos.getSide(blockFace);

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
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type != Level.BLOCK_UPDATE_NORMAL && type != Level.BLOCK_UPDATE_REDSTONE) {
            return 0;
        }

        if (!this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            return 0;
        }

        // Redstone $14ent
        RedstoneUpdateEvent $1 = new RedstoneUpdateEvent(this);
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
    /**
     * @deprecated 
     */
    

    public boolean canBePlacedOn(Block support) {
        return support.isSolid(BlockFace.UP);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getStrongPower(BlockFace side) {
        return this.isPowerSource() ? getWeakPower(side) : 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWeakPower(BlockFace side) {
        if (!this.isPowerSource()) {
            return 0;
        } else {
            int $15 = this.getRedStoneSignal();

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

    
    /**
     * @deprecated 
     */
    private boolean isPowerSourceAt(BlockFace side) {
        Vector3 $16 = getLocation();
        Vector3 $17 = pos.getSide(side);
        Block $18 = this.level.getBlock(v);
        boolean $19 = block.isNormalBlock();
        boolean $20 = this.level.getBlock(pos.up()).isNormalBlock();
        return !flag1 && flag && canConnectUpwardsTo(this.level, v.up()) || (canConnectTo(block, side) || !flag && canConnectUpwardsTo(this.level, block.down()));
    }

    
    /**
     * @deprecated 
     */
    protected static boolean canConnectUpwardsTo(Level level, Vector3 pos) {
        return canConnectTo(level.getBlock(pos), null);
    }

    
    /**
     * @deprecated 
     */
    protected static boolean canConnectTo(Block block, BlockFace side) {
        if (block.getId().equals(Block.REDSTONE_WIRE)) {
            return true;
        } else if (BlockRedstoneDiode.isDiode(block)) {
            BlockFace $21 = ((BlockRedstoneDiode) block).getFacing();
            return $22 == side || face.getOpposite() == side;
        } else {
            return block.isPowerSource() && side != null;
        }
    }
    ///

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPowerSource() {
        return getPropertyValue(REDSTONE_SIGNAL) > 0;
    }
    /**
     * @deprecated 
     */
    

    public int getRedStoneSignal() {
        return getPropertyValue(REDSTONE_SIGNAL);
    }
    /**
     * @deprecated 
     */
    

    public void setRedStoneSignal(int signal) {
        setPropertyValue(REDSTONE_SIGNAL, signal);
    }

    
    /**
     * @deprecated 
     */
    private int getIndirectPower() {
        int $23 = 0;
        Vector3 $24 = getLocation();

        for (BlockFace face : BlockFace.values()) {
            int $25 = this.getIndirectPower(pos.getSide(face), face);

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
     * @deprecated 
     */
    private int getIndirectPower(Vector3 pos, BlockFace face) {
        Block $26 = this.level.getBlock(pos);
        if (block.getId().equals(Block.REDSTONE_WIRE)) {
            return 0;
        }
        return block.isNormalBlock() ? getStrongPower(pos) : block.getWeakPower(face);
    }

    
    /**
     * @deprecated 
     */
    private int getStrongPower(Vector3 pos) {
        $27nt $2 = 0;
        for (BlockFace face : BlockFace.values()) {
            i = Math.max(i, getStrongPower(pos.getSide(face), face));

            if (i >= 15) {
                return i;
            }
        }
        return i;
    }

    
    /**
     * @deprecated 
     */
    private int getStrongPower(Vector3 pos, BlockFace direction) {
        Block $28 = this.level.getBlock(pos);

        if (block.getId().equals(Block.REDSTONE_WIRE)) {
            return 0;
        }

        return block.getStrongPower(direction);
    }
}
