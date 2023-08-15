package ru.tech.imageresizershrinker.presentation.main_screen.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.theme.FontFam
import ru.tech.imageresizershrinker.presentation.root.theme.Typography
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun FontSelectionItem(
    index: Int,
    font: FontFam,
    onFontSelected: (Int) -> Unit
) {
    val settingsState = LocalSettingsState.current
    val (_, name) = font
    val selected = font == settingsState.font
    MaterialTheme(
        typography = Typography(font)
    ) {
        PreferenceItem(
            onClick = {
                onFontSelected(index)
            },
            title = name ?: stringResource(id = R.string.defaultt),
            subtitle = LoremIpsum(15).values.joinToString(" "),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(
                alpha = animateFloatAsState(
                    if (selected) 0.7f
                    else 0.2f
                ).value
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .border(
                    width = settingsState.borderWidth,
                    color = animateColorAsState(
                        if (selected) MaterialTheme
                            .colorScheme
                            .onSecondaryContainer
                            .copy(alpha = 0.5f)
                        else Color.Transparent
                    ).value,
                    shape = RoundedCornerShape(16.dp)
                ),
            endIcon = if (selected) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked
        )
    }
}
