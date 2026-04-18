package cn.nukkit.blockentity;

import cn.nukkit.block.copper.golem.AbstractBlockCopperGolemStatue;
import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;

/**
 * @author Buddelbubi
 * @see <a href="https://github.com/GeyserMC/Geyser/blob/master/core/src/main/java/org/geysermc/geyser/translator/level/block/entity/CopperBlockEntityTranslator.java#L35">NBT Info</a>
 */
public class BlockEntityCopperGolemStatue extends BlockEntitySpawnable {

    public BlockEntityCopperGolemStatue(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        if (this.namedTag.containsKey("Pose", NbtType.INT)) {
            this.setPose(CopperPose.STANDING);
        }
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock() instanceof AbstractBlockCopperGolemStatue;
    }

    @Override
    public NbtMap getSpawnCompound() {
        return super.getSpawnCompound().toBuilder()
                .putBoolean("isMovable", false)
                .putInt("Pose", this.namedTag.getInt("Pose")
                ).build();
    }

    public void setPose(CopperPose pose) {
        this.namedTag = this.namedTag.toBuilder().putInt("Pose", pose.ordinal()).build();
    }

    public CopperPose getPose() {
        return CopperPose.values()[this.namedTag.getInt("Pose")];
    }

    public enum CopperPose {
        STANDING,
        SITTING,
        RUNNING,
        STAR
    }

}
