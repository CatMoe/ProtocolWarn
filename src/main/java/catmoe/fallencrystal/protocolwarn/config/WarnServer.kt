package catmoe.fallencrystal.protocolwarn.config

class WarnServer(
    val name: String,
    val protocol: List<Int>,
    val warn: Boolean,
    val warnMessage: List<String>
)