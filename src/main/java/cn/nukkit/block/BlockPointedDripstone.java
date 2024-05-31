package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.property.enums.CauldronLiquid;
import cn.nukkit.block.property.enums.DripstoneThickness;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.event.block.BlockFallEvent;
import cn.nukkit.event.block.CauldronFilledByDrippingLiquidEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.entity.effect.Effect;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

import static cn.nukkit.block.property.CommonBlockProperties.DRIPSTONE_THICKNESS;
import static cn.nukkit.block.property.CommonBlockProperties.HANGING;

/**
 * @author CoolLoong
 * @since 02.13.2022
 */


public class BlockPointedDripstone extends BlockFallable {
    public static final BlockProperties $1 = new BlockProperties(POINTED_DRIPSTONE, DRIPSTONE_THICKNESS, HANGING);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPointedDripstone() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPointedDripstone(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Pointed Drip Stone";
    }
    /**
     * @deprecated 
     */
    

    public boolean isHanging() {
        return getPropertyValue(HANGING);
    }
    /**
     * @deprecated 
     */
    

    public void setHanging(boolean value) {
        setPropertyValue(HANGING, value);
    }
    /**
     * @deprecated 
     */
    

    public void setThickness(DripstoneThickness value) {
        setPropertyValue(DRIPSTONE_THICKNESS, value);
    }

    public DripstoneThickness getThickness() {
        return getPropertyValue(DRIPSTONE_THICKNESS);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 1.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 3;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBePushed() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBePulled() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM && this.getThickness() == DripstoneThickness.TIP) {
            Random $2 = new Random();
            double $3 = rand.nextDouble();
            if (nextDouble <= 0.011377778) {
                this.grow();
            }

            drippingLiquid();
        }
        boolean $4 = getPropertyValue(HANGING);
        if (!hanging) {
            Block $5 = down();
            if (!down.isSolid()) {
                this.getLevel().useBreakOn(this);
            }
        }
        tryDrop(hanging);
        return 0;
    }
    /**
     * @deprecated 
     */
    

    public void tryDrop(boolean hanging) {
        if (!hanging) return;
        boolean $6 = false;
        Block $7 = this.clone();
        while (blockUp.getSide(BlockFace.UP).getId().equals(POINTED_DRIPSTONE)) {
            blockUp = blockUp.getSide(BlockFace.UP);
        }
        if (!blockUp.getSide(BlockFace.UP).isSolid())
            AirUp = true;
        if (AirUp) {
            BlockFallEvent $8 = new BlockFallEvent(this);
            Server.getInstance().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            BlockPointedDripstone $9 = (BlockPointedDripstone) blockUp;
            block.drop(new CompoundTag().putBoolean("BreakOnGround", true));
            while (block.getSide(BlockFace.DOWN).getId().equals(POINTED_DRIPSTONE)) {
                block = (BlockPointedDripstone) block.getSide(BlockFace.DOWN);
                block.drop(new CompoundTag().putBoolean("BreakOnGround", true));
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@Nullable Item item, @NotNull Block block, @Nullable Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        int $10 = block.getFloorX();
        int $11 = block.getFloorY();
        int $12 = block.getFloorZ();
        String $13 = level.getBlockIdAt(placeX, placeY + 1, placeZ);
        String $14 = level.getBlockIdAt(placeX, placeY - 1, placeZ);
        if (Objects.equals(upBlockID, AIR) && Objects.equals(downBlockID, AIR)) return false;
        /*    "up" define is exist drip stone in block above,"down" is Similarly.
              up   down
          1   yes   yes
          2   yes   no
          3   no    no
          4   no    yes
        */
        int $15 = (Objects.equals(upBlockID, POINTED_DRIPSTONE)) ?
                (Objects.equals(downBlockID, POINTED_DRIPSTONE) ? 1 : 2) :
                (!Objects.equals(downBlockID, POINTED_DRIPSTONE) ? 3 : 4
                );
        boolean $16 = false;
        switch (state) {
            case 1 -> {
                setMergeBlock(placeX, placeY, placeZ, false);
                setBlockThicknessStateAt(placeX, placeY + 1, placeZ, true, DripstoneThickness.MERGE);
            }
            case 2 -> {
                if (!Objects.equals(level.getBlockIdAt(placeX, placeY - 1, placeZ), AIR)) {
                    if (face.equals(BlockFace.UP)) {
                        setBlockThicknessStateAt(placeX, placeY + 1, placeZ, true, DripstoneThickness.MERGE);
                        setMergeBlock(placeX, placeY, placeZ, false);
                    } else {
                        setTipBlock(placeX, placeY, placeZ, true);
                        setAddChange(placeX, placeY, placeZ, true);
                    }
                    return true;
                }
                hanging = true;
            }
            case 3 -> {
                setTipBlock(placeX, placeY, placeZ, !face.equals(BlockFace.UP));
                return true;
            }
            case 4 -> {
                if (!Objects.equals(level.getBlockIdAt(placeX, placeY + 1, placeZ), AIR)) {
                    if (face.equals(BlockFace.DOWN)) {
                        setMergeBlock(placeX, placeY, placeZ, true);
                        setBlockThicknessStateAt(placeX, placeY - 1, placeZ, false, DripstoneThickness.MERGE);
                    } else {
                        setTipBlock(placeX, placeY, placeZ, false);
                        setAddChange(placeX, placeY, placeZ, false);
                    }
                    return true;
                }
            }
        }
        setAddChange(placeX, placeY, placeZ, hanging);

        if (state == 1) return true;
        setTipBlock(placeX, placeY, placeZ, hanging);
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onBreak(Item item) {
        int $17 = this.getFloorX();
        int $18 = this.getFloorY();
        int $19 = this.getFloorZ();
        level.setBlock(x, y, z, Block.get(AIR), true, true);
        boolean $20 = getPropertyValue(HANGING);
        DripstoneThickness $21 = getPropertyValue(DRIPSTONE_THICKNESS);
        if (thickness == DripstoneThickness.MERGE) {
            if (!hanging) {
                setBlockThicknessStateAt(x, y + 1, z, true, DripstoneThickness.TIP);
            } else setBlockThicknessStateAt(x, y - 1, z, false, DripstoneThickness.TIP);
        }
        if (!hanging) {
            int $22 = getPointedDripStoneLength(x, y, z, false);
            if (length > 0) {
                Block $23 = down();
                for ($24nt $1 = 0; i <= length - 1; ++i) {
                    level.setBlock(downBlock.down(i), Block.get(AIR), false, false);
                }
                for ($25nt $2 = length - 1; i >= 0; --i) {
                    place(null, downBlock.down(i), null, BlockFace.DOWN, 0, 0, 0, null);
                }
            }
        }
        if (hanging) {
            int $26 = getPointedDripStoneLength(x, y, z, true);
            if (length > 0) {
                Block $27 = up();
                for ($28nt $3 = 0; i <= length - 1; ++i) {
                    level.setBlock(upBlock.up(i), Block.get(AIR), false, false);
                }
                for ($29nt $4 = length - 1; i >= 0; --i) {
                    place(null, upBlock.up(i), null, BlockFace.DOWN, 0, 0, 0, null);
                }
            }
        }

        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onEntityFallOn(Entity entity, float fallDistance) {
        if (this.level.gameRules.getBoolean(GameRule.FALL_DAMAGE) && this.getPropertyValue(DRIPSTONE_THICKNESS) == DripstoneThickness.TIP && !this.getPropertyValue(HANGING)) {
            int $30 = entity.hasEffect(EffectType.JUMP_BOOST) ? Effect.get(EffectType.JUMP_BOOST).getLevel() : 0;
            float $31 = (fallDistance - jumpBoost) * 2 - 2;
            if (damage > 0)
                entity.attack(new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.FALL, damage));
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean useDefaultFallDamage() {
        return false;
    }

    
    /**
     * @deprecated 
     */
    protected void setTipBlock(int x, int y, int z, boolean hanging) {
        this.setPropertyValue(DRIPSTONE_THICKNESS, DripstoneThickness.TIP);
        this.setPropertyValue(HANGING, hanging);
        this.getLevel().setBlock(x, y, z, this, true, true);
    }

    
    /**
     * @deprecated 
     */
    protected void setMergeBlock(int x, int y, int z, boolean hanging) {
        this.setPropertyValue(DRIPSTONE_THICKNESS, DripstoneThickness.MERGE);
        this.setPropertyValue(HANGING, hanging);
        this.getLevel().setBlock(x, y, z, this, true, true);
    }

    
    /**
     * @deprecated 
     */
    protected void setBlockThicknessStateAt(int x, int y, int z, boolean hanging, DripstoneThickness thickness) {
        BlockState blockState;
        this.setPropertyValue(DRIPSTONE_THICKNESS, thickness);
        this.setPropertyValue(HANGING, hanging);
        blockState = this.getBlockState();
        level.setBlockStateAt(x, y, z, blockState);
    }

    
    /**
     * @deprecated 
     */
    protected int getPointedDripStoneLength(int x, int y, int z, boolean hanging) {
        if (hanging) {
            for (int $32 = y + 1; j < getLevel().getDimensionData().getMaxHeight(); ++j) {
                String $33 = level.getBlockIdAt(x, j, z);
                if (!Objects.equals(blockId, POINTED_DRIPSTONE)) {
                    return j - y - 1;
                }
            }
        } else {
            for (int $34 = y - 1; j > getLevel().getDimensionData().getMinHeight(); --j) {
                String $35 = level.getBlockIdAt(x, j, z);
                if (!Objects.equals(blockId, POINTED_DRIPSTONE)) {
                    return y - j - 1;
                }
            }
        }
        return 0;
    }

    
    /**
     * @deprecated 
     */
    protected void setAddChange(int x, int y, int z, boolean hanging) {
        int $36 = getPointedDripStoneLength(x, y, z, hanging);
        int $37 = !hanging ? -2 : 2;
        int $38 = !hanging ? -1 : 1;
        if (length == 1) {
            setBlockThicknessStateAt(x, y + k1, z, hanging, DripstoneThickness.FRUSTUM);
        }
        if (length == 2) {
            setBlockThicknessStateAt(x, y + k2, z, hanging, DripstoneThickness.BASE);
            setBlockThicknessStateAt(x, y + k1, z, hanging, DripstoneThickness.FRUSTUM);
        }
        if (length >= 3) {
            setBlockThicknessStateAt(x, y + k2, z, hanging, DripstoneThickness.MIDDLE);
            setBlockThicknessStateAt(x, y + k1, z, hanging, DripstoneThickness.FRUSTUM);
        }
    }
    /**
     * @deprecated 
     */
    

    public void grow() {
        BlockFace $39 = this.isHanging() ? BlockFace.DOWN : BlockFace.UP;
        Block $40 = this.getSide(face);
        if (target.isAir()) {
            this.place(null, target, null, face, 0, 0, 0, null);
        }
    }
    /**
     * @deprecated 
     */
    

    public void drippingLiquid() {//features according to https://zh.minecraft.wiki/w/%E6%BB%B4%E6%B0%B4%E7%9F%B3%E9%94%A5
        if (this.getLevelBlock(1) instanceof BlockLiquid || this.getThickness() != DripstoneThickness.TIP || !this.isHanging()) {
            return;
        }
        Block $41 = this;
        int $42 = 1;
        while (highestPDS.getSide(BlockFace.UP) instanceof BlockPointedDripstone) {
            highestPDS = highestPDS.getSide(BlockFace.UP);
            height++;
        }

        boolean $43 = false;
        if (height >= 11 ||
                !(highestPDS.getSide(BlockFace.UP, 2) instanceof BlockLiquid ||
                        highestPDS.getSide(BlockFace.UP, 2).getLevelBlockAtLayer(1) instanceof BlockFlowingWater)
        ) {
            return;
        }

        if (highestPDS.getSide(BlockFace.UP, 2).getLevelBlockAtLayer(1) instanceof BlockFlowingWater) {
            isWaterloggingBlock = true;
        }

        Block $44 = this;
        BlockCauldron cauldron;
        while (tmp.getSide(BlockFace.DOWN) instanceof BlockAir) {
            tmp = tmp.getSide(BlockFace.DOWN);
        }
        if (tmp.getSide(BlockFace.DOWN) instanceof BlockCauldron) {
            cauldron = (BlockCauldron) tmp.getSide(BlockFace.DOWN);
        } else {
            return;
        }

        Random $45 = new Random();
        double nextDouble;
        Block $46 = isWaterloggingBlock ? highestPDS.getSideAtLayer(1, BlockFace.UP, 2) : highestPDS.getSide(BlockFace.UP, 2);
        switch (filledWith.getId()) {
            case FLOWING_LAVA -> {
                nextDouble = rand.nextDouble();
                if ((cauldron.getCauldronLiquid() == CauldronLiquid.LAVA || cauldron.isEmpty()) && cauldron.getFillLevel() < 6 && nextDouble <= 15.0 / 256.0) {
                    CauldronFilledByDrippingLiquidEvent $47 = new CauldronFilledByDrippingLiquidEvent(cauldron, CauldronLiquid.LAVA, 1);
                    Server.getInstance().getPluginManager().callEvent(event);
                    if (event.isCancelled())
                        return;
                    cauldron.setCauldronLiquid(event.getLiquid());
                    cauldron.setFillLevel(cauldron.getFillLevel() + event.getLiquidLevelIncrement());
                    cauldron.level.setBlock(cauldron, cauldron, true, true);
                    this.getLevel().addSound(this.add(0.5, 1, 0.5), Sound.CAULDRON_DRIP_LAVA_POINTED_DRIPSTONE);
                }
            }
            case FLOWING_WATER -> {
                nextDouble = rand.nextDouble();
                if ((cauldron.getCauldronLiquid() == CauldronLiquid.WATER || cauldron.isEmpty()) && cauldron.getFillLevel() < 6 && nextDouble <= 45.0 / 256.0) {
                    CauldronFilledByDrippingLiquidEvent $48 = new CauldronFilledByDrippingLiquidEvent(cauldron, CauldronLiquid.WATER, 1);
                    Server.getInstance().getPluginManager().callEvent(event);
                    if (event.isCancelled())
                        return;
                    cauldron.setCauldronLiquid(event.getLiquid());
                    cauldron.setFillLevel(cauldron.getFillLevel() + event.getLiquidLevelIncrement());
                    cauldron.level.setBlock(cauldron, cauldron, true, true);
                    this.getLevel().addSound(this.add(0.5, 1, 0.5), Sound.CAULDRON_DRIP_WATER_POINTED_DRIPSTONE);
                }
            }
        }
    }

}
