package org.powernukkitx.blockentity;

import org.powernukkitx.block.Block;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.network.protocol.types.BannerPattern;
import org.powernukkitx.utils.DyeColor;

public class BlockEntityBanner extends BlockEntitySpawnable {
    public int color;

    public BlockEntityBanner(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        if (!this.nbt.contains("color")) {
            this.nbt.putByte("color", (byte) 0);
        }

        this.color = this.getNbt().getByte("color");
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId().equals(Block.WALL_BANNER) || this.getBlock().getId().equals(Block.STANDING_BANNER);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
       this.nbt.putByte("color", (byte) this.color);
    }

    @Override
    public String getName() {
        return "Banner";
    }

    public int getBaseColor() {
        return this.getNbt().getInt("Base");
    }

    public void setBaseColor(DyeColor color) {
        this.nbt.putInt("Base", color.getDyeData() & 0x0f);
    }

    public int getType() {
        return this.getNbt().getInt("Type");
    }

    public void setType(int type) {
      this.nbt.putInt("Type", type);
    }

    public void addPattern(BannerPattern pattern) {
        ListTag<CompoundTag> patterns = this.getNbt().getList("Patterns", CompoundTag.class);
        patterns.add(new CompoundTag()
                .putInt("Color", pattern.color().getDyeData() & 0x0f)
                .putString("Pattern", pattern.type().getCode()));
        this.nbt.putList("Patterns", patterns);
    }

    public BannerPattern getPattern(int index) {
        ListTag<CompoundTag> patterns = this.getNbt().getList("Patterns", CompoundTag.class);
        return BannerPattern.fromCompoundTag(patterns.size() > index && index >= 0 ?
                patterns.get(index) :
                new CompoundTag());
    }

    public void removePattern(int index) {
        ListTag<CompoundTag> patterns = this.getNbt().getList("Patterns", CompoundTag.class);
        if (patterns.size() > index && index >= 0) {
            patterns.remove(index);
        }
    }

    public int getPatternsSize() {
        return this.getNbt().getList("Patterns", CompoundTag.class).size();
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        scheduleUpdate();
    }

    @Override
    public boolean onUpdate() {
        if (!isBlockEntityValid()) {
            close();
        }
        return !closed;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .putInt("Base", getBaseColor())
                .putList("Patterns", (ListTag<CompoundTag>) this.getNbt().getList("Patterns", CompoundTag.class).copy())
                .putInt("Type", getType())
                .putByte("color", (byte) this.color);
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(color);
    }

}
