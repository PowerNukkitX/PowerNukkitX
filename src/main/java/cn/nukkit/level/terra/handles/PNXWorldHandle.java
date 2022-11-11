package cn.nukkit.level.terra.handles;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.terra.PNXAdapter;
import cn.nukkit.level.terra.delegate.PNXBlockStateDelegate;
import cn.nukkit.level.terra.delegate.PNXEntityType;
import cn.nukkit.utils.Config;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.entity.EntityType;
import com.dfsek.terra.api.handle.WorldHandle;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class PNXWorldHandle implements WorldHandle {
    public static final PNXBlockStateDelegate AIR = new PNXBlockStateDelegate(cn.nukkit.blockstate.BlockState.AIR);
    public static Map<JeBlockState, Map<String, Object>> jeBlocksMapping = new HashMap<>();
    public static int err = 0;

    static {
        final var jeBlocksMappingConfig = new Config(Config.JSON);
        try {
            jeBlocksMappingConfig.load(PNXWorldHandle.class.getModule().getResourceAsStream("jeMappings/jeBlocksMapping.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        jeBlocksMappingConfig.getAll().forEach((k, v) -> jeBlocksMapping.put(new JeBlockState(k), (Map<String, Object>) v));
    }

    @Override
    public @NotNull
    BlockState createBlockState(@NotNull String s) {
        //修正部分属性缺失以能正确获取对应基岩版映射
        switch (s) {
            case "minecraft:water" -> s = "minecraft:water[level=0]";
            case "minecraft:lava" -> s = "minecraft:lava[level=0]";
            case "minecraft:deepslate" -> s = "minecraft:deepslate[axis=y]";
            case "minecraft:grass_block" -> s = "minecraft:grass_block[snowy=false]";
            case "minecraft:podzol" -> s = "minecraft:podzol[snowy=false]";
            case "minecraft:mycelium" -> s = "minecraft:mycelium[snowy=false]";
            case "minecraft:sugar_cane" -> s = "minecraft:sugar_cane[age=0]";
            case "minecraft:brown_mushroom_block[down=false]" -> s = "minecraft:brown_mushroom_block[down=false,east=true,north=true,south=true,up=true,west=true]";
            case "minecraft:cactus" -> s = "minecraft:cactus[age=0]";
            case "minecraft:mushroom_stem" -> s = "minecraft:mushroom_stem[down=true,east=true,north=true,south=true,up=true,west=true]";
            case "minecraft:jungle_wood" -> s = "minecraft:jungle_wood[axis=y]";
            case "minecraft:redstone_ore" -> s = "minecraft:redstone_ore[lit=false]";
            case "minecraft:deepslate_redstone_ore" -> s = "minecraft:deepslate_redstone_ore[lit=false]";
            case "minecraft:basalt" -> s = "minecraft:basalt[axis=y]";
            case "minecraft:snow" -> s = "minecraft:snow[layers=1]";
            case "minecraft:cave_vines" -> s = "minecraft:cave_vines[age=0,berries=true]";
            case "minecraft:polished_basalt" -> s = "minecraft:polished_basalt[axis=y]";
            case "minecraft:azalea_leaves[persistent=true]" -> s = "minecraft:azalea_leaves[distance=1,persistent=true,waterlogged=false]";
            case "minecraft:flowering_azalea_leaves[persistent=true]" -> s = "minecraft:flowering_azalea_leaves[distance=1,persistent=true,waterlogged=false]";
            case "minecraft:deepslate_tile_wall" -> s = "minecraft:deepslate_tile_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=none]";
            case "minecraft:farmland" -> s = "minecraft:farmland[moisture=7]";
            case "minecraft:cobbled_deepslate_wall" -> s = "minecraft:cobbled_deepslate_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=none]";
            case "minecraft:polished_deepslate_wall" -> s = "minecraft:polished_deepslate_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=none]";
            case "minecraft:sculk_shrieker[can_summon=true]" -> s = "minecraft:sculk_shrieker[can_summon=true,shrieking=false,waterlogged=false]";
            case "minecraft:sculk_catalyst" -> s = "minecraft:sculk_catalyst[bloom=false]";
            case "minecraft:dark_oak_fence" -> s = "minecraft:dark_oak_fence[east=false,north=false,south=false,waterlogged=false,west=false]";
            case "minecraft:sculk_sensor" -> s = "minecraft:sculk_sensor[power=0,sculk_sensor_phase=inactive,waterlogged=false]";
            case "minecraft:deepslate_tile_stairs" -> s = "minecraft:deepslate_tile_stairs[facing=north,half=top,shape=straight,waterlogged=false]";
        }
        JeBlockState jeBlockState = new JeBlockState(s);
        var jeBlockIdentifier = jeBlockState.getIdentifier();
        var jeBlockAttributes = jeBlockState.getAttributes();
        if (jeBlockIdentifier.contains("log") || jeBlockIdentifier.contains("wood")) {
            jeBlockAttributes.putIfAbsent("axis", "y");
        }
        if (jeBlockIdentifier.equals("minecraft:jungle_leaves") || jeBlockIdentifier.equals("minecraft:spruce_leaves") || jeBlockIdentifier.equals("minecraft:oak_leaves")) {
            jeBlockAttributes.putIfAbsent("distance", "7");
            jeBlockAttributes.putIfAbsent("persistent", "true");
        }
        if (jeBlockIdentifier.equals("minecraft:bee_nest"))
            jeBlockAttributes.putIfAbsent("honey_level", "0");
        if (jeBlockIdentifier.equals("minecraft:vine")) {
            jeBlockAttributes.putIfAbsent("east", "false");
            jeBlockAttributes.putIfAbsent("north", "false");
            jeBlockAttributes.putIfAbsent("south", "false");
            jeBlockAttributes.putIfAbsent("up", "false");
            jeBlockAttributes.putIfAbsent("west", "false");
        }
        Map<String, Object> mappedData = jeBlocksMapping.get(jeBlockState);
        var toDefaultState = false;
        //若未获取到属性，排除掉含水再次尝试
        if (mappedData == null) {
            jeBlockState.setEqualsIgnoreWaterlogged(true);
            mappedData = jeBlocksMapping.get(jeBlockState);
        }
        //排除所有属性再次尝试
        if (mappedData == null) {
            jeBlockState.setEqualsIgnoreAttributes(true);
            mappedData = jeBlocksMapping.get(jeBlockState);
            toDefaultState = true;
        }
        if (mappedData == null) {
            return AIR;
        }
        final Map<String, Object> bedrockStates = new HashMap<>();
        if (mappedData.containsKey("bedrock_states") && !toDefaultState) {
            ((Map<String, Object>) mappedData.get("bedrock_states")).forEach((k, v) -> {
                if (v instanceof Boolean) {
                    if ((Boolean) v) {
                        bedrockStates.put(k, 1);
                    } else {
                        bedrockStates.put(k, 0);
                    }
                    return;
                }
                if (v instanceof Number) {
                    bedrockStates.put(k, ((Number) v).intValue());
                    return;
                }
                 bedrockStates.put(k, v);
            });
        }
        var identifier = (String) mappedData.get("bedrock_identifier");
        if (identifier.equals("minecraft:concretePowder"))//specific case
            identifier = "minecraft:concrete_powder";
        final var data = new StringBuilder();
        data.append(identifier);
        if (!bedrockStates.isEmpty()) {
            bedrockStates.forEach((k, v) -> data.append(";").append(k).append("=").append(v));
        }
        try {
            var delegate = PNXAdapter.adapt(cn.nukkit.blockstate.BlockState.of(data.toString()));
//            if (!stateDelegateList.contains(delegate)) {
//                stateDelegateList.add(delegate);
//                strDelegateList.add(s);
//            } else
//                throw new RuntimeException();
            return delegate;
        } catch (Exception e) {
            err++;
            return AIR;
        }
    }

    //test
    private static List<PNXBlockStateDelegate> stateDelegateList = new ArrayList<>();
    private static List<String> strDelegateList = new ArrayList<>();

    @Override
    public @NotNull
    BlockState air() {
        return AIR;
    }

    @Override
    public @NotNull
    EntityType getEntity(@NotNull String s) {
        //TODO: remove this hack
        if (s.startsWith("minecraft:")) s = s.substring(10);
        if (s.equals("bee")) s = "Bee";
        var entityType = new PNXEntityType(s);
        if (entityType.getHandle() == null) {
            throw new IllegalArgumentException("Unknown entity type!");
        }
        return entityType;
    }
}
