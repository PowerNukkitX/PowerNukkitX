package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityEyeOfEnderSignal;
import cn.nukkit.level.generator.populator.normal.StrongholdPopulator;
import cn.nukkit.math.ChunkVector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.types.LevelSoundEvent;
import cn.nukkit.utils.random.RandomSourceProvider;
import cn.nukkit.utils.random.Xoroshiro128;

public class ItemEnderEye extends ProjectileItem {
    private static final int STRONGHOLD_SEARCH_RADIUS_CHUNKS = 100;
    private Vector3 pendingTarget;

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
    public String getProjectileEntityType() {
        return Entity.EYE_OF_ENDER_SIGNAL;
    }

    @Override
    public float getThrowForce() {
        return 0f;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        Vector3 target = findNearestStronghold(player, STRONGHOLD_SEARCH_RADIUS_CHUNKS);
        if (target == null) {
            return true;
        }

        this.pendingTarget = target;
        try {
            return super.onClickAir(player, directionVector);
        } catch (IllegalArgumentException e) {
            return false;
        } finally {
            this.pendingTarget = null;
        }
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

    @Override
    protected Entity correctProjectile(Player player, Entity projectile) {
        if (!(projectile instanceof EntityEyeOfEnderSignal eye) || this.pendingTarget == null) {
            return null;
        }
        eye.signalTo(this.pendingTarget);
        return eye;
    }

    @Override
    protected void addThrowSound(Player player) {
        player.getLevel().addLevelSoundEvent(player, LevelSoundEvent.BOW, -1, "minecraft:player", false, false);
    }
}
