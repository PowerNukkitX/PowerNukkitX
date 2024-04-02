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

    public static final BlockProperties PROPERTIES = new BlockProperties(CACTUS,
            AGE_16);

    public BlockCactus(BlockState state) {
        super(state);
    }

    public BlockCactus() {
        this(PROPERTIES.getDefaultState());
    }
  
    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 0.4;
    }

    @Override
    public double getResistance() {
        return 2;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public double getMinX() {
        return this.x + 0.0625;
    }

    @Override
    public double getMinY() {
        return this.y;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.0625;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.9375;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.9375;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.9375;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new SimpleAxisAlignedBB(this.x, this.y, this.z, this.x + 1, this.y + 1, this.z + 1);
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.attack(new EntityDamageByBlockEvent(this, entity, DamageCause.CONTACT, 1));
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = down();
            if (!ItemTags.getItemSet(ItemTags.SAND).contains(down.getId())
                    && !(down instanceof BlockCactus)) {
                this.getLevel().useBreakOn(this);
                return 0;
            }
            for (int side = 2; side <= 5; ++side) {
                Block block = getSide(BlockFace.fromIndex(side));
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
            for (int y = 1; y < 3; ++y) {
                Block b = this.getLevel().getBlock(new Vector3(this.x, this.y + y, this.z));
                if (!b.isAir()) { continue; }
                BlockGrowEvent event = new BlockGrowEvent(b, Block.get(BlockID.CACTUS));
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
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        Block down = this.down();
        if (!ItemTags.getItemSet(ItemTags.SAND.toString()).contains(down.getId())
                && !(down instanceof BlockCactus)) {
            return false;
        }
        Block block0 = north();
        Block block1 = south();
        Block block2 = west();
        Block block3 = east();
        if (block0.canBeFlowedInto() && block1.canBeFlowedInto() && block2.canBeFlowedInto() && block3.canBeFlowedInto()) {
            this.getLevel().setBlock(this, this, true);
            return true;
        }
        return false;
    }

    @Override
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
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    public int getAge() {
        return this.getPropertyValue(AGE_16);
    }

    public void setAge(int age) {
        this.setPropertyValue(AGE_16, age);
    }

    public static int getMaxAge() {
        return AGE_16.getMax();
    }

    public static int getMinAge() {
        return AGE_16.getMin();
    }

}
