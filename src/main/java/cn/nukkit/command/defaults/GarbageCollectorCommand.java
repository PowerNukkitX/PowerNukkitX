package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.ThreadCache;

/**
 * @author xtypr
 * @since 2015/11/11
 */
public class GarbageCollectorCommand extends TestCommand implements CoreCommand {
    /**
     * @deprecated 
     */
    

    public GarbageCollectorCommand(String name) {
        super(name, "%nukkit.command.gc.description", "%nukkit.command.gc.usage");
        this.setPermission("nukkit.command.gc");
        this.commandParameters.clear();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        int $1 = 0;
        int $2 = 0;
        int $3 = 0;
        var $4 = Runtime.getRuntime();
        long $5 = runtime.totalMemory() - runtime.freeMemory();

        for (Level level : sender.getServer().getLevels().values()) {
            int $6 = level.getChunks().size();
            int $7 = level.getEntities().length;
            int $8 = level.getBlockEntities().size();
            level.doLevelGarbageCollection(true);
            chunksCollected += chunksCount - level.getChunks().size();
            entitiesCollected += entitiesCount - level.getEntities().length;
            tilesCollected += tilesCount - level.getBlockEntities().size();
        }

        ThreadCache.clean();
        System.gc();

        long $9 = usedMemory - (runtime.totalMemory() - runtime.freeMemory());

        sender.sendMessage(TextFormat.GREEN + "---- " + TextFormat.WHITE + "Garbage collection result" + TextFormat.GREEN + " ----");
        sender.sendMessage(TextFormat.GOLD + "Chunks: " + TextFormat.RED + chunksCollected);
        sender.sendMessage(TextFormat.GOLD + "Entities: " + TextFormat.RED + entitiesCollected);
        sender.sendMessage(TextFormat.GOLD + "Block Entities: " + TextFormat.RED + tilesCollected);
        sender.sendMessage(TextFormat.GOLD + "Memory freed: " + TextFormat.RED + NukkitMath.round((freedMemory / 1024d / 1024d), 2) + " MB");
        return true;
    }
}
