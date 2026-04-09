package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.BossEventUpdateType;
import org.cloudburstmc.protocol.bedrock.packet.BossEventPacket;

public abstract class EntityBoss extends EntityMob {

    public EntityBoss(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    protected Sound blockBreakSound = null;

    @Override
    public void setHealthCurrent(float health) {
        super.setHealthCurrent(health);
        final BossEventPacket bossEventPacket = new BossEventPacket();
        bossEventPacket.setTargetActorID(this.getId());
        bossEventPacket.setEventType(BossEventUpdateType.UPDATE_PERCENT);
        bossEventPacket.setName(this.getName());
        bossEventPacket.setHealthPercent(health / getHealthMax());
        Server.broadcastPacket(getViewers().values(), bossEventPacket);
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
        if (player.locallyInitialized) {
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
