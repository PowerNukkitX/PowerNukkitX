package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.event.block.FarmLandDecayEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

public class BlockFarmland extends BlockTransparent {
    public static final BlockProperties $1 = new BlockProperties(FARMLAND, CommonBlockProperties.MOISTURIZED_AMOUNT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockFarmland() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockFarmland(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Farmland";
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
    
    public double getHardness() {
        return 0.6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxY() {
        return this.y + 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.up().isSolid()) {
                var $2 = new FarmLandDecayEvent(null, this);
                this.level.getServer().getPluginManager().callEvent(farmEvent);
                if (farmEvent.isCancelled()) return 0;

                this.level.setBlock(this, Block.get(BlockID.DIRT), false, true);

                return type;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            Vector3 $3 = new Vector3();
            if (this.level.getBlock(v.setComponents(x, this.y + 1, z)) instanceof BlockCrops) {
                return 0;
            }

            boolean $4 = false;

            if (this.level.isRaining()) {
                found = true;
            } else {
                end:
                for (int $5 = (int) this.x - 4; x <= this.x + 4; x++) {
                    for (int $6 = (int) this.z - 4; z <= this.z + 4; z++) {
                        for (int $7 = (int) this.y; y <= this.y + 1; y++) {
                            if (z == this.z && x == this.x && y == this.y) {
                                continue;
                            }

                            v.setComponents(x, y, z);
                            String $8 = this.level.getBlockIdAt(v.getFloorX(), v.getFloorY(), v.getFloorZ());

                            if (block.equals(FLOWING_WATER) || block.equals(WATER) || block.equals(FROSTED_ICE)) {
                                found = true;
                                break end;
                            } else {
                                block = this.level.getBlockIdAt(v.getFloorX(), v.getFloorY(), v.getFloorZ(), 1);
                                if (block.equals(FLOWING_WATER) || block.equals(WATER) || block.equals(FROSTED_ICE)) {
                                    found = true;
                                    break end;
                                }
                            }
                        }
                    }
                }
            }

            Block $9 = this.level.getBlock(v.setComponents(x, y - 1, z));
            if (found || block instanceof BlockFlowingWater || block instanceof BlockFrostedIce) {
                if (getMoistureAmount() < 7) {
                    setMoistureAmount(7);
                    this.level.setBlock(this, this, false, getMoistureAmount() == 0);
                }
                return Level.BLOCK_UPDATE_RANDOM;
            }

            if (getMoistureAmount() > 0) {
                this.setMoistureAmount(getMoistureAmount() - 1);
                this.level.setBlock(this, this, false, getMoistureAmount() == 1);
            } else {
                var $10 = new FarmLandDecayEvent(null, this);
                this.level.getServer().getPluginManager().callEvent(farmEvent);
                if (farmEvent.isCancelled()) return 0;
                this.level.setBlock(this, Block.get(Block.DIRT), false, true);
            }

            return Level.BLOCK_UPDATE_RANDOM;
        }

        return 0;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.DIRT));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid(BlockFace side) {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isTransparent() {
        return true;
    }
    /**
     * @deprecated 
     */
    

    public int getMoistureAmount() {
        return getPropertyValue(CommonBlockProperties.MOISTURIZED_AMOUNT);
    }
    /**
     * @deprecated 
     */
    

    public void setMoistureAmount(int value) {
        setPropertyValue(CommonBlockProperties.MOISTURIZED_AMOUNT, value);
    }
}
