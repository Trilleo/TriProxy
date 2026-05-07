package net.trilleo.mc.plugins.triproxy.registration

import com.velocitypowered.api.command.SimpleCommand

/**
 * Base class for all plugin commands.
 *
 * Extend this class and place the subclass anywhere inside the
 * `net.trilleo.mc.plugins.triproxy.commands` package (or any subpackage) to
 * have it automatically discovered and registered at startup.
 *
 * The class must have either:
 * - A no-arg constructor, **or**
 * - A constructor that accepts a single [net.trilleo.mc.plugins.triproxy.Main] parameter
 *   (the plugin instance will be injected automatically).
 *
 * Example:
 * ```kotlin
 * package net.trilleo.mc.plugins.triproxy.commands
 *
 * import net.trilleo.mc.plugins.triproxy.registration.PluginCommand
 * import com.velocitypowered.api.command.SimpleCommand
 *
 * class PingCommand : PluginCommand(
 *     name = "ping",
 *     description = "Check your connection to the proxy",
 *     permission = "triproxy.ping"
 * ) {
 *     override fun execute(invocation: SimpleCommand.Invocation) {
 *         invocation.source().sendMessage(
 *             net.kyori.adventure.text.Component.text("Pong!")
 *         )
 *     }
 * }
 * ```
 */
abstract class PluginCommand(
    val name: String,
    val description: String = "",
    val aliases: List<String> = emptyList(),
    val permission: String? = null
) : SimpleCommand {

    /**
     * Called when the command is executed.
     *
     * @param invocation the invocation context, including the source and arguments
     */
    abstract override fun execute(invocation: SimpleCommand.Invocation)

    /**
     * Called when tab-completion is requested for this command.
     *
     * @param invocation the invocation context, including the source and typed arguments
     * @return a list of possible completions, or an empty list
     */
    override fun suggest(invocation: SimpleCommand.Invocation): List<String> = emptyList()

    /**
     * Returns whether the command source has permission to execute this command.
     *
     * When [permission] is null, everyone is allowed. Otherwise the source must
     * hold the specified permission node.
     *
     * @param invocation the invocation context, including the source
     * @return `true` if the source has permission to use this command
     */
    override fun hasPermission(invocation: SimpleCommand.Invocation): Boolean {
        val perm = permission ?: return true
        return invocation.source().hasPermission(perm)
    }
}
