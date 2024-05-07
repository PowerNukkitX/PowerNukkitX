package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.inventory.fake.FakeStructBlock;
import cn.nukkit.level.biome.BiomeID;

import java.util.Map;


public class TTCommand extends TestCommand {
    FakeStructBlock fakeStructBlock;

    public TTCommand(String name) {
        super(name, "tt");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newEnum("sub", new String[]{"get", "set"})
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        ParamList value = result.getValue();
        String s = value.getResult(0);

        if (sender.isOp()) {
            boolean isPlayer = sender.isPlayer();
            if (isPlayer) {
                Player player = sender.asPlayer();
                switch (s) {
                    case "get" -> {
                        player.sendMessage(player.getLevel().getBiomeId(0, 3, 0) + "");
                        player.sendMessage(player.getLevel().getBiomeId(0, 4, 0) + "");
                        player.sendMessage(player.getLevel().getBlock(player.down()).getBlockState() + "");
                    }
                    case "set" -> {
                        for (int i = 0; i < 16; i++) {
                            for (int j = 0; j < 16; j++) {
                                for (int k = 0; k < 16; k++) {
                                    player.getLevel().setBiomeId(i, j, k, BiomeID.SWAMPLAND);
                                }
                            }
                        }
                    }
                }
            }
            return 1;
        } else return 0;
    }
}
