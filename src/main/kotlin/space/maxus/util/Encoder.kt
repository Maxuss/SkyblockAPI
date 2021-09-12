package space.maxus.util

import org.bukkit.craftbukkit.libs.org.apache.commons.io.output.ByteArrayOutputStream
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.util.*

object Encoder {
    @JvmStatic
    fun encodeStacks(items: Array<ItemStack>) : String {
        return try {
            val outputStream = ByteArrayOutputStream()
            val dataOutput = BukkitObjectOutputStream(outputStream)
            dataOutput.writeInt(items.size)
            for (item in items) {
                if(item != null) dataOutput.writeObject(item.serializeAsBytes())
                else dataOutput.writeObject(null)
            }
            dataOutput.close()
            Base64Coder.encodeLines(outputStream.toByteArray()).replace("\r\n", "").replace("\\r\\n", "\r\n")
        } catch (e: Exception) {
            throw Exception("Unable to encode items!", e)
        }
    }
}