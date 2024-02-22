package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.selector.args.impl.Type;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Position;
import cn.nukkit.registry.Registries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SummonCommand extends VanillaCommand {

    public SummonCommand(String name) {
        super(name, "commands.summon.description");
        this.setPermission("nukkit.command.summon");
        this.commandParameters.clear();
        List<String> entity_key = new ArrayList<>();
        for (String key : Registries.ENTITY.getKnownEntities().keySet()) {
            entity_key.add(key.replace("minecraft:",""));
        }
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newEnum("entityType", false, entity_key.toArray(new String[0]), true),
                CommandParameter.newType("spawnPos", true, CommandParamType.POSITION),
                CommandParameter.newType("nameTag", true, CommandParamType.STRING),
                CommandParameter.newEnum("nameTagAlwaysVisible", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        String entityType = completionPrefix(list.getResult(0));
        if (entityType.equals("minecraft:player")) {
            log.addError("commands.summon.failed").output();
            return 0;
        }
        Integer entityId = Type.ENTITY_TYPE2ID.get(entityType);
        Position pos = sender.getPosition();
        if (list.hasResult(1)) {
            pos = list.getResult(1);
        }
        if (!pos.level.isYInRange((int) pos.y) || !pos.getChunk().isLoaded()) {
            log.addError("commands.summon.outOfWorld").output();
            return 0;
        }
        String nameTag = null;
        if (list.hasResult(2)) {
            nameTag = list.getResult(2);
        }
        boolean nameTagAlwaysVisible = false;
        if (list.hasResult(3)) {
            nameTagAlwaysVisible = list.getResult(3);
        }
        Entity entity;
        if (entityId != null) {
            //原版生物
            entity = Entity.createEntity(entityId, pos);
        } else {
            //自定义生物
            entity = Entity.createEntity(entityType, pos);
        }
        if (entity == null) {
            log.addError("commands.summon.failed").output();
            return 0;
        }
        if (nameTag != null) {
            entity.setNameTag(nameTag);
            entity.setNameTagAlwaysVisible(nameTagAlwaysVisible);
        }
        entity.spawnToAll();
        log.addSuccess("commands.summon.success").output();
        return 1;
    }

    protected String completionPrefix(String type) {
        var completed = type.contains(":") ? type : "minecraft:" + type;
        if (!Type.ENTITY_TYPE2ID.containsKey(type) && !Type.ENTITY_TYPE2ID.containsKey(completed)) {
            //是自定义生物，不需要补全
            return type;
        }
        return completed;
    }
}
