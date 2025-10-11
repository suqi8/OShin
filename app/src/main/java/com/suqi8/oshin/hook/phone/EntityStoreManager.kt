package com.suqi8.oshin.hook.phone

import android.annotation.SuppressLint
import android.os.Environment
import com.google.gson.GsonBuilder
import com.highcapable.yukihookapi.hook.log.YLog
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets

/**
 * 实体存储管理器。
 * 使用 Kotlin 的 `object` 关键字，这会自动实现单例模式，确保全局只有一个实例。
 * 主要功能是将各种数据实体（如短信规则、拦截应用列表等）以 JSON 格式存储到应用私有文件中，或从文件中读取。
 */
object EntityStoreManager {
    /**
     * 将一个实体对象列表序列化为 JSON 并存储到文件中。
     * @param T 泛型参数，代表实体的类型。
     * @param entityType 要存储的实体类型，用于决定文件名。
     * @param entities 要存储的实体对象列表。
     * @return 操作成功返回 `true`，失败返回 `false`。
     */
    fun <T> storeEntitiesToFile(entities: List<T?>?): Boolean {
        return try {
            val baseDir = File(Environment.getDataDirectory(), "data/com.android.phone").resolve("files")
            if (!baseDir.exists()) baseDir.mkdirs()
            val storeFile = baseDir.resolve("prev_code_record")
            // 使用 Kotlin 的扩展函数 `writer()` 和 `use` 块。
            // `writer(StandardCharsets.UTF_8)` 创建一个 OutputStreamWriter。
            // `use` 会在代码块执行完毕后自动关闭流，无需手动写 finally 和 close()，代码更简洁安全。
            FileOutputStream(storeFile).writer(StandardCharsets.UTF_8).use { writer ->
                GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(entities, writer)
            }

            setFileWorldWritable(storeFile, 0)
            true // 操作成功
        } catch (e: Exception) {
            YLog.error("",e)
            false // 发生异常，操作失败
        }
    }

    @JvmStatic
    @SuppressLint("SetWorldWritable", "SetWorldReadable")
    fun setFileWorldWritable(startFile: File?, parentDepth: Int) {
        if (startFile == null || !startFile.exists()) {
            return
        }
        var currentFile: File? = startFile
        repeat(parentDepth + 1) {
            currentFile?.apply {
                setExecutable(true, false)
                setWritable(true, false)
                setReadable(true, false)
            }
            currentFile = currentFile?.parentFile
        }
    }

    /**
     * 从文件中读取 JSON 数据并反序列化为实体对象列表。
     * @param T 泛型参数，代表实体的类型。
     * @param entityType 要加载的实体类型。
     * @param entityClass 实体的 Class 对象，用于 JSON 反序列化。
     * @return 返回一个包含实体对象的列表。如果文件不存在或发生错误，返回一个空列表。
     */
    fun <T> loadEntitiesFromFile(entityClass: Class<T>): List<T?> {
        val baseDir = File(Environment.getDataDirectory(), "data/com.android.phone").resolve("files")
        if (!baseDir.exists()) baseDir.mkdirs()
        val storeFile = baseDir.resolve("prev_code_record")
        // 如果文件不存在，直接返回空列表，避免不必要的 IO 操作
        if (!storeFile.exists()) {
            return emptyList()
        }

        return try {
            // 同样使用 `reader()` 和 `use` 块来自动管理资源
            FileInputStream(storeFile).reader(StandardCharsets.UTF_8).use { reader ->
                GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(reader, ListOfJson(entityClass))
            }
        } catch (e: Exception) {
            YLog.error("",e)
            emptyList()
        }
    }

    private class ListOfJson<T>(private val wrapped: Class<T>) : ParameterizedType {
        // 使用主构造函数属性和单表达式函数，代码更简洁
        override fun getActualTypeArguments(): Array<Type> = arrayOf(wrapped)
        override fun getRawType(): Type = List::class.java
        override fun getOwnerType(): Type? = null
    }

    /**
     * 从文件中加载单个实体的便捷方法。
     * 内部调用 `loadEntitiesFromFile` 并返回列表中的第一个元素。
     * @param T 泛型参数，代表实体的类型。
     * @param entityType 要加载的实体类型。
     * @param entityClass 实体的 Class 对象。
     * @return 如果成功加载且列表不为空，则返回第一个实体对象；否则返回 `null`。
     */
    fun <T> loadEntityFromFile(entityClass: Class<T>): T? {
        val entities = loadEntitiesFromFile(entityClass)
        // 使用 Kotlin 集合的扩展函数 `firstOrNull()`，它会安全地返回列表的第一个元素，
        // 如果列表为空，则返回 null。这比 Java 的 `if (list != null && !list.isEmpty())` 检查更简洁。
        return entities.firstOrNull()
    }
}
