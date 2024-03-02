package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.inventory.fake.FakeInventoryType;
import cn.nukkit.inventory.fake.FakeStructBlock;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

@ApiStatus.Internal
public class TTCommand extends TestCommand {
    FakeStructBlock fakeStructBlock;

    public TTCommand(String name) {
        super(name, "tt");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newEnum("sub", new String[]{"1", "2"}),
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        ParamList value = result.getValue();
        String v = value.getResult(0);

        if (sender.isOp()) {
            boolean isPlayer = sender.isPlayer();
            if (isPlayer) {
                Player player = sender.asPlayer();
                if (v.equals("1")) {
                    FakeInventory fakeInventory = new FakeInventory(FakeInventoryType.DOUBLE_CHEST);
                    fakeInventory.setDefaultItemHandler((inv, slot, item, e) -> {
                        e.setCancelled();
                    });
                    player.addWindow(fakeInventory);
                } else if (v.equals("2")) {
                }
            }
            return 1;
        } else return 0;
    }
}
