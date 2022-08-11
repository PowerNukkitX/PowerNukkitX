package cn.nukkit.command.defaults;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ExecutorCommandSender;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class ExecuteCommand extends VanillaCommand{

    public ExecuteCommand(String name) {
        super(name,"commands.execute.description", "commands.execute.usage");
        this.setPermission("nukkit.command.execute");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, preHandleArgs(args), false);

        return nextSubCommand(sender, parser);
    }

    /**
     * 因为在新的execute命令中我们不能使用命令解析器的模板匹配功能
     * 所以说我们需要预处理参数以将类似于"~~~"的输入分割为"~ ~ ~"
     */
    protected String[] preHandleArgs(String[] args){
        List<String> handled = new LinkedList<>();
        for (int argPointer = 0; argPointer < args.length; argPointer++){
            String arg = args[argPointer];
            //只解析run子命令之前的内容，防止干扰其他命令的解析
            if (arg.equals("run")){
                for (int i = argPointer;i < args.length;i++){
                    handled.add(args[i]);
                }
                break;
            }
            //不需要处理目标选择器
            if (arg.startsWith("@")) {
                handled.add(arg);
                continue;
            }
            char[] chars = arg.toCharArray();
            int pointer = 0;
            int start = 0;
            for (char c : chars){
                if (c == '~' || c == '^'){
                    if (start != pointer)
                        handled.add(arg.substring(start, pointer));
                    start = pointer;
                }
                pointer++;
            }
            //加上剩下的
            handled.add(arg.substring(start));
        }
        return handled.toArray(new String[0]);
    }

    protected boolean nextSubCommand(CommandSender sender,CommandParser parser){
        try {
            switch (parser.parseString()) {
                case "as" -> {
                    List<Entity> executors = parser.parseTargets();
                    boolean success = true;
                    for (Entity executor : executors){
                        ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender,executor,sender.getLocation());
                        if (!nextSubCommand(executorCommandSender, new CommandParser(parser,executorCommandSender)) && success){
                            success = false;
                        }
                    }
                    return success;
                }
                case "at" -> {
                    List<Entity> locations = parser.parseTargets();
                    if (locations.isEmpty()) return false;
                    boolean success = true;
                    for (Location location : locations){
                        ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender,sender.asEntity(),location);
                        if (!nextSubCommand(executorCommandSender, new CommandParser(parser,executorCommandSender)) && success){
                            success = false;
                        }
                    }
                    return success;
                }
                case "run" -> {
                    String command = parser.parseAllRemain();
                    if (command.isEmpty()) return false;
                    return sender.getServer().dispatchCommand(sender, command);
                }
                case "positioned" -> {
                    if (parser.parseString(false).equals("as")){
                        parser.parseString();//skip "as"
                        List<Entity> targets = parser.parseTargets();
                        boolean success = true;
                        for (Vector3 vec : targets){
                            Location newLoc = sender.getLocation();
                            newLoc.setX(vec.getX());
                            newLoc.setY(vec.getY());
                            newLoc.setZ(vec.getZ());
                            ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender,sender.asEntity(),newLoc);
                            if (!nextSubCommand(executorCommandSender, new CommandParser(parser,executorCommandSender)) && success){
                                success = false;
                            }
                        }
                        return success;
                    }else{
                        Vector3 vec = parser.parsePosition();
                        Location newLoc = sender.getLocation();
                        newLoc.setX(vec.getX());
                        newLoc.setY(vec.getY());
                        newLoc.setZ(vec.getZ());
                        ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender,sender.asEntity(),newLoc);
                        return nextSubCommand(executorCommandSender, new CommandParser(parser,executorCommandSender));
                    }
                }
            }
        } catch (CommandSyntaxException e){
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }
        return true;
    }
}
