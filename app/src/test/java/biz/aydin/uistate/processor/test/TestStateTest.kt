package biz.aydin.uistate.processor.test

import biz.aydin.uistate.processor.test.teststate.extensions.error
import biz.aydin.uistate.processor.test.teststate.extensions.invoke
import biz.aydin.uistate.processor.test.teststate.extensions.loading
import biz.aydin.uistate.processor.test.teststate.extensions.success
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TestStateTest {
    @Test
    fun `Given state is Loading, when executing success block, then it does not execute`() {
        var isExecuted = false
        val state: TestState = TestState.Loading
        state {
            success {
                isExecuted = true
            }
        }
        assertFalse(isExecuted)
    }

    @Test
    fun `Given state is Loading, when executing error block, then it does not execute`() {
        var isExecuted = false
        val state: TestState = TestState.Loading
        state {
            error {
                isExecuted = true
            }
        }
        assertFalse(isExecuted)
    }

    @Test
    fun `Given state is Loading, when executing loading block, then it does execute`() {
        var isExecuted = false
        val state: TestState = TestState.Loading
        state {
            loading {
                isExecuted = true
            }
        }
        assertTrue(isExecuted)
    }

    @Test
    fun `Given state is Error, when executing success block, then it does not execute`() {
        var isExecuted = false
        val state: TestState = TestState.Error(error = "SERIOUS ERROR HAPPENED")
        state {
            success {
                isExecuted = true
            }
        }
        assertFalse(isExecuted)
    }

    @Test
    fun `Given state is Error, when executing error block, then it does execute`() {
        var isExecuted = false
        val state: TestState = TestState.Error(error = "SERIOUS ERROR HAPPENED")
        state {
            error {
                isExecuted = true
                assertEquals("SERIOUS ERROR HAPPENED", error)
            }
        }
        assertTrue(isExecuted)
    }

    @Test
    fun `Given state is Error, when executing loading block, then it does not execute`() {
        var isExecuted = false
        val state: TestState = TestState.Error(error = "SERIOUS ERROR HAPPENED")
        state {
            loading {
                isExecuted = true
            }
        }
        assertFalse(isExecuted)
    }

    @Test
    fun `Given state is Success, when executing success block, then it does execute`() {
        var isExecuted = false
        val state: TestState = TestState.Success(data = "INFORMATION RECEIVED")
        state {
            success {
                isExecuted = true
                assertEquals("INFORMATION RECEIVED", data)
            }
        }
        assertTrue(isExecuted)
    }

    @Test
    fun `Given state is Success, when executing error block, then it does not execute`() {
        var isExecuted = false
        val state: TestState = TestState.Success(data = "INFORMATION RECEIVED")
        state {
            error {
                isExecuted = true
            }
        }
        assertFalse(isExecuted)
    }

    @Test
    fun `Given state is Success, when executing loading block, then it does not execute`() {
        var isExecuted = false
        val state: TestState = TestState.Success(data = "INFORMATION RECEIVED")
        state {
            loading {
                isExecuted = true
            }
        }
        assertFalse(isExecuted)
    }
}
