package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.block.property.enums.NetherReactorState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.MathHelper;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * This entity allows to manipulate the save state of a nether reactor core, but changing it
 * will cause no visual change. To see the changes in the world it would be necessary to
 * change the block data value to {@code 0 1 or 3} but that is impossible in the recent versions
 * because Minecraft Bedrock Edition has moved from block data to the block property {@code &} block state
 * system and did not create a block property for the old nether reactor core block, making it
 * impossible for the server to tell the client to render the red and dark versions of the block.
 */


public class BlockEntityNetherReactor extends BlockEntitySpawnable {
    private NetherReactorState reactorState;
    private int progress;

    public BlockEntityNetherReactor(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId() == BlockID.NETHERREACTOR;
    }

    public NetherReactorState getReactorState() {
        return reactorState;
    }

    public void setReactorState(NetherReactorState reactorState) {
        this.reactorState = reactorState;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = MathHelper.clamp(progress, 0, 900);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        reactorState = NetherReactorState.READY;
        if (namedTag.containsShort("Progress")) {
            progress = (short) namedTag.getShort("Progress");
        }

        if (namedTag.containsByte("HasFinished") && namedTag.getBoolean("HasFinished")) {
            reactorState = NetherReactorState.FINISHED;
        } else if (namedTag.containsByte("IsInitialized") && namedTag.getBoolean("IsInitialized")) {
            reactorState = NetherReactorState.INITIALIZED;
        } else {
            reactorState = NetherReactorState.READY;
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        NetherReactorState reactorState = getReactorState();
        namedTag.putShort("Progress", getProgress());
        namedTag.putBoolean("HasFinished", reactorState == NetherReactorState.FINISHED);
        namedTag.putBoolean("IsInitialized", reactorState == NetherReactorState.INITIALIZED);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        NetherReactorState reactorState = getReactorState();
        return super.getSpawnCompound()
                .putShort("Progress", getProgress())
                .putBoolean("HasFinished", reactorState == NetherReactorState.FINISHED)
                .putBoolean("IsInitialized", reactorState == NetherReactorState.INITIALIZED);
    }
}
