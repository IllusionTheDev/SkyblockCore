package me.illusion.skyblockcore.common.databaserewrite.persistence.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;
import me.illusion.skyblockcore.common.databaserewrite.SkyblockDatabaseTag;
import me.illusion.skyblockcore.common.databaserewrite.persistence.AbstractPersistenceDatabase;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

public abstract class MongoPersistenceDatabase extends AbstractPersistenceDatabase {

    protected MongoClient client;
    protected MongoDatabase database;

    protected String collectionName;

    protected MongoPersistenceDatabase() {
        addTag(SkyblockDatabaseTag.NO_SQL);
        addTag(SkyblockDatabaseTag.REMOTE);
    }

    @Override
    public String getName() {
        return "mongo";
    }

    @Override
    public CompletableFuture<Boolean> enable(ReadOnlyConfigurationSection properties) {
        setProperties(properties);

        return associate(() -> {
            String connectionString = properties.getString("connection-string");

            if (connectionString == null) {
                String ip = properties.getString("ip");
                int port = properties.getInt("port");
                String authsource = properties.getString("auth-source", "admin");
                String username = properties.getString("username");
                String password = properties.getString("password");
                boolean ssl = properties.getBoolean("ssl", false);

                connectionString = createConnectionString(ip, port, authsource, username, password, ssl);
            }

            String databaseName = properties.getString("database", getDefaultDatabase());
            collectionName = properties.getString("collection", getDefaultCollection());

            CodecRegistry registry = CodecRegistries.fromRegistries(
                CodecRegistries.fromCodecs(getCodecs())
            );

            try {
                client = MongoClients.create(connectionString);
                database = client.getDatabase(databaseName).withCodecRegistry(registry);

                initializeCollections();
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Void> wipe() {
        return associate(() -> database.getCollection(collectionName).drop());
    }

    protected String getDefaultDatabase() {
        return "skyblock";
    }

    protected abstract String getDefaultCollection();

    protected List<Codec<?>> getCodecs() {
        return Collections.emptyList();
    }

    protected abstract void initializeCollections();

    private String createConnectionString(String ip, int port, String authsource, String username, String password, boolean ssl) {
        StringBuilder builder = new StringBuilder();
        builder.append("mongodb://");
        if (username != null && !username.isEmpty()) {
            builder.append(username);
            if (password != null && !password.isEmpty()) {
                builder.append(":").append(password);
            }
            builder.append("@");
        }

        builder.append(ip).append(":").append(port);

        if (authsource != null && !authsource.isEmpty()) {
            builder.append("/?authSource=").append(authsource);
        }

        if (ssl) {
            builder.append("&ssl=true");
        }

        return builder.toString();
    }
}
