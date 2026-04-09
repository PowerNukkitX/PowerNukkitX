package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.packet.BlockActorDataPacket;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockEntitySpawnable extends BlockEntity {

    public BlockEntitySpawnable(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        this.spawnToAll();
    }

    public NbtMap getSpawnCompound() {
        return NbtMap.builder()
                .putString("id", namedTag.getString("id"))
                .putInt("x", getFloorX())
                .putInt("y", getFloorY())
                .putInt("z", getFloorZ())
                .build();
    }

    public void spawnTo(Player player) {
        if (this.closed) {
            return;
        }

        player.dataPacket(getSpawnPacket());
    }

    public BlockActorDataPacket getSpawnPacket() {
        return getSpawnPacket(null);
    }

    public BlockActorDataPacket getSpawnPacket(NbtMap nbt) {
        if (nbt == null) {
            nbt = this.getSpawnCompound();
        }

        final BlockActorDataPacket packet = new BlockActorDataPacket();
        packet.setBlockPosition(Vector3i.from(this.getFloorX(), this.getFloorY(), this.getFloorZ()));
        packet.setActorDataTags(nbt);

        return packet;
    }

    public void spawnToAll() {
        if (this.closed) {
            return;
        }

        for (Player player : this.getLevel().getChunkPlayers(this.chunk.getX(), this.chunk.getZ()).values()) {
            if (player.spawned) {
                this.spawnTo(player);
            }
        }
    }

    /**
     * Called when a player updates a block entity's NBT data
     * for example when writing on a sign.
     *
     * @param nbt    tag
     * @param player player
     * @return bool indication of success, will respawn the tile to the player if false.
     */
    public boolean updateCompoundTag(NbtMap nbt, Player player) {
        return false;
    }
}
