package info.dvkr.screenstream.ui.tabs.settings.app.settings.general

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.dvkr.screenstream.R
import info.dvkr.screenstream.common.ModuleSettings
import info.dvkr.screenstream.tile.TileActionService
import kotlinx.coroutines.CoroutineScope

internal object Tile : ModuleSettings.Item {
    override val id: String = "TILE"
    override val position: Int = 4
    override val available: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    override fun has(resources: Resources, text: String): Boolean = with(resources) {
        getString(R.string.app_pref_tile).contains(text, ignoreCase = true) ||
                getString(R.string.app_pref_tile_summary).contains(text, ignoreCase = true)
    }

    @Composable
    @SuppressLint("NewApi")
    override fun ItemUI(horizontalPadding: Dp, coroutineScope: CoroutineScope, enabled: Boolean, onDetailShow: () -> Unit) {
        val context = LocalContext.current

        TileUI(horizontalPadding) { TileActionService.showAddTileRequest(context) }
    }
}

@Composable
private fun TileUI(
    horizontalPadding: Dp,
    onDetailShow: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(role = Role.Button, onClick = onDetailShow)
            .padding(horizontal = horizontalPadding + 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icon_Tile, contentDescription = null, modifier = Modifier.padding(end = 16.dp))

        Column(modifier = Modifier.weight(1.0F)) {
            Text(
                text = stringResource(id = R.string.app_pref_tile),
                modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
                fontSize = 18.sp,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(id = R.string.app_pref_tile_summary),
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private val Icon_Tile: ImageVector = materialIcon(name = "GridView") {
    materialPath {
        moveTo(3f, 11f)
        horizontalLineTo(11f)
        verticalLineTo(3f)
        horizontalLineTo(3f)
        moveTo(5f, 5f)
        horizontalLineTo(9f)
        verticalLineTo(9f)
        horizontalLineTo(5f)
        moveTo(13f, 21f)
        horizontalLineTo(21f)
        verticalLineTo(13f)
        horizontalLineTo(13f)
        moveTo(15f, 15f)
        horizontalLineTo(19f)
        verticalLineTo(19f)
        horizontalLineTo(15f)
        moveTo(3f, 21f)
        horizontalLineTo(11f)
        verticalLineTo(13f)
        horizontalLineTo(3f)
        moveTo(5f, 15f)
        horizontalLineTo(9f)
        verticalLineTo(19f)
        horizontalLineTo(5f)
        moveTo(13f, 3f)
        verticalLineTo(11f)
        horizontalLineTo(21f)
        verticalLineTo(3f)
        moveTo(19f, 9f)
        horizontalLineTo(15f)
        verticalLineTo(5f)
        horizontalLineTo(19f)
        close()
    }
}