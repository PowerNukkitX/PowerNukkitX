package cn.nukkit.scoreboard.storage;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.scoreboard.Scoreboard;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;
import cn.nukkit.scoreboard.interfaces.AbstractScoreboardManager;
import cn.nukkit.scoreboard.interfaces.ScoreboardStorage;
import cn.nukkit.scoreboard.interfaces.Scorer;
import cn.nukkit.scoreboard.scorer.EntityScorer;
import cn.nukkit.scoreboard.scorer.FakeScorer;
import cn.nukkit.scoreboard.scorer.PlayerScorer;
import cn.nukkit.utils.Config;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class JSONScoreboardStorage implements ScoreboardStorage {

    protected Path filePath;
    protected Config json;

    public JSONScoreboardStorage(String path) {
        this.filePath = Paths.get(path);
        try {
            if (!Files.exists(this.filePath)) {
                Files.createFile(this.filePath);
            }
            json = new Config(this.filePath.toFile(), Config.JSON);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveScoreboard(Scoreboard scoreboard) {
        json.set("scoreboard." + scoreboard.getObjectiveName(), serializeToMap(scoreboard));
        json.save();
    }

    @Override
    public void saveScoreboard(Scoreboard[] scoreboards) {
        for (Scoreboard scoreboard : scoreboards) {
            saveScoreboard(scoreboard);
        }
    }

    @Override
    public void saveDisplay(Map<DisplaySlot, String> display) {
        for (Map.Entry<DisplaySlot, String> entry : display.entrySet()) {
            json.set("display." + entry.getKey().name(), entry.getValue());
        }
        json.save();
    }

    @Override
    public Map<String, Scoreboard> readScoreboard(AbstractScoreboardManager manager) {
        Map<String, Object> scoreboards = (Map<String, Object>) json.get("scoreboard");
        Map<String, Scoreboard> result = new HashMap<>();
        if (scoreboards == null) return result;
        for (Map.Entry<String, Object> entry : scoreboards.entrySet()) {
            result.put(entry.getKey(), deserializeFromMap((Map<String, Object>) entry.getValue(), manager));
        }
        return result;
    }

    @Override
    public Scoreboard readScoreboard(String name, AbstractScoreboardManager manager) {
        return json.get("scoreboard." + name) == null ? null : deserializeFromMap((Map<String, Object>) json.get("scoreboard." + name), manager);
    }

    @Override
    public Map<DisplaySlot, String> readDisplay() {
        Map<DisplaySlot, String> result = new HashMap<>();
        if (json.get("display") == null) return result;
        for (Map.Entry<String, String> e : ((Map<String, String>) json.get("display")).entrySet()) {
            DisplaySlot slot = DisplaySlot.valueOf(e.getKey());
            result.put(slot, e.getValue());
        }
        return result;
    }

    @Override
    public void removeScoreboard(String name) {
        json.remove("scoreboard." + name);
        json.save();
    }

    @Override
    public boolean containScoreboard(String name) {
        return json.exists("scoreboard." + name);
    }

    private Map<String, Object> serializeToMap(Scoreboard scoreboard) {
        Map<String, Object> map = new HashMap<>();
        map.put("objectiveName", scoreboard.getObjectiveName());
        map.put("displayName", scoreboard.getDisplayName());
        map.put("criteriaName", scoreboard.getCriteriaName());
        map.put("sortOrder", scoreboard.getSortOrder().name());
        List<Map<String, Object>> lines = new ArrayList<>();
        for (Scoreboard.ScoreboardLine e : scoreboard.getLines().values()) {
            Map<String, Object> line = new HashMap<>();
            line.put("score", e.getScore());
            line.put("scorerType", e.getScorer().getScorerType().name());
            line.put("name", switch (e.getScorer().getScorerType()) {
                case PLAYER -> ((PlayerScorer) e.getScorer()).getUuid().toString();
                case ENTITY -> ((EntityScorer) e.getScorer()).getEntityUuid().toString();
                case FAKE -> ((FakeScorer) e.getScorer()).getFakeName();
                default -> null;
            });
            lines.add(line);
        }
        map.put("lines", lines);
        return map;
    }

    private Scoreboard deserializeFromMap(Map<String, Object> map, AbstractScoreboardManager manager) {
        String objectiveName = map.get("objectiveName").toString();
        String displayName = map.get("displayName").toString();
        String criteriaName = map.get("criteriaName").toString();
        SortOrder sortOrder = SortOrder.valueOf(map.get("sortOrder").toString());
        Scoreboard scoreboard = new Scoreboard(objectiveName, displayName, criteriaName, sortOrder, manager);
        for (Map<String, Object> line : (List<Map<String, Object>>) map.get("lines")) {
            int score = ((Double) line.get("score")).intValue();
            Scorer scorer = null;
            switch (line.get("scorerType").toString()) {
                case "PLAYER":
                    scorer = new PlayerScorer(UUID.fromString((String) line.get("name")));
                    break;
                case "ENTITY":
                    scorer = new EntityScorer(UUID.fromString((String) line.get("name")));
                    break;
                case "FAKE":
                    scorer = new FakeScorer((String) line.get("name"));
                    break;
            }
            scoreboard.addLine(scorer, score);
        }
        return scoreboard;
    }
}
