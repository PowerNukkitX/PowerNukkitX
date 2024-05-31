package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.types.BannerPattern;
import cn.nukkit.utils.DyeColor;

public class BlockEntityBanner extends BlockEntitySpawnable {
    public int color;
    /**
     * @deprecated 
     */
    

    public BlockEntityBanner(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void loadNBT() {
        super.loadNBT();
        if (!this.namedTag.contains("color")) {
            this.namedTag.putByte("color", 0);
        }

        this.color = this.namedTag.getByte("color");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        return this.getBlock().getId().equals(Block.WALL_BANNER) || this.getBlock().getId().equals(Block.STANDING_BANNER);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putByte("color", this.color);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Banner";
    }
    /**
     * @deprecated 
     */
    

    public int getBaseColor() {
        return this.namedTag.getInt("Base");
    }
    /**
     * @deprecated 
     */
    

    public void setBaseColor(DyeColor color) {
        this.namedTag.putInt("Base", color.getDyeData() & 0x0f);
    }
    /**
     * @deprecated 
     */
    

    public int getType() {
        return this.namedTag.getInt("Type");
    }
    /**
     * @deprecated 
     */
    

    public void setType(int type) {
        this.namedTag.putInt("Type", type);
    }
    /**
     * @deprecated 
     */
    

    public void addPattern(BannerPattern pattern) {
        ListTag<CompoundTag> patterns = this.namedTag.getList("Patterns", CompoundTag.class);
        patterns.add(new CompoundTag().
                putInt("Color", pattern.color().getDyeData() & 0x0f).
                putString("Pattern", pattern.type().getCode()));
        this.namedTag.putList("Patterns", patterns);
    }

    public BannerPattern getPattern(int index) {
        return BannerPattern.fromCompoundTag(this.namedTag.getList("Patterns").size() > index && index >= 0 ?
                this.namedTag.getList("Patterns", CompoundTag.class).get(index) :
                new CompoundTag());
    }
    /**
     * @deprecated 
     */
    

    public void removePattern(int index) {
        ListTag<CompoundTag> patterns = this.namedTag.getList("Patterns", CompoundTag.class);
        if (patterns.size() > index && index >= 0) {
            patterns.remove(index);
        }
    }
    /**
     * @deprecated 
     */
    

    public int getPatternsSize() {
        return this.namedTag.getList("Patterns").size();
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .putInt("Base", getBaseColor())
                .putList("Patterns", this.namedTag.getList("Patterns"))
                .putInt("Type", getType())
                .putByte("color", this.color);
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(color);
    }

}
