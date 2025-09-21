package info.dvkr.screenstream.mjpeg.ui.settings.general

import android.content.res.Resources
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import info.dvkr.screenstream.common.ModuleSettings
import info.dvkr.screenstream.mjpeg.R
import info.dvkr.screenstream.mjpeg.settings.MjpegSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

internal object StopOnSleep : ModuleSettings.Item {
    override val id: String = MjpegSettings.Key.STOP_ON_SLEEP.name
    override val position: Int = 1
    override val available: Boolean = true

    override fun has(resources: Resources, text: String): Boolean = with(resources) {
        getString(R.string.mjpeg_pref_stop_on_sleep).contains(text, ignoreCase = true) ||
                getString(R.string.mjpeg_pref_stop_on_sleep_summary).contains(text, ignoreCase = true)
    }

    @Composable
    override fun ItemUI(horizontalPadding: Dp, coroutineScope: CoroutineScope, enabled: Boolean, onDetailShow: () -> Unit) {
        val mjpegSettings = koinInject<MjpegSettings>()
        val mjpegSettingsState = mjpegSettings.data.collectAsStateWithLifecycle()

        StopOnSleepUI(horizontalPadding, mjpegSettingsState.value.stopOnSleep) {
            if (mjpegSettingsState.value.stopOnSleep != it) {
                coroutineScope.launch { mjpegSettings.updateData { copy(stopOnSleep = it) } }
            }
        }
    }
}

@Composable
private fun StopOnSleepUI(
    horizontalPadding: Dp,
    stopOnSleep: Boolean,
    onValueChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .toggleable(value = stopOnSleep, onValueChange = onValueChange)
            .padding(start = horizontalPadding + 16.dp, end = horizontalPadding + 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icon_Stop, contentDescription = null, modifier = Modifier.padding(end = 16.dp))

        Column(modifier = Modifier.weight(1F)) {
            Text(
                text = stringResource(id = R.string.mjpeg_pref_stop_on_sleep),
                modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
                fontSize = 18.sp,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(id = R.string.mjpeg_pref_stop_on_sleep_summary),
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Switch(checked = stopOnSleep, onCheckedChange = null, modifier = Modifier.scale(0.7F))
    }
}

private val Icon_Stop: ImageVector = materialIcon(name = "Outlined.Stop") {
    materialPath {
        moveTo(16.0f, 8.0f)
        verticalLineToRelative(8.0f)
        horizontalLineTo(8.0f)
        verticalLineTo(8.0f)
        horizontalLineToRelative(8.0f)
        moveToRelative(2.0f, -2.0f)
        horizontalLineTo(6.0f)
        verticalLineToRelative(12.0f)
        horizontalLineToRelative(12.0f)
        verticalLineTo(6.0f)
        close()
    }
}