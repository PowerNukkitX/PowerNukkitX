package cn.nukkit.level.terra.handles;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.state.BlockStateRegistry;
import cn.nukkit.jemapping.JeBlockState;
import cn.nukkit.jemapping.JeMapping;
import cn.nukkit.level.terra.PNXAdapter;
import cn.nukkit.level.terra.delegate.PNXBlockStateDelegate;
import cn.nukkit.level.terra.delegate.PNXEntityType;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.entity.EntityType;
import com.dfsek.terra.api.handle.WorldHandle;
import org.jetbrains.annotations.NotNull;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class PNXWorldHandle implements WorldHandle {
    public static final PNXBlockStateDelegate AIR = new PNXBlockStateDelegate(cn.nukkit.block.state.BlockState.AIR);
    public static int err = 0;

    @Override
    public @NotNull BlockState createBlockState(@NotNull String s) {
        if (!s.startsWith("minecraft:")) {
            s = "minecraft:" + s;
        }
        // 修正部分属性缺失以能正确获取对应基岩版映射
        switch (s) {
            case "minecraft:water" -> s = "minecraft:water[level=0]";
            case "minecraft:lava" -> s = "minecraft:lava[level=0]";
            case "minecraft:deepslate" -> s = "minecraft:deepslate[axis=y]";
            case "minecraft:grass_block" -> s = "minecraft:grass_block[snowy=false]";
            case "minecraft:podzol" -> s = "minecraft:podzol[snowy=false]";
            case "minecraft:mycelium" -> s = "minecraft:mycelium[snowy=false]";
            case "minecraft:sugar_cane" -> s = "minecraft:sugar_cane[age=0]";
            case "minecraft:brown_mushroom_block[down=false]" -> s =
                    "minecraft:brown_mushroom_block[down=false,east=true,north=true,south=true,up=true,west=true]";
            case "minecraft:cactus" -> s = "minecraft:cactus[age=0]";
            case "minecraft:mushroom_stem" -> s =
                    "minecraft:mushroom_stem[down=true,east=true,north=true,south=true,up=true,west=true]";
            case "minecraft:jungle_wood" -> s = "minecraft:jungle_wood[axis=y]";
            case "minecraft:redstone_ore" -> s = "minecraft:redstone_ore[lit=false]";
            case "minecraft:deepslate_redstone_ore" -> s = "minecraft:deepslate_redstone_ore[lit=false]";
            case "minecraft:basalt" -> s = "minecraft:basalt[axis=y]";
            case "minecraft:snow" -> s = "minecraft:snow[layers=1]";
            case "minecraft:cave_vines" -> s = "minecraft:cave_vines[age=0,berries=true]";
            case "minecraft:polished_basalt" -> s = "minecraft:polished_basalt[axis=y]";
            case "minecraft:azalea_leaves[persistent=true]" -> s =
                    "minecraft:azalea_leaves[distance=1,persistent=true,waterlogged=false]";
            case "minecraft:flowering_azalea_leaves[persistent=true]" -> s =
                    "minecraft:flowering_azalea_leaves[distance=1,persistent=true,waterlogged=false]";
            case "minecraft:deepslate_tile_wall" -> s =
                    "minecraft:deepslate_tile_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=none]";
            case "minecraft:farmland" -> s = "minecraft:farmland[moisture=7]";
            case "minecraft:cobbled_deepslate_wall" -> s =
                    "minecraft:cobbled_deepslate_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=none]";
            case "minecraft:polished_deepslate_wall" -> s =
                    "minecraft:polished_deepslate_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=none]";
            case "minecraft:sculk_shrieker[can_summon=true]" -> s =
                    "minecraft:sculk_shrieker[can_summon=true,shrieking=false,waterlogged=false]";
            case "minecraft:sculk_catalyst" -> s = "minecraft:sculk_catalyst[bloom=false]";
            case "minecraft:dark_oak_fence" -> s =
                    "minecraft:dark_oak_fence[east=false,north=false,south=false,waterlogged=false,west=false]";
            case "minecraft:sculk_sensor" -> s =
                    "minecraft:sculk_sensor[power=0,sculk_sensor_phase=inactive,waterlogged=false]";
            case "minecraft:deepslate_tile_stairs" -> s =
                    "minecraft:deepslate_tile_stairs[facing=north,half=top,shape=straight,waterlogged=false]";
        }
        JeBlockState jeBlockState = new JeBlockState(s);
        var jeBlockIdentifier = jeBlockState.getIdentifier();
        var jeBlockAttributes = jeBlockState.getAttributes();
        if (jeBlockIdentifier.contains("log") || jeBlockIdentifier.contains("wood")) {
            jeBlockAttributes.putIfAbsent("axis", "y");
        }
        if (jeBlockIdentifier.contains("minecraft:leaves")) {
            jeBlockAttributes.putIfAbsent("distance", "7");
            jeBlockAttributes.putIfAbsent("persistent", "true");
        }
        if (jeBlockIdentifier.equals("minecraft:bee_nest")) jeBlockAttributes.putIfAbsent("honey_level", "0");
        if (jeBlockIdentifier.equals("minecraft:vine")) {
            jeBlockAttributes.putIfAbsent("east", "false");
            jeBlockAttributes.putIfAbsent("north", "false");
            jeBlockAttributes.putIfAbsent("south", "false");
            jeBlockAttributes.putIfAbsent("up", "false");
            jeBlockAttributes.putIfAbsent("west", "false");
        }
        var bedrockBlockStateHash = JeMapping.getBeBlockStateHashByJeBlockState(jeBlockState);
        // 若未获取到属性，排除掉含水再次尝试
        if (bedrockBlockStateHash == null) {
            jeBlockState.setEqualsIgnoreWaterlogged(true);
            bedrockBlockStateHash = JeMapping.getBeBlockStateHashByJeBlockState(jeBlockState);
        }
        // 排除所有属性再次尝试
        if (bedrockBlockStateHash == null) {
            jeBlockState.setEqualsIgnoreAttributes(true);
            bedrockBlockStateHash = JeMapping.getBeBlockStateHashByJeBlockState(jeBlockState);
        }
        if (bedrockBlockStateHash == null) {
            err++;
            return AIR;
        }
        var runtimeId = BlockStateRegistry.getRuntimeIdByBlockStateHash(bedrockBlockStateHash);
        if (runtimeId == -1) {
            err++;
            return AIR;
        }
        try {
            var bedrockState = BlockStateRegistry.getBlockStateByRuntimeId(runtimeId);
            if (bedrockState == null) {
                err++;
                return AIR;
            }
            return PNXAdapter.adapt(bedrockState);
        } catch (Exception e) {
            err++;
            return AIR;
        }
    }

    @Override
    public @NotNull BlockState air() {
        return AIR;
    }

    @Override
    public @NotNull EntityType getEntity(@NotNull String s) {
        // TODO: remove this hack
        if (s.startsWith("minecraft:")) s = s.substring(10);
        if (s.equals("bee")) s = "Bee";
        var entityType = new PNXEntityType(s);
        if (entityType.getHandle() == null) {
            throw new IllegalArgumentException("Unknown entity type!");
        }
        return entityType;
    }
}
