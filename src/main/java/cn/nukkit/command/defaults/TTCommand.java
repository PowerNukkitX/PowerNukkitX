package cn.nukkit.command.defaults;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.inventory.fake.FakeStructBlock;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

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
                Item item = Item.get(ItemID.APPLE);
                item.addCanDestroy(Block.get(BlockID.WARPED_DOOR));
                sender.asPlayer().getInventory().addItem(item);
            }
            return 1;
        } else return 0;
    }
}
