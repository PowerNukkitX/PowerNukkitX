package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.inventory.fake.FakeInventoryType;
import cn.nukkit.inventory.fake.FakeStructBlock;
import cn.nukkit.item.ItemApple;
import cn.nukkit.item.ItemEmerald;

import java.util.Map;


public class TTCommand extends TestCommand {
    FakeStructBlock fakeStructBlock;

    public TTCommand(String name) {
        super(name, "tt");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{});
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        if (sender.isOp()) {
            boolean isPlayer = sender.isPlayer();
            if (isPlayer) {
                var t = new FakeInventory(FakeInventoryType.CHEST);
                t.setItem(9, new ItemApple());
                t.setDefaultItemHandler((inventory, slot, targetItem, sourceItem, event) -> {
                    if (slot == 9) {
                        System.out.println("test");
                        inventory.setItem(9, new ItemEmerald());
                        event.setCancelled();
                    }
                });
                Player player = sender.asPlayer();
                player.addWindow(t);
            }
            return 1;
        } else return 0;
    }
}
