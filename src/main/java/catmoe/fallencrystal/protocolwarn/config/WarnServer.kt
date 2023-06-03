package catmoe.fallencrystal.protocolwarn.config

import dev.simplix.protocolize.data.Sound

class WarnServer(
    val name: String,
    val protocol: List<Int>,
    val warn: Boolean,
    val message: Message?
)