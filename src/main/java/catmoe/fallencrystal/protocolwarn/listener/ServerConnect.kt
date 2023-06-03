package catmoe.fallencrystal.protocolwarn.listener

import catmoe.fallencrystal.moefilter.api.event.EventListener
import catmoe.fallencrystal.moefilter.api.event.FilterEvent
import catmoe.fallencrystal.moefilter.api.event.events.bungee.AsyncServerConnectEvent
import catmoe.fallencrystal.moefilter.util.message.MessageUtil
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
        val server = ObjectConfig.getServer(event.server.name)
        val clientVersion = event.player.pendingConnection.version
        if (event.isConnected && server != null) {
            if (server.message == null) { MessageUtil.logWarn("[ProtocolWarn] 未找到指定消息. 检查您的配置文件."); return }
            var warn = server.warn
            server.protocol.forEach { if (clientVersion == it) { warn = false } }
            if (warn) { Timer().schedule(server.message.delay.toLong()) { sendMessage(server.message, event.player, server.protocol[0], clientVersion) } }
        }
    }

    private fun sendMessage(message: Message, player: ProxiedPlayer, s: Int, c: Int) {
        if (message.title.isNotEmpty() || message.subtitle.isNotEmpty() && message.titleStay != 0) {
            MessageUtil.sendTitle(player, rm(s,c,message.title), rm(s,c,message.subtitle), message.titleFadeIn, message.titleStay, message.titleFadeOut)
        }
        if (message.actionbar.isNotEmpty()) { MessageUtil.sendActionbar(player, rm(s,c,message.actionbar)) }
        if (message.sound != null) { Sound.playSound(player, message.sound) }
        if (message.message.isNotEmpty()) { message.message.forEach { MessageUtil.sendMessage(player, rm(s,c,it)) } }
    }

    private fun rm(s: Int, c: Int, m: String): String { return Version.replaceMessage(m, s, c) }
}