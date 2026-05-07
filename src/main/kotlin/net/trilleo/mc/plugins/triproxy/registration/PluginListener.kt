package net.trilleo.mc.plugins.triproxy.registration

/**
 * Marker base class for all plugin event listeners.
 *
 * Extend this class and place the subclass anywhere inside the
 * `net.trilleo.mc.plugins.triproxy.listeners` package (or any subpackage) to
 * have it automatically discovered and registered with Velocity's event bus at
 * startup.
 *
 * The class must have either:
 * - A no-arg constructor, **or**
 * - A constructor that accepts a single [net.trilleo.mc.plugins.triproxy.Main] parameter
 *   (the plugin instance will be injected automatically).
 *
 * Annotate each event handler method with `@Subscribe`. The method must accept
 * a single Velocity event parameter.
 *
 * Example:
 * ```kotlin
 * package net.trilleo.mc.plugins.triproxy.listeners
 *
 * import com.velocitypowered.api.event.Subscribe
 * import com.velocitypowered.api.event.connection.LoginEvent
 * import net.trilleo.mc.plugins.triproxy.registration.PluginListener
 *
 * class LoginListener : PluginListener() {
 *
 *     @Subscribe
 *     fun onLogin(event: LoginEvent) {
 *         event.player.sendMessage(
 *             net.kyori.adventure.text.Component.text("Welcome to the network!")
 *         )
 *     }
 * }
 * ```
 */
abstract class PluginListener
