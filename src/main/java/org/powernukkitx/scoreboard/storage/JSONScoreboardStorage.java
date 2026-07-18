package org.powernukkitx.scoreboard.storage;

import lombok.Getter;
import org.cloudburstmc.protocol.bedrock.data.ObjectiveSortOrder;
import org.powernukkitx.scoreboard.IScoreboard;
import org.powernukkitx.scoreboard.IScoreboardLine;
import org.powernukkitx.scoreboard.Scoreboard;
import org.powernukkitx.scoreboard.ScoreboardLine;
import org.powernukkitx.scoreboard.data.DisplaySlot;
import org.powernukkitx.scoreboard.scorer.EntityScorer;
import org.powernukkitx.scoreboard.scorer.FakeScorer;
import org.powernukkitx.scoreboard.scorer.IScorer;
import org.powernukkitx.scoreboard.scorer.PlayerScorer;
import org.powernukkitx.utils.Config;
import org.powernukkitx.utils.MapParsingUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;


@Getter
public class JSONScoreboardStorage implements IScoreboardStorage {

    protected Path filePath;
    protected Config json;
    private static final Function<String, RuntimeException> SCOREBOARD_ERROR =
            field -> new IllegalArgumentException("Invalid scoreboard data: " + field);

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
        if (scoreboard == null) return;
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
        Map<String, Object> scoreboards = MapParsingUtils.stringObjectMapOrNull(json.get("scoreboard"), "scoreboard", SCOREBOARD_ERROR);
        Map<String, IScoreboard> result = new HashMap<>();
        if (scoreboards == null) return result;
        for (Map.Entry<String, Object> entry : scoreboards.entrySet())
            result.put(entry.getKey(), deserializeFromMap(MapParsingUtils.stringObjectMap(entry.getValue(), "scoreboard entry", SCOREBOARD_ERROR)));
        return result;
    }

    @Override
    public IScoreboard readScoreboard(String name) {
        Object raw = json.get("scoreboard." + name);
        return raw == null ? null : deserializeFromMap(MapParsingUtils.stringObjectMap(raw, "scoreboard." + name, SCOREBOARD_ERROR));
    }

    @Override
    public Map<DisplaySlot, String> readDisplay() {
        Map<DisplaySlot, String> result = new HashMap<>();
        if (json.get("display") == null) return result;
        for (Map.Entry<String, String> e : MapParsingUtils.stringStringMapOrNull(json.get("display"), "display", SCOREBOARD_ERROR).entrySet()) {
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
                case CHANGE_PLAYER -> ((PlayerScorer) e.getScorer()).getUuid().toString();
                case CHANGE_ENTITY -> ((EntityScorer) e.getScorer()).getEntityUuid().toString();
                case CHANGE_FAKE_PLAYER -> ((FakeScorer) e.getScorer()).getFakeName();
                default -> null;
            });
            lines.add(line);
        }
        map.put("lines", lines);
        return map;
    }

    private IScoreboard deserializeFromMap(Map<String, Object> map) {
        if (map == null) return null;

        String objectiveName = Objects.toString(map.get("objectiveName"), null);
        String displayName = Objects.toString(map.get("displayName"), objectiveName);
        String criteriaName = Objects.toString(map.get("criteriaName"), "dummy");
        String sortRaw = Objects.toString(map.get("sortOrder"), ObjectiveSortOrder.ASCENDING.name());

        ObjectiveSortOrder sortOrder;
        try {
            sortOrder = ObjectiveSortOrder.valueOf(sortRaw);
        } catch (Exception e) {
            sortOrder = ObjectiveSortOrder.ASCENDING;
        }

        if (objectiveName == null) return null;
        IScoreboard scoreboard = new Scoreboard(objectiveName, displayName, criteriaName, sortOrder);
        Object linesObj = map.get("lines");
        if (!(linesObj instanceof List)) return scoreboard;

        for (Map<String, Object> line : (List<Map<String, Object>>) linesObj) {
            if (!line.containsKey("score") || !line.containsKey("scorerType")) continue;
            int score = ((Number) line.get("score")).intValue();
            String scorerType = Objects.toString(line.get("scorerType"), null);
            String name = Objects.toString(line.get("name"), null);
            if (scorerType == null) continue;
            IScorer scorer = null;
            switch (scorerType) {
                case "PLAYER":
                    if (name != null)
                        scorer = new PlayerScorer(UUID.fromString(name));
                    break;
                case "ENTITY":
                    if (name != null)
                        scorer = new EntityScorer(UUID.fromString(name));
                    break;
                case "FAKE":
                    if (name != null)
                        scorer = new FakeScorer(name);
                    break;
            }
            if (scorer != null) {
                scoreboard.addLine(new ScoreboardLine(scoreboard, scorer, score));
            }
        }
        return scoreboard;
    }
}
