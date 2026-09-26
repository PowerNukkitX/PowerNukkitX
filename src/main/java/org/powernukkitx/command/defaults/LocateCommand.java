package org.powernukkitx.command.defaults;

import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandEnum;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Location;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.generator.biome.OverworldBiomePicker;
import org.powernukkitx.level.generator.biome.result.BiomeResult;
import org.powernukkitx.level.generator.biome.result.OverworldBiomeResult;
import org.powernukkitx.level.generator.populator.generic.PopulatorRuinedPortal;
import org.powernukkitx.level.generator.populator.nether.BastionRemnantPopulator;
import org.powernukkitx.level.generator.populator.nether.NetherFortressPopulator;
import org.powernukkitx.level.generator.populator.nether.soulsand_valley.NetherFossilPopulator;
import org.powernukkitx.level.generator.populator.normal.*;
import org.powernukkitx.level.generator.populator.placement.StructurePlacement;
import org.powernukkitx.math.ChunkVector2;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import it.unimi.dsi.fastutil.Pair;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.powernukkitx.utils.random.Xoroshiro128;

import java.util.Map;

import static org.powernukkitx.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public class LocateCommand extends VanillaCommand {
    private static final int SEARCH_SPIRAL = 0;
    private static final int SEARCH_X_AXIS = 1;
    private static final int BIOME_SAMPLE_STEP = 16;
    private static final int CAVE_BIOME_DEPTH = 26;
    private static final int DEEP_DARK_BIOME_DEPTH = 127;

    public LocateCommand(String name) {
        super(name, "commands.locate.description");
        this.setPermission("nukkit.command.locate");
        this.commandParameters.clear();
        this.commandParameters.put("structure", new CommandParameter[]{
                CommandParameter.newEnum("mode", new CommandEnum("LocateModeStructure", "structure")),
                CommandParameter.newEnum("structures", new String[]{"woodland_mansion", "desert_pyramid", "igloo", "jungle_temple", "ocean_monument", "ocean_ruin", "pillager_outpost", "shipwreck", "stronghold", "swamp_hut", "trail_ruins", "trial_chambers", "village", "ruined_portal", "ancient_city", "bastion_remnant", "nether_fortress", "nether_fossil"}),
                CommandParameter.newEnum("teleport", true, CommandEnum.ENUM_BOOLEAN),
                CommandParameter.newType("radius", true, CommandParamType.INT)
        });
        this.commandParameters.put("biome", new CommandParameter[]{
                CommandParameter.newEnum("mode", new CommandEnum("LocateModeBiome", "biome")),
                CommandParameter.newEnum("biomes", Registries.BIOME.getBiomeDefinitions().stream().map(Pair::first).map(Registries.BIOME::getFromBiomeStringList).toArray(String[]::new)),
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

                int dimension = pos.getLevel().getDimension();
                boolean overworld = dimension == Level.DIMENSION_OVERWORLD;
                boolean nether = dimension == Level.DIMENSION_NETHER;
                StructurePlacement placement = switch (structure) {
                    case "woodland_mansion" -> overworld ? WoodlandMansionPopulator.PLACEMENT : null;
                    case "desert_pyramid" -> overworld ? DesertPyramidPopulator.PLACEMENT : null;
                    case "igloo" -> overworld ? IglooPopulator.PLACEMENT : null;
                    case "jungle_temple" -> overworld ? JungleTemplePopulator.PLACEMENT : null;
                    case "ocean_monument" -> overworld ? OceanMonumentPopulator.PLACEMENT : null;
                    case "ocean_ruin" -> overworld ? OceanRuinPopulator.PLACEMENT : null;
                    case "pillager_outpost" -> overworld ? PillagerOutpostPopulator.PLACEMENT : null;
                    case "shipwreck" -> overworld ? ShipwreckPopulator.PLACEMENT : null;
                    case "stronghold" -> overworld ? StrongholdPopulator.PLACEMENT : null;
                    case "swamp_hut" -> overworld ? SwampHutPopulator.PLACEMENT : null;
                    case "trail_ruins" -> overworld ? TrailRuinsPopulator.PLACEMENT : null;
                    case "trial_chambers" -> overworld ? TrialChambersPopulator.PLACEMENT : null;
                    case "village" -> overworld ? VillagePopulator.PLACEMENT : null;
                    case "ancient_city" -> overworld ? AncientCityPopulator.PLACEMENT : null;
                    case "ruined_portal" -> PopulatorRuinedPortal.getPlacement(dimension);
                    case "bastion_remnant" -> nether ? BastionRemnantPopulator.PLACEMENT : null;
                    case "nether_fortress" -> nether ? NetherFortressPopulator.PLACEMENT : null;
                    case "nether_fossil" -> nether ? NetherFossilPopulator.PLACEMENT : null;
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
                    log.addSuccess("commands.locate.structure.success", structure, _x, _z, _d);
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

                if (found != null) {
                    String _x = String.valueOf(found.getFloorX());
                    String _y = String.valueOf(found.getFloorY());
                    String _z = String.valueOf(found.getFloorZ());
                    String _d = String.valueOf((int) (found.distance(pos)));
                    log.addSuccess("commands.locate.biome.success", name, _x, _y, _z, _d);
                    if (list.hasResult(2) && (boolean) list.getResult(2) && sender.isPlayer()) {
                        sender.asPlayer().teleport(found);
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
        int maxRadiusChunks = (int) (((long) Math.max(0, maxRadiusBlocks) + 15L) >> 4);
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

    private Vector3 findBiomeSpiral(Position pos, int biomeId, int maxRadiusBlocks) {
        int radius = Math.max(0, maxRadiusBlocks);
        int sampleRadius = (int) (((long) radius + BIOME_SAMPLE_STEP - 1L) / BIOME_SAMPLE_STEP);
        int centerX = pos.getFloorX();
        int centerZ = pos.getFloorZ();
        long maxDistanceSq = (long) radius * radius;
        long nearestDistanceSq = Long.MAX_VALUE;
        int nearestX = 0;
        int nearestZ = 0;
        boolean found = false;

        for (int dx = -sampleRadius; dx <= sampleRadius; dx++) {
            int x = centerX + dx * BIOME_SAMPLE_STEP;
            long blockDx = (long) x - centerX;

            for (int dz = -sampleRadius; dz <= sampleRadius; dz++) {
                int z = centerZ + dz * BIOME_SAMPLE_STEP;
                long blockDz = (long) z - centerZ;
                long distanceSq = blockDx * blockDx + blockDz * blockDz;

                if (distanceSq > maxDistanceSq || distanceSq > nearestDistanceSq) {
                    continue;
                }
                if (!columnCanContainBiome(pos, biomeId, x, z)) {
                    continue;
                }
                if (!found || distanceSq < nearestDistanceSq
                        || (distanceSq == nearestDistanceSq && (x < nearestX || (x == nearestX && z < nearestZ)))) {
                    found = true;
                    nearestDistanceSq = distanceSq;
                    nearestX = x;
                    nearestZ = z;
                }
            }
        }

        return found ? resolveBiomePosition(pos, biomeId, nearestX, nearestZ) : null;
    }

    private Vector3 findBiomePosition(Position pos, int maxRadiusBlocks, int biomeId) {
        int radius = Math.max(0, maxRadiusBlocks);
        int centerX = pos.getFloorX();
        int centerZ = pos.getFloorZ();

        for (int offset = 0; offset <= radius; offset += BIOME_SAMPLE_STEP) {
            int x = centerX + offset;
            if (columnCanContainBiome(pos, biomeId, x, centerZ)) {
                return resolveBiomePosition(pos, biomeId, x, centerZ);
            }

            if (offset != 0) {
                x = centerX - offset;
                if (columnCanContainBiome(pos, biomeId, x, centerZ)) {
                    return resolveBiomePosition(pos, biomeId, x, centerZ);
                }
            }
        }

        return null;
    }

    private boolean columnCanContainBiome(Position pos, int biomeId, int x, int z) {
        var biomePicker = pos.getLevel().getBiomePicker();
        if (!(biomePicker instanceof OverworldBiomePicker overworldBiomePicker)) {
            return biomePicker.pick(x, SEA_LEVEL, z).getBiomeId() == biomeId;
        }

        OverworldBiomeResult result = overworldBiomePicker.pickRaw(x, SEA_LEVEL, z);
        if (result.getBiomeId() == biomeId) {
            return true;
        }

        result.correct(-CAVE_BIOME_DEPTH);
        if (result.getBiomeId() == biomeId) {
            return true;
        }

        result.reset();
        result.correct(-DEEP_DARK_BIOME_DEPTH);
        if (result.getBiomeId() != biomeId) {
            return false;
        }

        return overworldBiomePicker.predictSurfaceHeight(x, z) - DEEP_DARK_BIOME_DEPTH >= pos.getLevel().getMinHeight();
    }

    private Vector3 resolveBiomePosition(Position pos, int biomeId, int x, int z) {
        var biomePicker = pos.getLevel().getBiomePicker();
        if (!(biomePicker instanceof OverworldBiomePicker overworldBiomePicker)) {
            return new Vector3(x, SEA_LEVEL, z);
        }

        int surfaceY = overworldBiomePicker.predictSurfaceHeight(x, z);
        OverworldBiomeResult result = overworldBiomePicker.pickRaw(x, SEA_LEVEL, z);
        if (result.getBiomeId() == biomeId) {
            return new Vector3(x, surfaceY, z);
        }

        result.correct(-CAVE_BIOME_DEPTH);
        if (result.getBiomeId() == biomeId) {
            return new Vector3(x, surfaceY - CAVE_BIOME_DEPTH, z);
        }

        result.reset();
        result.correct(-DEEP_DARK_BIOME_DEPTH);
        int deepY = surfaceY - DEEP_DARK_BIOME_DEPTH;
        if (result.getBiomeId() == biomeId && deepY >= pos.getLevel().getMinHeight()) {
            return new Vector3(x, deepY, z);
        }

        return null;
    }
}
