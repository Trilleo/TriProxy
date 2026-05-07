package net.trilleo.mc.plugins.triproxy.registration

import net.trilleo.mc.plugins.triproxy.Main

/**
 * Discovers all concrete [PluginListener] subclasses inside the `listeners`
 * package (and its subpackages) and registers them with Velocity's
 * [com.velocitypowered.api.event.EventManager].
 *
 * Each listener class must have either:
 * - A no-arg constructor, **or**
 * - A constructor that accepts a single [Main] parameter.
 */
object ListenerRegistrar {

    private const val LISTENERS_PACKAGE = "net.trilleo.mc.plugins.triproxy.listeners"

    /**
     * Scans the listeners package, instantiates every [PluginListener] found,
     * and registers it with the proxy event manager.
     */
    fun registerAll(plugin: Main) {
        val listenerClasses = PackageScanner.findClasses(
            plugin, LISTENERS_PACKAGE, PluginListener::class.java
        )

        for (listenerClass in listenerClasses) {
            try {
                val listener = instantiate(listenerClass, plugin)
                plugin.proxy.eventManager.register(plugin, listener)
                plugin.logger.info("Registered listener: ${listenerClass.simpleName}")
            } catch (e: Exception) {
                plugin.logger.error(
                    "Failed to register listener ${listenerClass.simpleName}: ${e.message}"
                )
            }
        }

        plugin.logger.info("Registered ${listenerClasses.size} listener(s)")
    }

    /**
     * Tries to create an instance of [clazz] using a constructor that accepts
     * a [Main]; falls back to a no-arg constructor.
     */
    private fun instantiate(clazz: Class<out PluginListener>, plugin: Main): PluginListener {
        return try {
            clazz.getDeclaredConstructor(Main::class.java).newInstance(plugin)
        } catch (_: NoSuchMethodException) {
            try {
                clazz.getDeclaredConstructor().newInstance()
            } catch (_: NoSuchMethodException) {
                throw IllegalArgumentException(
                    "${clazz.simpleName} must declare either a no-arg constructor " +
                            "or a constructor accepting a single Main parameter"
                )
            }
        }
    }
}
