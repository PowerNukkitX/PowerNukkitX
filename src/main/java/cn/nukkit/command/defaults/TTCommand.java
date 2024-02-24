package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.inventory.fake.FakeInventoryType;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

@ApiStatus.Internal
public class TTCommand extends TestCommand {
    public TTCommand(String name) {
        super(name, "tt");
        this.commandParameters.clear();
        this.commandParameters.put("default", CommandParameter.EMPTY_ARRAY);
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        if (sender.isOp()) {
            boolean isPlayer = sender.isPlayer();
            if (isPlayer) {
                Player player = sender.asPlayer();
                FakeInventory test = new FakeInventory(player, FakeInventoryType.CHEST, "test");
                test.open();
            }
            return 1;
        } else return 0;
    }
}
