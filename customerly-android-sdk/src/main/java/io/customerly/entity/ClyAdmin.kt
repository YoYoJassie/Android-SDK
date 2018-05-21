package io.customerly.entity

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

import android.support.annotation.Px
import io.customerly.utils.ggkext.optTyped
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Gianni on 11/09/16.
 * Project: Customerly Android SDK
 */

@Throws(JSONException::class)
internal fun JSONObject.parseAdmin() : ClyAdmin {
    return ClyAdmin(
            account_id = this.optTyped(name = "account_id", fallback = 0L),
            name = this.optTyped(name = "name", fallback = ""),
            last_active = this.optTyped(name = "last_active", fallback = 0L))
}

internal data class ClyAdmin (
            private val account_id: Long,
            val name: String,
            val last_active: Long) {

    internal fun getImageUrl(@Px sizePx: Int) : String = urlImageAccount(accountId = this.account_id, sizePX = sizePx)
}