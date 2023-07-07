package catmoe.fallencrystal.protocolwarn

import catmoe.fallencrystal.moefilter.api.event.EventManager
import catmoe.fallencrystal.protocolwarn.config.ObjectConfig
import catmoe.fallencrystal.protocolwarn.listener.ServerConnect
import net.md_5.bungee.api.plugin.Plugin

class ProtocolWarn : Plugin() {

    init { instance=this }

    override fun onEnable() {
        ObjectPlugin.setPlugin(this)
        ObjectPlugin.setDataFolder(dataFolder)
        ObjectConfig.loadConfig()
        EventManager.registerListener(this, ServerConnect())
    }

    override fun onDisable() { EventManager.unregisterListener(ServerConnect()) }

    companion object {
        lateinit var instance: ProtocolWarn
            private set
    }
}
