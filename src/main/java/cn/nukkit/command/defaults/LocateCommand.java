package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;

import java.util.Map;

import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public class LocateCommand extends VanillaCommand {
    private static final int SEARCH_SPIRAL = 0;
    private static final int SEARCH_X_AXIS = 1;

    public LocateCommand(String name) {
        super(name, "commands.locate.description");
        this.setPermission("nukkit.command.locate");
        this.commandParameters.clear();
        this.commandParameters.put("biome", new CommandParameter[]{
                CommandParameter.newEnum("mode", new CommandEnum("LocateModeBiome", "biome")),
                CommandParameter.newEnum("biomes", Registries.BIOME.getBiomeDefinitions().stream().map(BiomeDefinition::getName).toArray(String[]::new)),
                CommandParameter.newEnum("teleport", true, CommandEnum.ENUM_BOOLEAN),
                CommandParameter.newType("radius", true, CommandParamType.INT),
                CommandParameter.newEnum("search", true, new String[]{"spiral", "xaxis"})
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        switch (result.getKey()) {
            case "biome" -> {
                if (!sender.hasPermission("nukkit.command.locate.biome")) {
                    log.addMessage("nukkit.command.generic.permission").output();
                    return 0;
                }
                String name = list.getResult(1);
                int biomeId = Registries.BIOME.getBiomeId(name);
                Vector3 found = null;
                Location pos = sender.getLocation();
                int maxRadius = 1000;
                if(list.hasResult(3)) {
                    maxRadius = list.getResult(3);
                }

                int searchType = resolveSearchType(list);

                if (searchType == SEARCH_SPIRAL) {
                    found = findBiomeSpiral(pos, biomeId, maxRadius);
                } else if (searchType == SEARCH_X_AXIS) {
                    found = findBiomePosition(pos, maxRadius, biomeId);
                }

                if(found != null) {
                    found.setY(pos.getLevel().getHeightMap(pos.getFloorX(), pos.getFloorZ()) + 16);
                    String _x = String.valueOf(found.getFloorX());
                    String _y = String.valueOf(found.getFloorY());
                    String _z = String.valueOf(found.getFloorZ());
                    String _d = String.valueOf((int) (found.distance(pos)));
                    log.addSuccess("commands.locate.biome.success", name, _x, _y, _z, _d);
                    if(list.hasResult(2)) {
                        if(list.getResult(2)) {
                            sender.asPlayer().teleport(found);
                        }
                    }
                } else log.addError("commands.locate.biome.fail", name);
            }
            default -> {
                return 0;
            }
        }
        log.output();
        return 1;
    }

    private int resolveSearchType(ParamList list) {
        if (!list.hasResult(4)) {
            return SEARCH_SPIRAL;
        }

        return "xaxis".equalsIgnoreCase(list.getResult(4).toString())
                ? SEARCH_X_AXIS
                : SEARCH_SPIRAL;
    }

    private Vector3 findBiomeSpiral(Position pos, int biomeId, int maxRadius) {
        int centerX = pos.getFloorX();
        int centerZ = pos.getFloorZ();

        int x = 0, z = 0;
        int dx = 0, dz = -1;

        int diameter = maxRadius * 2 + 1;
        int maxSteps = diameter * diameter;

        for (int step = 0; step < maxSteps; step++) {
            Vector3 check = new Vector3(centerX + (x << 4), pos.y, centerZ + (z << 4));

            if (pos.getLevel().pickBiome(check.getFloorX(), SEA_LEVEL, check.getFloorZ()) == biomeId) {
                return check;
            }

            if (x == z || (x < 0 && x == -z) || (x > 0 && x == 1 - z)) {
                int tmp = dx;
                dx = -dz;
                dz = tmp;
            }

            x += dx;
            z += dz;
        }

        return null;
    }


    private Vector3 findBiomePosition(Position pos, int maxRadius, int biomeId) {
        for (int x = 0; x <= maxRadius; x++) {
            int offset = x << 4;

            for (int i = 0; i < 2; i++) {
                int dx = (i == 0) ? offset : -offset;

                Vector3 check = new Vector3(pos.getFloorX() + dx, pos.y, pos.getFloorZ());

                if (pos.getLevel().pickBiome(check.getFloorX(), SEA_LEVEL, check.getFloorZ()) == biomeId) {
                    return check;
                }
            }
        }
        return null;
    }
}
