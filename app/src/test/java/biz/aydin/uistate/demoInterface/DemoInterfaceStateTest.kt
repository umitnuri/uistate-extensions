package biz.aydin.uistate.demoInterface

import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.deeplyNested
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.error
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.invoke
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.loaded
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.loading
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.nested
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoInterfaceStateTest {
    @Test
    fun `Given state is Loading, when executing loaded block, then it does not execute`() {
        var isExecuted = false
        val state: DemoInterfaceState = DemoInterfaceState.Loading
        state {
            loaded {
                isExecuted = true
            }
        }
        assertFalse(isExecuted)
    }

    @Test
    fun `Given state is Loading, when executing error block, then it does not execute`() {
        var isExecuted = false
        val state: DemoInterfaceState = DemoInterfaceState.Loading
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
        val state: DemoInterfaceState = DemoInterfaceState.Loading
        state {
            loading {
                isExecuted = true
            }
        }
        assertTrue(isExecuted)
    }

    @Test
    fun `Given state is Error, when executing loaded block, then it does not execute`() {
        var isExecuted = false
        val state: DemoInterfaceState = DemoInterfaceState.Error(error = "SERIOUS ERROR HAPPENED")
        state {
            loaded {
                isExecuted = true
            }
        }
        assertFalse(isExecuted)
    }

    @Test
    fun `Given state is Error, when executing error block, then it does execute`() {
        var isExecuted = false
        val state: DemoInterfaceState = DemoInterfaceState.Error(error = "SERIOUS ERROR HAPPENED")
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
        val state: DemoInterfaceState = DemoInterfaceState.Error(error = "SERIOUS ERROR HAPPENED")
        state {
            loading {
                isExecuted = true
            }
        }
        assertFalse(isExecuted)
    }

    @Test
    fun `Given state is Loaded, when executing loaded block, then it does execute`() {
        var isExecuted = false
        val state: DemoInterfaceState = DemoInterfaceState.Loaded(data = "INFORMATION RECEIVED")
        state {
            loaded {
                isExecuted = true
                assertEquals("INFORMATION RECEIVED", data)
            }
        }
        assertTrue(isExecuted)
    }

    @Test
    fun `Given state is Loaded, when executing error block, then it does not execute`() {
        var isExecuted = false
        val state: DemoInterfaceState = DemoInterfaceState.Loaded(data = "INFORMATION RECEIVED")
        state {
            error {
                isExecuted = true
            }
        }
        assertFalse(isExecuted)
    }

    @Test
    fun `Given state is Loaded, when executing loading block, then it does not execute`() {
        var isExecuted = false
        val state: DemoInterfaceState = DemoInterfaceState.Loaded(data = "INFORMATION RECEIVED")
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
        val state: DemoInterfaceState = DemoInterfaceState.Nested.DeeplyNested
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
        val state: DemoInterfaceState = DemoInterfaceState.Nested.DeeplyNested
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
        val state: DemoInterfaceState = DemoInterfaceState.Nested.DeeplyNested
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
        val state: DemoInterfaceState = DemoInterfaceState.Nested.DeeplyNested
        state {
            error {
                isExecuted = true
            }
        }
        assertFalse(isExecuted)
    }

    @Test
    fun `Given state is DeeplyNested, when executing loaded block, then it does not execute`() {
        var isExecuted = false
        val state: DemoInterfaceState = DemoInterfaceState.Nested.DeeplyNested
        state {
            loaded {
                isExecuted = true
            }
        }
        assertFalse(isExecuted)
    }

    @Test
    fun `Given state is Loading, when executing nested block, then it does not execute`() {
        var isExecuted = false
        val state: DemoInterfaceState = DemoInterfaceState.Loading
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
        val state: DemoInterfaceState = DemoInterfaceState.Error(error = "ERROR")
        state {
            nested {
                isExecuted = true
            }
        }
        assertFalse(isExecuted)
    }

    @Test
    fun `Given state is Loaded, when executing nested block, then it does not execute`() {
        var isExecuted = false
        val state: DemoInterfaceState = DemoInterfaceState.Loaded(data = "DATA")
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
        val state: DemoInterfaceState = DemoInterfaceState.Loading
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
        val state: DemoInterfaceState = DemoInterfaceState.Error(error = "ERROR")
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
    fun `Given state is Loaded, when executing nested block with deeplyNested inside, then deeplyNested does not execute`() {
        var isExecuted = false
        val state: DemoInterfaceState = DemoInterfaceState.Loaded(data = "DATA")
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
        var loadedExecuted = false
        val state: DemoInterfaceState = DemoInterfaceState.Loading

        state {
            loading {
                loadingExecuted = true
            }
            error {
                errorExecuted = true
            }
            loaded {
                loadedExecuted = true
            }
        }

        assertTrue(loadingExecuted)
        assertFalse(errorExecuted)
        assertFalse(loadedExecuted)
    }

    @Test
    fun `Given state is Error, when chaining multiple blocks, then only error block executes`() {
        var loadingExecuted = false
        var errorExecuted = false
        var loadedExecuted = false
        val state: DemoInterfaceState = DemoInterfaceState.Error(error = "ERROR")

        state {
            loading {
                loadingExecuted = true
            }
            error {
                errorExecuted = true
            }
            loaded {
                loadedExecuted = true
            }
        }

        assertFalse(loadingExecuted)
        assertTrue(errorExecuted)
        assertFalse(loadedExecuted)
    }

    @Test
    fun `Given state is Loaded, when chaining multiple blocks, then only loaded block executes`() {
        var loadingExecuted = false
        var errorExecuted = false
        var loadedExecuted = false
        val state: DemoInterfaceState = DemoInterfaceState.Loaded(data = "DATA")

        state {
            loading {
                loadingExecuted = true
            }
            error {
                errorExecuted = true
            }
            loaded {
                loadedExecuted = true
            }
        }

        assertFalse(loadingExecuted)
        assertFalse(errorExecuted)
        assertTrue(loadedExecuted)
    }

    @Test
    fun `Given state is DeeplyNested, when chaining blocks, then nested and deeplyNested blocks execute`() {
        var loadingExecuted = false
        var nestedExecuted = false
        var deeplyNestedExecuted = false
        val state: DemoInterfaceState = DemoInterfaceState.Nested.DeeplyNested

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
        val state: DemoInterfaceState = DemoInterfaceState.Error(error = "ERROR")
        val result = state.error { }
        assertEquals(state, result)
    }

    @Test
    fun `Given state is Loaded, when calling loaded extension, then returns the same state instance`() {
        val state: DemoInterfaceState = DemoInterfaceState.Loaded(data = "DATA")
        val result = state.loaded { }
        assertEquals(state, result)
    }

    @Test
    fun `Given state is Loading, when calling loading extension, then returns the same state instance`() {
        val state: DemoInterfaceState = DemoInterfaceState.Loading
        val result = state.loading { }
        assertEquals(state, result)
    }

    @Test
    fun `Given state is DeeplyNested, when calling deeplyNested extension, then returns the same state instance`() {
        val state: DemoInterfaceState.Nested = DemoInterfaceState.Nested.DeeplyNested
        val result = state.deeplyNested { }
        assertEquals(state, result)
    }

    @Test
    fun `Given state is DeeplyNested, when calling nested extension, then returns the same state instance`() {
        val state: DemoInterfaceState = DemoInterfaceState.Nested.DeeplyNested
        val result = state.nested { }
        assertEquals(state, result)
    }

    @Test
    fun `Given state is Loading, when calling non-matching extension, then returns the same state instance`() {
        val state: DemoInterfaceState = DemoInterfaceState.Loading
        val result = state.error { }
        assertEquals(state, result)
    }
}


