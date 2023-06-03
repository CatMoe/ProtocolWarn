package catmoe.fallencrystal.protocolwarn.util

import dev.simplix.protocolize.api.Protocolize
import dev.simplix.protocolize.api.SoundCategory
import dev.simplix.protocolize.data.Sound
import net.md_5.bungee.api.connection.ProxiedPlayer

object Sound {
    fun playSound(player: ProxiedPlayer, sound: Sound) {
        val protocolPlayer = Protocolize.playerProvider().player(player.uniqueId) ?: return
        protocolPlayer.playSound(sound, SoundCategory.MASTER, 5f, 1f)
    }
}