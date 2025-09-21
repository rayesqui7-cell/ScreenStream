package info.dvkr.screenstream.rtsp.ui.main.server

import android.content.ClipData
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.dvkr.screenstream.common.generateQRBitmap
import info.dvkr.screenstream.rtsp.R
import info.dvkr.screenstream.rtsp.settings.RtspSettings
import info.dvkr.screenstream.rtsp.ui.RtspBinding
import info.dvkr.screenstream.rtsp.ui.RtspState
import info.dvkr.screenstream.rtsp.ui.RtspTransportState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun ServerMode(
    rtspState: State<RtspState>,
    modifier: Modifier = Modifier,
    rtspSettings: RtspSettings = koinInject(),
    scope: CoroutineScope = rememberCoroutineScope()
) {
    val status = rtspState.value.transport.status
    val bindings = when (status) {
        is RtspTransportState.Status.Ready -> status.bindings
        is RtspTransportState.Status.Active -> status.bindings
        else -> emptyList()
    }

    Column(modifier = modifier) {
        if (bindings.isEmpty()) {
            Text(
                text = stringResource(id = R.string.rtsp_interfaces_no_address),
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleMedium
            )
        } else {
            bindings.forEachIndexed { index, binding ->
                AddressRow(
                    binding = binding,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp, start = 12.dp, end = 0.dp)
                )
                if (index != bindings.lastIndex) {
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun AddressRow(
    binding: RtspBinding,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = stringResource(id = R.string.rtsp_interfaces_title))

        val fullAddress = binding.fullAddress

        Text(
            text = fullAddress,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 18.sp,
            style = MaterialTheme.typography.titleMedium.copy(textDecoration = TextDecoration.Underline)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = stringResource(id = R.string.rtsp_interfaces_interface_label, binding.label),
                modifier = Modifier.weight(1F),
                style = MaterialTheme.typography.bodySmall,
            )
            CopyAddressButton(fullAddress)
            ShareAddressButton(fullAddress)
            ShowQRCodeButton(fullAddress)
        }
    }
}

@Composable
private fun CopyAddressButton(
    fullAddress: String,
    clipboard: Clipboard = LocalClipboard.current,
    scope: CoroutineScope = rememberCoroutineScope()
) {
    val context = LocalContext.current

    IconButton(
        onClick = {
            scope.launch { clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(fullAddress, fullAddress))) }
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                Toast.makeText(context, R.string.rtsp_interfaces_copied, Toast.LENGTH_LONG).show()
            }
        }
    ) {
        Icon(imageVector = Icon_ContentCopy, contentDescription = stringResource(id = R.string.rtsp_interfaces_description_copy))
    }
}

@Composable
private fun ShareAddressButton(
    fullAddress: String
) {
    val context = LocalContext.current
    val shareTitle = stringResource(R.string.rtsp_interfaces_share_title)

    IconButton(
        onClick = {
            val sharingIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, fullAddress)
            }
            context.startActivity(Intent.createChooser(sharingIntent, shareTitle))
        }
    ) {
        Icon(imageVector = Icon_Share, contentDescription = stringResource(id = R.string.rtsp_interfaces_description_share))
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ShowQRCodeButton(
    fullAddress: String
) {
    val showQRDialog = remember { mutableStateOf(false) }

    IconButton(onClick = { showQRDialog.value = true }) {
        Icon(imageVector = Icon_QrCode, contentDescription = stringResource(id = R.string.rtsp_interfaces_description_qr))
    }

    if (showQRDialog.value) {
        BasicAlertDialog(
            onDismissRequest = { showQRDialog.value = false },
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .background(Color.White)
                .padding(16.dp)
                .size(192.dp + 32.dp)
        ) {
            val qrCodeSizePx = with(LocalDensity.current) { 192.dp.roundToPx() }
            val qrImageBitmap = remember(fullAddress) { mutableStateOf<ImageBitmap?>(null) }
            LaunchedEffect(fullAddress) {
                qrImageBitmap.value = fullAddress.generateQRBitmap(qrCodeSizePx).asImageBitmap()
            }
            if (qrImageBitmap.value != null) {
                Image(
                    bitmap = qrImageBitmap.value!!,
                    contentDescription = stringResource(id = R.string.rtsp_interfaces_qr_content_description)
                )
            }
        }
    }
}

private val Icon_Share: ImageVector = materialIcon(name = "Filled.Share") {
    materialPath {
        moveTo(18.0f, 16.08f)
        curveToRelative(-0.76f, 0.0f, -1.44f, 0.3f, -1.96f, 0.77f)
        lineTo(8.91f, 12.7f)
        curveToRelative(0.05f, -0.23f, 0.09f, -0.46f, 0.09f, -0.7f)
        reflectiveCurveToRelative(-0.04f, -0.47f, -0.09f, -0.7f)
        lineToRelative(7.05f, -4.11f)
        curveToRelative(0.54f, 0.5f, 1.25f, 0.81f, 2.04f, 0.81f)
        curveToRelative(1.66f, 0.0f, 3.0f, -1.34f, 3.0f, -3.0f)
        reflectiveCurveToRelative(-1.34f, -3.0f, -3.0f, -3.0f)
        reflectiveCurveToRelative(-3.0f, 1.34f, -3.0f, 3.0f)
        curveToRelative(0.0f, 0.24f, 0.04f, 0.47f, 0.09f, 0.7f)
        lineTo(8.04f, 9.81f)
        curveTo(7.5f, 9.31f, 6.79f, 9.0f, 6.0f, 9.0f)
        curveToRelative(-1.66f, 0.0f, -3.0f, 1.34f, -3.0f, 3.0f)
        reflectiveCurveToRelative(1.34f, 3.0f, 3.0f, 3.0f)
        curveToRelative(0.79f, 0.0f, 1.5f, -0.31f, 2.04f, -0.81f)
        lineToRelative(7.12f, 4.16f)
        curveToRelative(-0.05f, 0.21f, -0.08f, 0.43f, -0.08f, 0.65f)
        curveToRelative(0.0f, 1.61f, 1.31f, 2.92f, 2.92f, 2.92f)
        curveToRelative(1.61f, 0.0f, 2.92f, -1.31f, 2.92f, -2.92f)
        reflectiveCurveToRelative(-1.31f, -2.92f, -2.92f, -2.92f)
        close()
    }
}

private val Icon_ContentCopy: ImageVector = materialIcon(name = "Filled.ContentCopy") {
    materialPath {
        moveTo(16.0f, 1.0f)
        lineTo(4.0f, 1.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(14.0f)
        horizontalLineToRelative(2.0f)
        lineTo(4.0f, 3.0f)
        horizontalLineToRelative(12.0f)
        lineTo(16.0f, 1.0f)
        close()
        moveTo(19.0f, 5.0f)
        lineTo(8.0f, 5.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(14.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(11.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(21.0f, 7.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(19.0f, 21.0f)
        lineTo(8.0f, 21.0f)
        lineTo(8.0f, 7.0f)
        horizontalLineToRelative(11.0f)
        verticalLineToRelative(14.0f)
        close()
    }
}

private val Icon_QrCode: ImageVector = materialIcon(name = "Filled.QrCode") {
    materialPath {
        moveTo(3.0f, 11.0f)
        horizontalLineToRelative(8.0f)
        verticalLineTo(3.0f)
        horizontalLineTo(3.0f)
        verticalLineTo(11.0f)
        close()
        moveTo(5.0f, 5.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(4.0f)
        horizontalLineTo(5.0f)
        verticalLineTo(5.0f)
        close()
    }
    materialPath {
        moveTo(3.0f, 21.0f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(-8.0f)
        horizontalLineTo(3.0f)
        verticalLineTo(21.0f)
        close()
        moveTo(5.0f, 15.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(4.0f)
        horizontalLineTo(5.0f)
        verticalLineTo(15.0f)
        close()
    }
    materialPath {
        moveTo(13.0f, 3.0f)
        verticalLineToRelative(8.0f)
        horizontalLineToRelative(8.0f)
        verticalLineTo(3.0f)
        horizontalLineTo(13.0f)
        close()
        moveTo(19.0f, 9.0f)
        horizontalLineToRelative(-4.0f)
        verticalLineTo(5.0f)
        horizontalLineToRelative(4.0f)
        verticalLineTo(9.0f)
        close()
    }
    materialPath {
        moveTo(19.0f, 19.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-2.0f)
        close()
    }
    materialPath {
        moveTo(13.0f, 13.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-2.0f)
        close()
    }
    materialPath {
        moveTo(15.0f, 15.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-2.0f)
        close()
    }
    materialPath {
        moveTo(13.0f, 17.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-2.0f)
        close()
    }
    materialPath {
        moveTo(15.0f, 19.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-2.0f)
        close()
    }
    materialPath {
        moveTo(17.0f, 17.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-2.0f)
        close()
    }
    materialPath {
        moveTo(17.0f, 13.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-2.0f)
        close()
    }
    materialPath {
        moveTo(19.0f, 15.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-2.0f)
        close()
    }
}