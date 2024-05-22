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
    public static final int MAX_TRIPWIRE_CIRCUIT_LENGTH = 42;

    public static final BlockProperties PROPERTIES = new BlockProperties(TRIPWIRE_HOOK,
            DIRECTION, ATTACHED_BIT, POWERED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTripwireHook() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTripwireHook(BlockState state) {
        super(state);
    }

    @Override
    public String getName() {
        return "Tripwire Hook";
    }

    @Override
    public int onUpdate(int type) {
        switch(type) {
            case Level.BLOCK_UPDATE_NORMAL -> {
                var supportBlock = this.getSide(this.getFacing().getOpposite());
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
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        var supportBlock = this.getSide(face.getOpposite());
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
    public boolean onBreak(Item item) {
        super.onBreak(item);
        boolean attached = isAttached();
        boolean powered = isPowered();

        if (attached || powered) {
            this.updateLine(true, false);
        }

        if (powered) {
            updateAroundRedstone();
            RedstoneComponent.updateAroundRedstone(this.getSide(getFacing().getOpposite()));
        }

        return true;
    }

    public void updateLine(boolean isHookBroken, boolean doUpdateAroundHook,
                           int eventDistance, BlockTripWire eventBlock) {
        if (!this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            return;
        }

        BlockFace facing = this.getFacing();
        Position position = this.getLocation();
        boolean wasConnected = this.isAttached();
        boolean wasPowered = this.isPowered();

        boolean isConnected = !isHookBroken;
        boolean isPowered = false;

        int pairedHookDistance = -1;
        BlockTripWire[] line = new BlockTripWire[MAX_TRIPWIRE_CIRCUIT_LENGTH];
        //Skip the starting hook in potential circuit
        for (int steps = 1; steps < MAX_TRIPWIRE_CIRCUIT_LENGTH; ++steps) {
            Block b = this.level.getBlock(position.getSide(facing, steps));

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

            boolean notDisarmed = !tripwire.isDisarmed();
            isPowered |= notDisarmed && tripwire.isPowered();

            if (steps == eventDistance) {
                this.level.scheduleUpdate(this, 10);
                isConnected &= notDisarmed;
            }
            line[steps] = tripwire;
        }

        boolean foundPairedHook = pairedHookDistance > 1;
        isConnected &= foundPairedHook;
        isPowered &= isConnected;

        BlockTripwireHook updatedHook = (BlockTripwireHook) Block.get(BlockID.TRIPWIRE_HOOK);
        updatedHook.setLevel(this.level);
        updatedHook.setAttached(isConnected);
        updatedHook.setPowered(isPowered);

        if (foundPairedHook) {
            Position pairedPos = position.getSide(facing, pairedHookDistance);
            BlockFace pairedFace = facing.getOpposite();
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
        for (int steps = 1; steps < pairedHookDistance; steps++) {
            BlockTripWire wire = line[steps];
            if(wire == null) { continue; }
            Vector3 vc = position.getSide(facing, steps);
            wire.setAttached(isConnected);
            this.level.setBlock(vc, wire, true, true);
        }
    }

    public void updateLine(boolean isHookBroken, boolean doUpdateAroundHook) {
        this.updateLine(isHookBroken, doUpdateAroundHook, -1, null);
    }

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

    public int getDirection() {
        return this.getPropertyValue(DIRECTION);
    }

    public boolean isAttached() {
        return this.getPropertyValue(ATTACHED_BIT);
    }

    public boolean isPowered() {
        return this.getPropertyValue(POWERED_BIT);
    }

    public void setPowered(boolean isPowered) {
        if (this.isPowered() == isPowered) { return; }
        this.setPropertyValue(POWERED_BIT, isPowered);
        var pos = this.add(0.5, 0.5, 0.5);
        VibrationType vibrationType = (isPowered) ? VibrationType.BLOCK_ACTIVATE : VibrationType.BLOCK_DEACTIVATE;
        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, pos, vibrationType));
    }

    public void setAttached(boolean isAttached) {
        if (this.isAttached() == isAttached) { return; }
        this.setPropertyValue(ATTACHED_BIT, isAttached);
        var pos = this.add(0.5, 0.5, 0.5);
        VibrationType vibrationType = (isAttached) ? VibrationType.BLOCK_ATTACH : VibrationType.BLOCK_DETACH;
        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, pos, vibrationType));
    }

    public void setFace(BlockFace face) {
        int direction = face.getHorizontalIndex();
        if(this.getDirection() == direction) { return; }
        this.setPropertyValue(DIRECTION, direction);
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace face) {
        return isPowered() ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return !isPowered() ? 0 : getFacing() == side ? 15 : 0;
    }

    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }
}
