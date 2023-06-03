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
        if (event.isConnected && server != null) {
            if (server.message == null) { MessageUtil.logWarn("[ProtocolWarn] 未找到指定消息. 检查您的配置文件."); return }
            var warn = server.warn
            server.protocol.forEach { if (event.player.pendingConnection.version == it) { warn = false } }
            if (warn) { sendMessage(server.message, event.player) }
        }
    }

    private fun sendMessage(message: Message, player: ProxiedPlayer) {
        if (message.title.isNotEmpty() || message.subtitle.isNotEmpty() && message.titleStay != 0) {
            MessageUtil.sendTitle(player, message.title, message.subtitle, message.titleFadeIn, message.titleStay, message.titleFadeOut)
        }
        if (message.actionbar.isNotEmpty()) { MessageUtil.sendActionbar(player, message.actionbar) }
        if (message.sound != null) { Sound.playSound(player, message.sound) }
        if (message.message.isNotEmpty()) { message.message.forEach { MessageUtil.sendMessage(player, it) } }
    }
}