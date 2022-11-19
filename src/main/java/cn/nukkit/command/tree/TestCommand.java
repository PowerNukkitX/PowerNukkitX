package cn.nukkit.command.tree;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CoreCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Position;

public class TestCommand extends CoreCommand {
    private final ParamTree paramTree;

    public TestCommand(String name) {
        super(name, "commands.tp.description", "commands.tp.usage", new String[]{"testtp"});
        this.setPermission("nukkit.command.teleport");
        this.commandParameters.clear();
        this.commandParameters.put("pos", new CommandParameter[]{
                CommandParameter.newType("destination", CommandParamType.POSITION)
        });
        this.paramTree = new ParamTree(this);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        var result = paramTree.matchAndParse(sender, args);
        Position pos = result.get(0).get();
        System.out.println(pos);
        return true;
    }
}
