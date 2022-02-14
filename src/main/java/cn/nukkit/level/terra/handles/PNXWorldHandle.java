package cn.nukkit.level.terra.handles;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Position;
import cn.nukkit.level.terra.delegate.PNXBlockStateDelegate;
import cn.nukkit.level.terra.delegate.PNXEntityType;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.entity.EntityType;
import com.dfsek.terra.api.handle.WorldHandle;
import org.jetbrains.annotations.NotNull;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public class PNXWorldHandle implements WorldHandle {
    public static final PNXBlockStateDelegate AIR = new PNXBlockStateDelegate(cn.nukkit.blockstate.BlockState.AIR);
    public static int ok = 0;
    public static int err = 0;

    @Override
    public @NotNull
    BlockState createBlockState(@NotNull String s) {
        // TODO: 2022/2/14 从java版正确映射方块
        try {
            ok++;
            return new PNXBlockStateDelegate(cn.nukkit.blockstate.BlockState.of(s.replace("[", ";").replace("]", "")));
        } catch (Exception e) {
            err++;
            return new PNXBlockStateDelegate(cn.nukkit.blockstate.BlockState.of(BlockID.AIR));
        }

    }

    @Override
    public @NotNull
    BlockState air() {
        return AIR;
    }

    @Override
    public @NotNull
    EntityType getEntity(@NotNull String s) {
        if (s.startsWith("minecraft:")) s = s.substring(10);
        return new PNXEntityType(Entity.createEntity(s, new Position(0, 0, 0, Server.getInstance().getDefaultLevel())));
    }
}
