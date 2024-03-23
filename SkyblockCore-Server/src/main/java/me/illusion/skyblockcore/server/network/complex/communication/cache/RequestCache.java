package me.illusion.skyblockcore.server.network.complex.communication.cache;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import me.illusion.skyblockcore.common.scheduler.SkyblockScheduler;
import me.illusion.skyblockcore.common.scheduler.ThreadContext;
import me.illusion.skyblockcore.common.utilities.time.Time;
import me.illusion.skyblockcore.server.network.complex.communication.data.Request;

public class RequestCache<R> {

    private final Map<UUID, Request<R>> requests = new ConcurrentHashMap<>();
    private final SkyblockScheduler scheduler;
    private final Time timeout;

    public RequestCache(SkyblockScheduler scheduler, Time timeout) {
        this.scheduler = scheduler;
        this.timeout = timeout;
    }

    public Request<R> createRequest() {
        return createRequest(UUID.randomUUID());
    }

    protected Request<R> createRequest(UUID requestId) {
        Request<R> request = new Request<>(requestId);

        requests.put(requestId, request);

        scheduler.scheduleOnce(ThreadContext.ASYNC, () -> {
            Request<R> cachedRequest = requests.remove(requestId);
            if (cachedRequest != null) {
                cachedRequest.timeout();
            }
        }, timeout);

        return request;
    }

    public Request<R> getRequest(UUID requestId) {
        return requests.get(requestId);
    }

    public void completeRequest(UUID requestId, R response) {
        Request<R> request = requests.remove(requestId);
        if (request != null) {
            request.complete(response);
        }
    }

    public CompletableFuture<R> getFuture(UUID requestId) {
        Request<R> request = requests.get(requestId);
        return request != null ? request.getFuture() : null;
    }

}
