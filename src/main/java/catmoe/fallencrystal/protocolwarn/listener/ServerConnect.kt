package catmoe.fallencrystal.protocolwarn.listener

import catmoe.fallencrystal.moefilter.api.event.EventListener
import catmoe.fallencrystal.moefilter.api.event.FilterEvent
import catmoe.fallencrystal.moefilter.api.event.events.bungee.AsyncServerConnectEvent
import catmoe.fallencrystal.moefilter.util.message.MessageUtil
import catmoe.fallencrystal.protocolwarn.Version
import catmoe.fallencrystal.protocolwarn.config.ObjectConfig
import java.util.*
import kotlin.concurrent.schedule

class ServerConnect : EventListener {
    @FilterEvent
    fun onServerConnected(event: AsyncServerConnectEvent) {
        if (event.isConnected) {
            Timer().schedule(1000L) {
                val warnServer = ObjectConfig.getServer(event.server.name) ?: return@schedule
                val allowProtocol = warnServer.protocol
                val clientProtocol = event.player.pendingConnection.version
                var shouldBeWarn = true
                allowProtocol.forEach { if (clientProtocol == it) { shouldBeWarn = false } }
                val warnMessage = warnServer.warnMessage
                if (warnServer.warn && shouldBeWarn)
                { warnMessage.forEach { MessageUtil.sendMessage(event.player, Version.replaceMessage(it, allowProtocol[0], clientProtocol)) } }
            }
        }
    }
}