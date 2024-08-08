//package com.worldpay.access.checkout.sample.utils
//
//import android.content.Context
//import android.graphics.Bitmap.CompressFormat
//import android.os.Environment.DIRECTORY_PICTURES
//import androidx.test.core.app.ApplicationProvider.getApplicationContext
//import androidx.test.runner.screenshot.ScreenCaptureProcessor
//import androidx.test.runner.screenshot.Screenshot
//import org.junit.rules.TestWatcher
//import org.junit.runner.Description
//import java.io.File
//import java.io.IOException
//
//
//class ScreenshotTestRule : TestWatcher() {
//    override fun failed(e: Throwable?, description: Description) {
//        super.failed(e, description)
//        val filename: String =
//            description.testClass.simpleName + "-" + description.methodName
//        val capture = Screenshot.capture()
//        capture.name = filename
//        capture.format = CompressFormat.PNG
//        val processors = HashSet<ScreenCaptureProcessor>()
//        processors.add(
//            CustomScreenCaptureProcessor(
//            File(
////                InstrumentationRegistry.getTargetContext().getExternalFilesDir(DIRECTORY_PICTURES),
//                getApplicationContext<Context>().getExternalFilesDir(DIRECTORY_PICTURES),
//                "test_run_screenshots"
//            )
//        )
//        )
//        try {
//            capture.process(processors)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }
//}