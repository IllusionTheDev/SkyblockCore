package me.illusion.skyblockcore.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

@Plugin(id = "SkyblockVelocity", name = "Velocity integration for Skyblock", version = "1.0")
public class SkyblockVelocityPlugin {

    private final ProxyServer proxy;

    @Inject
    public SkyblockVelocityPlugin(ProxyServer proxy) {
        this.proxy = proxy;
    }

}
