package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.CommonPropertyMap;
import cn.nukkit.block.property.enums.Orientation;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCrafter;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.recipe.MultiRecipe;
import cn.nukkit.recipe.Recipe;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BlockCrafter extends BlockSolid implements RedstoneComponent, BlockEntityHolder<BlockEntityCrafter> {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRAFTER, CommonBlockProperties.CRAFTING, CommonBlockProperties.ORIENTATION, CommonBlockProperties.TRIGGERED_BIT);

    private static final List<Location> manualOverrides = new ArrayList<>();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrafter(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityCrafter> getBlockEntityClass() {
        return BlockEntityCrafter.class;
    }

    @Override
    public @NotNull String getBlockEntityType() {
        return BlockEntity.CRAFTER;
    }

    @Override
    public String getName() {
        return "Crafter";
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getResistance() {
        return 1.5D;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Orientation orientation;
        if(player != null) {
            double pitch = player.getLookingAngleAtPitch(new Vector3(fx, fy, fz)) * (player.getPitch()/Math.abs(player.getPitch()));
            BlockFace primary = BlockFace.fromHorizontalIndex(player.getDirection().getOpposite().getHorizontalIndex());
            if(pitch > 70 || player.getPitch() > 80) primary = BlockFace.UP;
            if(pitch < -45) primary = BlockFace.DOWN;
            BlockFace secondary;
            if(primary.getAxis().isHorizontal()) {
                secondary = BlockFace.UP;
            } else {
                secondary = BlockFace.fromHorizontalIndex(player.getDirection().getOpposite().getHorizontalIndex());
            }
            orientation = Orientation.getByFaces(primary, secondary);
        } else orientation = Orientation.DOWN_EAST;
        setOrientation(orientation);
        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true) != null;
    }

    public void setOrientation(Orientation orientation) {
        setPropertyValue(CommonBlockProperties.ORIENTATION, orientation);
    }

    public boolean isCrafting() {
        return getPropertyValue(CommonBlockProperties.CRAFTING);
    }

    public void setCrafting(boolean value) {
        setPropertyValue(CommonBlockProperties.CRAFTING, value);
    }

    public boolean isTriggered() {
        return getPropertyValue(CommonBlockProperties.TRIGGERED_BIT);
    }

    public void setTriggered(boolean value) {
        setPropertyValue(CommonBlockProperties.TRIGGERED_BIT, value);
    }

    public void setManualOverride(boolean val) {
        Location location = this.getLocation();
        if (val) {
            manualOverrides.add(location);
        } else {
            manualOverrides.remove(location);
        }
    }

    public boolean getManualOverride() {
        Location location = this.getLocation();
        return manualOverrides.contains(location);
    }

    @Override
    public int onUpdate(int type) {
        if (!this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
            return 0;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if(isTriggered()) {
                getLevel().addSound(this, craft() ? Sound.CRAFTER_CRAFT : Sound.CRAFTER_FAIL);
                updateAllAroundRedstone();
                this.setTriggered(false);
                setCrafting(true);
                this.level.setBlock(this, this, false, false);
                level.scheduleUpdate(this, this, 4);
            } else if(isCrafting()) {
                setCrafting(false);
                this.level.setBlock(this, this, false, false);
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_REDSTONE) {
            if (this.isGettingPower() && !isTriggered() && !getManualOverride()) {
                this.setManualOverride(true);
                this.setTriggered(true);
                this.level.setBlock(this, this, false, false);
                level.scheduleUpdate(this, this, 4);
            }
            if(!isGettingPower()) this.setManualOverride(false);
            return type;
        }

        return 0;
    }

    public boolean craft() {
        InventoryHolder blockEntity = getBlockEntity();

        if (blockEntity == null) {
            return false;
        }

        Recipe recipe = getBlockEntity().getInventory().getRecipe();
        if(recipe == null || recipe instanceof MultiRecipe) return false;

        CraftItemEvent event = new CraftItemEvent(blockEntity, getBlockEntity().getInventory().getInput().getFlatItems(), recipe, 1);
        getLevel().getServer().getPluginManager().callEvent(event);
        if(event.isCancelled()) return false;
        
        for(Item target : recipe.getResults()) {

            LevelEventPacket pk = new LevelEventPacket();

            BlockFace facing = getBlockFace();

            pk.x = 0.5f + facing.getXOffset() * 0.7f;
            pk.y = 0.5f + facing.getYOffset() * 0.7f;
            pk.z = 0.5f + facing.getZOffset() * 0.7f;

            pk.evid = LevelEventPacket.EVENT_PARTICLE_SHOOT;
            pk.data = 7;
            this.level.addChunkPacket(getChunkX(), getChunkZ(), pk);

            Block side = this.getSide(facing);

            if (this.level.getBlockEntityIfLoaded(side) instanceof InventoryHolder) {
                InventoryHolder invHolder = (InventoryHolder) this.level.getBlockEntityIfLoaded(side);
                Inventory targetInv = invHolder.getInventory();

                if (targetInv.canAddItem(target)) {
                    targetInv.addItem(target);
                } else {
                    return false;
                }
            } else {
                this.level.addSound(this, Sound.RANDOM_CLICK);
                Vector3 dispensePos = this.getDispensePosition();

                if (facing.getAxis() == BlockFace.Axis.Y) {
                    dispensePos.y -= 0.125;
                } else {
                    dispensePos.y -= 0.15625;
                }

                Random rand = ThreadLocalRandom.current();
                Vector3 motion = new Vector3();

                double offset = rand.nextDouble() * 0.1 + 0.2;

                motion.x = facing.getXOffset() * offset;
                motion.y = 0.20000000298023224;
                motion.z = facing.getZOffset() * offset;

                motion.x += rand.nextGaussian() * 0.007499999832361937 * 6;
                motion.y += rand.nextGaussian() * 0.007499999832361937 * 6;
                motion.z += rand.nextGaussian() * 0.007499999832361937 * 6;

                this.level.dropItem(dispensePos, target, motion);
            }
        }
        Inventory inventory = blockEntity.getInventory();
        for(int i = 0; i < inventory.getSize(); i++) {
            inventory.decreaseCount(i);
        }
        return true;
    }

    public Vector3 getDispensePosition() {
        BlockFace facing = getBlockFace();
        return this.add(
                0.5 + 0.7 * facing.getXOffset(),
                0.5 + 0.7 * facing.getYOffset(),
                0.5 + 0.7 * facing.getZOffset()
        );
    }

    BlockFace getBlockFace() {
        return CommonPropertyMap.ORIENTATION_BLOCKFACE.get(getPropertyValue(CommonBlockProperties.ORIENTATION));
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (isNotActivate(player)) return false;
        BlockEntityCrafter crafter = this.getOrCreateBlockEntity();
        player.addWindow(crafter.getInventory());
        return true;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        InventoryHolder blockEntity = getBlockEntity();
        if (blockEntity != null) {
            return getBlockEntity().getInventory().getComperatorOutput();
        }
        return 0;
    }
}