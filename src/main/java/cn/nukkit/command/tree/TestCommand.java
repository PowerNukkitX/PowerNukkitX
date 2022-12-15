package cn.nukkit.command.tree;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.command.tree.node.WildcardIntNode;
import cn.nukkit.level.Position;

public class TestCommand extends VanillaCommand {
    private final ParamTree paramTree;

    public TestCommand(String name) {
        super(name, "commands.tp.description", "commands.tp.usage", new String[]{"testtp"});
        this.setPermission("nukkit.command.teleport");
        this.commandParameters.clear();
        this.commandParameters.put("pos", new CommandParameter[]{
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newType("max", CommandParamType.WILDCARD_INT, new WildcardIntNode(Integer.MAX_VALUE)),
                CommandParameter.newType("min", true, CommandParamType.WILDCARD_INT, new WildcardIntNode(Integer.MIN_VALUE))
        });
        this.paramTree = new ParamTree(this);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        var result = paramTree.matchAndParse(sender, args);
        if (result != null) {
            System.out.println(result.getKey());
            var list = result.getValue();
            Position pos = list.getResult(0);
            int value1 = list.getResult(1);
            System.out.println(pos);
            System.out.println(value1);
            if (list.hasResult(2)) {
                int value2 = result.getValue().getResult(2);
                System.out.println(value2);
            }
            return true;
        }
        return false;
    }
}
