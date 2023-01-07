package com.ngedev.newsapplicationcompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ngedev.newsapplicationcompose.domain.model.Article
import com.ngedev.newsapplicationcompose.domain.usecase.ArticleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

class DiscoverViewModel(private val useCase: ArticleUseCase) : ViewModel() {

    private val _pagingArticles = MutableStateFlow<PagingData<Article>>(PagingData.empty())
    val listArticle: StateFlow<PagingData<Article>> = _pagingArticles

    init {
        getArticleRelateWith("google")
    }

    fun getArticleRelateWith(query:  String) {
        viewModelScope.launch {
            useCase.getArticlesRelateWith(query).cachedIn(viewModelScope).collect { pagingDataArticle ->
                _pagingArticles.value = pagingDataArticle
            }
        }
    }

    companion object {
        fun inject() = module {
            viewModelOf(::DiscoverViewModel)
        }
    }
}

