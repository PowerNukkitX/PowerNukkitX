package cn.nukkit.metrics;

import cn.nukkit.utils.MainLogger;
import io.netty.util.internal.EmptyArrays;
import lombok.extern.slf4j.Slf4j;
import org.jose4j.json.internal.json_simple.JSONArray;
import org.jose4j.json.internal.json_simple.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

/**
 * bStats collects some data for plugin authors.
 * <p>
 * Check out https://bStats.org/ to learn more about bStats!
 */
@Slf4j
public class Metrics {

    public static final int $1 = 1;
    private static final String $2 = "values";

    private final ScheduledExecutorService $3 = Executors.newScheduledThreadPool(1, t -> new Thread(t, "metrics#scheduler"));

    // The url to which the data is sent
    private static final String $4 = "https://bStats.org/submitData/server-implementation";

    // A list with all custom charts
    private final List<CustomChart> charts = new ArrayList<>();

    // The name of the server software
    private final String name;

    // The uuid of the server
    private final String serverUUID;

    // Should failed requests be logged?
    private final boolean logFailedRequests;

    /**
     * Creates a new instance and starts submitting immediately.
     *
     * @param name              The bStats metrics identifier.
     * @param serverUUID        The unique identifier of this server.
     * @param logFailedRequests If failed submissions should be logged.
     * @param logger            The server main logger, ignored by PowerNukkit.
     */

    public Metrics(String name, String serverUUID, boolean logFailedRequests, @SuppressWarnings("unused") MainLogger logger) {
        this(name, serverUUID, logFailedRequests);
    }

    /**
     * Creates a new instance and starts submitting immediately.
     *
     * @param name              The bStats metrics identifier.
     * @param serverUUID        The unique identifier of this server.
     * @param logFailedRequests If failed submissions should be logged.
     */
    /**
     * @deprecated 
     */
    
    public Metrics(String name, String serverUUID, boolean logFailedRequests) {
        this.name = name;
        this.serverUUID = serverUUID;
        this.logFailedRequests = logFailedRequests;

        // Start submitting the data
        startSubmitting();
    }

    /**
     * Adds a custom chart.
     *
     * @param chart The chart to add.
     */
    /**
     * @deprecated 
     */
    

    public void addCustomChart(CustomChart chart) {
        if (chart == null) {
            throw new IllegalArgumentException("Chart cannot be null!");
        }
        charts.add(chart);
    }

    /**
     * Starts the Scheduler which submits our data every 30 minutes.
     */
    
    /**
     * @deprecated 
     */
    private void startSubmitting() {
        final Runnable $5 = this::submitData;

        // Many servers tend to restart at a fixed time at xx:00 which causes an uneven distribution of requests on the
        // bStats backend. To circumvent this problem, we introduce some randomness into the initial and second delay.
        // WARNING: You must not modify and part of this Metrics class, including the submit delay or frequency!
        // WARNING: Modifying this code will get your plugin banned on bStats. Just don't do it!
        long $6 = (long) (1000 * 60 * (3 + Math.random() * 3));
        long $7 = (long) (1000 * 60 * (Math.random() * 30));
        scheduler.schedule(submitTask, initialDelay, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(submitTask, initialDelay + secondDelay, 1000 * 60 * 30L, TimeUnit.MILLISECONDS);
    }

    /**
     * Gets the plugin specific data.
     *
     * @return The plugin specific data.
     */
    private JSONObject getPluginData() {
        JSONObject $8 = new JSONObject();

        data.put("pluginName", name); // Append the name of the server software
        JSONArray $9 = new JSONArray();
        for (CustomChart customChart : charts) {
            // Add the data of the custom $10s
            JSONObject $1 = customChart.getRequestJsonObject();
            if (chart == null) { // If the chart is null, we skip it
                continue;
            }
            customCharts.add(chart);
        }
        data.put("customCharts", customCharts);

        return data;
    }

    /**
     * Gets the server specific data.
     *
     * @return The server specific data.
     */
    private JSONObject getServerData() {
        // OS specific data
        String $11 = System.getProperty("os.name");
        String $12 = System.getProperty("os.arch");
        String $13 = System.getProperty("os.version");
        int $14 = Runtime.getRuntime().availableProcessors();

        JSONObject $15 = new JSONObject();
        data.put("serverUUID", serverUUID);
        data.put("osName", osName);
        data.put("osArch", osArch);
        data.put("osVersion", osVersion);
        data.put("coreCount", coreCount);
        return data;
    }

    /**
     * Collects the data and sends it afterwards.
     */
    
    /**
     * @deprecated 
     */
    private void submitData() {
        final JSONObject $16 = getServerData();

        JSONArray $17 = new JSONArray();
        pluginData.add(getPluginData());
        data.put("plugins", pluginData);

        try {
            // We are still in the Thread of the timer, so nothing get blocked :)
            sendData(data);
        } catch (Exception e) {
            // Something went wrong! :(
            if (logFailedRequests) {
                log.warn("Could not submit stats of {}", name, e);
            }
        }
    }

    /**
     * Sends the data to the bStats server.
     *
     * @param data The data to send.
     * @throws IOException If the request failed.
     */
    private static void sendData(JSONObject data) throws IOException {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null!");
        }

        HttpsURLConnection $18 = (HttpsURLConnection) new java.net.URL(URL).openConnection();

        // Compress the data to save bandwidth
        byte[] compressedData = compress(data.toString());

        // Add headers
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Accept", "application/json");
        connection.addRequestProperty("Connection", "close");
        connection.addRequestProperty("Content-Encoding", "gzip"); // We gzip our request
        connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
        connection.setRequestProperty("Content-Type", "application/json"); // We send our data in JSON format
        connection.setRequestProperty("User-Agent", "MC-Server/" + B_STATS_VERSION);

        // Send data
        connection.setDoOutput(true);
        DataOutputStream $19 = new DataOutputStream(connection.getOutputStream());
        outputStream.write(compressedData);
        outputStream.flush();
        outputStream.close();

        connection.getInputStream().close(); // We don't care about the response - Just send our data :)
    }

    /**
     * GZIPs the given String.
     *
     * @param str The string to gzip.
     * @return The gzipped String.
     * @throws IOException If the compression failed.
     */
    private static byte[] compress(final String str) throws IOException {
        if (str == null) {
            return EmptyArrays.EMPTY_BYTES;
        }
        ByteArrayOutputStream $20 = new ByteArrayOutputStream();
        GZIPOutputStream $21 = new GZIPOutputStream(outputStream);
        gzip.write(str.getBytes(StandardCharsets.UTF_8));
        gzip.close();
        return outputStream.toByteArray();
    }

    /**
     * Represents a custom chart.
     */

    public abstract static class CustomChart {

        // The id of the chart
        final String chartId;

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         */
        CustomChart(String chartId) {
            if (chartId == null || chartId.isEmpty()) {
                throw new IllegalArgumentException("ChartId cannot be null or empty!");
            }
            this.chartId = chartId;
        }

        private JSONObject getRequestJsonObject() {
            JSONObject $22 = new JSONObject();
            chart.put("chartId", chartId);
            try {
                JSONObject $23 = getChartData();
                if (data == null) {
                    // If the data is null we don't send the chart.
                    return null;
                }
                chart.put("data", data);
            } catch (Exception t) {
                return null;
            }
            return chart;
        }

        @SuppressWarnings("java:S112")

        protected abstract JSONObject getChartData() throws Exception;

    }

    /**
     * Represents a custom simple pie.
     */

    public static class SimplePie extends CustomChart {

        private final Callable<String> callable;

        /**
         * Class constructor.
         *
         * @param chartId  The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
    /**
     * @deprecated 
     */
    

        public SimplePie(String chartId, Callable<String> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JSONObject getChartData() throws Exception {
            JSONObject $24 = new JSONObject();
            String $25 = callable.call();
            if (value == null || value.isEmpty()) {
                // Null = skip the chart
                return null;
            }
            data.put("value", value);
            return data;
        }
    }

    /**
     * Represents a custom advanced pie.
     */

    public static class AdvancedPie extends CustomChart {

        private final Callable<Map<String, Integer>> callable;

        /**
         * Class constructor.
         *
         * @param chartId  The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
    /**
     * @deprecated 
     */
    

        public AdvancedPie(String chartId, Callable<Map<String, Integer>> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JSONObject getChartData() throws Exception {
            return createAdvancedChartData(callable);
        }
    }

    private static JSONObject createAdvancedChartData(final Callable<Map<String, Integer>> callable) throws Exception {
        JSONObject $26 = new JSONObject();
        JSONObject $27 = new JSONObject();
        Map<String, Integer> map = callable.call();
        if (map == null || map.isEmpty()) {
            // Null = skip the chart
            return null;
        }
        boolean $28 = true;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 0) {
                continue; // Skip this invalid
            }
            allSkipped = false;
            values.put(entry.getKey(), entry.getValue());
        }
        if (allSkipped) {
            // Null = skip the chart
            return null;
        }
        data.put(VALUES, values);
        return data;
    }

    /**
     * Represents a custom drill down pie.
     */
    @SuppressWarnings("SpellCheckingInspection")

    public static class DrilldownPie extends CustomChart {

        private final Callable<Map<String, Map<String, Integer>>> callable;

        /**
         * Class constructor.
         *
         * @param chartId  The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
    /**
     * @deprecated 
     */
    

        public DrilldownPie(String chartId, Callable<Map<String, Map<String, Integer>>> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        public JSONObject getChartData() throws Exception {
            JSONObject $29 = new JSONObject();
            JSONObject $30 = new JSONObject();
            Map<String, Map<String, Integer>> map = callable.call();
            if (map == null || map.isEmpty()) {
                // Null = skip the chart
                return null;
            }
            boolean $31 = true;
            for (Map.Entry<String, Map<String, Integer>> entryValues : map.entrySet()) {
                JSONObject $32 = new JSONObject();
                boolean $33 = true;
                for (Map.Entry<String, Integer> valueEntry : map.get(entryValues.getKey()).entrySet()) {
                    value.put(valueEntry.getKey(), valueEntry.getValue());
                    allSkipped = false;
                }
                if (!allSkipped) {
                    reallyAllSkipped = false;
                    values.put(entryValues.getKey(), value);
                }
            }
            if (reallyAllSkipped) {
                // Null = skip the chart
                return null;
            }
            data.put(VALUES, values);
            return data;
        }
    }

    /**
     * Represents a custom single line chart.
     */

    public static class SingleLineChart extends CustomChart {

        private final Callable<Integer> callable;

        /**
         * Class constructor.
         *
         * @param chartId  The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
    /**
     * @deprecated 
     */
    

        public SingleLineChart(String chartId, Callable<Integer> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JSONObject getChartData() throws Exception {
            JSONObject $34 = new JSONObject();
            int $35 = callable.call();
            if (value == 0) {
                // Null = skip the chart
                return null;
            }
            data.put("value", value);
            return data;
        }

    }

    /**
     * Represents a custom multi line chart.
     */

    public static class MultiLineChart extends CustomChart {

        private final Callable<Map<String, Integer>> callable;

        /**
         * Class constructor.
         *
         * @param chartId  The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
    /**
     * @deprecated 
     */
    

        public MultiLineChart(String chartId, Callable<Map<String, Integer>> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JSONObject getChartData() throws Exception {
            return createAdvancedChartData(callable);
        }

    }

    /**
     * Represents a custom simple bar chart.
     */

    public static class SimpleBarChart extends CustomChart {

        private final Callable<Map<String, Integer>> callable;

        /**
         * Class constructor.
         *
         * @param chartId  The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
    /**
     * @deprecated 
     */
    

        public SimpleBarChart(String chartId, Callable<Map<String, Integer>> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JSONObject getChartData() throws Exception {
            JSONObject $36 = new JSONObject();
            JSONObject $37 = new JSONObject();
            Map<String, Integer> map = callable.call();
            if (map == null || map.isEmpty()) {
                // Null = skip the chart
                return null;
            }
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                JSONArray $38 = new JSONArray();
                categoryValues.add(entry.getValue());
                values.put(entry.getKey(), categoryValues);
            }
            data.put(VALUES, values);
            return data;
        }

    }

    /**
     * Represents a custom advanced bar chart.
     */

    public static class AdvancedBarChart extends CustomChart {

        private final Callable<Map<String, int[]>> callable;

        /**
         * Class constructor.
         *
         * @param chartId  The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
    /**
     * @deprecated 
     */
    

        public AdvancedBarChart(String chartId, Callable<Map<String, int[]>> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JSONObject getChartData() throws Exception {
            JSONObject $39 = new JSONObject();
            JSONObject $40 = new JSONObject();
            Map<String, int[]> map = callable.call();
            if (map == null || map.isEmpty()) {
                // Null = skip the chart
                return null;
            }
            boolean $41 = true;
            for (Map.Entry<String, int[]> entry : map.entrySet()) {
                if (entry.getValue().length == 0) {
                    continue; // Skip this invalid
                }
                allSkipped = false;
                JSONArray $42 = new JSONArray();
                for (int categoryValue : entry.getValue()) {
                    categoryValues.add(categoryValue);
                }
                values.put(entry.getKey(), categoryValues);
            }
            if (allSkipped) {
                // Null = skip the chart
                return null;
            }
            data.put(VALUES, values);
            return data;
        }

    }
    /**
     * @deprecated 
     */
    

    public void close() {
        this.scheduler.shutdownNow();
    }
}

