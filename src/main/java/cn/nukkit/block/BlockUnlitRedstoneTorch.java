package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

public class BlockUnlitRedstoneTorch  extends BlockTorch implements RedstoneComponent {
    public static final BlockProperties PROPERTIES = new BlockProperties(UNLIT_REDSTONE_TORCH, CommonBlockProperties.TORCH_FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockUnlitRedstoneTorch() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockUnlitRedstoneTorch(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Unlit Redstone Torch";
    }

    @Override
    public int getLightLevel() {
        return 0;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return 0;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.REDSTONE_TORCH));
    }

    @Override
    public int onUpdate(int type) {
        if (super.onUpdate(type) == 0) {
            if (!this.level.getServer().isRedstoneEnabled()) {
                return 0;
            }

            if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
                this.level.scheduleUpdate(this, tickRate());
            } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
                RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
                getLevel().getServer().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return 0;
                }

                if (checkState()) {
                    return 1;
                }
            }
        }

        return 0;
    }

    private boolean checkState() {
        if (!this.isPoweredFromSide()) {
            this.level.setBlock(getLocation(), Block.get(BlockID.REDSTONE_TORCH).setPropertyValues(getPropertyValues()), false, true);
            updateAllAroundRedstone(getBlockFace().getOpposite());
            return true;
        }

        return false;
    }

    protected boolean isPoweredFromSide() {
        BlockFace face = getBlockFace().getOpposite();
        if (this.getSide(face) instanceof BlockPistonBase && this.getSide(face).isGettingPower()) {
            return true;
        }

        return this.level.isSidePowered(this.getLocation().getSide(face), face);
    }

    @Override
    public int tickRate() {
        return 2;
    }
}