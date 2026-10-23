package org.powernukkitx.command.defaults;

import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.cloudburstmc.protocol.bedrock.data.payload.structure.Mirror;
import org.cloudburstmc.protocol.bedrock.data.payload.structure.Rotation;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandEnum;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.structure.Structure;
import org.powernukkitx.level.structure.StructureAPI;
import org.powernukkitx.level.structure.StructureAnimationTask;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implements the Bedrock /structure save, load and delete commands.
 *
 * @author Curse
 */
public final class StructureCommand extends VanillaCommand {

    public StructureCommand(String name) {
        super(name, "commands.structure.description");
        this.setPermission("nukkit.command.structure");
        this.getCommandParameters().clear();

        this.addCommandParameters("save-mode", new CommandParameter[]{
                CommandParameter.newEnum("action", new String[]{"save"}),
                CommandParameter.newType("name", CommandParamType.ID),
                CommandParameter.newType("from", CommandParamType.POSITION),
                CommandParameter.newType("to", CommandParamType.POSITION),
                CommandParameter.newEnum("saveMode", true, new String[]{"memory", "disk"})
        });

        this.addCommandParameters("save-options", new CommandParameter[]{
                CommandParameter.newEnum("action", new String[]{"save"}),
                CommandParameter.newType("name", CommandParamType.ID),
                CommandParameter.newType("from", CommandParamType.POSITION),
                CommandParameter.newType("to", CommandParamType.POSITION),
                CommandParameter.newEnum("includeEntities", true, CommandEnum.ENUM_BOOLEAN),
                CommandParameter.newEnum("saveMode", true, new String[]{"memory", "disk"}),
                CommandParameter.newEnum("includeBlocks", true, CommandEnum.ENUM_BOOLEAN)
        });

        this.addCommandParameters("load", new CommandParameter[]{
                CommandParameter.newEnum("action", new String[]{"load"}),
                CommandParameter.newType("name", CommandParamType.ID),
                CommandParameter.newType("to", CommandParamType.POSITION),
                CommandParameter.newEnum("rotation", true, new String[]{
                        "0_degrees", "90_degrees", "180_degrees", "270_degrees"
                }),
                CommandParameter.newEnum("mirror", true, new String[]{"none", "x", "z", "xz"}),
                CommandParameter.newEnum("includeEntities", true, CommandEnum.ENUM_BOOLEAN),
                CommandParameter.newEnum("includeBlocks", true, CommandEnum.ENUM_BOOLEAN),
                CommandParameter.newEnum("waterlogged", true, CommandEnum.ENUM_BOOLEAN),
                CommandParameter.newType("integrity", true, CommandParamType.FLOAT),
                CommandParameter.newType("seed", true, CommandParamType.ID)
        });

        this.addCommandParameters("load-animated", new CommandParameter[]{
                CommandParameter.newEnum("action", new String[]{"load"}),
                CommandParameter.newType("name", CommandParamType.ID),
                CommandParameter.newType("to", CommandParamType.POSITION),
                CommandParameter.newEnum("rotation", true, new String[]{
                        "0_degrees", "90_degrees", "180_degrees", "270_degrees"
                }),
                CommandParameter.newEnum("mirror", true, new String[]{"none", "x", "z", "xz"}),
                CommandParameter.newEnum("animationMode", true, new String[]{"block_by_block", "layer_by_layer"}),
                CommandParameter.newType("animationSeconds", true, CommandParamType.FLOAT),
                CommandParameter.newEnum("includeEntities", true, CommandEnum.ENUM_BOOLEAN),
                CommandParameter.newEnum("includeBlocks", true, CommandEnum.ENUM_BOOLEAN),
                CommandParameter.newEnum("waterlogged", true, CommandEnum.ENUM_BOOLEAN),
                CommandParameter.newType("integrity", true, CommandParamType.FLOAT),
                CommandParameter.newType("seed", true, CommandParamType.ID)
        });

        this.addCommandParameters("delete", new CommandParameter[]{
                CommandParameter.newEnum("action", new String[]{"delete"}),
                CommandParameter.newType("name", CommandParamType.ID)
        });

        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String label, Map.Entry<String, ParamList> result, CommandLogger log) {
        return switch (result.getKey()) {
            case "save-mode", "save-options" -> save(result.getKey(), result.getValue(), log);
            case "load", "load-animated" -> load(result.getKey(), result.getValue(), log);
            case "delete" -> delete(result.getValue(), log);
            default -> 0;
        };
    }

    private int save(String overload, ParamList args, CommandLogger log) {
        String name = args.getResult(1);
        if (!StructureAPI.isValidName(name)) {
            log.addError("commands.structure.invalid.name").output();
            return 0;
        }

        Position from = args.getResult(2);
        Position to = args.getResult(3);
        Level level = from.getLevel();
        if (level == null || level != to.getLevel()) return error(log, "commands.generic.outOfWorld");

        boolean includeEntities = !overload.equals("save-options") || args.getResult(4, true);
        boolean includeBlocks = !overload.equals("save-options") || args.getResult(6, true);
        String saveMode = args.getResult(overload.equals("save-options") ? 5 : 4, "memory");

        if (!includeEntities && !includeBlocks) return error(log, "commands.structure.empty.save");

        int minX = Math.min(from.getFloorX(), to.getFloorX());
        int minY = Math.min(from.getFloorY(), to.getFloorY());
        int minZ = Math.min(from.getFloorZ(), to.getFloorZ());
        int maxX = Math.max(from.getFloorX(), to.getFloorX());
        int maxY = Math.max(from.getFloorY(), to.getFloorY());
        int maxZ = Math.max(from.getFloorZ(), to.getFloorZ());

        if (!level.isYInRange(minY) || !level.isYInRange(maxY)) return error(log, "commands.generic.outOfWorld");

        long sizeX = (long) maxX - minX + 1;
        long sizeY = (long) maxY - minY + 1;
        long sizeZ = (long) maxZ - minZ + 1;

        if (sizeX > Integer.MAX_VALUE || sizeY > Integer.MAX_VALUE || sizeZ > Integer.MAX_VALUE
                || (double) sizeX * sizeY * sizeZ > Integer.MAX_VALUE) {
            return error(log, "Structure dimensions exceed the supported volume");
        }

        try {
            Structure structure = Structure.create(level, minX, minY, minZ,
                    (int) sizeX, (int) sizeY, (int) sizeZ, includeEntities, includeBlocks);

            if (!includeBlocks && structure.getEntities().isEmpty()) {
                return error(log, "commands.structure.empty.save");
            }

            if (saveMode.equals("disk")) {
                if (!StructureAPI.saveDisk(structure, name)) return error(log, "Failed to save structure " + name);
            } else {
                StructureAPI.saveMemory(structure, name);
            }

            log.addSuccess("commands.structure.save.success", name).output();
            return 1;
        } catch (RuntimeException e) {
            return error(log, "Failed to save structure " + name + ": " + e.getMessage());
        }
    }

    private int load(String overload, ParamList args, CommandLogger log) {
        String name = args.getResult(1);
        if (!StructureAPI.isValidName(name)) {
            return error(log, "commands.structure.invalid.name");
        }

        Position destination = args.getResult(2);
        Level level = destination.getLevel();
        if (level == null) return error(log, "commands.generic.outOfWorld");

        Rotation rotation = switch (args.getResult(3, "0_degrees")) {
            case "90_degrees" -> Rotation.ROTATE_90;
            case "180_degrees" -> Rotation.ROTATE_180;
            case "270_degrees" -> Rotation.ROTATE_270;
            default -> Rotation.NONE;
        };

        Mirror mirror = switch (args.getResult(4, "none")) {
            case "x" -> Mirror.X;
            case "z" -> Mirror.Z;
            case "xz" -> Mirror.XZ;
            default -> Mirror.NONE;
        };

        boolean animated = overload.equals("load-animated");
        String animationMode = animated ? args.getResult(5, "none") : "none";
        float animationSeconds = animated && args.hasResult(6)
                ? ((Number) args.getResult(6)).floatValue() : 0f;

        int options = animated ? 7 : 5;
        boolean includeEntities = args.getResult(options, true);
        boolean includeBlocks = args.getResult(options + 1, true);
        boolean waterlogged = args.getResult(options + 2, false);
        float integrity = args.hasResult(options + 3)
                ? ((Number) args.getResult(options + 3)).floatValue() : 100f;
        String seedArgument = args.getResult(options + 4, (String) null);

        if (!Float.isFinite(integrity) || integrity < 0f || integrity > 100f) {
            return error(log, "commands.structure.invalid.integrity");
        }
        if (!Float.isFinite(animationSeconds) || animationSeconds < 0f
                || animationSeconds > Integer.MAX_VALUE / 20f) {
            return error(log, "commands.structure.invalid.animationTime");
        }
        if (!includeEntities && !includeBlocks) return error(log, "commands.structure.empty.load");

        try {
            Structure structure = StructureAPI.load(name);
            if (structure == null) return error(log, "commands.structure.notFound", name);

            if (rotation != Rotation.NONE) structure = structure.rotate(rotation);
            if (mirror != Mirror.NONE) structure = structure.mirror(mirror);

            long maxY = (long) destination.getFloorY() + structure.getSizeY() - 1;
            if (!level.isYInRange(destination.getFloorY()) || maxY > Integer.MAX_VALUE
                    || !level.isYInRange((int) maxY)) {
                return error(log, "commands.generic.outOfWorld");
            }

            long seed = seedArgument == null ? ThreadLocalRandom.current().nextInt() : parseSeed(seedArgument);
            if (integrity < 100f) structure = structure.filteredByIntegrity(integrity, seed);
            if (includeBlocks && waterlogged) structure = structure.withWaterlogging(destination);

            int durationTicks = (int) Math.floor(animationSeconds * 20d);
            int mode = switch (animationMode) {
                case "layer_by_layer" -> 1;
                case "block_by_block" -> 2;
                default -> 0;
            };

            if (!includeBlocks || mode == 0 || durationTicks == 0) {
                structure.place(destination, includeEntities, !includeBlocks);
            } else {
                level.getScheduler().scheduleRepeatingTask(
                        new StructureAnimationTask(structure, destination, includeEntities,
                                mode, durationTicks, name, StructureAPI.getTemplateRevision(name)), 1);
            }

            log.addSuccess("commands.structure.load.success", name).output();
            return 1;
        } catch (RuntimeException e) {
            return error(log, "Failed to load structure " + name + ": " + e.getMessage());
        }
    }

    private int delete(ParamList args, CommandLogger log) {
        String name = args.getResult(1);
        if (!StructureAPI.isValidName(name)) return error(log, "commands.structure.invalid.name");
        if (!StructureAPI.exists(name)) return error(log, "commands.structure.notFound", name);
        if (!StructureAPI.delete(name)) return error(log, "Failed to delete structure " + name);

        log.addSuccess("commands.structure.delete.success", name).output();
        return 1;
    }

    private static long parseSeed(String seed) {
        try {
            return Long.parseLong(seed);
        } catch (NumberFormatException ignored) {
            return seed.hashCode();
        }
    }

    private static int error(CommandLogger log, String message, String... parameters) {
        log.addError(message, parameters).output();
        return 0;
    }
}
