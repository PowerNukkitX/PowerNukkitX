package cn.nukkit.command.selector.args.impl;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.selector.ParseUtils;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.command.selector.args.CachedSimpleSelectorArgument;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.custom.CustomEntity;
import cn.nukkit.level.Location;
import cn.nukkit.network.protocol.AddEntityPacket;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Predicate;

//TODO: 不支持自定义生物
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class Type extends CachedSimpleSelectorArgument {

    protected static final Map<Integer, String> ENTITY_ID2TYPE = AddEntityPacket.LEGACY_IDS;
    protected static final Map<String, Integer> ENTITY_TYPE2ID;

    static {
        ImmutableMap.Builder<String, Integer> builder = ImmutableMap.builder();
        ENTITY_ID2TYPE.forEach((id, name) -> builder.put(name, id));
        ENTITY_TYPE2ID = builder.build();
    }

    @Override
    protected Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) {
        final var have = new ArrayList<String>();
        final var dontHave = new ArrayList<String>();
        for (var type : arguments) {
            boolean reversed = ParseUtils.checkReversed(type);
            if (reversed) {
                type = completionPrefix(type.substring(1));
                dontHave.add(type);
            } else have.add(completionPrefix(type));
        }
        //predicates.add(entity -> entity != null && (entity instanceof Player && identifier.equals("minecraft:player") || ENTITY_NAME2ID.get(identifier) == entity.getNetworkId()) != inverted);
        return entity -> have.stream().allMatch(type -> isType(entity, type)) && dontHave.stream().noneMatch(type ->isType(entity, type));
    }

    @Override
    public String getKeyName() {
        return "type";
    }

    @Override
    public int getPriority() {
        return 3;
    }

    protected String completionPrefix(String type) {
        var completed = type.startsWith("minecraft:") ? type : "minecraft:" + type;
        if (!ENTITY_TYPE2ID.containsKey(type) && !ENTITY_TYPE2ID.containsKey(completed)) {
            //是自定义生物，不需要补全
            return type;
        }
        return completed;
    }

    protected boolean isType(Entity entity, String type) {
        if (entity instanceof Player)
            //player需要特判，因为EntityHuman的getNetworkId()返回-1
            return type.equals("minecraft:player");
        else if (entity instanceof CustomEntity customEntity)
            return customEntity.getDefinition().getStringId().equals(type);
        else
            return ENTITY_TYPE2ID.containsKey(type) && entity.getNetworkId() == ENTITY_TYPE2ID.get(type);

    }
}
