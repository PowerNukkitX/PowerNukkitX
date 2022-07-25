package cn.nukkit.entity.ai.memory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import lombok.Getter;
import lombok.Setter;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class PlayerBreedingMemory extends PlayerMemory<Player> implements TimedMemory {

    protected int breedingTime;

    public PlayerBreedingMemory(){
        super(null);
        breedingTime = -1;
    }

    public PlayerBreedingMemory(Player player) {
        super(player);
        breedingTime = Server.getInstance().getTick();
    }

    @Override
    public void setData(Player data) {
        super.setData(data);
        breedingTime = Server.getInstance().getTick();
    }

    @Override
    public int getTime() {
        return breedingTime;
    }
}
