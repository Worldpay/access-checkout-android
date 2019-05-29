package com.worldpay.access.checkout.views

import android.content.Context
import android.content.res.Resources
import com.worldpay.access.checkout.testutils.mock
import org.junit.Test
import org.mockito.BDDMockito.given
import kotlin.test.assertEquals

class ExtensionFunctionsTest {

    @Test
    fun givenAContextThenShouldReturnResourceIDByName() {
        val resourceName = "someResourceName"
        val resourceType = "drawable"
        val packageName = "some-package"

        val context: Context = mock()
        val resources: Resources = mock()
        given(context.packageName).willReturn(packageName)
        given(context.resources).willReturn(resources)
        val resId = java.util.Random().nextInt()
        given(resources.getIdentifier(resourceName, resourceType, packageName)).willReturn(resId)

        val actualResID = context.resIdByName(resourceName, "drawable")

        assertEquals(resId, actualResID)
    }
}