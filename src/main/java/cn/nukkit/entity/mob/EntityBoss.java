package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.BossEventPacket;

public abstract class EntityBoss extends EntityMob {

    public EntityBoss(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    protected Sound blockBreakSound = null;

    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        BossEventPacket pkBoss = new BossEventPacket();
        pkBoss.bossEid = this.id;
        pkBoss.type = BossEventPacket.TYPE_HEALTH_PERCENT;
        pkBoss.healthPercent = health / getMaxHealth();
        Server.broadcastPacket(getViewers().values(), pkBoss);
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
        if(player.locallyInitialized) {
            addBossbar(player);
        }
    }

    protected boolean canBreakBlock(Block block) {
        return switch (block.getId()) {
            case Block.BARRIER,
                 Block.BEDROCK,
                 Block.COMMAND_BLOCK,
                 Block.CHAIN_COMMAND_BLOCK,
                 Block.REPEATING_COMMAND_BLOCK,
                 Block.CRYING_OBSIDIAN,
                 Block.END_STONE,
                 Block.END_PORTAL,
                 Block.END_PORTAL_FRAME,
                 Block.END_GATEWAY,
                 Block.FIRE,
                 Block.IRON_BARS,
                 Block.JIGSAW,
                 Block.OBSIDIAN,
                 Block.REINFORCED_DEEPSLATE,
                 Block.RESPAWN_ANCHOR,
                 Block.SOUL_FIRE,
                 Block.STRUCTURE_BLOCK -> false;
            default -> true;
        };
    }

    public abstract void addBossbar(Player player);

}
