package com.dorukaneskiceri.spaceflightnews.ui.articledetail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dorukaneskiceri.spaceflightnews.ui.articledetail.model.ArticleDetailNavigationModel
import com.dorukaneskiceri.spaceflightnews.ui.articledetail.model.ArticleDetailUiState.Companion.initial
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val articleDetail = savedStateHandle.toRoute<ArticleDetailNavigationModel>()

    private var _state = MutableStateFlow(initial())
    val state = _state.onStart {
        _state.update {
            it.copy(articleDetail = articleDetail)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = initial()
    )

}