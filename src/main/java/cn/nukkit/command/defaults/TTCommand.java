package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.node.PlayersNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.inventory.fake.FakeStructBlock;
import cn.nukkit.item.Item;

import java.util.Map;


public class TTCommand extends TestCommand {
    FakeStructBlock fakeStructBlock;

    public TTCommand(String name) {
        super(name, "tt");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newEnum("sub", new String[]{"get", "set"}),
                CommandParameter.newType("name", false, CommandParamType.TARGET, new PlayersNode()),
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        ParamList value = result.getValue();
        String s = value.getResult(0);

        if (sender.isOp()) {
            Player p = sender.asPlayer();
            switch (s) {
                case "get" -> {
                    Item itemInHand = p.getInventory().getItemInHand();
                    itemInHand.setDamage(0);
                    p.getInventory().setItemInHand(itemInHand);
                }
                case "set" -> {
                }
            }
            return 1;
        } else return 0;
    }
}
