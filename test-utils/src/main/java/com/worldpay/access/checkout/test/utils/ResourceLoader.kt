package com.worldpay.access.checkout.test.utils

import android.content.Context
import java.io.File

object ResourceLoader {
    fun getResourceAsText(resourcePath: String): String {
        return ResourceLoader.javaClass.classLoader.getResource(resourcePath)?.readText()
            ?: throw IllegalArgumentException("Resource not found: $resourcePath")
    }

    fun getResourceAsStream(resourcePath: String): java.io.InputStream {
        return ResourceLoader.javaClass.classLoader.getResourceAsStream(resourcePath)
            ?: throw IllegalArgumentException("Resource not found: $resourcePath")
    }

    fun getResourcePath(resourcePath: String): String {
        return ResourceLoader.javaClass.classLoader.getResource(resourcePath)?.path
            ?: throw IllegalArgumentException("Resource not found: $resourcePath")
    }

    fun copyAssetRecursively(context: Context, assetPath: String, dest: File) {
        val assetManager = context.assets
        val assets = assetManager.list(assetPath)

        if (assets.isNullOrEmpty()) {
            // It's a file
            println("Copying asset file: $assetPath -> ${dest.absolutePath}")
            dest.parentFile?.mkdirs()
            assetManager.open(assetPath).use { input ->
                dest.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        } else {
            // It's a directory
            dest.mkdirs()
            for (file in assets) {
                copyAssetRecursively(context, "$assetPath/$file", File(dest, file))
            }
        }
    }

}