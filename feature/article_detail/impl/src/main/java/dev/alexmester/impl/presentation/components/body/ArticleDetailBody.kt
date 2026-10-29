package dev.alexmester.impl.presentation.components.body

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.presentation.mvi.TranslationState
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.ui.components.bottom_bar.LocalBottomBarHeight

@Composable
internal fun ArticleDetailBody(
    article: NewsArticle,
    translatedTitle: String?,
    translatedText: String?,
    translationState: TranslationState,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
    ) {
        ArticleDetailCategory(category = article.category)
        Spacer(modifier = Modifier.height(8.dp))
        ArticleDetailTitle(
            title = article.title,
            articleId = article.id,
            translatedTitle = translatedTitle,
            translationState = translationState,
        )
        Spacer(modifier = Modifier.height(16.dp))
        ArticleDetailAuthorRow(
            authors = article.authors,
            publishDate = article.publishDate,
            sentiment = article.sentiment
        )
        Spacer(modifier = Modifier.height(24.dp))
        ArticleDetailText(
            text = article.text,
            summary = article.summary,
            translatedText = translatedText,
            translationState = translationState,
        )
        article.video?.let { videoUrl ->
            Spacer(modifier = Modifier.height(24.dp))
            ArticleDetailVideoPlayer(videoUrl = videoUrl)
        }
        Spacer(modifier = Modifier.height(LocalBottomBarHeight.current + 16.dp))
    }
}