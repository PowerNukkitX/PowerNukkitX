package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.*;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlockBigDripleaf extends BlockFlowable implements Faceable {

    public AtomicBoolean tilting = new AtomicBoolean(false);
    public AtomicBoolean recovering = new AtomicBoolean(false);

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
        if (block.getSide(BlockFace.DOWN) instanceof BlockBigDripleaf) {
            BlockBigDripleaf blockDown = (BlockBigDripleaf) this.level.getBlock(block.getSide(BlockFace.DOWN));
            blockDown.setHead(false);
            blockDown.recovering.set(false);
            blockDown.tilting.set(false);
            blockBigDripleafTop.setBlockFace(((BlockBigDripleaf) block.getSide(BlockFace.DOWN)).getBlockFace());
            this.level.setBlock(blockDown,blockDown,true,true);
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
        if (this.isGettingPower()) {
            this.tilting.set(false);
            this.recovering.set(false);
            this.setTilt(Tilt.NONE);
            this.level.setBlock(this,this,true,true);
            return 0;
        }
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
                if (!tilting.get() && this.getTilt() == Tilt.NONE){
                    tilt();
                }
            }else {
                if (!recovering.get() && this.getTilt() != Tilt.NONE) {
                    recovery();
                }
            }
        }
        return super.onUpdate(type);
    }

    private void tilt(){
        tilting.set(true);
        Server.getInstance().getScheduler().scheduleDelayedTask(() -> {
            if(!tilting.get())
                return;
            if (isBlockChanged())
                return;
            this.setTilt(Tilt.PARTIAL_TILT);
            this.level.setBlock(this,this,true,true);
        }, 15);
        Server.getInstance().getScheduler().scheduleDelayedTask(() -> {
            if(!tilting.get())
                return;
            if (isBlockChanged()){
                tilting.set(false);
                return;
            }
            this.setTilt(Tilt.FULL_TILT);
            this.level.setBlock(this,this,true,true);
            tilting.set(false);
            this.onUpdate(Level.BLOCK_UPDATE_NORMAL);
            this.level.scheduleUpdate(this,20);//make sure that the block is updated
        }, 30);
    }

    private void recovery(){
        recovering.set(true);
        Server.getInstance().getScheduler().scheduleDelayedTask(() -> {
            if(!recovering.get())
                return;
            if (isBlockChanged()) {
                recovering.set(false);
                return;
            }
            this.setTilt(Tilt.NONE);
            this.level.setBlock(this,this,true,true);
            recovering.set(false);
        },100);
    }

    public boolean isBlockChanged() {
        return !(this.getLevelBlock() instanceof BlockBigDripleaf) || !(((BlockBigDripleaf) this.getLevelBlock()).getTilt() == this.getTilt());
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
        if(block instanceof BlockBigDripleaf blockBigDripleaf) {
            blockBigDripleaf.recovering.set(false);
            blockBigDripleaf.tilting.set(false);
        }

        int maxHeightIncreased = 0;
        Block blockUp = block.getBlock();
        for(int i = 1;i<=heightIncreased;i++){
            if ((blockUp = blockUp.getSide(BlockFace.UP)) instanceof BlockAir && blockUp.getY() < 320)
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

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean onProjectileHit(@Nonnull Entity projectile, @Nonnull Position position, @Nonnull Vector3 motion) {
        this.setTilt(Tilt.FULL_TILT);
        this.level.setBlock(this,this,true,true);
        return true;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public boolean canPassThrough() {
        return !this.isHead();
    }

    @Override
    public double getMinY() {
        return this.y + 0.9;
    }

    public enum Tilt{
        NONE,
        PARTIAL_TILT,
        FULL_TILT,
        UNSTABLE
    }
}
