package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.generator.biome.result.BiomeResult;
import cn.nukkit.level.generator.biome.result.OverworldBiomeResult;
import cn.nukkit.level.generator.populator.generic.PopulatorRuinedPortal;
import cn.nukkit.level.generator.populator.nether.BastionRemnantPopulator;
import cn.nukkit.level.generator.populator.nether.NetherFortressPopulator;
import cn.nukkit.level.generator.populator.nether.soulsand_valley.NetherFossilPopulator;
import cn.nukkit.level.generator.populator.normal.DesertPyramidPopulator;
import cn.nukkit.level.generator.populator.normal.IglooPopulator;
import cn.nukkit.level.generator.populator.normal.JungleTemplePopulator;
import cn.nukkit.level.generator.populator.normal.OceanMonumentPopulator;
import cn.nukkit.level.generator.populator.normal.OceanRuinPopulator;
import cn.nukkit.level.generator.populator.normal.PillagerOutpostPopulator;
import cn.nukkit.level.generator.populator.normal.ShipwreckPopulator;
import cn.nukkit.level.generator.populator.normal.StrongholdPopulator;
import cn.nukkit.level.generator.populator.normal.SwampHutPopulator;
import cn.nukkit.level.generator.populator.normal.TrailRuinsPopulator;
import cn.nukkit.level.generator.populator.normal.TrialChambersPopulator;
import cn.nukkit.level.generator.populator.normal.VillagePopulator;
import cn.nukkit.level.generator.populator.normal.WoodlandMansionPopulator;
import cn.nukkit.level.generator.populator.placement.StructurePlacement;
import cn.nukkit.math.ChunkVector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.random.RandomSourceProvider;
import cn.nukkit.utils.random.Xoroshiro128;

import java.util.Map;

import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public class LocateCommand extends VanillaCommand {
    private static final int SEARCH_SPIRAL = 0;
    private static final int SEARCH_X_AXIS = 1;
    public LocateCommand(String name) {
        super(name, "commands.locate.description");
        this.setPermission("nukkit.command.locate");
        this.commandParameters.clear();
        this.commandParameters.put("structure", new CommandParameter[]{
                CommandParameter.newEnum("mode", new CommandEnum("LocateModeStructure", "structure")),
                CommandParameter.newEnum("structures", new String[]{"woodland_mansion", "desert_pyramid", "igloo", "jungle_temple", "ocean_monument", "ocean_ruin", "pillager_outpost", "shipwreck", "stronghold", "swamp_hut", "trail_ruins", "trial_chambers", "village", "ruined_portal", "bastion_remnant", "nether_fortress", "nether_fossil"}),
                CommandParameter.newEnum("teleport", true, CommandEnum.ENUM_BOOLEAN),
                CommandParameter.newType("radius", true, CommandParamType.INT)
        });
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
            case "structure" -> {
                if (!sender.hasPermission("nukkit.command.locate.structure")) {
                    log.addMessage("nukkit.command.generic.permission").output();
                    return 0;
                }
                String structure = list.getResult(1);
                Location pos = sender.getLocation();
                int maxRadius = 65536;
                if (list.hasResult(3)) {
                    maxRadius = list.getResult(3);
                }

                StructurePlacement placement = switch (structure) {
                    case "woodland_mansion" -> WoodlandMansionPopulator.PLACEMENT;
                    case "desert_pyramid" -> DesertPyramidPopulator.PLACEMENT;
                    case "igloo" -> IglooPopulator.PLACEMENT;
                    case "jungle_temple" -> JungleTemplePopulator.PLACEMENT;
                    case "ocean_monument" -> OceanMonumentPopulator.PLACEMENT;
                    case "ocean_ruin" -> OceanRuinPopulator.PLACEMENT;
                    case "pillager_outpost" -> PillagerOutpostPopulator.PLACEMENT;
                    case "shipwreck" -> ShipwreckPopulator.PLACEMENT;
                    case "stronghold" -> StrongholdPopulator.PLACEMENT;
                    case "swamp_hut" -> SwampHutPopulator.PLACEMENT;
                    case "trail_ruins" -> TrailRuinsPopulator.PLACEMENT;
                    case "trial_chambers" -> TrialChambersPopulator.PLACEMENT;
                    case "village" -> VillagePopulator.PLACEMENT;
                    case "ruined_portal" -> PopulatorRuinedPortal.PLACEMENT;
                    case "bastion_remnant" -> BastionRemnantPopulator.PLACEMENT;
                    case "nether_fortress" -> NetherFortressPopulator.PLACEMENT;
                    case "nether_fossil" -> NetherFossilPopulator.PLACEMENT;
                    default -> null;
                };
                if (placement == null) {
                    log.addError("commands.locate.structure.fail.nostructurefound");
                    break;
                }

                Vector3 found = findStructure(pos, placement, maxRadius);
                if (found != null) {
                    String _x = String.valueOf(found.getFloorX());
                    String _y = String.valueOf(found.getFloorY());
                    String _z = String.valueOf(found.getFloorZ());
                    String _d = String.valueOf((int) found.distance(pos));
                    log.addSuccess("commands.locate.structure.success", structure, _x, _y, _z, _d);
                    if (list.hasResult(2) && (boolean) list.getResult(2) && sender.isPlayer()) {
                        sender.asPlayer().teleport(found);
                    }
                } else {
                    log.addError("commands.locate.structure.fail.nostructurefound");
                }
            }
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

    private Vector3 findStructure(Position pos, StructurePlacement placement, int maxRadiusBlocks) {
        int maxRadiusChunks = Math.max(1, maxRadiusBlocks >> 4);
        RandomSourceProvider random = new Xoroshiro128(pos.getLevel().getSeed());
        ChunkVector2 center = new ChunkVector2(pos.getFloorX() >> 4, pos.getFloorZ() >> 4);
        ChunkVector2 found = placement.findNearestGenerationChunk(center, random, pos.getLevel().getBiomePicker(), maxRadiusChunks);
        if (found == null) {
            return null;
        }
        int x = (found.getX() << 4) + 8;
        int z = (found.getZ() << 4) + 8;
        int y = pos.getFloorY();
        return new Vector3(x, y, z);
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

            BiomeResult result = pos.getLevel().getBiomePicker().pick(check.getFloorX(), SEA_LEVEL, check.getFloorZ());
            if(result instanceof OverworldBiomeResult biomeResult) {
                int height = SEA_LEVEL;
                while (height > pos.level.getMinHeight()) {
                    biomeResult.correct(height - SEA_LEVEL); //We dont know the actual height before generating.
                    if (pos.getLevel().pickBiome(check.getFloorX(), height, check.getFloorZ()) == biomeId) {
                        return check;
                    }
                    height -= 8;
                }
            } else if(result.getBiomeId() == biomeId) return check;


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
