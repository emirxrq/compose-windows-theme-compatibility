import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sun.jna.Native
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.ptr.IntByReference
import com.sun.jna.win32.W32APIOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface DwmApi : com.sun.jna.Library {
    fun DwmSetWindowAttribute(hwnd: WinDef.HWND?, dwAttribute: Int, pvAttribute: IntByReference?, cbAttribute: Int): Int

    companion object {
        val INSTANCE: DwmApi = Native.load("dwmapi", DwmApi::class.java, W32APIOptions.DEFAULT_OPTIONS)
    }
}

fun setTitleBarBasedOnTheme() {
    println(isWindowsDarkMode());
    val hwnd = User32.INSTANCE.GetForegroundWindow()
    val darkMode = IntByReference(if (isWindowsDarkMode()) 1 else 0)

    DwmApi.INSTANCE.DwmSetWindowAttribute(hwnd, 20 /* DWMWA_USE_IMMERSIVE_DARK_MODE */, darkMode, 4)
}

@Composable
@Preview
fun App() {
    val isDarkMode = isWindowsDarkMode()

    val darkColors = darkColors(
        primary = Color(0xFFBB86FC),
        primaryVariant = Color(0xFFBB86FC),
        secondary = Color(0xFF03DAC6),
        background = Color.Black,
        surface = Color(0xFF121212),
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White
    )

    val lightColors = lightColors(
        primary = Color(0xFF6200EE),
        primaryVariant = Color(0xFF3700B3),
        secondary = Color(0xFF03DAC6),
        background = Color(0xFFFFFFFF),
        surface = Color(0xFFFFFFFF),
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black
    )

    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme(colors = if (isDarkMode) darkColors else lightColors) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Button(
                onClick = {
                    text = "Hello, Desktop!"
                },
                modifier = Modifier
                    .padding(16.dp) 
            ) {
                Text(text)
            }
        }
    }
}



fun main() = application {
    val scope = rememberCoroutineScope()

    Window(onCloseRequest = ::exitApplication) {
        LaunchedEffect(Unit) {
            scope.launch(Dispatchers.IO) {
                delay(100)
                setTitleBarBasedOnTheme()
            }
        }

        App()
    }
}
