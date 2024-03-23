package me.illusion.skyblockcore.server.network.complex.communication.data;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Request<R> {

    private final UUID requestId;
    private final long timestamp;
    private final CompletableFuture<R> future;

    public Request(UUID requestId) {
        this.requestId = requestId;
        this.timestamp = System.currentTimeMillis();
        this.future = new CompletableFuture<>();
    }

    public UUID getRequestId() {
        return requestId;
    }

    public CompletableFuture<R> getFuture() {
        return future;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void timeout() {
        future.complete(null);
    }

    public void complete(R response) {
        future.complete(response);
    }


}
