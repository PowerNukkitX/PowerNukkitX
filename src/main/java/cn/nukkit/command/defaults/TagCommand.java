package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TagCommand extends VanillaCommand{
    public TagCommand(String name){
        super(name, "commands.tag.description");
        this.setPermission("nukkit.command.tag");
        this.commandParameters.clear();
        this.commandParameters.put("add",new CommandParameter[]{
                CommandParameter.newType("targets", CommandParamType.TARGET),
                CommandParameter.newEnum("add", new String[]{"add"}),
                CommandParameter.newType("name", CommandParamType.STRING)
        });
        this.commandParameters.put("remove",new CommandParameter[]{
                CommandParameter.newType("targets", CommandParamType.TARGET),
                CommandParameter.newEnum("remove", new String[]{"remove"}),
                CommandParameter.newType("name", CommandParamType.STRING)}
        );
        this.commandParameters.put("list",new CommandParameter[]{
                CommandParameter.newType("targets", CommandParamType.TARGET),
                CommandParameter.newEnum("list", new String[]{"list"}),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this,sender,args);
        try{
            String form = parser.matchCommandForm();
            if (form == null){
                sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
                return false;
            };
            List<Entity> entities = parser.parseTargets();
            if (entities.isEmpty()){
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.noTargetMatch"));
                return false;
            }
            switch (form){
                case "add" ->{
                    parser.parseString();//jump over "add"
                    String tag = parser.parseString();
                    int success_count = 0;
                    for (Entity entity : entities) {
                        if (entity.containTag(tag))
                            continue;
                        entity.addTag(tag);
                        success_count++;
                    }
                    if (success_count == 0){
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.tag.add.failed"));
                        return false;
                    }
                    if (entities.size() == 1){
                        sender.sendMessage(new TranslationContainer("commands.tag.add.success.single",tag,entities.get(0).getName()));
                    }else{
                        sender.sendMessage(new TranslationContainer("commands.tag.add.success.multiple",tag,String.valueOf(entities.size())));
                    }
                    return true;
                }
                case "remove" ->{
                    parser.parseString();//jump over "remove"
                    String tag = parser.parseString();
                    int success_count = 0;
                    for (Entity entity : entities) {
                        if (!entity.containTag(tag))
                            continue;
                        entity.removeTag(tag);
                        success_count++;
                    }
                    if (success_count == 0){
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.tag.remove.failed"));
                        return false;
                    }
                    if (entities.size() == 1){
                        sender.sendMessage(new TranslationContainer("commands.tag.remove.success.single",tag,entities.get(0).getName()));
                    }else{
                        sender.sendMessage(new TranslationContainer("commands.tag.remove.success.multiple",tag,String.valueOf(entities.size())));
                    }
                    return true;
                }
                case "list" ->{
                    Set<String> tagSet = new HashSet<>();
                    for(Entity entity : entities) {
                        tagSet.addAll(entity.getAllTags().stream().map(t -> t.data).collect(Collectors.toSet()));
                    }
                    int tagCount = tagSet.size();
                    String tagStr = tagSet.stream().collect(Collectors.joining(" "));

                    if (tagStr.isEmpty()){
                        if (entities.size() == 1){
                            sender.sendMessage(new TranslationContainer("commands.tag.list.single.empty",entities.get(0).getName()));
                        }else{
                            sender.sendMessage(new TranslationContainer("commands.tag.list.multiple.empty",String.valueOf(entities.size())));
                        }
                    }else{
                        if (entities.size() == 1){
                            sender.sendMessage(new TranslationContainer("commands.tag.list.single.success",entities.get(0).getName(),String.valueOf(tagCount),tagStr));
                        }else{
                            sender.sendMessage(new TranslationContainer("commands.tag.list.multiple.success",String.valueOf(entities.size()),String.valueOf(tagCount),tagStr));
                        }
                    }
                    return true;
                }
            }
        } catch (CommandSyntaxException e) {
             sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
        }
        return false;
    }
}
