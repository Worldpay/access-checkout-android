package com.worldpay.access.checkout.sample.testutil

import android.graphics.Bitmap
import androidx.test.runner.screenshot.BasicScreenCaptureProcessor
import androidx.test.runner.screenshot.ScreenCaptureProcessor
import androidx.test.runner.screenshot.Screenshot
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.IOException

class ScreenshotTestRule : TestWatcher() {

    override fun failed(e: Throwable?, description: Description?) {
        super.failed(e, description)

        takeScreenshot(description)
    }

    private fun takeScreenshot(description: Description?) {
        val filename = description?.testClass?.simpleName.toString() + "-" + description?.methodName

        val capture = Screenshot.capture()
        capture.name = filename
        capture.format = Bitmap.CompressFormat.PNG

        val processors: HashSet<ScreenCaptureProcessor> = HashSet()
        processors.add(BasicScreenCaptureProcessor())
        try {
            capture.process(processors)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
