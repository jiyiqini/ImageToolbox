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

package ru.tech.imageresizershrinker.feature.erase_background.data

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.Segmenter
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

object MlKitBackgroundRemover {

    private val segment: Segmenter
    private var buffer = ByteBuffer.allocate(0)
    private var width = 0
    private var height = 0


    init {
        val segmentOptions = SelfieSegmenterOptions.Builder()
            .setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
            .build()
        segment = Segmentation.getClient(segmentOptions)
    }


    /**
     * Process the image to get buffer and image height and width
     * @param bitmap Bitmap which you want to remove background.
     * @param listener listener for success and failure callback.
     **/
    fun bitmapForProcessing(
        bitmap: Bitmap,
        scope: CoroutineScope,
        listener: suspend (Result<Bitmap>) -> Unit
    ) {
        //Generate a copy of bitmap just in case the if the bitmap is immutable.
        val copyBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val input = InputImage.fromBitmap(copyBitmap, 0)
        segment.process(input)
            .addOnSuccessListener { segmentationMask ->
                buffer = segmentationMask.buffer
                width = segmentationMask.width
                height = segmentationMask.height

                scope.launch {
                    withContext(Dispatchers.IO) {
                        val resultBitmap = removeBackgroundFromImage(copyBitmap)
                        listener(Result.success(resultBitmap))
                    }
                }
            }
            .addOnFailureListener { e ->
                scope.launch {
                    withContext(Dispatchers.IO) {
                        listener(Result.failure(e))
                    }
                }
            }
    }


    /**
     * Change the background pixels color to transparent.
     * */
    private suspend fun removeBackgroundFromImage(
        image: Bitmap
    ): Bitmap {
        val bitmap = CoroutineScope(Dispatchers.IO).async {
            image.setHasAlpha(true)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val bgConfidence = ((1.0 - buffer.float) * 255).toInt()
                    if (bgConfidence >= 100) {
                        image.setPixel(x, y, 0)
                    }
                }
            }
            buffer.rewind()
            return@async image
        }
        return bitmap.await()
    }

}