package cn.nukkit.entity.ai.sensor;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.PlayerMemory;
import lombok.Getter;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class NearestPlayerSensor implements ISensor{

    protected double range;

    public NearestPlayerSensor(double range){
        this.range = range;
    }
    @Override
    public PlayerMemory sense(EntityIntelligent entity) {
        Player player = null;
        double rangeSquared = this.range * this.range;
        //寻找范围内最近的玩家
        for(Player p : entity.getLevel().getPlayers().values()){
            if(entity.distanceSquared(p) <= rangeSquared){
                if(player == null){
                    player = p;
                }else{
                    if(entity.distanceSquared(p) < entity.distanceSquared(player)){
                        player = p;
                    }
                }
            }
        }
        return new PlayerMemory(player);
    }
}
