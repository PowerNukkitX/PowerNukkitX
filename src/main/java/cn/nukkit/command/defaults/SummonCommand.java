package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.selector.args.impl.Type;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.BVector3;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.registry.Registries;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SummonCommand extends VanillaCommand {

    public SummonCommand(String name) {
        super(name, "commands.summon.description");
        this.setPermission("nukkit.command.summon");
        this.commandParameters.clear();

        List<String> entity_key = new ArrayList<>();
        for (String key : Registries.ENTITY.getKnownEntities().keySet()) {
            entity_key.add(key.replace("minecraft:", ""));
        }
        String[] entities = entity_key.toArray(new String[0]);

        // Syntax 1: /summon <entityType> <nameTag> [spawnPos]
        this.commandParameters.put("BasicWithName", new CommandParameter[]{
                CommandParameter.newEnum("entityType", false, entities, true),
                CommandParameter.newType("nameTag", false, CommandParamType.STRING),
                CommandParameter.newType("spawnPos", true, CommandParamType.POSITION)
        });

        // Syntax 2: /summon <entityType> [spawnPos] [yRot] [xRot] [nameTag]
        this.commandParameters.put("WithRotation", new CommandParameter[]{
                CommandParameter.newEnum("entityType", false, entities, true),
                CommandParameter.newType("spawnPos", true, CommandParamType.POSITION),
                CommandParameter.newType("yRot", true, CommandParamType.VALUE),
                CommandParameter.newType("xRot", true, CommandParamType.VALUE),
                CommandParameter.newType("nameTag", true, CommandParamType.STRING)
        });

        // Syntax 3: /summon <entityType> [spawnPos] facing <lookAtEntity> [nameTag]
        this.commandParameters.put("FacingEntity", new CommandParameter[]{
                CommandParameter.newEnum("entityType", false, entities, true),
                CommandParameter.newType("spawnPos", true, CommandParamType.POSITION),
                CommandParameter.newEnum("facing", false, new String[]{"facing"}),
                CommandParameter.newType("lookAtEntity", CommandParamType.TARGET),
                CommandParameter.newType("nameTag", true, CommandParamType.STRING)
        });

        // Syntax 4: /summon <entityType> [spawnPos] facing <lookAtPosition> [nameTag]
        this.commandParameters.put("FacingPos", new CommandParameter[]{
                CommandParameter.newEnum("entityType", false, entities, true),
                CommandParameter.newType("spawnPos", true, CommandParamType.POSITION),
                CommandParameter.newEnum("facing", false, new String[]{"facing"}),
                CommandParameter.newType("lookAtPosition", CommandParamType.POSITION),
                CommandParameter.newType("nameTag", true, CommandParamType.STRING)
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

        Position spawnPos = sender.getPosition();
        float yaw = 0;
        float pitch = 0;
        String nameTag = null;

        switch (result.getKey()) {
            case "BasicWithName" -> {
                nameTag = list.getResult(1);
                if (list.hasResult(2)) spawnPos = list.getResult(2);
            }
            case "WithRotation" -> {
                if (list.hasResult(1)) spawnPos = list.getResult(1);
                if (list.hasResult(2)) yaw = ((Number) list.getResult(2)).floatValue();
                if (list.hasResult(3)) pitch = ((Number) list.getResult(3)).floatValue();
                if (list.hasResult(4)) nameTag = list.getResult(4);
            }
            case "FacingEntity" -> {
                if (list.hasResult(1)) spawnPos = list.getResult(1);
                List<Entity> targets = list.getResult(3);
                if (!targets.isEmpty()) {
                    BVector3 bv = BVector3.fromPos(targets.getFirst().subtract(spawnPos));
                    yaw = (float) bv.getYaw();
                    pitch = (float) bv.getPitch();
                }
                if (list.hasResult(4)) nameTag = list.getResult(4);
            }
            case "FacingPos" -> {
                if (list.hasResult(1)) spawnPos = list.getResult(1);
                Position lookAt = list.getResult(3);
                BVector3 bv = BVector3.fromPos(lookAt.subtract(spawnPos));
                yaw = (float) bv.getYaw();
                pitch = (float) bv.getPitch();
                if (list.hasResult(4)) nameTag = list.getResult(4);
            }
        }

        if (!spawnPos.level.isYInRange((int) spawnPos.y) || !spawnPos.getChunk().isLoaded()) {
            log.addError("commands.summon.outOfWorld").output();
            return 0;
        }

        Integer entityId = Type.ENTITY_TYPE2ID.get(entityType);
        String fullId = Optional.ofNullable(entityId).map(Registries.ENTITY::getEntityIdentifier).orElse(entityType);
        EntityRegistry.EntityDefinition def = Registries.ENTITY.getEntityDefinition(fullId);

        if (def == null || !def.isSummonable()) {
            log.addError("commands.summon.failed").output();
            return 0;
        }

        Location finalLoc = new Location(spawnPos.x, spawnPos.y, spawnPos.z, yaw, pitch, spawnPos.level);
        Entity entity = Entity.createEntity(fullId, finalLoc);

        if (entity == null) {
            log.addError("commands.summon.failed").output();
            return 0;
        }

        if (nameTag != null && !nameTag.isEmpty()) {
            entity.setNameTag(nameTag);
            entity.setNameTagAlwaysVisible(true);
        }

        entity.spawnToAll();
        log.addSuccess("commands.summon.success").output();
        return 1;
    }

    protected String completionPrefix(String type) {
        var completed = type.contains(":") ? type : "minecraft:" + type;
        if (!Type.ENTITY_TYPE2ID.containsKey(type) && !Type.ENTITY_TYPE2ID.containsKey(completed)) {
            return type;
        }
        return completed;
    }
}