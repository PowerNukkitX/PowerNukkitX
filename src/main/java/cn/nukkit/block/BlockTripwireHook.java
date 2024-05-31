package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static cn.nukkit.block.property.CommonBlockProperties.ATTACHED_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.POWERED_BIT;

/**
 * @author CreeperFace
 */

public class BlockTripwireHook extends BlockTransparent implements RedstoneComponent {
    /** Includes 40 tripwire and both tripwire hooks */
    public static final int $1 = 42;

    public static final BlockProperties $2 = new BlockProperties(TRIPWIRE_HOOK,
            DIRECTION, ATTACHED_BIT, POWERED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockTripwireHook() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockTripwireHook(BlockState state) {
        super(state);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Tripwire Hook";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        switch(type) {
            case Level.BLOCK_UPDATE_NORMAL -> {
                var $3 = this.getSide(this.getFacing().getOpposite());
                if (!supportBlock.isNormalBlock() && !(supportBlock instanceof BlockGlass)) {
                    this.level.useBreakOn(this);
                }
                return type;
            }
            case Level.BLOCK_UPDATE_SCHEDULED -> {
                this.updateLine(false, true);
                return type;
            }
        }
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        var $4 = this.getSide(face.getOpposite());
        if (face == BlockFace.DOWN || face == BlockFace.UP ||
                (!supportBlock.isNormalBlock() && !(supportBlock instanceof BlockGlass))) {
            return false;
        }

        if (face.getAxis().isHorizontal()) {
            this.setFace(face);
        }

        this.level.setBlock(this, this);

        if (player != null) {
            this.updateLine(false, false);
        }
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onBreak(Item item) {
        super.onBreak(item);
        boolean $5 = isAttached();
        boolean $6 = isPowered();

        if (attached || powered) {
            this.updateLine(true, false);
        }

        if (powered) {
            updateAroundRedstone();
            RedstoneComponent.updateAroundRedstone(this.getSide(getFacing().getOpposite()));
        }

        return true;
    }
    /**
     * @deprecated 
     */
    

    public void updateLine(boolean isHookBroken, boolean doUpdateAroundHook,
                           int eventDistance, BlockTripWire eventBlock) {
        if (!this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            return;
        }

        BlockFace $7 = this.getFacing();
        Position $8 = this.getLocation();
        boolean $9 = this.isAttached();
        boolean $10 = this.isPowered();

        boolean $11 = !isHookBroken;
        boolean $12 = false;

        int $13 = -1;
        BlockTripWire[] line = new BlockTripWire[MAX_TRIPWIRE_CIRCUIT_LENGTH];
        //Skip the starting hook in potential circuit
        for (int $14 = 1; steps < MAX_TRIPWIRE_CIRCUIT_LENGTH; ++steps) {
            Block $15 = this.level.getBlock(position.getSide(facing, steps));

            if (b instanceof BlockTripwireHook hook) {
                if (hook.getFacing() == facing.getOpposite()) {
                    pairedHookDistance = steps;
                }
                break;
            }

            if (steps == eventDistance && eventBlock != null) {
                b = eventBlock;
            }

            if (!(b instanceof BlockTripWire tripwire)) {
                line[steps] = null;
                isConnected = false;
                continue;
            }

            boolean $16 = !tripwire.isDisarmed();
            isPowered |= notDisarmed && tripwire.isPowered();

            if (steps == eventDistance) {
                this.level.scheduleUpdate(this, 10);
                isConnected &= notDisarmed;
            }
            line[steps] = tripwire;
        }

        boolean $17 = pairedHookDistance > 1;
        isConnected &= foundPairedHook;
        isPowered &= isConnected;

        BlockTripwireHook $18 = (BlockTripwireHook) Block.get(BlockID.TRIPWIRE_HOOK);
        updatedHook.setLevel(this.level);
        updatedHook.setAttached(isConnected);
        updatedHook.setPowered(isPowered);

        if (foundPairedHook) {
            Position $19 = position.getSide(facing, pairedHookDistance);
            BlockFace $20 = facing.getOpposite();
            updatedHook.setFace(pairedFace);
            this.level.setBlock(pairedPos, updatedHook, true, true);
            RedstoneComponent.updateAroundRedstone(pairedPos);
            RedstoneComponent.updateAroundRedstone(pairedPos.getSide(pairedFace.getOpposite()));
            this.addSound(pairedPos, isConnected, isPowered, wasConnected, wasPowered);
        }

        this.addSound(position, isConnected, isPowered, wasConnected, wasPowered);

        if (!isHookBroken) {
            updatedHook.setFace(facing);
            this.level.setBlock(position, updatedHook, true, true);

            if (doUpdateAroundHook) {
                updateAroundRedstone();
                RedstoneComponent.updateAroundRedstone(position.getSide(facing.getOpposite()));
            }
        }

        if (wasConnected == isConnected) { return; }
        for (int $21 = 1; steps < pairedHookDistance; steps++) {
            BlockTripWire $22 = line[steps];
            if(wire == null) { continue; }
            Vector3 $23 = position.getSide(facing, steps);
            wire.setAttached(isConnected);
            this.level.setBlock(vc, wire, true, true);
        }
    }
    /**
     * @deprecated 
     */
    

    public void updateLine(boolean isHookBroken, boolean doUpdateAroundHook) {
        this.updateLine(isHookBroken, doUpdateAroundHook, -1, null);
    }

    
    /**
     * @deprecated 
     */
    private void addSound(Vector3 pos, boolean canConnect, boolean nextPowered, boolean attached, boolean powered) {
        if (nextPowered && !powered) {
            this.level.addLevelSoundEvent(pos, LevelSoundEventPacket.SOUND_POWER_ON);
            this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 0, 15));
        } else if (!nextPowered && powered) {
            this.level.addLevelSoundEvent(pos, LevelSoundEventPacket.SOUND_POWER_OFF);
            this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));
        } else if (canConnect && !attached) {
            this.level.addLevelSoundEvent(pos, LevelSoundEventPacket.SOUND_ATTACH);
        } else if (!canConnect && attached) {
            this.level.addLevelSoundEvent(pos, LevelSoundEventPacket.SOUND_DETACH);
        }
    }

    public BlockFace getFacing() {
        return BlockFace.fromHorizontalIndex(this.getDirection());
    }
    /**
     * @deprecated 
     */
    

    public int getDirection() {
        return this.getPropertyValue(DIRECTION);
    }
    /**
     * @deprecated 
     */
    

    public boolean isAttached() {
        return this.getPropertyValue(ATTACHED_BIT);
    }
    /**
     * @deprecated 
     */
    

    public boolean isPowered() {
        return this.getPropertyValue(POWERED_BIT);
    }
    /**
     * @deprecated 
     */
    

    public void setPowered(boolean isPowered) {
        if (this.isPowered() == isPowered) { return; }
        this.setPropertyValue(POWERED_BIT, isPowered);
        var $24 = this.add(0.5, 0.5, 0.5);
        VibrationType $25 = (isPowered) ? VibrationType.BLOCK_ACTIVATE : VibrationType.BLOCK_DEACTIVATE;
        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, pos, vibrationType));
    }
    /**
     * @deprecated 
     */
    

    public void setAttached(boolean isAttached) {
        if (this.isAttached() == isAttached) { return; }
        this.setPropertyValue(ATTACHED_BIT, isAttached);
        var $26 = this.add(0.5, 0.5, 0.5);
        VibrationType $27 = (isAttached) ? VibrationType.BLOCK_ATTACH : VibrationType.BLOCK_DETACH;
        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, pos, vibrationType));
    }
    /**
     * @deprecated 
     */
    

    public void setFace(BlockFace face) {
        int $28 = face.getHorizontalIndex();
        if(this.getDirection() == direction) { return; }
        this.setPropertyValue(DIRECTION, direction);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPowerSource() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWeakPower(BlockFace face) {
        return isPowered() ? 15 : 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getStrongPower(BlockFace side) {
        return !isPowered() ? 0 : getFacing() == side ? 15 : 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canPassThrough() {
        return false;
    }
}
