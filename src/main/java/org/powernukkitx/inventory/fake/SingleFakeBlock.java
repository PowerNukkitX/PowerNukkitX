package org.powernukkitx.inventory.fake;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockChest;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityChest;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.RuntimeBlockDefinition;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.packet.BlockActorDataPacket;
import org.cloudburstmc.protocol.bedrock.packet.UpdateBlockPacket;

import java.util.HashSet;
import java.util.Set;


public class SingleFakeBlock implements FakeBlock {
    protected final Block block;
    protected final String tileId;
    protected Object2ObjectArrayMap<Player, HashSet<Vector3>> lastPositions = new Object2ObjectArrayMap<>();

    public SingleFakeBlock(String blockId) {
        this.block = Block.get(blockId);
        this.tileId = "default";
    }

    public SingleFakeBlock(String blockId, String tileId) {
        this.block = Block.get(blockId);
        this.tileId = tileId;
    }

    public SingleFakeBlock(Block block, String tileId) {
        this.block = block;
        this.tileId = tileId;
    }

    @Override
    public void create(Player player) {
        create(player, "default");
    }

    @Override
    public void create(Player player, String titleName) {
        createAndGetLastPositions(player).addAll(this.getPlacePositions(player));
        HashSet<Vector3> lastPositions = this.lastPositions.get(player);
        HashSet<Vector3> additional = new HashSet<>();
        lastPositions.forEach(position -> {

            if (this.block instanceof BlockChest && lastPositions.size() == 1) {
                if (player.getLevel().getBlock(position) instanceof BlockChest chest) {
                    BlockEntityChest blockEntity = chest.getOrCreateBlockEntity();
                    if (blockEntity.isBlockEntityValid() && blockEntity.isPaired()) {
                        Vector3 pair = blockEntity.getPair();
                        player.getLevel().sendBlocks(new Player[]{player}, new Vector3[]{BlockAir.STATE.toBlock(Position.fromObject(pair))}, Set.of(UpdateBlockPacket.Flag.NETWORK), 0);
                        additional.add(pair);
                    }
                }
            }

            final Vector3i vector3i = Vector3i.from(position.getFloorX(), position.getFloorY(), position.getFloorZ());
            final UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
            updateBlockPacket.setBlockPosition(vector3i);
            updateBlockPacket.setDefinition(new RuntimeBlockDefinition(this.block.getRuntimeId()));
            player.sendPacket(updateBlockPacket);

            final BlockActorDataPacket blockActorDataPacket = new BlockActorDataPacket();
            blockActorDataPacket.setBlockPosition(vector3i);
            blockActorDataPacket.setActorDataTags(this.getBlockEntityDataAt(position, titleName).toNetwork());

            player.sendPacket(blockActorDataPacket);
        });
        lastPositions.addAll(additional);
    }

    @Override
    public void remove(Player player) {
        Level level = player.getLevel();
        level.sendBlocks(new Player[]{player}, getLastPositions(player).stream().map(level::getBlock).toArray(Block[]::new), Set.of(UpdateBlockPacket.Flag.NETWORK), 0);
        lastPositions.remove(player);
    }

    protected CompoundTag getBlockEntityDataAt(Vector3 position, String title) {
        return BlockEntity.getDefaultCompound(position, title)
                .putBoolean("isMovable", true)
                .putString("CustomName", title);
    }

    public HashSet<Vector3> createAndGetLastPositions(Player player) {
        if (!lastPositions.containsKey(player)) lastPositions.put(player, new HashSet<>());
        return lastPositions.get(player);
    }

    public HashSet<Vector3> getLastPositions(Player player) {
        return lastPositions.getOrDefault(player, new HashSet<>());
    }
}
