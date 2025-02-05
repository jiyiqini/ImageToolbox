/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.core.ui.widget.sheets

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.theme.White
import ru.tech.imageresizershrinker.core.ui.utils.navigation.LocalNavController
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem

@Composable
fun ProcessImagesPreferenceSheet(
    uris: List<Uri>,
    extraImageType: String? = null,
    visible: MutableState<Boolean>,
    navController: NavController<Screen> = LocalNavController.current,
    navigate: (Screen) -> Unit = { screen ->
        navController.apply {
            this.navigate(screen)
            visible.value = false
        }
    }
) {
    SimpleSheet(
        title = {
            TitleItem(
                text = stringResource(R.string.image),
                icon = Icons.Rounded.Image
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = Color.Transparent,
                onClick = {
                    visible.value = false
                },
            ) {
                AutoSizeText(stringResource(id = R.string.cancel))
            }
        },
        sheetContent = {
            val gifAvailableScreens by remember(uris) {
                derivedStateOf {
                    listOf(
                        Screen.Cipher(uris.firstOrNull()),
                        Screen.GifTools(
                            Screen.GifTools.Type.GifToImage(
                                uris.firstOrNull()
                            )
                        )
                    )
                }
            }
            val pdfAvailableScreens by remember(uris) {
                derivedStateOf {
                    listOf(
                        Screen.Cipher(uris.firstOrNull()),
                        Screen.PdfTools(
                            Screen.PdfTools.Type.Preview(
                                uris.firstOrNull()
                            )
                        ),
                        Screen.PdfTools(
                            Screen.PdfTools.Type.PdfToImages(
                                uris.firstOrNull()
                            )
                        )
                    )
                }
            }
            val singleImageScreens by remember(uris) {
                derivedStateOf {
                    listOf(
                        Screen.SingleEdit(uris.firstOrNull()),
                        Screen.ResizeAndConvert(uris),
                        Screen.ResizeByBytes(uris),
                        Screen.Crop(uris.firstOrNull()),
                        Screen.Filter(
                            type = Screen.Filter.Type.Basic(uris)
                        ),
                        Screen.Draw(uris.firstOrNull()),
                        Screen.RecognizeText(uris.firstOrNull()),
                        Screen.EraseBackground(uris.firstOrNull()),
                        Screen.Filter(
                            type = Screen.Filter.Type.Masking(uris.firstOrNull())
                        ),
                        Screen.Watermarking(uris),
                        Screen.GradientMaker(uris),
                        Screen.PdfTools(
                            Screen.PdfTools.Type.ImagesToPdf(uris)
                        ),
                        Screen.GifTools(),
                        Screen.Cipher(uris.firstOrNull()),
                        Screen.ImagePreview(uris),
                        Screen.PickColorFromImage(uris.firstOrNull()),
                        Screen.GeneratePalette(uris.firstOrNull()),
                        Screen.DeleteExif(uris),
                        Screen.LimitResize(uris)
                    )
                }
            }
            val multipleImagesScreens by remember(uris) {
                derivedStateOf {
                    mutableListOf(
                        Screen.ResizeAndConvert(uris),
                        Screen.ResizeByBytes(uris),
                        Screen.Filter(
                            type = Screen.Filter.Type.Basic(uris)
                        )
                    ).apply {
                        if (uris.size >= 2) add(Screen.ImageStitching(uris))
                        add(Screen.PdfTools(Screen.PdfTools.Type.ImagesToPdf(uris)))
                        if (uris.size == 2) add(Screen.Compare(uris))
                        add(Screen.GradientMaker(uris))
                        add(Screen.Watermarking(uris))
                        add(Screen.GifTools())
                        add(Screen.ImagePreview(uris))
                        add(Screen.LimitResize(uris))
                        Screen.DeleteExif(uris)
                    }
                }
            }

            val urisCorrespondingScreens by remember(
                extraImageType,
                uris,
                pdfAvailableScreens,
                singleImageScreens,
                multipleImagesScreens
            ) {
                derivedStateOf {
                    if (extraImageType == "pdf") {
                        pdfAvailableScreens
                    } else if (extraImageType == "gif") {
                        gifAvailableScreens
                    } else if (uris.size <= 1) {
                        singleImageScreens
                    } else {
                        multipleImagesScreens
                    }
                }
            }

            Box(Modifier.fillMaxWidth()) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(250.dp),
                    contentPadding = PaddingValues(16.dp),
                    verticalItemSpacing = 8.dp,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item(
                        span = StaggeredGridItemSpan.FullLine
                    ) {
                        val pic: @Composable (Uri?, Dp, Int) -> Unit =
                            { uri, height, extra ->
                                Box(
                                    Modifier
                                        .padding(
                                            bottom = 8.dp,
                                            start = 4.dp,
                                            end = 4.dp
                                        )
                                        .height(height)
                                        .width(120.dp)
                                        .container(
                                            shape = MaterialTheme.shapes.extraLarge,
                                            resultPadding = 0.dp
                                        )
                                ) {
                                    Picture(
                                        model = uri,
                                        shape = RectangleShape,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    if (extra > 0) {
                                        Box(
                                            Modifier
                                                .fillMaxSize()
                                                .background(
                                                    MaterialTheme.colorScheme.scrim.copy(
                                                        alpha = 0.6f
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "+$extra",
                                                color = White,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        if (extraImageType != "pdf") {
                            if (uris.size in 1..2) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    repeat(uris.size) {
                                        pic(uris.getOrNull(it), 100.dp, 0)
                                    }
                                }
                            } else if (uris.size >= 3) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    pic(uris.getOrNull(0), 100.dp, 0)
                                    Column {
                                        pic(uris.getOrNull(1), 60.dp, 0)
                                        pic(uris.getOrNull(2), 60.dp, uris.size - 3)
                                    }
                                }
                            }
                        }
                    }
                    items(urisCorrespondingScreens) {
                        PreferenceItem(
                            onClick = { navigate(it) },
                            icon = it.icon,
                            title = stringResource(it.title),
                            subtitle = stringResource(it.subtitle),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        visible = visible,
    )
}