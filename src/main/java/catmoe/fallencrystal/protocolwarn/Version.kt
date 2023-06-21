package catmoe.fallencrystal.protocolwarn

import catmoe.fallencrystal.protocolwarn.config.ObjectConfig

object Version {
    fun replaceMessage(original: String, serverVersion: Int, clientVersion: Int): String {
        return original.replace("[ServerVersion]", protocolFromConfig(serverVersion)).replace("[ClientVersion]", protocolFromConfig(clientVersion))
    }

    @Deprecated("Protocol now can custom in config.", ReplaceWith("Protocol now can custom in config."))
    private fun protocolToName(version: Int): String {
        return when (version) {
            762 -> { "1.19.4" }
            761 -> { "1.19.3" }
            760 -> { "1.19.1/2" }
            759 -> { "1.19" }
            758 -> { "1.18.2" }
            757 -> { "1.18/.1" }
            756 -> { "1.17.1" }
            755 -> { "1.17" }
            754 -> { "1.16.4/5" }
            753 -> { "1.16.3" }
            751 -> { "1.16.2" }
            736 -> { "1.16.1" }
            735 -> { "1.16" }
            578 -> { "1.15.2" }
            573 -> { "1.15" }
            498 -> { "1.14.4" }
            490 -> { "1.14.3" }
            485 -> { "1.14.2" }
            480 -> { "1.14.1" }
            477 -> { "1.14" }
            404 -> { "1.13.2" }
            401 -> { "1.13.1" }
            393 -> { "1.13" }
            340 -> { "1.12.2" }
            338 -> { "1.12.1" }
            335 -> { "1.12" }
            316 -> { "1.11.2" }
            315 -> { "1.11" }
            210 -> { "1.10.2" }
            110 -> { "1.9.4" }
            109 -> { "1.9.2/3" }
            108 -> { "1.9.1" }
            107 -> { "1.9" }
            47 -> { "1.8.x" }
            5 -> { "1.7.6-1.7.10" }
            4 -> { "1.7.x-1.7.5" }
            else -> { "Unknown (${version})" }
        }
    }

    private fun protocolFromConfig(version: Int): String { return try { ObjectConfig.getConfig().getString("protocol.$version") } catch (ex: Exception) { "Unknown ($version)" } }
}