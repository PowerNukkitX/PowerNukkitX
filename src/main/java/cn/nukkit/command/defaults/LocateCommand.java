package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;

import java.util.Map;

import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public class LocateCommand extends VanillaCommand {

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

                int type = 0;
                if(list.hasResult(4)) {
                    type = switch (list.getResult(4).toString()) {
                        case "xaxis" -> 1;
                        default -> 0;
                    };
                }
                if(type == 0) {
                    int centerX = (int) pos.x;
                    int centerZ = (int) pos.z;

                    int x = 0, z = 0;
                    int dx = 0, dz = -1;
                    int maxSteps = (maxRadius * 2 + 1) * (maxRadius * 2 + 1);

                    for (int step = 0; step < maxSteps; step++) {
                        Vector3 check = new Vector3(centerX + (x << 4), pos.y, centerZ + (z << 4));
                        if(pos.getLevel().pickBiome(check.getFloorX(), SEA_LEVEL, check.getFloorZ()) == biomeId) {
                            found = check;
                            break;
                        }
                        if (x == z || (x < 0 && x == -z) || (x > 0 && x == 1 - z)) {
                            int tmp = dx;
                            dx = -dz;
                            dz = tmp;
                        }

                        x += dx;
                        z += dz;
                    }
                } else if(type == 1) {
                    u:
                    for(int x = 0; x <= maxRadius; x++) {
                        for(int i = 0; i < 2; i++) {
                            Vector3 check = new Vector3(pos.getFloorX() + ((x * (i == 0 ? 1 : -1)) << 4), pos.y, pos.getFloorZ());
                            if (pos.getLevel().pickBiome(check.getFloorX(), SEA_LEVEL, check.getFloorZ()) == biomeId) {
                                found = check;
                                break u;
                            }
                        }
                    }
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
}
