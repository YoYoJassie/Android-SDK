package io.customerly;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Gianni on 09/09/16.
 * Project: CustomerlySDK
 */
class IEr_CustomerlyErrorHandler {

    @IntDef({ERROR_CODE__CUSTOMERLY_NOT_CONFIGURED, ERROR_CODE__IO_ERROR, ERROR_CODE__HTTP_REQUEST_ERROR,
            ERROR_CODE__HTTP_RESPONSE_ERROR, ERROR_CODE__GLIDE_ERROR, ERROR_CODE__ATTACHMENT_ERROR,
            ERROR_CODE__GENERIC})
    @Retention(RetentionPolicy.SOURCE)
    @interface ErrorCode {}

    @ErrorCode private static final int ERROR_CODE__CUSTOMERLY_NOT_CONFIGURED = 1;
    @ErrorCode static final int ERROR_CODE__IO_ERROR = 2;
    @ErrorCode static final int ERROR_CODE__HTTP_REQUEST_ERROR = 3;
    @ErrorCode static final int ERROR_CODE__HTTP_RESPONSE_ERROR = 4;
    @ErrorCode static final int ERROR_CODE__GLIDE_ERROR = 5;
    @ErrorCode static final int ERROR_CODE__ATTACHMENT_ERROR = 6;
    @ErrorCode static final int ERROR_CODE__GENERIC = 7;

    static void sendNotConfiguredError() {
        IEr_CustomerlyErrorHandler.sendError(IEr_CustomerlyErrorHandler.ERROR_CODE__CUSTOMERLY_NOT_CONFIGURED, BuildConfig.CUSTOMERLY_SDK_NAME + "is not configured", null);
    }
    static void sendError(@ErrorCode int pErrorCode, @NonNull String pDescription, @Nullable Throwable pThrowable) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stacktrace = pThrowable != null ? pThrowable.getStackTrace() : Thread.currentThread().getStackTrace();
        for(StackTraceElement sse : stacktrace) {
            sb.append(sse.toString()).append('\n');
        }
        sb.setLength(sb.length() - 1);

        //noinspection SpellCheckingInspection
        new IApi_Request.Builder<Void>(IApi_Request.ENDPOINT_REPORT_CRASH)
                .param("error_code", pErrorCode)
                .param("error_message", pDescription)
                .param("fullstacktrace", sb.toString())
                .start();

        Customerly._Instance._log("Error sent -> code: " + pErrorCode + " ||| message: " + pDescription + " ||| stack:\n" + sb.toString());
    }
}