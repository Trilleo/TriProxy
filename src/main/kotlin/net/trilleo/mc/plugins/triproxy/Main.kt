package net.trilleo.mc.plugins.triproxy;

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import org.slf4j.Logger

@Plugin(
    id = "triproxy",
    name = "TriProxy",
    version = "0.1.0",
    description = "Management plugin for TriUniverse Proxy",
    url = "https://www.trilleo.net",
    authors = ["Trilleo"]
)
class Main @Inject constructor(val logger: Logger) {

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
    }
}
