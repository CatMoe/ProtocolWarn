package catmoe.fallencrystal.protocolwarn.config

import dev.simplix.protocolize.data.Sound

class Message(
    val name: String,
    val message: List<String>,
    val actionbar: String,
    val title: String,
    val subtitle: String,
    val titleFadeIn: Int,
    val titleStay: Int,
    val titleFadeOut: Int,
    val sound: Sound?,
    val delay: Int,
)