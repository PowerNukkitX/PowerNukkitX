package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.tickingarea.TickingArea;
import cn.nukkit.level.tickingarea.manager.TickingAreaManager;
import cn.nukkit.math.Vector2;
import cn.nukkit.utils.TextFormat;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class TickingAreaCommand extends VanillaCommand{

    public TickingAreaCommand(String name) {
        super(name, "commands.tickingarea.description");
        this.setPermission("nukkit.command.tickingarea");
        this.commandParameters.clear();
        this.commandParameters.put("add-pos", new CommandParameter[]{
                CommandParameter.newEnum("add",new String[]{"add"}),
                CommandParameter.newType("from", CommandParamType.POSITION),
                CommandParameter.newType("to", CommandParamType.POSITION),
                CommandParameter.newType("name",true, CommandParamType.STRING)
        });
        this.commandParameters.put("add-circle",new CommandParameter[]{
                CommandParameter.newEnum("add",new String[]{"add"}),
                CommandParameter.newEnum("circle",new String[]{"circle"}),
                CommandParameter.newType("center", CommandParamType.POSITION),
                CommandParameter.newType("radius", CommandParamType.INT),
                CommandParameter.newType("name",true, CommandParamType.STRING)
        });
        this.commandParameters.put("remove-pos",new CommandParameter[]{
                CommandParameter.newEnum("remove",new String[]{"remove"}),
                CommandParameter.newType("position", CommandParamType.POSITION)
        });
        this.commandParameters.put("remove-name",new CommandParameter[]{
                CommandParameter.newEnum("remove",new String[]{"remove"}),
                CommandParameter.newType("name", CommandParamType.STRING)
        });
        this.commandParameters.put("remove-all",new CommandParameter[]{
                CommandParameter.newEnum("remove-all",new String[]{"remove-all"})
        });
        this.commandParameters.put("list",new CommandParameter[]{
                CommandParameter.newEnum("list",new String[]{"list"}),
                CommandParameter.newEnum("all-dimensions",true,new String[]{"all-dimensions"})
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this,sender,args);
        String form;
        if((form = parser.matchCommandForm()) == null){
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }

        TickingAreaManager manager = Server.getInstance().getTickingAreaManager();
        Level level = sender.getPosition().getLevel();

        try {
            switch (form) {
                case "add-pos": {
                    parser.parseString();//skip "add"
                    Position from = parser.parsePosition();
                    Position to = parser.parsePosition();
                    String name = "";//will auto generate name if not set, like "Area0"
                    if (parser.hasNext())
                        name = parser.parseString();
                    if (manager.containTickingArea(name)){
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.tickingarea-add.conflictingname", name));
                        return false;
                    }
                    TickingArea area = new TickingArea(name,level.getName());
                    for (int chunkX = Math.min(from.getChunkX(),to.getChunkX()) ; chunkX <= Math.max(from.getChunkX(),to.getChunkX()) ; chunkX++){
                        for (int chunkZ = Math.min(from.getChunkZ(),to.getChunkZ()) ; chunkZ <= Math.max(from.getChunkZ(),to.getChunkZ()) ; chunkZ++){
                            area.addChunk(new TickingArea.ChunkPos(chunkX,chunkZ));
                        }
                    }
                    manager.addTickingArea(area);
                    sender.sendMessage(new TranslationContainer("commands.tickingarea-add-bounds.success",(int)from.x + "," + (int)from.y + "," + (int)from.z,(int)to.x + "," + (int)to.y + "," + (int)to.z));
                    return true;
                }
                case "add-circle": {
                    parser.parseString();//skip "add"
                    parser.parseString();//skip "circle"
                    Position center = parser.parsePosition();
                    int radius = parser.parseInt();
                    String name = "";//will auto generate name if not set, like "Area0"
                    if (parser.hasNext())
                        name = parser.parseString();
                    if (manager.containTickingArea(name)){
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.tickingarea-add.conflictingname", name));
                        return false;
                    }
                    //计算出哪些区块和圆重合
                    TickingArea area = new TickingArea(name,level.getName());
                    Vector2 centerVec2 = new Vector2(center.getChunkX(),center.getChunkZ());
                    for (int chunkX = center.getChunkX() - radius ; chunkX <= center.getChunkX() + radius ; chunkX++){
                        for (int chunkZ = center.getChunkZ() - radius ; chunkZ <= center.getChunkZ() + radius ; chunkZ++){
                            double distance = new Vector2(chunkX,chunkZ).distance(centerVec2);
                            if (distance <= radius){
                                area.addChunk(new TickingArea.ChunkPos(chunkX,chunkZ));
                            }
                        }
                    }
                    manager.addTickingArea(area);
                    sender.sendMessage(new TranslationContainer("commands.tickingarea-add-circle.success",(int)center.x + "," + (int)center.y + "," + (int)center.z,String.valueOf(radius)));
                    return true;
                }
                case "remove-pos": {
                    parser.parseString();//skip "remove"
                    Position pos = parser.parsePosition();
                    if (manager.getTickingAreaByPos(pos) == null) {
                        sender.sendMessage(new TranslationContainer("commands.tickingarea-remove.failure", String.valueOf((int) pos.x), String.valueOf((int) pos.y), String.valueOf((int) pos.z)));
                        return false;
                    }
                    manager.removeTickingArea(manager.getTickingAreaByPos(pos).getName());
                    sender.sendMessage(new TranslationContainer("commands.tickingarea-remove.success"));
                    return true;
                }
                case "remove-name": {
                    parser.parseString();//skip "remove"
                    String name = parser.parseString();
                    if (!manager.containTickingArea(name)) {
                        sender.sendMessage(new TranslationContainer("commands.tickingarea-remove.byname.failure", name));
                        return false;
                    }
                    manager.removeTickingArea(name);
                    sender.sendMessage(new TranslationContainer("commands.tickingarea-remove.success"));
                    return true;
                }
                case "remove-all": {
                    if (manager.getAllTickingArea().isEmpty()){
                        sender.sendMessage(new TranslationContainer("commands.tickingarea-list.failure.allDimensions"));
                        return false;
                    }
                    manager.removeAllTickingArea();
                    sender.sendMessage(new TranslationContainer("commands.tickingarea-remove_all.success"));
                    return true;
                }
                case "list": {
                    Set<TickingArea> areas = manager.getAllTickingArea();
                    parser.parseString();
                    boolean showAll = parser.hasNext();
                    if (!showAll){
                        areas = areas.stream().filter(area -> area.getLevelName().equals(level.getName())).collect(Collectors.toSet());
                        if (areas.isEmpty()){
                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.tickingarea-remove_all.failure"));
                            return false;
                        }
                        sender.sendMessage(new TranslationContainer(TextFormat.GREEN + "%commands.tickingarea-list.success.currentDimension"));
                        for (TickingArea area : areas){
                            List<TickingArea.ChunkPos> minAndMax = area.minAndMaxChunkPos();
                            TickingArea.ChunkPos min = minAndMax.get(0);
                            TickingArea.ChunkPos max = minAndMax.get(1);
                            sender.sendMessage(new TranslationContainer(" - " + area.getName() + ": " + min.x + " " + min.z + " %commands.tickingarea-list.to " + max.x + " " + max.z));
                        }
                        sender.sendMessage(new TranslationContainer("commands.tickingarea.inuse",String.valueOf(areas.size()),"∞"));
                    }else{
                        if (areas.isEmpty()){
                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.tickingarea-list.failure.allDimensions"));
                            return false;
                        }
                        sender.sendMessage(new TranslationContainer(TextFormat.GREEN + "%commands.tickingarea-list.success.allDimensions"));
                        for (TickingArea area : areas){
                            List<TickingArea.ChunkPos> minAndMax = area.minAndMaxChunkPos();
                            TickingArea.ChunkPos min = minAndMax.get(0);
                            TickingArea.ChunkPos max = minAndMax.get(1);
                            sender.sendMessage(new TranslationContainer(" - " + area.getName() + ": " + min.x + " " + min.z + " %commands.tickingarea-list.to " + max.x + " " + max.z));
                        }
                        sender.sendMessage(new TranslationContainer("commands.tickingarea.inuse",String.valueOf(areas.size()),"∞"));
                    }
                    return true;
                }
            }
        } catch (CommandSyntaxException e) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }
        return true;
    }
}
