package catmoe.fallencrystal.protocolwarn

import net.md_5.bungee.api.plugin.Plugin
import java.io.File

object ObjectPlugin {
    private var plugin: Plugin? = null
    private var dataFolder: File? = null

    fun setPlugin(p: Plugin) { plugin = p }

    fun getPlugin(): Plugin { return plugin!! }

    fun setDataFolder(file: File) { dataFolder = file }

    fun getDataFolder(): File { return dataFolder!! }
}