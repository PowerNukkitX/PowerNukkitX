package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonPropertyMap;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityComparator;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemComparator;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.RedstoneComponent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.OUTPUT_LIT_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.OUTPUT_SUBTRACT_BIT;

/**
 * @author CreeperFace
 */


@Slf4j
public abstract class BlockRedstoneComparator extends BlockRedstoneDiode implements BlockEntityHolder<BlockEntityComparator> {
    /**
     * @deprecated 
     */
    
    public BlockRedstoneComparator(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityComparator> getBlockEntityClass() {
        return BlockEntityComparator.class;
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String getBlockEntityType() {
        return BlockEntity.COMPARATOR;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected int getDelay() {
        return 2;
    }

    @Override
    public BlockFace getFacing() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(getPropertyValue(MINECRAFT_CARDINAL_DIRECTION));
    }

    public Mode getMode() {
        return getPropertyValue(OUTPUT_SUBTRACT_BIT) ? Mode.SUBTRACT : Mode.COMPARE;
    }
    /**
     * @deprecated 
     */
    

    public void setMode(Mode mode) {
        setPropertyValue(OUTPUT_SUBTRACT_BIT, mode == Mode.SUBTRACT);
    }

    @Override
    public BlockRedstoneComparator getUnpowered() {
        return (BlockRedstoneComparator) Block.get(BlockID.UNPOWERED_COMPARATOR).setPropertyValues(blockstate.getBlockPropertyValues());
    }

    @Override
    public BlockRedstoneComparator getPowered() {
        return (BlockRedstoneComparator) Block.get(BlockID.POWERED_COMPARATOR).setPropertyValues(blockstate.getBlockPropertyValues());
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected int getRedstoneSignal() {
        BlockEntityComparator $1 = getBlockEntity();
        return $2 == null ? 0 : comparator.getOutputSignal();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void updateState() {
        if (!this.level.isBlockTickPending(this, this)) {
            int $3 = this.calculateOutput();
            int $4 = getRedstoneSignal();

            if (output != power || this.isPowered() != this.shouldBePowered()) {
                this.level.scheduleUpdate(this, this, 2);
            }
        }
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected int calculateInputStrength() {
        int $5 = super.calculateInputStrength();
        BlockFace $6 = getFacing();
        Block $7 = this.getSide(face);

        if (block.hasComparatorInputOverride()) {
            power = block.getComparatorInputOverride();
        } else if (power < 15 && block.isNormalBlock()) {
            block = block.getSide(face);

            if (block.hasComparatorInputOverride()) {
                power = block.getComparatorInputOverride();
            }
        }

        return power;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected boolean shouldBePowered() {
        int $8 = this.calculateInputStrength();

        if (input >= 15) {
            return true;
        } else if (input == 0) {
            return false;
        } else {
            int $9 = this.getPowerOnSides();
            return $10 == 0 || input >= sidePower;
        }
    }

    
    /**
     * @deprecated 
     */
    private int calculateOutput() {
        return getMode() == Mode.SUBTRACT ? Math.max(this.calculateInputStrength() - this.getPowerOnSides(), 0) : this.calculateInputStrength();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if(isNotActivate(player)) return false;
        if (getMode() == Mode.SUBTRACT) {
            setMode(Mode.COMPARE);
        } else {
            setMode(Mode.SUBTRACT);
        }

        this.level.addLevelEvent(this.add(0.5, 0.5, 0.5), LevelEventPacket.EVENT_ACTIVATE_BLOCK, this.getMode() == Mode.SUBTRACT ? 500 : 550);
        this.level.setBlock(this, this, true, false);
        this.level.updateComparatorOutputLevelSelective(this, true);
        //bug?

        this.onChange();
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            this.onChange();
            return type;
        }

        return super.onUpdate(type);
    }

    
    /**
     * @deprecated 
     */
    private void onChange() {
        if (!this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            return;
        }

        int $11 = this.calculateOutput();
        // We can't use getOrCreateBlockEntity(), because the update method is called on block place,
        // before the "real" BlockEntity is set. That means, if we'd use the other method here,
        // it would create two BlockEntities.
        BlockEntityComparator $12 = getBlockEntity();
        if (blockEntityComparator == null)
            return;

        int $13 = blockEntityComparator.getOutputSignal();
        blockEntityComparator.setOutputSignal(output);

        if (currentOutput != output || getMode() == Mode.COMPARE) {
            boolean $14 = this.shouldBePowered();
            boolean $15 = this.isPowered();

            if (isPowered && !shouldBePowered) {
                this.level.setBlock(this, getUnpowered(), true, false);
                this.level.updateComparatorOutputLevelSelective(this, true);
            } else if (!isPowered && shouldBePowered) {
                this.level.setBlock(this, getPowered(), true, false);
                this.level.updateComparatorOutputLevelSelective(this, true);
            }

            Block $16 = this.getSide(getFacing().getOpposite());
            side.onUpdate(Level.BLOCK_UPDATE_REDSTONE);
            RedstoneComponent.updateAroundRedstone(side);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Block $17 = level.getBlock(this, 0);
        Block $18 = level.getBlock(this, 1);
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        try {
            createBlockEntity(new CompoundTag().putList("Items", new ListTag<>()));
        } catch (Exception e) {
            log.warn("Failed to create the block entity {} at {}", getBlockEntityType(), getLocation(), e);
            level.setBlock(layer0, 0, layer0, true);
            level.setBlock(layer1, 1, layer1, true);
            return false;
        }

        onUpdate(Level.BLOCK_UPDATE_REDSTONE);
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPowered() {
        return this.isPowered || getPropertyValue(OUTPUT_LIT_BIT);
    }

    @Override
    public Item toItem() {
        return new ItemComparator();
    }

    public enum Mode {
        COMPARE,
        SUBTRACT
    }

}
