package catmoe.fallencrystal.protocolwarn

import catmoe.fallencrystal.protocolwarn.config.ObjectConfig
import catmoe.fallencrystal.protocolwarn.listener.ServerConnect
import catmoe.fallencrystal.translation.event.EventManager
import net.md_5.bungee.api.plugin.Plugin

@Suppress("unused")
class ProtocolWarn : Plugin() {

    init { instance=this }

    override fun onEnable() {
        ObjectConfig.loadConfig()
        EventManager.register(ServerConnect())
    }

    override fun onDisable() { EventManager.unregister(ServerConnect()) }

    companion object {
        lateinit var instance: ProtocolWarn
            private set
    }
}