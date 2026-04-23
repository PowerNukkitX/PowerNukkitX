package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityEyeOfEnderSignal;
import cn.nukkit.level.generator.populator.normal.StrongholdPopulator;
import cn.nukkit.math.ChunkVector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.types.LevelSoundEvent;
import cn.nukkit.utils.random.RandomSourceProvider;
import cn.nukkit.utils.random.Xoroshiro128;

public class ItemEnderEye extends Item {
    private static final int STRONGHOLD_SEARCH_RADIUS_CHUNKS = 100;
    private static final double EYE_SPAWN_Y_OFFSET = 0.3;

    public ItemEnderEye() {
        this(0, 1);
    }

    public ItemEnderEye(Integer meta) {
        this(meta, 1);
    }

    public ItemEnderEye(Integer meta, int count) {
        super(ENDER_EYE, meta, count, "Ender Eye");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        Vector3 target = findNearestStronghold(player, STRONGHOLD_SEARCH_RADIUS_CHUNKS);
        if (target == null) {
            return true;
        }

        CompoundTag nbt = createEyeNbt(player);

        Entity created = Entity.createEntity(Entity.EYE_OF_ENDER_SIGNAL, player.getLevel().getChunk(player.getFloorX() >> 4, player.getFloorZ() >> 4), nbt);
        if (!(created instanceof EntityEyeOfEnderSignal eye)) {
            return false;
        }

        eye.setItem(this);
        eye.signalTo(target);
        eye.spawnToAll();
        player.getLevel().addLevelSoundEvent(player, LevelSoundEvent.BOW, -1, eye.getIdentifier(), false, false);

        if (!player.isCreative()) {
            this.count--;
        }

        return true;
    }

    private Vector3 findNearestStronghold(Player player, int maxRadiusChunks) {
        var level = player.getLevel();
        RandomSourceProvider random = new Xoroshiro128(level.getSeed());
        ChunkVector2 center = new ChunkVector2(player.getFloorX() >> 4, player.getFloorZ() >> 4);
        ChunkVector2 found = StrongholdPopulator.PLACEMENT.findNearestGenerationChunk(center, random, level.getBiomePicker(), maxRadiusChunks);
        if (found == null) {
            return null;
        }

        int x = (found.getX() << 4) + 8;
        int z = (found.getZ() << 4) + 8;
        return new Vector3(x, player.y, z);
    }

    private CompoundTag createEyeNbt(Player player) {
        Vector3 spawnPos = player.add(0, player.getEyeHeight() - EYE_SPAWN_Y_OFFSET, 0);
        return Entity.getDefaultNBT(spawnPos, null, (float) player.yaw, (float) player.pitch);
    }
}
