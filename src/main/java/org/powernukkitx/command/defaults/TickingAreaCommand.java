package org.powernukkitx.command.defaults;

import org.powernukkitx.Server;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandEnum;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.tickingarea.TickingArea;
import org.powernukkitx.level.tickingarea.manager.TickingAreaManager;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class TickingAreaCommand extends VanillaCommand {
    private static final CommandEnum MODE_ADD = new CommandEnum("TickingAreaModeAdd", "add");
    private static final CommandEnum ADD_TYPE = new CommandEnum("AddTickingAreaType", "circle");
    private static final CommandEnum MODE_LIST = new CommandEnum("TickingAreaModeList", "list");
    private static final CommandEnum MODE_PRELOAD = new CommandEnum("TickingAreaModePreload", "preload");
    private static final CommandEnum MODE_REMOVE = new CommandEnum("TickingAreaModeRemove", "remove");
    private static final CommandEnum MODE_REMOVE_ALL = new CommandEnum("TickingAreaModeRemoveAll", "remove_all");
    private static final CommandEnum ALL_DIMENSIONS = new CommandEnum("AllDimensions", "all-dimensions");
    private static final int MAX_CHUNKS = 100;
    private static final int MAX_CIRCLE_RADIUS = 4;

    public TickingAreaCommand(String name) {
        super(name, "commands.tickingarea.description");
        this.setPermission("nukkit.command.tickingarea");
        this.commandParameters.clear();
        this.commandParameters.put("add-pos", new CommandParameter[]{
                CommandParameter.newEnum("mode", MODE_ADD),
                CommandParameter.newType("from", CommandParamType.POSITION),
                CommandParameter.newType("to", CommandParamType.POSITION),
                CommandParameter.newType("name", true, CommandParamType.ID),
                CommandParameter.newEnum("preload", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("add-circle", new CommandParameter[]{
                CommandParameter.newEnum("mode", MODE_ADD),
                CommandParameter.newEnum("circle", ADD_TYPE),
                CommandParameter.newType("center", CommandParamType.POSITION),
                CommandParameter.newType("radius", CommandParamType.INT),
                CommandParameter.newType("name", true, CommandParamType.ID),
                CommandParameter.newEnum("preload", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("list", new CommandParameter[]{
                CommandParameter.newEnum("mode", MODE_LIST),
                CommandParameter.newEnum("all-dimensions", true, ALL_DIMENSIONS)
        });
        this.commandParameters.put("preload-name", new CommandParameter[]{
                CommandParameter.newEnum("mode", MODE_PRELOAD),
                CommandParameter.newType("name", CommandParamType.ID),
                CommandParameter.newEnum("preload", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("preload-pos", new CommandParameter[]{
                CommandParameter.newEnum("mode", MODE_PRELOAD),
                CommandParameter.newType("position", CommandParamType.POSITION),
                CommandParameter.newEnum("preload", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("remove-pos", new CommandParameter[]{
                CommandParameter.newEnum("mode", MODE_REMOVE),
                CommandParameter.newType("position", CommandParamType.POSITION)
        });
        this.commandParameters.put("remove-name", new CommandParameter[]{
                CommandParameter.newEnum("mode", MODE_REMOVE),
                CommandParameter.newType("name", CommandParamType.ID)
        });
        this.commandParameters.put("remove-all", new CommandParameter[]{
                CommandParameter.newEnum("mode", MODE_REMOVE_ALL)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        TickingAreaManager manager = Server.getInstance().getTickingAreaManager();
        Level level = sender.getPosition().getLevel();
        switch (result.getKey()) {
            case "add-pos" -> {
                Position from = list.getResult(1);
                Position to = list.getResult(2);
                String name = "";//will auto generate name if not set, like "Area0"
                if (list.hasResult(3))
                    name = list.getResult(3);
                boolean preload = list.hasResult(4) && list.<Boolean>getResult(4);
                if (name.length() > 255) {
                    log.addError("Name cannot be longer than 255 characters").output();
                    return 0;
                }
                int minChunkX = Math.min(from.getChunkX(), to.getChunkX());
                int minChunkZ = Math.min(from.getChunkZ(), to.getChunkZ());
                int maxChunkX = Math.max(from.getChunkX(), to.getChunkX());
                int maxChunkZ = Math.max(from.getChunkZ(), to.getChunkZ());
                long chunkCount = ((long) maxChunkX - minChunkX + 1) * ((long) maxChunkZ - minChunkZ + 1);
                if (chunkCount > MAX_CHUNKS) {
                    log.addError("commands.tickingarea-add.chunkfailure", String.valueOf(MAX_CHUNKS)).output();
                    return 0;
                }
                if (manager.containTickingArea(level, name)) {
                    log.addError("commands.tickingarea-add.conflictingname", name).output();
                    return 0;
                }
                if (!manager.canAddTickingArea(level)) {
                    log.addError("commands.tickingarea-add.failure", String.valueOf(manager.getMaxTickingAreas(level))).output();
                    return 0;
                }
                TickingArea area = new TickingArea(name, level.getName(), false, 0, preload);
                for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
                    for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                        area.addChunk(new TickingArea.ChunkPos(chunkX, chunkZ));
                    }
                }
                manager.addTickingArea(area);
                log.addSuccess(preload ? "commands.tickingarea-add-bounds.preload.success" : "commands.tickingarea-add-bounds.success", (int) from.x + "," + (int) from.y + "," + (int) from.z, (int) to.x + "," + (int) to.y + "," + (int) to.z).output();
                return 1;
            }
            case "add-circle" -> {
                Position center = list.getResult(2);
                int radius = list.getResult(3);
                String name = ""; // Will auto generate name if not set, like "Area0"
                if (list.hasResult(4))
                    name = list.getResult(4);
                boolean preload = list.hasResult(5) && list.<Boolean>getResult(5);
                if (radius < 0) {
                    log.addError("commands.generic.radiusNegative").output();
                    return 0;
                }
                if (radius > MAX_CIRCLE_RADIUS) {
                    log.addError("commands.tickingarea-add.radiusfailure", String.valueOf(MAX_CIRCLE_RADIUS)).output();
                    return 0;
                }
                if (name.length() > 255) {
                    log.addError("Name cannot be longer than 255 characters").output();
                    return 0;
                }
                if (manager.containTickingArea(level, name)) {
                    log.addError("commands.tickingarea-add.conflictingname", name).output();
                    return 0;
                }
                if (!manager.canAddTickingArea(level)) {
                    log.addError("commands.tickingarea-add.failure", String.valueOf(manager.getMaxTickingAreas(level))).output();
                    return 0;
                }
                TickingArea area = new TickingArea(name, level.getName(), true, radius, preload);
                for (int chunkX = center.getChunkX() - radius; chunkX <= center.getChunkX() + radius; chunkX++) {
                    for (int chunkZ = center.getChunkZ() - radius; chunkZ <= center.getChunkZ() + radius; chunkZ++) {
                        area.addChunk(new TickingArea.ChunkPos(chunkX, chunkZ));
                    }
                }
                manager.addTickingArea(area);
                log.addSuccess(preload ? "commands.tickingarea-add-circle.preload.success" : "commands.tickingarea-add-circle.success", (int) center.x + "," + (int) center.y + "," + (int) center.z, String.valueOf(radius)).output();
                return 1;
            }
            case "preload-name" -> {
                String name = list.getResult(1);
                TickingArea area = manager.getTickingArea(level, name);
                if (area == null) {
                    log.addError("commands.tickingarea-preload.byname.failure", name).output();
                    return 0;
                }
                return handlePreload(manager, Set.of(area), list, 2, log);
            }
            case "preload-pos" -> {
                Position pos = list.getResult(1);
                Set<TickingArea> areas = manager.getTickingAreasByPos(pos);
                if (areas.isEmpty()) {
                    log.addError("commands.tickingarea-preload.byposition.failure", (int) pos.x + " " + (int) pos.y + " " + (int) pos.z).output();
                    return 0;
                }
                return handlePreload(manager, areas, list, 2, log);
            }
            case "remove-pos" -> {
                Position pos = list.getResult(1);
                Set<TickingArea> areas = manager.getTickingAreasByPos(pos);
                if (areas.isEmpty()) {
                    log.addError("commands.tickingarea-remove.failure", (int) pos.x + " " + (int) pos.y + " " + (int) pos.z).output();
                    return 0;
                }
                for (TickingArea area : areas)
                    manager.removeTickingArea(area);
                log.addSuccess("commands.tickingarea-remove.success").output();
                return 1;
            }
            case "remove-name" -> {
                String name = list.getResult(1);
                TickingArea area = manager.getTickingArea(level, name);
                if (area == null) {
                    log.addError("commands.tickingarea-remove.byname.failure", name).output();
                    return 0;
                }
                manager.removeTickingArea(area);
                log.addSuccess("commands.tickingarea-remove.success").output();
                return 1;
            }
            case "remove-all" -> {
                if (manager.getTickingAreas(level).isEmpty()) {
                    log.addError("commands.tickingarea.noneExist.currentDimension").output();
                    return 0;
                }
                manager.removeAllTickingArea(level);
                log.addSuccess("commands.tickingarea-remove_all.success").output();
                return 1;
            }
            case "list" -> {
                boolean showAll = list.hasResult(1);
                Set<TickingArea> areas = showAll ? manager.getTickingAreasInWorld(level) : manager.getTickingAreas(level);
                if (areas.isEmpty()) {
                    log.addError(showAll ? "commands.tickingarea-list.failure.allDimensions" : "commands.tickingarea.noneExist.currentDimension").output();
                    return 0;
                }
                log.addSuccess(showAll ? "%commands.tickingarea-list.success.allDimensions" : "%commands.tickingarea-list.success.currentDimension").output();
                for (TickingArea area : areas)
                    log.addSuccess(formatArea(area)).output();
                log.addSuccess("commands.tickingarea.inuse", String.valueOf(manager.getTickingAreaCount(level)), String.valueOf(manager.getMaxTickingAreas(level))).output();
                return 1;
            }
            default -> {
                return 0;
            }
        }
    }

    private static String formatArea(TickingArea area) {
        List<TickingArea.ChunkPos> bounds = area.minAndMaxChunkPos();
        TickingArea.ChunkPos min = bounds.get(0);
        TickingArea.ChunkPos max = bounds.get(1);
        String preload = area.isPreload() ? " %commands.tickingarea-list.preload" : "";
        if (area.isCircle()) {
            int centerX = (min.x + max.x + 1) << 3;
            int centerZ = (min.z + max.z + 1) << 3;
            int radius = (max.x - min.x + 1) / 2;
            return " - " + area.getName() + " (%commands.tickingarea-list.type.circle): " + centerX + " 0 " + centerZ
                    + " %commands.tickingarea-list.circle.radius: " + radius + " %commands.tickingarea-list.chunks" + preload;
        }
        return " - " + area.getName() + ": " + (min.x << 4) + " 0 " + (min.z << 4)
                + " %commands.tickingarea-list.to " + ((max.x << 4) | 15) + " 0 " + ((max.z << 4) | 15) + preload;
    }

    private static int handlePreload(TickingAreaManager manager, Set<TickingArea> areas, ParamList list, int index, CommandLogger log) {
        if (!list.hasResult(index)) {
            long count = areas.stream().filter(TickingArea::isPreload).count();
            log.addSuccess("commands.tickingarea-preload.count", String.valueOf(count)).output();
            return 1;
        }
        boolean preload = list.<Boolean>getResult(index);
        for (TickingArea area : areas)
            manager.setTickingAreaPreload(area, preload);
        log.addSuccess("commands.tickingarea-preload.success").output();
        return 1;
    }
}
