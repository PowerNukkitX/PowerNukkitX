package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlockBigDripleaf extends BlockFlowable implements Faceable {

    public Boolean tilting = false;
    public Boolean recovering = false;

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    public static final BlockProperty<Tilt> TILT = new ArrayBlockProperty<>("big_dripleaf_tilt",false,new Tilt[]{Tilt.NONE, Tilt.PARTIAL_TILT, Tilt.FULL_TILT,Tilt.UNSTABLE});

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    public static final BooleanBlockProperty HEAD = new BooleanBlockProperty("big_dripleaf_head", false);

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    public static final BlockProperties PROPERTIES = new BlockProperties(CommonBlockProperties.DIRECTION,TILT,HEAD);

    protected BlockBigDripleaf() {
        super(0);
    }

    @Override
    public String getName() {
        return "Big Dripleaf";
    }

    @Override
    public int getId() {
        return BlockID.BIG_DRIPLEAF;
    }

    @Since("1.6.0.0-PNX")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(CommonBlockProperties.DIRECTION);
    }

    @Since("1.6.0.0-PNX")
    @PowerNukkitOnly
    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.DIRECTION, face);
    }

    public boolean isHead(){
        return this.getBooleanValue(HEAD);
    }

    public void setHead(boolean isHead){
        this.setBooleanValue(HEAD,isHead);
    }

    public Tilt getTilt(){
        return this.getPropertyValue(TILT);
    }

    public void setTilt(Tilt tilt){
        this.setPropertyValue(TILT,tilt);
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_NONE;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        BlockBigDripleaf blockBigDripleafTop = new BlockBigDripleaf();
        blockBigDripleafTop.setHead(true);
        blockBigDripleafTop.setBlockFace(player.getDirection().getOpposite());
        if(canKeepAlive(block)){
            this.level.setBlock(block,blockBigDripleafTop,true,true);
            return true;
        }
        return false;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        if (item.isFertilizer()) {
            boolean state = this.grow(this,1);
            if(state) {
                this.level.addParticleEffect(this.add(0.5, 0.5, 0.5), ParticleEffect.CROP_GROWTH);
                item.count--;
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean onBreak(@Nonnull Item item) {
        this.level.setBlock(this, new BlockAir(), true, true);
        this.level.dropItem(this, this.toItem());
        if(this.getSide(BlockFace.UP).getId() == BlockID.BIG_DRIPLEAF){
            this.level.getBlock(this.getSide(BlockFace.UP)).onBreak(null);
        }
        if(this.getSide(BlockFace.DOWN).getId() == BlockID.BIG_DRIPLEAF){
            this.level.getBlock(this.getSide(BlockFace.DOWN)).onBreak(null);
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (!canKeepAlive(this)) {
            this.level.setBlock(this, new BlockAir(), true, true);
            this.level.dropItem(this, this.toItem());
        }
        if (this.isHead()) {
            AtomicBoolean hasEntityOn = new AtomicBoolean(false);
            this.getChunk().getEntities().values().stream().parallel().forEach(entity -> {
                if (entity.asBlockVector3().equals(this.asBlockVector3()))
                    hasEntityOn.set(true);
            });
            if (hasEntityOn.get()){
                if (!tilting && this.getTilt() == Tilt.NONE){
                    tilting = true;
                    Server.getInstance().getScheduler().scheduleDelayedTask(() -> {
                        if (!(this.getBlock() instanceof BlockBigDripleaf) || !(((BlockBigDripleaf)this.getBlock()).getTilt() == this.getTilt()))
                            return;
                        this.setTilt(Tilt.PARTIAL_TILT);
                        this.level.setBlockStateAt(this.getFloorX(),this.getFloorY(),this.getFloorZ(),this.getCurrentState());
                    }, 15);
                    Server.getInstance().getScheduler().scheduleDelayedTask(() -> {
                        if (!(this.getBlock() instanceof BlockBigDripleaf) || !(((BlockBigDripleaf)this.getBlock()).getTilt() == this.getTilt())) {
                            tilting = false;
                            return;
                        }
                        this.setTilt(Tilt.FULL_TILT);
                        this.level.setBlockStateAt(this.getFloorX(),this.getFloorY(),this.getFloorZ(),this.getCurrentState());
                        tilting = false;
                        this.onUpdate(Level.BLOCK_UPDATE_NORMAL);
                    }, 30);
                }
            }else {
                if (!recovering && this.getTilt() != Tilt.NONE) {
                    recovering = true;
                    Server.getInstance().getScheduler().scheduleDelayedTask(() -> {
                        if (!(this.getBlock() instanceof BlockBigDripleaf) || !(((BlockBigDripleaf)this.getBlock()).getTilt() == this.getTilt())) {
                            recovering = false;
                            return;
                        }
                        this.setTilt(Tilt.NONE);
                        this.level.setBlockStateAt(this.getFloorX(),this.getFloorY(),this.getFloorZ(),this.getCurrentState());
                        recovering = false;
                    },100);
                }
            }
        }
        return super.onUpdate(type);
    }

    public boolean canKeepAlive(Position pos){
        Block blockDown = this.level.getBlock(pos.getSide(BlockFace.DOWN));
        if (this.level.getBlock(blockDown) instanceof BlockBigDripleaf && !((BlockBigDripleaf)this.level.getBlock(blockDown)).isHead()){
            return true;
        }
        if (blockDown instanceof BlockDirt || blockDown instanceof BlockDirtWithRoots || blockDown instanceof BlockMoss || blockDown instanceof BlockClay){
            return true;
        }
        return false;
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    public boolean grow(Position pos,int heightIncreased){
        Block block = pos.getLevelBlock();
        if (block instanceof BlockBigDripleaf) {
            while (block.getSide(BlockFace.UP) instanceof BlockBigDripleaf) {
                block = block.getSide(BlockFace.UP);
            }
        }

        int maxHeightIncreased = 0;
        Block blockUp = block.getBlock();
        for(int i = 1;i<=heightIncreased;i++){
            if ((blockUp = blockUp.getSide(BlockFace.UP)) instanceof BlockAir)
                maxHeightIncreased++;
        }
        BlockBigDripleaf blockBigDripleafDown = new BlockBigDripleaf();
        BlockBigDripleaf blockBigDripleafHead = new BlockBigDripleaf();
        blockBigDripleafDown.setBlockFace(((BlockBigDripleaf)block).getBlockFace());
        blockBigDripleafHead.setBlockFace(((BlockBigDripleaf)block).getBlockFace());
        blockBigDripleafHead.setHead(true);
        for(int height = 0;height<maxHeightIncreased;height++){
            this.level.setBlock(block.add(0,height,0),blockBigDripleafDown,true,true);
        }
        this.level.setBlock(block.add(0,maxHeightIncreased,0),blockBigDripleafHead,true,true);
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{new BlockSmallDripleaf().toItem()};
    }

    public enum Tilt{
        NONE,
        PARTIAL_TILT,
        FULL_TILT,
        UNSTABLE
    }
}
