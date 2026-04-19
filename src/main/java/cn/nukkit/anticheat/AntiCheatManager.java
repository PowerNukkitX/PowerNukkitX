package cn.nukkit.anticheat;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.anticheat.check.impl.FlyCheck;
import cn.nukkit.anticheat.check.impl.ReachCheck;
import cn.nukkit.anticheat.check.impl.SpeedCheck;
import cn.nukkit.anticheat.check.impl.WaterFlyCheck;
import cn.nukkit.anticheat.check.impl.FastBreakCheck;
import cn.nukkit.anticheat.check.impl.TimerCheck;
import cn.nukkit.anticheat.check.impl.AutoClickerCheck;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;
public class AntiCheatManager {
    @Getter
    private static final AntiCheatManager instance = new AntiCheatManager();
    private final List<ICheatCheck> checks = new ArrayList<>();

    private final AutoClickerCheck autoClickerCheck = new AutoClickerCheck();

    private AntiCheatManager() {
        checks.add(new FlyCheck());
        checks.add(new SpeedCheck());
        checks.add(new ReachCheck());
        checks.add(new WaterFlyCheck());
        checks.add(new FastBreakCheck());
        checks.add(new TimerCheck());
        checks.add(autoClickerCheck);
    }

    public AutoClickerCheck getAutoClickerCheck() {
        return autoClickerCheck;
    }

    public List<ICheatCheck> getChecks() {
        return checks;
    }

    public void runChecks(Player player) {
        if (!isEnabled()) return;

        for (ICheatCheck check : checks) {
            check.check(player);
        }
    }
    public boolean isEnabled() {
        return Server.getInstance().getSettings().antiCheatSettings().enabled();
    }
}
