package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.EntitySelector;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.List;

public class SummonCommand extends VanillaCommand {

    public SummonCommand(String name){
        super(name, "commands.summon.description");
        this.setPermission("nukkit.command.summon");
        this.commandParameters.clear();
        List<String> entity_key = new ArrayList<>();
        for (String key : EntitySelector.ENTITY_ID2NAME.values()) {
            entity_key.add(key);
            entity_key.add(key.substring(10));
        }
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newEnum("entityType",false, entity_key.toArray(new String[0])),
                CommandParameter.newType("spawnPos",true, CommandParamType.POSITION),
                CommandParameter.newType("nameTag",true, CommandParamType.STRING),
                CommandParameter.newEnum("nameTagAlwaysVisible",true,CommandEnum.ENUM_BOOLEAN)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this,sender,args);
        try {
            String entityType = parser.parseString();
            if (!entityType.startsWith("minecraft:"))
                entityType = "minecraft:" + entityType;
            if (entityType.equals("minecraft:player")) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.summon.failed"));
                return false;
            }
            Integer entityId = EntitySelector.ENTITY_NAME2ID.get(entityType);
            if (entityId == null) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.summon.failed"));
                return false;
            }
            Position pos = sender.getPosition();
            String nameTag = null;
            boolean nameTagAlwaysVisible = false;
            if (parser.hasNext()) {
                pos = parser.parsePosition();
            }
            if (!pos.level.isYInRange((int) pos.y) || !pos.getChunk().isLoaded()) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.summon.outOfWorld"));
                return false;
            }
            if (parser.hasNext()) {
                nameTag = parser.parseString();
            }
            if (parser.hasNext()) {
                nameTagAlwaysVisible = parser.parseBoolean();
            }
            Entity entity = Entity.createEntity(entityId,pos);
            if (nameTag != null) {
                entity.setNameTag(nameTag);
                entity.setNameTagAlwaysVisible(nameTagAlwaysVisible);
            }
            entity.spawnToAll();
            sender.sendMessage(new TranslationContainer("commands.summon.success"));
            return true;
        } catch (CommandSyntaxException e) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }
    }
}
