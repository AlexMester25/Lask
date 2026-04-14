package dev.alexmester.ui.components.list_card

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.ui.components.list_card.components.DefaultLayout
import dev.alexmester.ui.components.list_card.components.LeadingLayout

enum class ArticleCardVariant {
    Leading,
    Default
}

@Composable
fun LaskArticleCard(
    modifier: Modifier = Modifier,
    article: NewsArticle,
    selectionMode: Boolean = false,
    variant: ArticleCardVariant = ArticleCardVariant.Default,
    isKept: Boolean = true,
    isRead: Boolean = false,
    onBookmarkToggle: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    when (variant) {
        ArticleCardVariant.Leading -> {
            LeadingLayout(
                modifier = modifier,
                article = article,
                isRead = isRead,
                onClick = onClick
            )
        }
        ArticleCardVariant.Default -> {
            DefaultLayout(
                modifier = modifier,
                article = article,
                isRead = isRead,
                selectionMode = selectionMode,
                isKept = isKept,
                onBookmarkToggle = onBookmarkToggle,
                onClick = onClick
            )
        }
    }
}







