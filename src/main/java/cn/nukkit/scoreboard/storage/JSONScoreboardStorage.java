package cn.nukkit.scoreboard.storage;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;
import cn.nukkit.scoreboard.scoreboard.IScoreboard;
import cn.nukkit.scoreboard.scoreboard.IScoreboardLine;
import cn.nukkit.scoreboard.scoreboard.Scoreboard;
import cn.nukkit.scoreboard.scoreboard.ScoreboardLine;
import cn.nukkit.scoreboard.scorer.EntityScorer;
import cn.nukkit.scoreboard.scorer.FakeScorer;
import cn.nukkit.scoreboard.scorer.IScorer;
import cn.nukkit.scoreboard.scorer.PlayerScorer;
import cn.nukkit.utils.Config;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@PowerNukkitXOnly
@Since("1.19.30-r1")
@Getter
public class JSONScoreboardStorage implements IScoreboardStorage {

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
    public void saveScoreboard(IScoreboard scoreboard) {
        json.set("scoreboard." + scoreboard.getObjectiveName(), serializeToMap(scoreboard));
        json.save();
    }

    @Override
    public void saveScoreboard(Collection<IScoreboard> scoreboards) {
        for (var scoreboard : scoreboards) saveScoreboard(scoreboard);
    }

    @Override
    public void saveDisplay(Map<DisplaySlot, IScoreboard> display) {
        for (Map.Entry<DisplaySlot, IScoreboard> entry : display.entrySet()) {
            json.set("display." + entry.getKey().name(), entry.getValue() != null ? entry.getValue().getObjectiveName() : null);
        }
        json.save();
    }

    @Override
    public Map<String, IScoreboard> readScoreboard() {
        Map<String, Object> scoreboards = (Map<String, Object>) json.get("scoreboard");
        Map<String, IScoreboard> result = new HashMap<>();
        if (scoreboards == null) return result;
        for (Map.Entry<String, Object> entry : scoreboards.entrySet())
            result.put(entry.getKey(), deserializeFromMap((Map<String, Object>) entry.getValue()));
        return result;
    }

    @Override
    public IScoreboard readScoreboard(String name) {
        return json.get("scoreboard." + name) == null ? null : deserializeFromMap((Map<String, Object>) json.get("scoreboard." + name));
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
    public void removeAllScoreboard() {
        json.remove("scoreboard");
        json.save();
    }

    @Override
    public boolean containScoreboard(String name) {
        return json.exists("scoreboard." + name);
    }

    private Map<String, Object> serializeToMap(IScoreboard scoreboard) {
        Map<String, Object> map = new HashMap<>();
        map.put("objectiveName", scoreboard.getObjectiveName());
        map.put("displayName", scoreboard.getDisplayName());
        map.put("criteriaName", scoreboard.getCriteriaName());
        map.put("sortOrder", scoreboard.getSortOrder().name());
        List<Map<String, Object>> lines = new ArrayList<>();
        for (IScoreboardLine e : scoreboard.getLines().values()) {
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

    private IScoreboard deserializeFromMap(Map<String, Object> map) {
        String objectiveName = map.get("objectiveName").toString();
        String displayName = map.get("displayName").toString();
        String criteriaName = map.get("criteriaName").toString();
        SortOrder sortOrder = SortOrder.valueOf(map.get("sortOrder").toString());
        IScoreboard scoreboard = new Scoreboard(objectiveName, displayName, criteriaName, sortOrder);
        for (Map<String, Object> line : (List<Map<String, Object>>) map.get("lines")) {
            int score = Integer.parseInt(line.get("score").toString());
            IScorer scorer = null;
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
            scoreboard.addLine(new ScoreboardLine(scoreboard, scorer, score));
        }
        return scoreboard;
    }
}
