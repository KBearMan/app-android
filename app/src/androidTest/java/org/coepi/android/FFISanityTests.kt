package org.coepi.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.coepi.android.api.Callback
import org.coepi.android.api.FFINestedParameterStruct
import org.coepi.android.api.FFIParameterStruct
import org.coepi.android.api.NativeApi
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
class CoroutineTestRule(val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()) : TestWatcher() {
    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class FFISanityTests {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()


    @Test
    fun testSendReceiveString() {
        val n = NativeApi()
        val gr = n.testSendReceiveString("getReports")
        assertEquals(
            "Hello getReports",
            gr
        )
    }

    @Test
    fun testbootstrap_core() {
        val n = NativeApi()
        val gr = n.bootstrapCore("getReports2")
        assertEquals(
            "Hello getReports2",
            gr
        )
    }

    @Test
    fun testpostReport() {
        val n = NativeApi()
        val gr = n.postReport("getReports2", Callback())
        println("#### rust returned: $gr")
        assertEquals(
            "Hello getReports2",
            gr
        )
    }

    @Test
    fun testSendStruct() {
        val n = NativeApi()
        val myStruct = FFIParameterStruct(
            123,
            "hi from Android",
            // TODO UByte
//            FFINestedParameterStruct(250.toUByte())
            FFINestedParameterStruct(250)
        )

        val value = n.passStruct(myStruct, Callback())
        assertEquals(value, 1)
    }

    @Test
    fun testReturnStruct() {
        val n = NativeApi()
        val value = n.returnStruct(Callback())
        assertEquals(value, FFIParameterStruct(123, "my string parameter", FFINestedParameterStruct(123)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testCallCallback() = runBlockingTest {
        val n = NativeApi()
        val result = suspendCancellableCoroutine<String> { continuation ->
            n.callCallback(object: Callback() {
                override fun log(foo: String) {
                    continuation.resume(foo, onCancellation = {
                    })
                }
            })
        }
        assertEquals(result, "hi!")
        // Make the test finish quicker. TODO better way?
        advanceTimeBy(1200000)
    }

    @Test
    fun testRegisterCallback() {
    }
}

