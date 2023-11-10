package me.illusion.skyblockcore.common.database.metrics.influx;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteOptions;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.client.write.events.WriteSuccessEvent;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import me.illusion.skyblockcore.common.config.section.ConfigurationSection;
import me.illusion.skyblockcore.common.database.AbstractSkyblockDatabase;
import me.illusion.skyblockcore.common.metrics.SkyblockMetric;
import me.illusion.skyblockcore.common.metrics.SkyblockMetricBroker;
import me.illusion.skyblockcore.common.metrics.data.InternalSkyblockMetric;
import me.illusion.skyblockcore.common.metrics.data.MetricTimestamp;
import me.illusion.skyblockcore.common.metrics.provider.SkyblockMetricProvider;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;
import me.illusion.skyblockcore.common.scheduler.ThreadContext;
import me.illusion.skyblockcore.common.utilities.time.Time;
import me.illusion.skyblockcore.common.utilities.time.TimeParser;

public class InfluxMetricsDatabase extends AbstractSkyblockDatabase implements SkyblockMetricBroker {

    private final Map<String, SkyblockMetricProvider> providers = new ConcurrentHashMap<>();

    private CompletableFuture<Void> currentWrite = new CompletableFuture<>();

    private InfluxDBClient client;
    private WriteApi writeApi;

    @Override
    public String getName() {
        return "influx";
    }

    @Override
    public CompletableFuture<Boolean> enable(SkyblockPlatform platform, ConfigurationSection properties) {
        setProperties(properties);

        return associate(() -> {
            String host = properties.getString("host");
            int port = properties.getInt("port");
            String bucket = properties.getString("bucket");
            String token = properties.getString("token");
            String org = properties.getString("org");

            client = InfluxDBClientFactory.create(host + ":" + port, token.toCharArray(), org, bucket);

            if (!client.ping()) {
                return false;
            }

            Time updateTime = TimeParser.parse(properties.getString("update-interval", "5 seconds"));

            platform.getScheduler().scheduleRepeating(ThreadContext.SYNC, () -> run(ThreadContext.SYNC), updateTime, updateTime);
            platform.getScheduler().scheduleRepeating(ThreadContext.ASYNC, () -> run(ThreadContext.ASYNC), updateTime, updateTime);

            Time batchTime = TimeParser.parse(properties.getString("batch-time", "1 second"));

            writeApi = client.makeWriteApi(
                WriteOptions.builder()
                    .flushInterval(batchTime.as(TimeUnit.MILLISECONDS))
                    .build()
            );

            writeApi.listenEvents(WriteSuccessEvent.class, event -> {
                currentWrite.complete(null);
                currentWrite = new CompletableFuture<>();
            });

            return true;
        });
    }

    @Override
    public CompletableFuture<Void> wipe() {
        // no-op
        // TODO: Use Offsets and stuff
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public SkyblockMetric createMetric(String name) {
        MetricTimestamp timestamp = MetricTimestamp.currentMillis();

        return new InternalSkyblockMetric(this, name, timestamp);
    }

    @Override
    public void registerProvider(SkyblockMetricProvider provider) {
        providers.put(provider.getName(), provider);
    }

    @Override
    public CompletableFuture<Void> submit(SkyblockMetric metric) {
        InternalSkyblockMetric internalMetric = (InternalSkyblockMetric) metric;
        MetricTimestamp timestamp = internalMetric.getTimestamp();

        long ns = timestamp.getTimeUnit().convert(timestamp.getTime(), TimeUnit.NANOSECONDS);

        Point point = Point.measurement(internalMetric.getName())
            .addFields(internalMetric.getData())
            .time(ns, WritePrecision.NS);

        writeApi.writePoint(point);

        return currentWrite;
    }

    public void run(ThreadContext context) {
        for (SkyblockMetricProvider provider : providers.values()) {

            if (provider.getThreadContext() != context) {
                continue;
            }

            SkyblockMetric metric = createMetric(provider.getName());
            provider.fillData(metric);
            submit(metric);
        }
    }
}
