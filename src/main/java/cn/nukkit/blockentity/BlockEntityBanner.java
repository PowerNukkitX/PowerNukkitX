package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.network.protocol.types.BannerPattern;
import cn.nukkit.utils.DyeColor;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;

import java.util.List;

public class BlockEntityBanner extends BlockEntitySpawnable {
    public int color;

    public BlockEntityBanner(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        if (!this.nbt.containsKey("color")) {
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
        List<NbtMap> patterns = this.getNbt().getList("Patterns", NbtType.COMPOUND);
        patterns.add(NbtMap.builder().
                putInt("Color", pattern.color().getDyeData() & 0x0f).
                putString("Pattern", pattern.type().getCode())
                .build());
        this.nbt.putList("Patterns", NbtType.COMPOUND, patterns);
    }

    public BannerPattern getPattern(int index) {
        return BannerPattern.fromCompoundTag(this.getNbt().getList("Patterns", NbtType.COMPOUND).size() > index && index >= 0 ?
                this.getNbt().getList("Patterns", NbtType.COMPOUND).get(index) :
                NbtMap.EMPTY);
    }

    public void removePattern(int index) {
        List<NbtMap> patterns = this.getNbt().getList("Patterns", NbtType.COMPOUND);
        if (patterns.size() > index && index >= 0) {
            patterns.remove(index);
        }
    }

    public int getPatternsSize() {
        return this.getNbt().getList("Patterns", NbtType.COMPOUND).size();
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
    public NbtMap getSpawnCompound() {
        return super.getSpawnCompound().toBuilder()
                .putInt("Base", getBaseColor())
                .putList("Patterns", NbtType.COMPOUND, this.getNbt().getList("Patterns", NbtType.COMPOUND))
                .putInt("Type", getType())
                .putByte("color", (byte) this.color)
                .build();
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(color);
    }

}
