package me.illusion.skyblockcore.shared.storage.handler;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import me.illusion.skyblockcore.shared.storage.StorageUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class S3StorageHandler extends FileStorageHandler {

    private AWSCredentials credentials;
    private Regions region;
    private String bucketName;
    private AmazonS3 s3client;


    @Override
    public CompletableFuture<Boolean> setup(File folder, Map<String, Object> data) {
        super.setup(folder, data);

        return CompletableFuture.supplyAsync(() -> {
            String accessKey = data.get("access-key").toString();
            String secretKey = data.get("secret-key").toString();

            region = Regions.fromName(data.get("region").toString());
            bucketName = data.getOrDefault("bucket", "skyblock-storage").toString();

            credentials = new BasicAWSCredentials(accessKey, secretKey);

            try {
                connect();
            } catch (Exception e) {
                return false;
            }

            if (s3client == null)
                return false;

            if (!s3client.doesBucketExistV2(bucketName)) {
                s3client.createBucket(bucketName);
            }

            return true;
        });
    }

    private void connect() {
        s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }


    @Override
    public CompletableFuture<Object> get(UUID uuid, String category) {
        return CompletableFuture.supplyAsync(() -> {
            Class<?> clazz = getClassByCategory(category);
            if (clazz == null) {
                return null;
            }

            String name = category + "-" + uuid + "." + category.toLowerCase(Locale.ROOT);

            if (!s3client.doesObjectExist(bucketName, name))
                return null;

            S3Object object = s3client.getObject(bucketName, name);

            if (object == null) {
                return null;
            }

            S3ObjectInputStream inputStream = object.getObjectContent();

            try {
                byte[] targetArray = new byte[inputStream.available()];

                inputStream.read(targetArray);

                return StorageUtils.getObject(targetArray);
            } catch (Exception e) {
                return null;
            }

        });
    }

    @Override
    public CompletableFuture<Void> save(UUID uuid, Object object, String category) {
        return CompletableFuture.runAsync(() -> {
            Class<?> clazz = getClassByCategory(category);

            if (clazz == null) {
                return;
            }

            s3client.putObject(
                    bucketName,
                    category + "-" + uuid + "." + category.toLowerCase(Locale.ROOT),
                    new ByteArrayInputStream(StorageUtils.getBytes(object)),
                    new ObjectMetadata()
            );
        });

    }

    @Override
    public CompletableFuture<Void> delete(UUID uuid, String category) {
        return CompletableFuture.runAsync(() -> {
            Class<?> clazz = getClassByCategory(category);

            if (clazz == null) {
                return;
            }

            s3client.deleteObject(bucketName, category + "-" + uuid + "." + category.toLowerCase(Locale.ROOT));
        });
    }
}
