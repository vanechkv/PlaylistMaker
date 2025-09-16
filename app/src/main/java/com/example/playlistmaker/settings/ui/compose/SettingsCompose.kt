package com.example.playlistmaker.settings.ui.compose

import android.util.Log
import android.view.ContextThemeWrapper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.main.ui.ui.theme.PlaylistMakerTheme
import com.example.playlistmaker.settings.ui.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val isDarkTheme by viewModel.getDarkTheme.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Header(R.string.settings)

        Spacer(modifier = Modifier.height(24.dp))

        SwitchSetting(
            title = R.string.dark_mode,
            checked = isDarkTheme,
            onCheckedChange = { viewModel.setDarkTheme(it) }
        )
        ItemSetting(
            title = R.string.share_app,
            icon = R.drawable.vector_share,
            onItemClick = { viewModel.shareApp() }
        )
        ItemSetting(
            title = R.string.write_to_support,
            icon = R.drawable.group_support,
            onItemClick = { viewModel.openSupport() }
        )
        ItemSetting(
            title = R.string.user_agreement,
            icon = R.drawable.vector_forward,
            onItemClick = { viewModel.openTerms() }
        )
    }
}

@Composable
fun Header(title: Int) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 14.dp, bottom = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSecondary,
        )
    }
}

@Composable
fun ItemSetting(
    title: Int,
    icon: Int,
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 21.dp, horizontal = 16.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onItemClick() }
    ) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(icon),
            contentDescription = stringResource(title),
            tint = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun SwitchSetting(
    title: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        AndroidView(
            factory = { context ->
                val switch =
                    SwitchMaterial(ContextThemeWrapper(context, R.style.SwitchSettingStyle))
                switch.thumbTintList =
                    ContextCompat.getColorStateList(context, R.color.switch_thumb_color)
                switch.trackTintList =
                    ContextCompat.getColorStateList(context, R.color.switch_track_color)
                switch
            },
            update = { switch ->
                switch.setOnCheckedChangeListener(null)
                switch.isChecked = checked
                switch.setOnCheckedChangeListener { _, isChecked ->
                    onCheckedChange(isChecked)
                }
            }
        )
    }
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
fun HeaderPreview() {
    PlaylistMakerTheme(dynamicColor = false) {
        Header(title = R.string.settings)
    }
}

@Preview(showBackground = true)
@Composable
fun ItemSettingPreview() {
    PlaylistMakerTheme(dynamicColor = false) {
        ItemSetting(
            title = R.string.share_app,
            icon = R.drawable.vector_share,
            onItemClick = {
                Log.d("ItemSettingPreview", "Item clicked")
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SwitchSettingPreview() {
    PlaylistMakerTheme(dynamicColor = false) {
        SwitchSetting(
            title = R.string.dark_mode,
            checked = true,
            onCheckedChange = {
                Log.d("SwitchSettingPreview", "Switch checked: $it")
            }
        )
    }
}