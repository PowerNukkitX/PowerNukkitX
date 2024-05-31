package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.tags.ItemTags;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static cn.nukkit.block.property.CommonBlockProperties.AGE_16;
public class BlockCactus extends BlockTransparent implements BlockFlowerPot.FlowerPotBlock {

    public static final BlockProperties $1 = new BlockProperties(CACTUS,
            AGE_16);
    /**
     * @deprecated 
     */
    

    public BlockCactus(BlockState state) {
        super(state);
    }
    /**
     * @deprecated 
     */
    

    public BlockCactus() {
        this(PROPERTIES.getDefaultState());
    }
  
    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.4;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 2;
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
    
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinX() {
        return this.x + 0.0625;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinY() {
        return this.y;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinZ() {
        return this.z + 0.0625;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxX() {
        return this.x + 0.9375;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxY() {
        return this.y + 0.9375;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxZ() {
        return this.z + 0.9375;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new SimpleAxisAlignedBB(this.x, this.y, this.z, this.x + 1, this.y + 1, this.z + 1);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onEntityCollide(Entity entity) {
        entity.attack(new EntityDamageByBlockEvent(this, entity, DamageCause.CONTACT, 1));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block $2 = down();
            if (!ItemTags.getItemSet(ItemTags.SAND).contains(down.getId())
                    && !(down instanceof BlockCactus)) {
                this.getLevel().useBreakOn(this);
                return 0;
            }
            for (int $3 = 2; side <= 5; ++side) {
                Block $4 = getSide(BlockFace.fromIndex(side));
                if (!block.canBeFlowedInto()) {
                    this.getLevel().useBreakOn(this);
                }
            }
            return 0;
        }
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (down() instanceof BlockCactus) { return 0; }
            if (this.getAge() < getMaxAge()) {
                this.setAge(this.getAge() + 1);
                this.getLevel().setBlock(this, this);
                return 0;
            }
            for (int $5 = 1; y < 3; ++y) {
                Block $6 = this.getLevel().getBlock(new Vector3(this.x, this.y + y, this.z));
                if (!b.isAir()) { continue; }
                BlockGrowEvent $7 = new BlockGrowEvent(b, Block.get(BlockID.CACTUS));
                Server.getInstance().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    this.getLevel().setBlock(b, event.getNewState(), true);
                }
                break;
            }
            this.setAge(getMinAge());
        }

        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        Block $8 = this.down();
        if (!ItemTags.getItemSet(ItemTags.SAND.toString()).contains(down.getId())
                && !(down instanceof BlockCactus)) {
            return false;
        }
        Block $9 = north();
        Block $10 = south();
        Block $11 = west();
        Block $12 = east();
        if (block0.canBeFlowedInto() && block1.canBeFlowedInto() && block2.canBeFlowedInto() && block3.canBeFlowedInto()) {
            this.getLevel().setBlock(this, this, true);
            return true;
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Cactus";
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(BlockID.CACTUS, 0, 1)
        };
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean sticksToPiston() {
        return false;
    }
    /**
     * @deprecated 
     */
    

    public int getAge() {
        return this.getPropertyValue(AGE_16);
    }
    /**
     * @deprecated 
     */
    

    public void setAge(int age) {
        this.setPropertyValue(AGE_16, age);
    }
    /**
     * @deprecated 
     */
    

    public static int getMaxAge() {
        return AGE_16.getMax();
    }
    /**
     * @deprecated 
     */
    

    public static int getMinAge() {
        return AGE_16.getMin();
    }

}
