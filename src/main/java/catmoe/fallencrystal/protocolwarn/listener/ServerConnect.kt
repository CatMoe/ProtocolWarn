package catmoe.fallencrystal.protocolwarn.listener

import catmoe.fallencrystal.moefilter.event.PluginReloadEvent
import catmoe.fallencrystal.moefilter.util.message.v2.MessageUtil
import catmoe.fallencrystal.moefilter.util.message.v2.packet.type.MessagesType
import catmoe.fallencrystal.protocolwarn.Version
import catmoe.fallencrystal.protocolwarn.config.Message
import catmoe.fallencrystal.protocolwarn.config.ObjectConfig
import catmoe.fallencrystal.protocolwarn.util.Sound
import catmoe.fallencrystal.translation.event.EventListener
import catmoe.fallencrystal.translation.event.annotations.AsynchronousHandler
import catmoe.fallencrystal.translation.event.annotations.EventHandler
import catmoe.fallencrystal.translation.event.annotations.HandlerPriority
import catmoe.fallencrystal.translation.event.events.player.PlayerConnectServerEvent
import catmoe.fallencrystal.translation.platform.Platform
import catmoe.fallencrystal.translation.platform.ProxyPlatform
import catmoe.fallencrystal.translation.player.bungee.BungeePlayer
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.protocol.ProtocolConstants
import java.util.*
import kotlin.concurrent.schedule

class ServerConnect : EventListener {
    @EventHandler(PlayerConnectServerEvent::class, priority = HandlerPriority.MEDIUM)
    @AsynchronousHandler
    @Platform(ProxyPlatform.BUNGEE)
    fun onServerConnected(event: PlayerConnectServerEvent) {
        val server = ObjectConfig.getServer(event.server.getName().lowercase())
        val player = (event.player.upstream as BungeePlayer).player
        val clientVersion = player.pendingConnection.version
        if (event.isConnected && server != null && event.player.hasPermission("protocolwarn.ignore.server.${event.server.getName()}")) {
            if (server.message == null) { MessageUtil.logWarn("[ProtocolWarn] 未找到指定消息. 检查您的配置文件."); return }
            var warn = server.warn
            server.protocol.forEach { if (clientVersion == it) { warn = false } }
            if (warn) { Timer().schedule(server.message.delay.toLong()) { sendMessage(server.message, player, server.protocol[0], clientVersion) } }
        }
    }

    @EventHandler(PluginReloadEvent::class, priority = HandlerPriority.LOW)
    @AsynchronousHandler
    @Platform(ProxyPlatform.BUNGEE)
    fun onReload(event: PluginReloadEvent) { ObjectConfig.loadConfig() }

    private fun sendMessage(message: Message, player: ProxiedPlayer, s: Int, c: Int) {
        if (player.hasPermission("protocolwarn.ignore.message.${message.name}")) { return }
        if (message.title.isNotEmpty() || message.subtitle.isNotEmpty() && message.titleStay != 0) {
            //MessageUtil.sendTitle(player, colorizeMiniMessage(rm(s,c,message.title)), colorizeMiniMessage(rm(s,c,message.subtitle)), message.titleFadeIn, message.titleStay, message.titleFadeOut)
            val hex = c > ProtocolConstants.MINECRAFT_1_16
            val title = ProxyServer.getInstance().createTitle()
            title.title(MessageUtil.colorize(rm(s,c,message.title), hex))
            title.subTitle(MessageUtil.colorize(rm(s,c,message.subtitle), hex))
            title.fadeIn(message.titleFadeIn)
            title.stay(message.titleStay)
            title.fadeOut(message.titleFadeOut)
            title.send(player)
        }
        if (message.actionbar.isNotEmpty()) { MessageUtil.sendMessage(rm(s,c,message.actionbar), MessagesType.ACTION_BAR, player) }
        if (message.sound != null) { Sound.playSound(player, message.sound) }
        // message.message.forEach { MessageUtil.sendMessage(player, rm(s,c,it)) } -- Legacy
        if (message.message.isNotEmpty()) {
            // <reset> and <newline> is MiniMessage tag.
            MessageUtil.sendMessage(rm(s, c, message.message.joinToString("<reset><newline>")), MessagesType.CHAT, player)
        }
    }

    private fun rm(s: Int, c: Int, m: String): String { return Version.replaceMessage(m, s, c) }
}