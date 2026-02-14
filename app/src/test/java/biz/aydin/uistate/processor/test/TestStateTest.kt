package biz.aydin.uistate.processor.test

import biz.aydin.uistate.processor.test.teststate.extensions.deeplyNested
import biz.aydin.uistate.processor.test.teststate.extensions.error
import biz.aydin.uistate.processor.test.teststate.extensions.invoke
import biz.aydin.uistate.processor.test.teststate.extensions.loading
import biz.aydin.uistate.processor.test.teststate.extensions.nested
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

    @Test
    fun `Given state is DeeplyNested, when executing deeplyNested block, then it does execute`() {
        var isExecuted = false
        val state: TestState = TestState.Nested.DeeplyNested
        state {
            nested {
                deeplyNested {
                    isExecuted = true
                }
            }
        }
        assertTrue(isExecuted)
    }

    @Test
    fun `Given state is DeeplyNested, when executing nested block, then it does execute`() {
        var isExecuted = false
        val state: TestState = TestState.Nested.DeeplyNested
        state {
            nested {
                isExecuted = true
            }
        }
        assertTrue(isExecuted)
    }

    @Test
    fun `Given state is DeeplyNested, when executing loading block, then it does not execute`() {
        var isExecuted = false
        val state: TestState = TestState.Nested.DeeplyNested
        state {
            loading {
                isExecuted = true
            }
        }
        assertFalse(isExecuted)
    }

    @Test
    fun `Given state is DeeplyNested, when executing error block, then it does not execute`() {
        var isExecuted = false
        val state: TestState = TestState.Nested.DeeplyNested
        state {
            error {
                isExecuted = true
            }
        }
        assertFalse(isExecuted)
    }

    @Test
    fun `Given state is DeeplyNested, when executing success block, then it does not execute`() {
        var isExecuted = false
        val state: TestState = TestState.Nested.DeeplyNested
        state {
            success {
                isExecuted = true
            }
        }
        assertFalse(isExecuted)
    }

    @Test
    fun `Given state is Loading, when executing nested block, then it does not execute`() {
        var isExecuted = false
        val state: TestState = TestState.Loading
        state {
            nested {
                isExecuted = true
            }
        }
        assertFalse(isExecuted)
    }

    @Test
    fun `Given state is Error, when executing nested block, then it does not execute`() {
        var isExecuted = false
        val state: TestState = TestState.Error(error = "ERROR")
        state {
            nested {
                isExecuted = true
            }
        }
        assertFalse(isExecuted)
    }

    @Test
    fun `Given state is Success, when executing nested block, then it does not execute`() {
        var isExecuted = false
        val state: TestState = TestState.Success(data = "DATA")
        state {
            nested {
                isExecuted = true
            }
        }
        assertFalse(isExecuted)
    }

    @Test
    fun `Given state is Loading, when executing nested block with deeplyNested inside, then deeplyNested does not execute`() {
        var isExecuted = false
        val state: TestState = TestState.Loading
        state {
            nested {
                deeplyNested {
                    isExecuted = true
                }
            }
        }
        assertFalse(isExecuted)
    }

    @Test
    fun `Given state is Error, when executing nested block with deeplyNested inside, then deeplyNested does not execute`() {
        var isExecuted = false
        val state: TestState = TestState.Error(error = "ERROR")
        state {
            nested {
                deeplyNested {
                    isExecuted = true
                }
            }
        }
        assertFalse(isExecuted)
    }

    @Test
    fun `Given state is Success, when executing nested block with deeplyNested inside, then deeplyNested does not execute`() {
        var isExecuted = false
        val state: TestState = TestState.Success(data = "DATA")
        state {
            nested {
                deeplyNested {
                    isExecuted = true
                }
            }
        }
        assertFalse(isExecuted)
    }

    @Test
    fun `Given state is Loading, when chaining multiple blocks, then only loading block executes`() {
        var loadingExecuted = false
        var errorExecuted = false
        var successExecuted = false
        val state: TestState = TestState.Loading

        state {
            loading {
                loadingExecuted = true
            }
            error {
                errorExecuted = true
            }
            success {
                successExecuted = true
            }
        }

        assertTrue(loadingExecuted)
        assertFalse(errorExecuted)
        assertFalse(successExecuted)
    }

    @Test
    fun `Given state is Error, when chaining multiple blocks, then only error block executes`() {
        var loadingExecuted = false
        var errorExecuted = false
        var successExecuted = false
        val state: TestState = TestState.Error(error = "ERROR")

        state {
            loading {
                loadingExecuted = true
            }
            error {
                errorExecuted = true
            }
            success {
                successExecuted = true
            }
        }

        assertFalse(loadingExecuted)
        assertTrue(errorExecuted)
        assertFalse(successExecuted)
    }

    @Test
    fun `Given state is Success, when chaining multiple blocks, then only success block executes`() {
        var loadingExecuted = false
        var errorExecuted = false
        var successExecuted = false
        val state: TestState = TestState.Success(data = "DATA")

        state {
            loading {
                loadingExecuted = true
            }
            error {
                errorExecuted = true
            }
            success {
                successExecuted = true
            }
        }

        assertFalse(loadingExecuted)
        assertFalse(errorExecuted)
        assertTrue(successExecuted)
    }

    @Test
    fun `Given state is DeeplyNested, when chaining blocks, then nested and deeplyNested blocks execute`() {
        var loadingExecuted = false
        var nestedExecuted = false
        var deeplyNestedExecuted = false
        val state: TestState = TestState.Nested.DeeplyNested

        state {
            loading {
                loadingExecuted = true
            }
            nested {
                nestedExecuted = true
                deeplyNested {
                    deeplyNestedExecuted = true
                }
            }
        }

        assertFalse(loadingExecuted)
        assertTrue(nestedExecuted)
        assertTrue(deeplyNestedExecuted)
    }

    @Test
    fun `Given state is Error, when calling error extension, then returns the same state instance`() {
        val state: TestState = TestState.Error(error = "ERROR")
        val result = state.error { }
        assertEquals(state, result)
    }

    @Test
    fun `Given state is Success, when calling success extension, then returns the same state instance`() {
        val state: TestState = TestState.Success(data = "DATA")
        val result = state.success { }
        assertEquals(state, result)
    }

    @Test
    fun `Given state is Loading, when calling loading extension, then returns the same state instance`() {
        val state: TestState = TestState.Loading
        val result = state.loading { }
        assertEquals(state, result)
    }

    @Test
    fun `Given state is DeeplyNested, when calling deeplyNested extension, then returns the same state instance`() {
        val state: TestState.Nested = TestState.Nested.DeeplyNested
        val result = state.deeplyNested { }
        assertEquals(state, result)
    }

    @Test
    fun `Given state is DeeplyNested, when calling nested extension, then returns the same state instance`() {
        val state: TestState = TestState.Nested.DeeplyNested
        val result = state.nested { }
        assertEquals(state, result)
    }

    @Test
    fun `Given state is Loading, when calling non-matching extension, then returns the same state instance`() {
        val state: TestState = TestState.Loading
        val result = state.error { }
        assertEquals(state, result)
    }
}
