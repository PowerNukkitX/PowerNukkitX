package cn.nukkit.inventory.fake;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockChest;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.MinecraftCardinalDirection;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.packet.UpdateBlockPacket;
import org.cloudburstmc.protocol.bedrock.data.structure.StructureRotation;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.Faceable;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.HashMap;
import java.util.HashSet;


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

            if(this.block instanceof BlockChest && lastPositions.size() == 1) {
                if(player.getLevel().getBlock(position) instanceof BlockChest chest) {
                    BlockEntityChest blockEntity = chest.getOrCreateBlockEntity();
                    if(blockEntity.isBlockEntityValid() && blockEntity.isPaired()) {
                        Vector3 pair = blockEntity.getPair();
                        player.getLevel().sendBlocks(new Player[]{player}, new Vector3[]{BlockAir.STATE.toBlock(Position.fromObject(pair))}, 2, 0);
                        additional.add(pair);
                    }
                }
            }

            UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
            updateBlockPacket.setDefinition(() -> block.getRuntimeId());
            updateBlockPacket.getFlags().add(UpdateBlockPacket.Flag.NETWORK);
            updateBlockPacket.setDataLayer(0);
            updateBlockPacket.setBlockPosition(Vector3i.from(position.getFloorX(), position.getFloorY(), position.getFloorZ()));
            player.dataPacket(updateBlockPacket);
        });
        lastPositions.addAll(additional);
    }

    @Override
    public void remove(Player player) {
        Level level = player.getLevel();
        level.sendBlocks(new Player[]{player}, getLastPositions(player).stream().map(level::getBlock).toArray(Block[]::new), 2, 0);
        lastPositions.remove(player);
    }

    protected CompoundTag getBlockEntityDataAt(Vector3 position, String title) {
        return BlockEntity.getDefaultCompound(position, title)
                .putBoolean("isMovable", true)
                .putString("CustomName", title);
    }

    public HashSet<Vector3> createAndGetLastPositions(Player player) {
        if(!lastPositions.containsKey(player)) lastPositions.put(player, new HashSet<>());
        return lastPositions.get(player);
    }

    public HashSet<Vector3> getLastPositions(Player player) {
        return lastPositions.getOrDefault(player, new HashSet<>());
    }
}
