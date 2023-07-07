package catmoe.fallencrystal.protocolwarn.listener

import catmoe.fallencrystal.moefilter.api.event.EventListener
import catmoe.fallencrystal.moefilter.api.event.FilterEvent
import catmoe.fallencrystal.moefilter.api.event.events.PluginReloadEvent
import catmoe.fallencrystal.moefilter.api.event.events.bungee.AsyncServerConnectEvent
import catmoe.fallencrystal.moefilter.network.bungee.util.bconnection.ConnectionUtil
import catmoe.fallencrystal.moefilter.util.message.v2.packet.type.MessagesType
import catmoe.fallencrystal.moefilter.util.message.v2.MessageUtil
import catmoe.fallencrystal.protocolwarn.Version
import catmoe.fallencrystal.protocolwarn.config.Message
import catmoe.fallencrystal.protocolwarn.config.ObjectConfig
import catmoe.fallencrystal.protocolwarn.util.Sound
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*
import kotlin.concurrent.schedule

class ServerConnect : EventListener {
    @FilterEvent
    fun onServerConnected(event: AsyncServerConnectEvent) {
        val server = ObjectConfig.getServer(event.server.name.lowercase())
        val clientVersion = event.player.pendingConnection.version
        if (event.isConnected && server != null && event.player.hasPermission("protocolwarn.ignore.server.${event.server.name}")) {
            if (server.message == null) { MessageUtil.logWarn("[ProtocolWarn] 未找到指定消息. 检查您的配置文件."); return }
            var warn = server.warn
            server.protocol.forEach { if (clientVersion == it) { warn = false } }
            if (warn) { Timer().schedule(server.message.delay.toLong()) { sendMessage(server.message, event.player, server.protocol[0], clientVersion) } }
        }
    }

    @FilterEvent
    fun onReload(event: PluginReloadEvent) { ObjectConfig.loadConfig() }

    private fun sendMessage(message: Message, player: ProxiedPlayer, s: Int, c: Int) {
        val connection = ConnectionUtil(player)
        if (player.hasPermission("protocolwarn.ignore.message.${message.name}")) { return }
        if (message.title.isNotEmpty() || message.subtitle.isNotEmpty() && message.titleStay != 0) {
            // sendTitle(player, colorizeMiniMessage(rm(s,c,message.title)), colorizeMiniMessage(rm(s,c,message.subtitle)), message.titleFadeIn, message.titleStay, message.titleFadeOut)
            title(player, message.title, message.subtitle, message.titleFadeIn, message.titleStay, message.titleFadeOut, c, s)
        }
        // if (message.actionbar.isNotEmpty()) { MessageUtil.sendActionbar(player, rm(s,c,message.actionbar)) }
        if (message.actionbar.isNotEmpty()) { MessageUtil.sendMessage(connection, MessagesType.ACTIONBAR, rm(s,c,message.actionbar)) }
        if (message.sound != null) { Sound.playSound(player, message.sound) }
        // message.message.forEach { MessageUtil.sendMessage(player, rm(s,c,it)) } -- Legacy
        if (message.message.isNotEmpty()) {
            val input = mutableListOf<String>()
            message.message.forEach { input.add(rm(s,c,it)) }
            // <reset> and <newline> are MiniMessage tag.
            MessageUtil.sendMessage(connection, MessagesType.CHAT, input.joinToString("<reset><newline>")
        }
    }
    
    @Suppress("deprecation)
    private fun title(player: ProxiedPlayer, title: String, subtitle: String, fadeIn: Int, stay: Int, fadeOut: Int, client: Int, server: Int) {
        val colorizeTitle = colorize(rm(server, client, title))
        val colorizeSubtitle = colorize(rm(server, client, subtitle)
        catmoe.fallencrystal.moefilter.util.message.MessageUtil.sendTitle(player, colorizeTitle, colorizeSubtitle, fadeIn, stay, fadeOut)
    }
    
    @Suppress("deprecation)
    private fun colorize(str: String): BaseComponent { return catmoe.fallencrystal.moefilter.util.message.MessageUtil.colorizeMiniMessage(str) }

    private fun rm(s: Int, c: Int, m: String): String { return Version.replaceMessage(m, s, c) }
}
