package contacteraser.mobile.mypapit.net.contacteraser

import contacteraser.mobile.mypapit.net.contacteraser.data.model.ThemeMode
import contacteraser.mobile.mypapit.net.contacteraser.data.preferences.ThemePreferencesRepository
import contacteraser.mobile.mypapit.net.contacteraser.ui.state.ThemeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ThemeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun setThemeMode_updatesPersistedFlowAcrossModes() = runTest {
        val repository = FakeThemePreferencesRepository(ThemeMode.SYSTEM)
        val viewModel = ThemeViewModel(repository)
        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.themeMode.collect()
        }

        assertEquals(ThemeMode.SYSTEM, viewModel.themeMode.value)

        viewModel.setThemeMode(ThemeMode.DARK)
        advanceUntilIdle()
        assertEquals(ThemeMode.DARK, viewModel.themeMode.value)

        viewModel.setThemeMode(ThemeMode.LIGHT)
        advanceUntilIdle()
        assertEquals(ThemeMode.LIGHT, viewModel.themeMode.value)

        collectJob.cancel()
    }

    private class FakeThemePreferencesRepository(initial: ThemeMode) : ThemePreferencesRepository {
        private val state = MutableStateFlow(initial)
        override val themeMode: Flow<ThemeMode> = state

        override suspend fun setThemeMode(mode: ThemeMode) {
            state.value = mode
        }
    }
}
