package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.inventory.fake.FakeStructBlock;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.object.BlockManager;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


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
                    }
                    case "set" -> {
                        Position startPos = new Position(0, 0, 0);
                        Position endPos = new Position(100, 100, 100);
                        BlockManager blockManager = new BlockManager(player.getLevel());
                        for (int x = (int) startPos.getX(); x <= endPos.getX(); x++) {
                            for (int y = (int) startPos.getY(); y <= endPos.getY(); y++) {
                                for (int z = (int) startPos.getZ(); z <= endPos.getZ(); z++) {
                                    Block randomBlockId = ThreadLocalRandom.current().nextBoolean() ? Block.get(BlockID.AMETHYST_BLOCK) : Block.get(BlockID.BAMBOO_BLOCK);
                                    blockManager.setBlockStateAt(x, y, z, randomBlockId.getBlockState());
                                }
                            }
                        }
                        blockManager.applySubChunkUpdate();
                    }
                }
            }
            return 1;
        } else return 0;
    }
}
