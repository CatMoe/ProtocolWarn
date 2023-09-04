package catmoe.fallencrystal.protocolwarn

import catmoe.fallencrystal.protocolwarn.config.ObjectConfig
import catmoe.fallencrystal.protocolwarn.listener.ServerConnect
import catmoe.fallencrystal.translation.event.EventManager
import net.md_5.bungee.api.plugin.Plugin

@Suppress("unused")
class ProtocolWarn : Plugin() {

    override fun onEnable() {
        ObjectPlugin.setPlugin(this)
        ObjectPlugin.setDataFolder(dataFolder)
        ObjectConfig.loadConfig()
        EventManager.register(ServerConnect())
    }

    override fun onDisable() { EventManager.unregister(ServerConnect()) }
}