package cn.nukkit.entity.ai.executor;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.passive.EntitySheep;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.network.protocol.EntityEventPacket;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class EatGrassExecutor implements IBehaviorExecutor{

    protected int duration;
    protected int currentTick = 0;

    public EatGrassExecutor(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        if (currentTick == 0){
            playEatGrassAnimation(entity);
        }
        currentTick++;
        if (currentTick > duration){
            currentTick = 0;
            entity.level.addParticle(new DestroyBlockParticle(entity, Block.get(Block.TALL_GRASS)));
            if (entity.level.getGameRules().getBoolean(GameRule.MOB_GRIEFING)) {
                if (entity.getLevelBlock().getId() == Block.TALL_GRASS) {
                    entity.level.setBlock(entity, Block.get(Block.AIR));
                } else {
                    entity.level.setBlock(entity.add(0, -1, 0), Block.get(Block.DIRT));
                }
            }
            if (entity instanceof EntitySheep sheep){
                if (sheep.sheared) {
                    sheep.growWool();
                    return false;
                }
                if (sheep.isBaby())
                    sheep.setBaby(false);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        currentTick = 0;
    }

    protected void playEatGrassAnimation(EntityIntelligent entity){
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = entity.getId();
        pk.event = EntityEventPacket.EAT_GRASS_ANIMATION;
        Server.broadcastPacket(entity.getViewers().values(),pk);
    }
}
