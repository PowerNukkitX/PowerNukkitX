package cn.nukkit.command.defaults;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.Entity;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class TagCommand extends VanillaCommand {
    public TagCommand(String name) {
        super(name, "commands.tag.description");
        this.setPermission("nukkit.command.tag");
        this.commandParameters.clear();
        this.commandParameters.put("add", new CommandParameter[]{
                CommandParameter.newType("targets", CommandParamType.TARGET),
                CommandParameter.newEnum("add", new String[]{"add"}),
                CommandParameter.newType("name", CommandParamType.STRING)
        });
        this.commandParameters.put("remove", new CommandParameter[]{
                CommandParameter.newType("targets", CommandParamType.TARGET),
                CommandParameter.newEnum("remove", new String[]{"remove"}),
                CommandParameter.newType("name", CommandParamType.STRING)}
        );
        this.commandParameters.put("list", new CommandParameter[]{
                CommandParameter.newType("targets", CommandParamType.TARGET),
                CommandParameter.newEnum("list", new String[]{"list"}),
        });
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        List<Entity> entities = list.getResult(0);
        switch (result.getKey()) {
            case "add" -> {
                String tag = list.getResult(2);
                int success_count = 0;
                for (Entity entity : entities) {
                    if (entity.containTag(tag))
                        continue;
                    entity.addTag(tag);
                    success_count++;
                }
                if (success_count == 0) {
                    log.addError("commands.tag.add.failed").output();
                    return 0;
                }
                if (entities.size() == 1) {
                    log.addSuccess("commands.tag.add.success.single", tag, entities.get(0).getName());
                } else {
                    log.addSuccess("commands.tag.add.success.multiple", tag, String.valueOf(entities.size()));
                }
                log.output();
                return 1;
            }
            case "remove" -> {
                String tag = list.getResult(2);
                int success_count = 0;
                for (Entity entity : entities) {
                    if (!entity.containTag(tag))
                        continue;
                    entity.removeTag(tag);
                    success_count++;
                }
                if (success_count == 0) {
                    log.addError("commands.tag.remove.failed").output();
                    return 0;
                }
                if (entities.size() == 1) {
                    log.addSuccess("commands.tag.remove.success.single", tag, entities.get(0).getName());
                } else {
                    log.addSuccess("commands.tag.remove.success.multiple", tag, String.valueOf(entities.size()));
                }
                log.output();
                return 1;
            }
            case "list" -> {
                Set<String> tagSet = new HashSet<>();
                for (Entity entity : entities) {
                    tagSet.addAll(entity.getAllTags().stream().map(t -> t.data).collect(Collectors.toSet()));
                }
                int tagCount = tagSet.size();
                String tagStr = tagSet.stream().collect(Collectors.joining(" "));

                if (tagStr.isEmpty()) {
                    if (entities.size() == 1) {
                        log.addError("commands.tag.list.single.empty", entities.get(0).getName());
                    } else {
                        log.addError("commands.tag.list.multiple.empty", String.valueOf(entities.size()));
                    }
                    log.output();
                    return 0;
                } else {
                    if (entities.size() == 1) {
                        log.addSuccess("commands.tag.list.single.success", entities.get(0).getName(), String.valueOf(tagCount), tagStr);
                    } else {
                        log.addSuccess("commands.tag.list.multiple.success", String.valueOf(entities.size()), String.valueOf(tagCount), tagStr);
                    }
                    log.output();
                    return 1;
                }
            }
            default -> {
                return 0;
            }
        }
    }
}
