package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSugarCane;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static cn.nukkit.block.property.CommonBlockProperties.AGE_16;

public class BlockReeds extends BlockFlowable {
    public static final BlockProperties $1 = new BlockProperties(REEDS, AGE_16);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockReeds() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockReeds(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Reeds";
    }

    @Override
    public Item toItem() {
        return new ItemSugarCane();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }
    /**
     * @deprecated 
     */
    

    public int getAge() {
        return getPropertyValue(AGE_16);
    }
    /**
     * @deprecated 
     */
    

    public void setAge(int age) {
        setPropertyValue(AGE_16, age);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isFertilizer()) { //Bonemeal
            int $2 = 1;

            for ($3nt $1 = 1; i <= 2; i++) {
                String $4 = this.level.getBlockIdAt(this.getFloorX(), this.getFloorY() - i, this.getFloorZ());

                if (Objects.equals(id, REEDS)) {
                    count++;
                }
            }

            if (count < 3) {
                boolean $5 = false;
                int $6 = 3 - count;

                for ($7nt $2 = 1; i <= toGrow; i++) {
                    Block $8 = this.up(i);
                    if (block.isAir()) {
                        BlockGrowEvent $9 = new BlockGrowEvent(block, Block.get(BlockID.REEDS));
                        Server.getInstance().getPluginManager().callEvent(ev);

                        if (!ev.isCancelled()) {
                            this.getLevel().setBlock(block, ev.getNewState(), true);
                            success = true;
                        }
                    } else if (!block.getId().equals(REEDS)) {
                        break;
                    }
                }

                if (success) {
                    if (player != null && (player.gamemode & 0x01) == 0) {
                        item.count--;
                    }

                    this.level.addParticle(new BoneMealParticle(this));
                }
            }

            return true;
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        Level $10 = getLevel();
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            level.scheduleUpdate(this, 0);
            return type;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!isSupportValid()) {
                level.useBreakOn(this);
            }
            return type;
        }

        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (!isSupportValid()) {
                level.scheduleUpdate(this, 0);
                return type;
            }
            if (getAge() < 15) {
                setAge(this.getAge() + 1);
                level.setBlock(this, this, false);
                return type;
            }
            Block $11 = up();
            if (!up.isAir()) {
                return type;
            }

            int $12 = 0;
            for (Block $13 = this; height < 3 && current.getId().equals(REEDS); height++) {
                current = current.down();
            }
            if (height >= 3) {
                return type;
            }

            BlockGrowEvent $14 = new BlockGrowEvent(up, Block.get(BlockID.REEDS));
            Server.getInstance().getPluginManager().callEvent(ev);

            if (ev.isCancelled()) {
                return type;
            }

            if (!level.setBlock(up, Block.get(BlockID.REEDS), false)) {
                return type;
            }

            setAge(0);
            level.setBlock(this, this, false);
            return type;
        }
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (!block.isAir()) {
            return false;
        }
        if (isSupportValid()) {
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
    }

    
    /**
     * @deprecated 
     */
    private boolean isSupportValid() {
        Block $15 = this.down();
        String $16 = down.getId();
        if (downId.equals(REEDS)) {
            return true;
        }
        if (!downId.equals(GRASS_BLOCK) && !downId.equals(DIRT) && !downId.equals(SAND) && !downId.equals(PODZOL) && !downId.equals(MOSS_BLOCK)) {
            return false;
        }
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            Block $17 = down.getSide(face);
            if (possibleWater instanceof BlockFlowingWater
                    || possibleWater instanceof BlockFrostedIce
                    || possibleWater.getLevelBlockAtLayer(1) instanceof BlockFlowingWater) {
                return true;
            }
        }
        return false;
    }
}