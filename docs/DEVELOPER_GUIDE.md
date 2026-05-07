# TriProxy - Developer Guide

This guide explains how to create **commands** and **event listeners** using TriProxy's registration system. Both
systems follow the same pattern: extend a base class, place the file in the correct package, and the plugin handles
registration automatically at startup.

## How Auto-Registration Works

TriProxy uses a `PackageScanner` to discover classes at runtime. When the plugin starts, it scans specific packages
for concrete (non-abstract) classes and registers them automatically.

| System    | Base Class      | Package                                        |
|:----------|:----------------|:-----------------------------------------------|
| Commands  | `PluginCommand` | `net.trilleo.mc.plugins.triproxy.commands`     |
| Listeners | `PluginListener`| `net.trilleo.mc.plugins.triproxy.listeners`    |

Subpackages are also scanned, so you can freely organise classes into folders like `commands/admin/` or
`listeners/player/`.

## Constructor Requirements

Every command and listener class must have one of the following constructors:

| Constructor                       | When to Use                                    |
|:----------------------------------|:-----------------------------------------------|
| No-arg constructor                | When you don't need a reference to the plugin  |
| Constructor accepting a `Main`    | When you need to access the plugin instance    |

The plugin instance is injected automatically when a `Main` constructor is available.

---

## Commands

To create a command, extend `PluginCommand` and place the class anywhere inside the `commands` package or a
subpackage.

### PluginCommand Properties

| Property      | Type           | Default       | Description                                                  |
|:--------------|:---------------|:--------------|:-------------------------------------------------------------|
| `name`        | `String`       | *(required)*  | The command name as typed in-game (e.g. `"ping"` for `/ping`)|
| `description` | `String`       | `""`          | A brief description of what the command does                 |
| `aliases`     | `List<String>` | `emptyList()` | Alternative names the command can be invoked with            |
| `permission`  | `String?`      | `null`        | Permission node required to use the command                  |

### Methods to Override

| Method       | Required | Description                                           |
|:-------------|:---------|:------------------------------------------------------|
| `execute`    | Yes      | Called when a player or console runs the command      |
| `suggest`    | No       | Called when tab-completion is requested               |
| `hasPermission` | No    | Override for custom permission logic (default: checks `permission` property) |

### Example

```kotlin
package net.trilleo.mc.plugins.triproxy.commands

import com.velocitypowered.api.command.SimpleCommand
import net.kyori.adventure.text.Component
import net.trilleo.mc.plugins.triproxy.registration.PluginCommand

class PingCommand : PluginCommand(
    name = "ping",
    description = "Check your connection to the proxy",
    permission = "triproxy.ping"
) {
    override fun execute(invocation: SimpleCommand.Invocation) {
        invocation.source().sendMessage(Component.text("Pong!"))
    }
}
```

### Example with Tab Completion

```kotlin
package net.trilleo.mc.plugins.triproxy.commands

import com.velocitypowered.api.command.SimpleCommand
import net.kyori.adventure.text.Component
import net.trilleo.mc.plugins.triproxy.registration.PluginCommand

class ServerCommand : PluginCommand(
    name = "server",
    description = "Connect to a server",
    permission = "triproxy.server"
) {
    private val servers = listOf("lobby", "survival", "creative")

    override fun execute(invocation: SimpleCommand.Invocation) {
        val args = invocation.arguments()
        if (args.isEmpty() || args[0] !in servers) {
            invocation.source().sendMessage(Component.text("Usage: /server <${servers.joinToString("|")}>"))
            return
        }
        invocation.source().sendMessage(Component.text("Connecting to ${args[0]}..."))
    }

    override fun suggest(invocation: SimpleCommand.Invocation): List<String> {
        val args = invocation.arguments()
        if (args.size <= 1) {
            val prefix = args.firstOrNull() ?: ""
            return servers.filter { it.startsWith(prefix, ignoreCase = true) }
        }
        return emptyList()
    }
}
```

### Example with Plugin Instance

```kotlin
package net.trilleo.mc.plugins.triproxy.commands

import com.velocitypowered.api.command.SimpleCommand
import net.kyori.adventure.text.Component
import net.trilleo.mc.plugins.triproxy.Main
import net.trilleo.mc.plugins.triproxy.registration.PluginCommand

class DebugCommand(private val plugin: Main) : PluginCommand(
    name = "tpdebug",
    description = "Print debug information",
    permission = "triproxy.debug"
) {
    override fun execute(invocation: SimpleCommand.Invocation) {
        plugin.logger.info("Debug triggered by ${invocation.source()}")
        invocation.source().sendMessage(Component.text("Debug info printed to console."))
    }
}
```

---

## Listeners

To create an event listener, extend `PluginListener` and place the class anywhere inside the `listeners` package or a
subpackage.

### Methods

Annotate each event handler method with `@Subscribe`. The method must accept a single Velocity event parameter.

### Example

```kotlin
package net.trilleo.mc.plugins.triproxy.listeners

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import net.trilleo.mc.plugins.triproxy.registration.PluginListener

class DisconnectListener : PluginListener() {

    @Subscribe
    fun onDisconnect(event: DisconnectEvent) {
        // Handle player disconnection
    }
}
```

### Example with Plugin Instance

```kotlin
package net.trilleo.mc.plugins.triproxy.listeners.connection

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.LoginEvent
import net.trilleo.mc.plugins.triproxy.Main
import net.trilleo.mc.plugins.triproxy.registration.PluginListener

class LoginListener(private val plugin: Main) : PluginListener() {

    @Subscribe
    fun onLogin(event: LoginEvent) {
        plugin.logger.info("${event.username} logged in")
    }
}
```

---

## Package Structure

The recommended layout for the `net.trilleo.mc.plugins.triproxy` package is:

```
triproxy/
├── Main.kt
├── commands/
│   ├── PingCommand.kt
│   └── admin/
│       └── DebugCommand.kt
├── listeners/
│   ├── DisconnectListener.kt
│   └── connection/
│       └── LoginListener.kt
└── registration/
    ├── CommandRegistrar.kt
    ├── ListenerRegistrar.kt
    ├── PackageScanner.kt
    ├── PluginCommand.kt
    └── PluginListener.kt
```
