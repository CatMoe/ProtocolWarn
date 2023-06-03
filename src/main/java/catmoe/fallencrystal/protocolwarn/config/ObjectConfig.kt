package catmoe.fallencrystal.protocolwarn.config

import catmoe.fallencrystal.moefilter.util.message.MessageUtil
import catmoe.fallencrystal.protocolwarn.ObjectPlugin
import com.github.benmanes.caffeine.cache.Caffeine
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import dev.simplix.protocolize.data.Sound
import java.io.File

object ObjectConfig {
    private val folder = ObjectPlugin.getDataFolder()
    private val path = folder.absolutePath
    private val configFile = File(path, "config.conf")

    // Name, Server
    private val serverCache = Caffeine.newBuilder().build<String, WarnServer>()

    private val messageCache = Caffeine.newBuilder().build<String, Message>()

    private var config: Config? = null

    private val defaultConfig = """
                servers=[
                    {
                        server-name="example"
                        protocol=[47]
                        warn=true
                        custom-message=[
                            "",
                            "Here is custom warn message. support [ServerVersion] and [ClientVersion] placeholder",
                            ""
                        ]
                    }
                    {
                        server-name="example2"
                        protocol=[762, 761]
                        # 是否向玩家发送警告消息. 如果为true则发送
                        warn=false
                        # 如空将会使用global-message
                        message="default"
                    }
                ]
                messages=[
                    {
                        name="default"
                        message=[
                            "",
                            " &e您当前所游玩的服务器的版本 [ServerVersion]",
                            " &e与您的客户端版本 [ClientVersion] 不一致",
                            " &e您的游戏体验可能会受到些许影响 仍然建议切换到与服务器相同的版本.",
                            ""
                        ]
                        # 空则不发送消息.
                        actionbar=""
                        # title如果title或subtitle有一个不为空则发送title. stay必须不为0
                        title {
                            title=""
                            subtitle=""
                            # 以tick为单位.
                            fadeIn=0
                            stay=0
                            fadeOut=0
                        }
                        sound=BLOCK_ENDER_CHEST_OPEN
                    }
                ]
                protocol {
                    762="1.19.4"
                    761="1.19.3"
                    760="1.19.1/2"
                    759="1.19"
                    758="1.18.2"
                    757="1.18/.1"
                    756="1.17.1"
                    755="1.17"
                    754="1.16.4/5"
                    753="1.16.3"
                    751="1.16.2"
                    736="1.16.1"
                    735="1.16"
                    578="1.15.1/2"
                    573="1.15"
                    498="1.14.4"
                    490="1.14.3"
                    485="1.14.2"
                    480="1.14.1"
                    477="1.14"
                    404="1.13.2"
                    401="1.13.1"
                    393="1.13"
                    340="1.12.2"
                    338="1.12.1"
                    335="1.12"
                    316="1.11.2"
                    315="1.11"
                    210="1.10.2"
                    110="1.9.4"
                    109="1.9.2/3"
                    108="1.9.1"
                    107="1.9"
                    47="1.8.x"
                }
    """.trimIndent()

    fun loadConfig() {
        if (!folder.exists()) { folder.mkdirs() }
        if (!configFile.exists()) { configFile.createNewFile(); configFile.writeText(defaultConfig) }
        config = ConfigFactory.parseFile(configFile)
        loadMessage()
        MessageUtil.logInfo("[ProtocolWarn] 已加载消息列表")
        loadServerList()
        MessageUtil.logInfo("[ProtocolWarn] 已加载服务器列表")
    }

    private fun loadServerList() {
        val serverList = config!!.getConfigList("servers").map { serverConfig ->
            val name = serverConfig.getString("server-name")
            val protocol = serverConfig.getIntList("protocol") ?: listOf(0)
            val warn = serverConfig.getBoolean("warn")
            val warnMessage = messageCache.getIfPresent(serverConfig.getString("message"))
            WarnServer(name, protocol, warn, warnMessage)
        }
        serverList.forEach { serverCache.put(it.name, it) }
    }

    private fun loadMessage() {
        val messageList = config!!.getConfigList("messages").map { messageConfig ->
            val name = messageConfig.getString("name")
            val message = messageConfig.getStringList("message")
            val actionbar = messageConfig.getString("actionbar")
            val title = messageConfig.getString("title.title")
            val subtitle = messageConfig.getString("title.subtitle")
            val titleFadeIn = messageConfig.getInt("title.fadeIn")
            val titleStay = messageConfig.getInt("title.stay")
            val titleFadeOut = messageConfig.getInt("title.fadeOut")
            val anySound = messageConfig.getAnyRef("sound")
            val sound = (try { anySound as Sound } catch (ex: Exception) { MessageUtil.logWarn("[ProtocolWarn] $anySound 不是一个有效的声音类型. 如果您不想启用声音 可以安全忽略此消息"); null } )
            Message(name, message, actionbar, title, subtitle, titleFadeIn, titleStay, titleFadeOut, sound)
        }
        messageList.forEach { messageCache.put(it.name, it) }
    }

    fun getServer(server: String): WarnServer? { return serverCache.getIfPresent(server) }

    fun getMessage(message: String): Message? { return messageCache.getIfPresent(message) }

    fun getConfig(): Config { return config!! }
}