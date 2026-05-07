package net.trilleo.mc.plugins.triproxy.commands

import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ConnectionRequestBuilder.Status
import net.kyori.adventure.text.Component
import net.trilleo.mc.plugins.triproxy.Main
import net.trilleo.mc.plugins.triproxy.registration.PluginCommand

class LobbyCommand(private val plugin: Main) : PluginCommand(
    name = "lobby",
    description = "Connect to the lobby server"
) {

    override fun execute(invocation: SimpleCommand.Invocation) {
        val source = invocation.source()

        if (source !is Player) {
            source.sendMessage(Component.text("Only players can use this command."))
            return
        }

        val lobbyServer = plugin.proxy.getServer("lobby")

        if (lobbyServer.isEmpty) {
            source.sendMessage(Component.text("The lobby server is not available."))
            return
        }

        val server = lobbyServer.get()

        if (source.currentServer.map { it.serverInfo.name == server.serverInfo.name }.orElse(false)) {
            source.sendMessage(Component.text("You are already on the lobby server."))
            return
        }

        source.sendMessage(Component.text("Connecting you to the lobby..."))
        source.createConnectionRequest(server).connect().thenAccept { result ->
            if (result.status != Status.SUCCESS) {
                source.sendMessage(Component.text("Failed to connect to the lobby server."))
            }
        }
    }
}
