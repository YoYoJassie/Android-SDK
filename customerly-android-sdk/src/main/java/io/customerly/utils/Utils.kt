package io.customerly.utils

/*
 * Copyright (C) 2017 Customerly
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.graphics.Color
import io.customerly.sxdependencies.annotations.SXColorInt
import io.customerly.sxdependencies.annotations.SXFloatRange
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

/**
 * Created by Gianni on 04/04/18.
 * Project: Customerly-KAndroid-SDK
 */
@SXColorInt
fun Int.getContrastBW(): Int {
    return if (this == 0 || 0.299 * Color.red(this) + (0.587 * Color.green(this) + 0.114 * Color.blue(this)) > 186) {
        Color.BLACK
    } else {
        Color.WHITE
    }
}


@SXColorInt
fun Int.alterColor(@SXFloatRange(from = 0.0, to = 255.0) factor: Float): Int {
    return Color.argb(Color.alpha(this),
            min(255f, Color.red(this) * factor).toInt(),
            min(255f, Color.green(this) * factor).toInt(),
            min(255f, Color.blue(this) * factor).toInt())
}


val shortDateFomatter: DateFormat? by lazy { SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, Locale.ITALIAN) }