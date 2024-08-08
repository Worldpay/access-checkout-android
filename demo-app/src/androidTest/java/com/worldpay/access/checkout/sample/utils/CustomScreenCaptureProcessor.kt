//package com.worldpay.access.checkout.sample.utils
//
//import android.os.Build
//import android.util.Log
//import androidx.annotation.VisibleForTesting
//import androidx.test.runner.screenshot.ScreenCapture
//import androidx.test.runner.screenshot.ScreenCaptureProcessor
//import java.io.BufferedOutputStream
//import java.io.File
//import java.io.FileOutputStream
//import java.io.IOException
//import java.util.Locale
//import java.util.UUID
//
//class CustomScreenCaptureProcessor(val defaultScreenshotPath: File) : ScreenCaptureProcessor {
//    private val sAndroidRuntimeVersion = Build.VERSION.SDK_INT
//    private val sAndroidDeviceName = Build.DEVICE
//
//    val mTag = "CustomScreenCaptureProcessor"
//    val mFileNameDelimiter = "-"
//    val mDefaultFilenamePrefix = "screenshot"
//    val mDefaultScreenshotPath = defaultScreenshotPath
//
//    @Throws(IOException::class)
//    override fun process(capture: ScreenCapture): String? {
//        var filename = if (capture.name == null) getDefaultFilename() else getFilename(capture.name)
//        filename += "." + capture.format.toString().lowercase(Locale.getDefault())
//        val imageFolder: File = mDefaultScreenshotPath
//        imageFolder.mkdirs()
//        if (!imageFolder.isDirectory && !imageFolder.canWrite()) {
//            throw IOException(
//                String.format(
//                    "The directory %s does not exist and could not be created or is not " + "writable.",
//                    imageFolder
//                )
//            )
//        }
//        val imageFile = File(imageFolder, filename)
//        var out: BufferedOutputStream? = null
//        try {
//            out = BufferedOutputStream(FileOutputStream(imageFile))
//            capture.bitmap.compress(capture.format, 100, out)
//            out.flush()
//        } finally {
//            try {
//                out?.close()
//            } catch (e: IOException) {
//                Log.e(mTag, "Could not close output steam.", e)
//            }
//        }
//        return filename
//    }
//
//    /** Returns the default filename for this class suffixed with a UUID.  */
//    protected fun getDefaultFilename(): String? {
//        return getFilename(
//            mDefaultFilenamePrefix
//                    + mFileNameDelimiter
//                    + Companion.sAndroidDeviceName
//                    + mFileNameDelimiter
//                    + Companion.sAndroidRuntimeVersion
//        )
//    }
//
//    /** Returns the filename created from the given prifix and suffixed with a UUID.  */
//    protected fun getFilename(prefix: String): String? {
//        return prefix + mFileNameDelimiter + UUID.randomUUID()
//    }
//
//    @VisibleForTesting
//    fun setAndroidDeviceName(deviceName: String?) {
//        Companion.sAndroidDeviceName = deviceName
//    }
//
//    @VisibleForTesting
//    fun setAndroidRuntimeVersion(sdkInt: Int) {
//        Companion.sAndroidRuntimeVersion = sdkInt
//    }
//
//    companion object {
//        var sAndroidDeviceName: String? = ""
//        var sAndroidRuntimeVersion: Int? = 0
//    }
//
//}
//
