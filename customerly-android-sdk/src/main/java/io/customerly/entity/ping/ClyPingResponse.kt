package io.customerly.entity.ping

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

import android.content.SharedPreferences
import android.graphics.Color
import android.support.annotation.ColorInt
import io.customerly.Customerly
import io.customerly.entity.*
import io.customerly.entity.chat.ClyMessage
import io.customerly.entity.chat.parseMessage
import io.customerly.utils.COLORINT_BLUE_MALIBU
import io.customerly.utils.ggkext.*
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Gianni on 29/04/18.
 * Project: Customerly-KAndroid-SDK
 */
internal fun JSONObject.parsePing(): ClyPingResponse {
    return try {
        val minVersion = this.optTyped(name = "min-version-android", fallback = "0.0.0")
        val activeAdmins = this.optArray<JSONObject, ClyAdmin>(name = "active_admins", map = { it.parseAdmin() })
        val userProfilingForms = this.optArray<JSONObject, ClyProfilingForm>(name = "user_profiling_forms", map = { it.parseProfilingForm() })?.apply { this.sortBy { it.id } }
        val appStates = this.getTyped<JSONObject>("app_states").asValuesSequence().mapNotNull { (it as? JSONObject)?.parseAppState() }.toList().toTypedArray()
        val lastSurveys = this.optArray<JSONObject, ClySurvey>(name = "last_surveys", map = { it.parseSurvey() })
        val lastMessages = this.optArray<JSONObject, ClyMessage>(name = "last_messages", map = { it.nullOnException { it.parseMessage() } })
        val userDataAttributes = this.optTyped<JSONObject>("user")?.parseUserDataAttributes()
        this.optTyped<JSONObject>(name = "app_config")?.let { appConfig ->
            @ColorInt val widgetColor: Int = Customerly.widgetColorHardcoded ?: appConfig.optTyped<String>(name = "widget_color")?.takeIf { it.isNotEmpty() }?.let {
                    when {
                        it.firstOrNull() != '#' -> "#$it"
                        else -> it
                    }
                }?.let {
                    try {
                        Color.parseColor(it)
                    } catch (exception: IllegalArgumentException) {
                        clySendError(errorCode = ERROR_CODE__HTTP_RESPONSE_ERROR, description = "ClyPingResponse:data.apps.app_config.widget_color is an invalid argb color: '$it'", throwable = exception)
                        null
                    }
                } ?: COLORINT_BLUE_MALIBU

            ClyPingResponse(
                    minVersion = minVersion,
                    widgetColor = widgetColor,
                    widgetBackgroundUrl = appConfig.optTyped(name = "widget_background_url"),
                    privacyUrl = appConfig.optTyped(name = "widget_privacy_url"),
                    poweredBy = appConfig.optTyped(name = "powered_by", fallback = 0L) == 1L,
                    welcomeMessageUsers = appConfig.optTyped(name = "welcome_message_users"),
                    welcomeMessageVisitors = appConfig.optTyped(name = "welcome_message_visitors"),
                    userProfilingFormEnabled = appConfig.optTyped(name = "user_profiling_form_enabled", fallback = 0) == 1,
                    userProfilingForms = userProfilingForms,
                    appStates = appStates,
                    officeHours = appConfig.optArray<JSONObject, ClyOfficeHours>(name = "office_hours", map = { it.parseOfficeHours() }),
                    replyTime = appConfig.optTyped(name = "reply_time", fallback = 0).toClyReplyTime,
                    activeAdmins = activeAdmins,
                    lastSurveys = lastSurveys,
                    lastMessages = lastMessages,
                    userDataAttributes = userDataAttributes)

        } ?: ClyPingResponse(
                minVersion = minVersion,
                activeAdmins = activeAdmins,
                lastSurveys = lastSurveys,
                lastMessages = lastMessages,
                userProfilingForms = userProfilingForms,
                appStates = appStates,
                userDataAttributes = userDataAttributes)
    } catch (wrongJson: JSONException) {
        ClyPingResponse()
    }
}

private const val PREFS_KEY_MIN_VERSION             = "CUSTOMERLY_LASTPING_MIN_VERSION"
private const val PREFS_KEY_WIDGET_COLOR            = "CUSTOMERLY_LASTPING_WIDGET_COLOR"
private const val PREFS_KEY_BACKGROUND_THEME_URL    = "CUSTOMERLY_LASTPING_BACKGROUND_THEME_URL"
private const val PREFS_KEY_PRIVACY_URL             = "CUSTOMERLY_LASTPING_PRIVACY_URL"
private const val PREFS_KEY_POWERED_BY              = "CUSTOMERLY_LASTPING_POWERED_BY"
private const val PREFS_KEY_WELCOME_USERS           = "CUSTOMERLY_LASTPING_WELCOME_USERS"
private const val PREFS_KEY_WELCOME_VISITORS        = "CUSTOMERLY_LASTPING_WELCOME_VISITORS"

internal fun SharedPreferences.lastPingRestore() : ClyPingResponse {
    return ClyPingResponse(
            minVersion = this.safeString(io.customerly.entity.ping.PREFS_KEY_MIN_VERSION, "0.0.0"),
            widgetColor = this.safeInt(io.customerly.entity.ping.PREFS_KEY_WIDGET_COLOR, Customerly.widgetColorFallback),
            widgetBackgroundUrl = this.safeString(io.customerly.entity.ping.PREFS_KEY_BACKGROUND_THEME_URL),
            privacyUrl = this.safeString(io.customerly.entity.ping.PREFS_KEY_PRIVACY_URL),
            poweredBy = this.safeBoolean(io.customerly.entity.ping.PREFS_KEY_POWERED_BY, true),
            welcomeMessageUsers = this.safeString(io.customerly.entity.ping.PREFS_KEY_WELCOME_USERS),
            welcomeMessageVisitors = this.safeString(io.customerly.entity.ping.PREFS_KEY_WELCOME_VISITORS))
}

private fun SharedPreferences?.lastPingStore(lastPing: ClyPingResponse) {
    this?.edit()
            ?.putString(PREFS_KEY_MIN_VERSION, lastPing.minVersion)
            ?.putInt(PREFS_KEY_WIDGET_COLOR, lastPing.widgetColor)
            ?.putString(PREFS_KEY_BACKGROUND_THEME_URL, lastPing.widgetBackgroundUrl)
            ?.putString(PREFS_KEY_PRIVACY_URL, lastPing.privacyUrl)
            ?.putBoolean(PREFS_KEY_POWERED_BY, lastPing.poweredBy)
            ?.putString(PREFS_KEY_WELCOME_USERS, lastPing.welcomeMessageUsers)
            ?.putString(PREFS_KEY_WELCOME_VISITORS, lastPing.welcomeMessageVisitors)
        ?.apply()
}

internal class ClyPingResponse(
        internal val minVersion: String = "0.0.0",
        @ColorInt internal val widgetColor: Int = Customerly.widgetColorFallback,
        internal val widgetBackgroundUrl: String? = null,
        internal val privacyUrl: String? = null,
        internal val poweredBy: Boolean = true,
        internal val welcomeMessageUsers: String? = null,
        internal val welcomeMessageVisitors:String? = null,
        internal val userProfilingFormEnabled:Boolean = false,
        internal val activeAdmins: Array<ClyAdmin>? = null,
        internal val lastSurveys: Array<ClySurvey>? = null,
        internal val lastMessages: Array<ClyMessage>? = null,
        internal val officeHours: Array<ClyOfficeHours>? = null,
        internal val replyTime: ClyReplyTime? = null,
        internal val userProfilingForms: Array<ClyProfilingForm>? = null,
        internal val appStates: Array<ClyFormDetails>? = null,
        internal val userDataAttributes: ClyUserDataAttributes? = null) {

    init {
        Customerly.preferences?.lastPingStore(lastPing = this)
    }


    internal fun tryShowSurvey(): Boolean {
        return if(Customerly.isSurveyEnabled) {
            val survey = this.lastSurveys?.firstOrNull { !it.isRejectedOrConcluded }
            if(survey != null) {
                survey.postDisplay()
                true
            } else {
                Customerly.log(message = "No Survey to display")
                false
            }
        } else {
            false
        }
    }

    internal fun tryShowLastMessage(): Boolean {
        return if(Customerly.isSupportEnabled) {
            val lastMessage = this.lastMessages?.firstOrNull()
            if(lastMessage != null) {
                lastMessage.postDisplay()
                true
            } else {
                Customerly.log(message = "No Last Messages to display")
                false
            }
        } else {
            false
        }
    }
    
    internal fun tryProfilingForm(): ClyFormDetails? {
        return if(this.userProfilingFormEnabled) {
            this.userProfilingForms?.asSequence()?.mapNotNull { form ->
                if(this.userDataAttributes?.needFormProfiling(form) != false) {
                    this.appStates?.firstOrNull { state -> state.attributeName == form.appStateName }
                } else {
                    null
                }
            }?.firstOrNull()
        } else {
            null
        }
    }
}