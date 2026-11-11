package dev.alexmester.ui.error

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import dev.alexmester.models.error.AppError
import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.error.StorageError
import dev.alexmester.ui.R
import dev.alexmester.ui.design_system.LaskColors
import dev.alexmester.ui.uitext.UiText

data class NotificationUi(
    val image: ImageVector,
    val text: String,
    val tint: Color
)

object AppErrorUiMapper {

    fun toUiText(error: AppError): UiText = when (error) {
        is NetworkError -> error.toUiText()
        is StorageError -> error.toUiText()
    }

    private fun NetworkError.toUiText(): UiText = when (this) {
        is NetworkError.NoInternet -> UiText.StringResource(R.string.error_no_internet)
        is NetworkError.Timeout -> UiText.StringResource(R.string.error_timeout)
        is NetworkError.PaymentRequired -> UiText.StringResource(R.string.error_payment_required)
        is NetworkError.RateLimit -> toUiText()
        is NetworkError.BadGateway -> UiText.StringResource(R.string.error_server)
        is NetworkError.TranslateError -> UiText.StringResource(R.string.error_translation_failed)
        is NetworkError.ParseError -> UiText.StringResource(R.string.error_parse)
        is NetworkError.HttpError -> toUiText()
        is NetworkError.Unknown -> UiText.StringResource(R.string.error_unknown)
    }

    private fun StorageError.toUiText(): UiText = when (this) {
        is StorageError.NoSpace -> UiText.StringResource(R.string.error_storage_full)
        is StorageError.Busy -> UiText.StringResource(R.string.error_storage_busy)
        is StorageError.Corrupted -> UiText.StringResource(R.string.error_storage_corrupted)
        is StorageError.Constraint,
        is StorageError.Io,
        is StorageError.Unknown -> UiText.StringResource(R.string.error_storage_failed)
    }

    private fun NetworkError.RateLimit.toUiText(): UiText =
        retryAfterSeconds?.let { seconds ->
            UiText.StringResource(R.string.error_rate_limit_retry, args = arrayOf(seconds))
        } ?: UiText.StringResource(R.string.error_rate_limit)

    private fun NetworkError.HttpError.toUiText(): UiText = when (code) {
        400 -> UiText.StringResource(R.string.error_bad_request)
        401 -> UiText.StringResource(R.string.error_unauthorized)
        403 -> UiText.StringResource(R.string.error_forbidden)
        404 -> UiText.StringResource(R.string.error_not_found)
        408 -> UiText.StringResource(R.string.error_timeout)
        429 -> UiText.StringResource(R.string.error_rate_limit)
        in 500..599 -> UiText.StringResource(R.string.error_server)
        in 400..499 -> UiText.StringResource(R.string.error_bad_request)
        else -> UiText.StringResource(R.string.error_unknown)
    }

    @Composable
    fun mapToNotificationUi(error: AppError): NotificationUi {
        val image = when (error) {
            is NetworkError.NoInternet -> ImageVector.vectorResource(R.drawable.ic_no_internet_error)
            is NetworkError.PaymentRequired -> ImageVector.vectorResource(R.drawable.ic_payment_required)
            is NetworkError.RateLimit -> ImageVector.vectorResource(R.drawable.ic_rate_limit_error)
            is NetworkError.Timeout,
            is NetworkError.BadGateway,
            is NetworkError.TranslateError,
            is NetworkError.ParseError,
            is NetworkError.HttpError,
            is NetworkError.Unknown,
            is StorageError -> ImageVector.vectorResource(R.drawable.ic_unknown_error)
        }
        return NotificationUi(
            image = image,
            text = toUiText(error).asString(),
            tint = MaterialTheme.LaskColors.error
        )
    }
}
