package com.worldpay.access.checkout.testutils

import java.lang.reflect.Method
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.InitializationError
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.Config.Builder
import org.robolectric.internal.SandboxTestRunner
import org.robolectric.internal.bytecode.Sandbox

class PlainRobolectricTestRunner(testClass: Class<*>?) : RobolectricTestRunner(testClass) {

    override fun afterTest(method: FrameworkMethod?, bootstrappedMethod: Method?) {}

    override fun beforeTest(
        sandbox: Sandbox?,
        method: FrameworkMethod?,
        bootstrappedMethod: Method?
    ) {}

    override fun getHelperTestRunner(bootstrappedTestClass: Class<*>?): SandboxTestRunner.HelperTestRunner? {
        return try {
            SandboxTestRunner.HelperTestRunner(bootstrappedTestClass)
        } catch (initializationError: InitializationError) {
            throw RuntimeException(initializationError)
        }
    }

    override fun finallyAfterTest(method: FrameworkMethod?) {}

    @Deprecated("this method is deprecated in the parent class")
    override fun buildGlobalConfig(): Config = Builder().setManifest(Config.NONE).build()
}
