package catmoe.fallencrystal.protocolwarn.config

import catmoe.fallencrystal.protocolwarn.ObjectPlugin
import com.github.benmanes.caffeine.cache.Caffeine
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.io.File

object ObjectConfig {
    private val folder = ObjectPlugin.getDataFolder()
    private val path = folder.absolutePath
    private val configFile = File(path, "config.conf")

    // Name, Server
    private val serverCache = Caffeine.newBuilder().build<String, WarnServer>()

    private var config: Config? = null

    private val defaultConfig = """
                global-message=[
                    "&c您当前所游玩的服务器的主要版本 [ServerVersion] 跟您的客户端版本 [ClientVersion] 不一致.",
                    "因此导致的不符合预期的行为后果自负"
                ]
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
                        custom-message=[]
                    }
                ]
    """.trimIndent()

    fun loadConfig() {
        if (!folder.exists()) { folder.mkdirs() }
        if (!configFile.exists()) { configFile.createNewFile(); configFile.writeText(defaultConfig) }
        config = ConfigFactory.parseFile(configFile)
        loadServerList()
    }

    private fun loadServerList() {
        val serverList = config!!.getConfigList("servers").map { serverConfig ->
            val name = serverConfig.getString("server-name")
            val protocol = serverConfig.getIntList("protocol")
            val warn = serverConfig.getBoolean("warn")
            val customMessage = serverConfig.getStringList("custom-message")
            val warnMessage = if (customMessage.isEmpty()) config!!.getStringList("global-message") else customMessage
            WarnServer(name, protocol, warn, warnMessage)
        }
        serverList.forEach { serverCache.put(it.name, it) }
    }

    fun getServer(server: String): WarnServer? { return serverCache.getIfPresent(server) }

    fun getConfig(): Config { return config!! }
}