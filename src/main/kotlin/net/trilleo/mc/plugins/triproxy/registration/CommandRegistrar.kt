package net.trilleo.mc.plugins.triproxy.registration

import net.trilleo.mc.plugins.triproxy.Main

/**
 * Discovers all concrete [PluginCommand] subclasses inside the `commands`
 * package (and its subpackages) and registers them with Velocity's
 * [com.velocitypowered.api.command.CommandManager].
 */
object CommandRegistrar {

    private const val COMMANDS_PACKAGE = "net.trilleo.mc.plugins.triproxy.commands"

    /**
     * Scans the commands package, instantiates every [PluginCommand] found,
     * and registers it with the proxy command manager.
     */
    fun registerAll(plugin: Main) {
        val commandClasses = PackageScanner.findClasses(
            plugin, COMMANDS_PACKAGE, PluginCommand::class.java
        )

        val commandManager = plugin.proxy.commandManager

        for (commandClass in commandClasses) {
            try {
                val command = instantiate(commandClass, plugin)
                val meta = commandManager.metaBuilder(command.name)
                    .aliases(*command.aliases.toTypedArray())
                    .plugin(plugin)
                    .build()
                commandManager.register(meta, command)
                plugin.logger.info("Registered command: /${command.name}")
            } catch (e: Exception) {
                plugin.logger.error(
                    "Failed to register command ${commandClass.simpleName}: ${e.message}"
                )
            }
        }

        plugin.logger.info("Registered ${commandClasses.size} command(s)")
    }

    /**
     * Tries to create an instance of [clazz] using a constructor that accepts
     * a [Main]; falls back to a no-arg constructor.
     */
    private fun instantiate(clazz: Class<out PluginCommand>, plugin: Main): PluginCommand {
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
