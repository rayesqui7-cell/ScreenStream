package info.dvkr.screenstream.rtsp.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import info.dvkr.screenstream.rtsp.R
import info.dvkr.screenstream.rtsp.settings.RtspSettings
import info.dvkr.screenstream.rtsp.ui.RtspState
import info.dvkr.screenstream.rtsp.ui.main.client.ClientMode
import info.dvkr.screenstream.rtsp.ui.main.server.ServerMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun ModeCard(
    rtspState: State<RtspState>,
    modifier: Modifier = Modifier,
    rtspSettings: RtspSettings = koinInject(),
    scope: CoroutineScope = rememberCoroutineScope()
) {
    val rtspSettingsState = rtspSettings.data.collectAsStateWithLifecycle()
    val selectedMode = rtspSettingsState.value.mode
    val isStreaming = rtspState.value.isStreaming

    ElevatedCard(modifier = modifier) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
            tonalElevation = 0.dp
        ) {
            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                Text(
                    text = stringResource(id = R.string.rtsp_mode_card_title),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium
                )

                ModeSelector(
                    selected = selectedMode,
                    onSelect = { mode -> scope.launch { rtspSettings.updateData { copy(mode = mode) } } },
                    enabled = isStreaming.not()
                )
            }
        }

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        when (selectedMode) {
            RtspSettings.Values.Mode.SERVER -> ServerMode(rtspState = rtspState)

            RtspSettings.Values.Mode.CLIENT -> ClientMode(
                rtspState = rtspState,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun ModeSelector(
    selected: RtspSettings.Values.Mode,
    onSelect: (RtspSettings.Values.Mode) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val modes = listOf(RtspSettings.Values.Mode.SERVER, RtspSettings.Values.Mode.CLIENT)

    Column(modifier = modifier.fillMaxWidth()) {
        modes.forEachIndexed { index, mode ->
            val isSelected = selected == mode
            val labelRes = when (mode) {
                RtspSettings.Values.Mode.SERVER -> R.string.rtsp_mode_server
                RtspSettings.Values.Mode.CLIENT -> R.string.rtsp_mode_client
            }
            val helperRes = when (mode) {
                RtspSettings.Values.Mode.SERVER -> R.string.rtsp_mode_server_hint
                RtspSettings.Values.Mode.CLIENT -> R.string.rtsp_mode_client_hint
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = isSelected,
                        enabled = enabled,
                        onClick = { if (enabled) onSelect(mode) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = null,
                    modifier = Modifier.padding(start = 8.dp),
                    enabled = enabled
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp)
                ) {
                    Text(
                        text = stringResource(id = labelRes),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(id = helperRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
