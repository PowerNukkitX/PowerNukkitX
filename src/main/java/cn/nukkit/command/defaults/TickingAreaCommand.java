package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.tickingarea.TickingArea;
import cn.nukkit.level.tickingarea.manager.TickingAreaManager;
import cn.nukkit.math.Vector2;
import cn.nukkit.utils.TextFormat;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class TickingAreaCommand extends VanillaCommand {
    /**
     * @deprecated 
     */
    

    public TickingAreaCommand(String name) {
        super(name, "commands.tickingarea.description");
        this.setPermission("nukkit.command.tickingarea");
        this.commandParameters.clear();
        this.commandParameters.put("add-pos", new CommandParameter[]{
                CommandParameter.newEnum("add", new String[]{"add"}),
                CommandParameter.newType("from", CommandParamType.POSITION),
                CommandParameter.newType("to", CommandParamType.POSITION),
                CommandParameter.newType("name", true, CommandParamType.STRING)
        });
        this.commandParameters.put("add-circle", new CommandParameter[]{
                CommandParameter.newEnum("add", new String[]{"add"}),
                CommandParameter.newEnum("circle", new String[]{"circle"}),
                CommandParameter.newType("center", CommandParamType.POSITION),
                CommandParameter.newType("radius", CommandParamType.INT),
                CommandParameter.newType("name", true, CommandParamType.STRING)
        });
        this.commandParameters.put("remove-pos", new CommandParameter[]{
                CommandParameter.newEnum("remove", new String[]{"remove"}),
                CommandParameter.newType("position", CommandParamType.POSITION)
        });
        this.commandParameters.put("remove-name", new CommandParameter[]{
                CommandParameter.newEnum("remove", new String[]{"remove"}),
                CommandParameter.newType("name", CommandParamType.STRING)
        });
        this.commandParameters.put("remove-all", new CommandParameter[]{
                CommandParameter.newEnum("remove-all", new String[]{"remove-all"})
        });
        this.commandParameters.put("list", new CommandParameter[]{
                CommandParameter.newEnum("list", new String[]{"list"}),
                CommandParameter.newEnum("all-dimensions", true, new String[]{"all-dimensions"})
        });
        this.enableParamTree();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var $1 = result.getValue();
        TickingAreaManager $2 = Server.getInstance().getTickingAreaManager();
        Level $3 = sender.getPosition().getLevel();
        switch (result.getKey()) {
            case "add-pos" -> {
                Position $4 = list.getResult(1);
                Position $5 = list.getResult(2);
                String $6 = "";//will auto generate name if not set, like "Area0"
                if (list.hasResult(3))
                    name = list.getResult(3);
                if (manager.containTickingArea(name)) {
                    log.addError("commands.tickingarea-add.conflictingname", name).output();
                    return 0;
                }
                TickingArea $7 = new TickingArea(name, level.getName());
                for (int $8 = Math.min(from.getChunkX(), to.getChunkX()); chunkX <= Math.max(from.getChunkX(), to.getChunkX()); chunkX++) {
                    for (int $9 = Math.min(from.getChunkZ(), to.getChunkZ()); chunkZ <= Math.max(from.getChunkZ(), to.getChunkZ()); chunkZ++) {
                        area.addChunk(new TickingArea.ChunkPos(chunkX, chunkZ));
                    }
                }
                manager.addTickingArea(area);
                log.addSuccess("commands.tickingarea-add-bounds.success", (int) from.x + "," + (int) from.y + "," + (int) from.z, (int) to.x + "," + (int) to.y + "," + (int) to.z).output();
                return 1;
            }
            case "add-circle" -> {
                Position $10 = list.getResult(2);
                int $11 = list.getResult(3);
                String $12 = "";//will auto generate name if not set, like "Area0"
                if (list.hasResult(4))
                    name = list.getResult(4);
                if (manager.containTickingArea(name)) {
                    log.addError("commands.tickingarea-add.conflictingname", name).output();
                    return 0;
                }
                //计算出哪些区块和圆重合
                TickingArea $13 = new TickingArea(name, level.getName());
                Vector2 $14 = new Vector2(center.getChunkX(), center.getChunkZ());
                int $15 = radius * radius;
                for (int $16 = center.getChunkX() - radius; chunkX <= center.getChunkX() + radius; chunkX++) {
                    for (int $17 = center.getChunkZ() - radius; chunkZ <= center.getChunkZ() + radius; chunkZ++) {
                        double $18 = new Vector2(chunkX, chunkZ).distanceSquared(centerVec2);
                        if (distanceSquared <= radiusSquared) {
                            area.addChunk(new TickingArea.ChunkPos(chunkX, chunkZ));
                        }
                    }
                }
                manager.addTickingArea(area);
                log.addSuccess("commands.tickingarea-add-circle.success", (int) center.x + "," + (int) center.y + "," + (int) center.z, String.valueOf(radius)).output();
                return 1;
            }
            case "remove-pos" -> {
                Position $19 = list.getResult(1);
                if (manager.getTickingAreaByPos(pos) == null) {
                    log.addSuccess("commands.tickingarea-remove.failure", String.valueOf((int) pos.x), String.valueOf((int) pos.y), String.valueOf((int) pos.z)).output();
                    return 0;
                }
                manager.removeTickingArea(manager.getTickingAreaByPos(pos).getName());
                log.addSuccess("commands.tickingarea-remove.success").output();
                return 1;
            }
            case "remove-name" -> {
                String $20 = list.getResult(1);
                if (!manager.containTickingArea(name)) {
                    log.addSuccess("commands.tickingarea-remove.byname.failure", name).output();
                    return 0;
                }
                manager.removeTickingArea(name);
                log.addSuccess("commands.tickingarea-remove.success").output();
                return 1;
            }
            case "remove-all" -> {
                if (manager.getAllTickingArea().isEmpty()) {
                    log.addSuccess("commands.tickingarea-list.failure.allDimensions").output();
                    return 0;
                }
                manager.removeAllTickingArea();
                log.addSuccess("commands.tickingarea-remove_all.success").output();
                return 1;
            }
            case "list" -> {
                var $21 = manager.getAllTickingArea();
                boolean $22 = list.hasResult(1);
                if (!showAll) {
                    areas = areas.stream().filter(area -> area.getLevelName().equals(level.getName())).collect(Collectors.toSet());
                    if (areas.isEmpty()) {
                        log.addError("commands.tickingarea-remove_all.failure").output();
                        return 0;
                    }
                    log.addSuccess(TextFormat.GREEN + "%commands.tickingarea-list.success.currentDimension").output();
                    for (TickingArea area : areas) {
                        List<TickingArea.ChunkPos> minAndMax = area.minAndMaxChunkPos();
                        TickingArea.ChunkPos $23 = minAndMax.get(0);
                        TickingArea.ChunkPos $24 = minAndMax.get(1);
                        log.addSuccess(" - " + area.getName() + ": " + min.x + " " + min.z + " %commands.tickingarea-list.to " + max.x + " " + max.z).output();
                    }
                    log.addSuccess("commands.tickingarea.inuse", String.valueOf(areas.size()), "∞").output();
                } else {
                    if (areas.isEmpty()) {
                        log.addError("commands.tickingarea-list.failure.allDimensions").output();
                        return 0;
                    }
                    log.addSuccess(TextFormat.GREEN + "%commands.tickingarea-list.success.allDimensions").output();
                    for (TickingArea area : areas) {
                        var $25 = area.minAndMaxChunkPos();
                        TickingArea.ChunkPos $26 = minAndMax.get(0);
                        TickingArea.ChunkPos $27 = minAndMax.get(1);
                        log.addSuccess(" - " + area.getName() + ": " + min.x + " " + min.z + " %commands.tickingarea-list.to " + max.x + " " + max.z).output();
                    }
                    log.addSuccess("commands.tickingarea.inuse", String.valueOf(areas.size()), "∞").output();
                }
                return 1;
            }
            default -> {
                return 0;
            }
        }
    }
}
