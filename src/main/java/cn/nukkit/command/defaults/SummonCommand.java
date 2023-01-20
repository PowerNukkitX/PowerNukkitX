package cn.nukkit.command.defaults;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.command.utils.EntitySelector;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class SummonCommand extends VanillaCommand {

    public SummonCommand(String name) {
        super(name, "commands.summon.description");
        this.setPermission("nukkit.command.summon");
        this.commandParameters.clear();
        List<String> entity_key = new ArrayList<>();
        for (String key : EntitySelector.ENTITY_ID2NAME.values()) {
            entity_key.add(key);
            entity_key.add(key.substring(10));
        }
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newEnum("entityType", false, entity_key.toArray(new String[0])),
                CommandParameter.newType("spawnPos", true, CommandParamType.POSITION),
                CommandParameter.newType("nameTag", true, CommandParamType.STRING),
                CommandParameter.newEnum("nameTagAlwaysVisible", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        String entityType = list.getResult(0);
        if (!entityType.startsWith("minecraft:"))
            entityType = "minecraft:" + entityType;
        if (entityType.equals("minecraft:player")) {
            log.addError("commands.summon.failed").output();
            return 0;
        }
        Integer entityId = EntitySelector.ENTITY_NAME2ID.get(entityType);
        if (entityId == null) {
            log.addError("commands.summon.failed").output();
            return 0;
        }
        Position pos = sender.getPosition();
        String nameTag = null;
        boolean nameTagAlwaysVisible = false;
        if (list.hasResult(1)) {
            pos = list.getResult(1);
        }
        if (!pos.level.isYInRange((int) pos.y) || !pos.getChunk().isLoaded()) {
            log.addError("commands.summon.outOfWorld").output();
            return 0;
        }
        if (list.hasResult(2)) {
            nameTag = list.getResult(2);
        }
        if (list.hasResult(3)) {
            nameTagAlwaysVisible = list.getResult(3);
        }
        Entity entity = Entity.createEntity(entityId, pos);
        if (nameTag != null) {
            entity.setNameTag(nameTag);
            entity.setNameTagAlwaysVisible(nameTagAlwaysVisible);
        }
        entity.spawnToAll();
        log.addSuccess("commands.summon.success").output();
        return 1;
    }
}
