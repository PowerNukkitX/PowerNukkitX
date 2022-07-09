package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.route.*;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.SpawnParticleEffectPacket;
import lombok.Getter;

import java.util.Arrays;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class WalkToTargetExecutor extends BaseMoveExecutor{

    //指示执行器应该从哪个Memory获取目标位置
    protected Class<?> memoryClazz;

    protected AStarRouteFinder routeFinder;

    public WalkToTargetExecutor(Class<?> memoryClazz){
        this.memoryClazz = memoryClazz;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        //获取目标位置
        Position target = (Position) entity.getBehaviorGroup().getMemory().get(memoryClazz).getData();
        if (target == null) {
            return false;
        }
        if (routeFinder == null) {
            routeFinder = new AStarRouteFinder(entity, entity, target, target.level);
            routeFinder.setEnableFloydSmooth(false);
            routeFinder.setMaxSearchDepth(500);
        }
        if (!routeFinder.isSearching()){
            var nodes = routeFinder.getRoute();

            routeFinder.asyncSearch();
        }
        //等待直到路径计算完成
        return true;
    }

    //todo: remove debug
    private static void sendParticle(String identifier, Position pos,Player[] showPlayers) {
        Arrays.stream(showPlayers).forEach(player -> {
            if (!player.isOnline())
                return;
            SpawnParticleEffectPacket packet = new SpawnParticleEffectPacket();
            packet.identifier = identifier;
            packet.dimensionId = pos.getLevel().getDimension();
            packet.position = pos.asVector3f();
            try {
                player.dataPacket(packet);
            }catch (Throwable t){}
        });
    }
}
