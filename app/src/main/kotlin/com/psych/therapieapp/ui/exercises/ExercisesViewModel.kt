package com.psych.therapieapp.ui.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.psych.therapieapp.data.local.Converters
import com.psych.therapieapp.data.local.entity.Exercise
import com.psych.therapieapp.data.local.entity.ExerciseCategory
import com.psych.therapieapp.data.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExercisesViewModel @Inject constructor(
    private val repository: ExerciseRepository
) : ViewModel() {

    private val converters = Converters()

    private val _selectedCategory = MutableStateFlow(ExerciseCategory.BREATHING)
    val selectedCategory: StateFlow<ExerciseCategory> = _selectedCategory

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val listByCategory: StateFlow<List<Exercise>> =
        _selectedCategory
            .flatMapLatest { repository.getByCategory(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    init {
        viewModelScope.launch {
            repository.seedDatabase()
        }
    }

    fun selectCategory(category: ExerciseCategory) {
        _selectedCategory.value = category
    }

    fun getExerciseFlow(id: Long): Flow<Exercise?> = kotlinx.coroutines.flow.flow {
        emit(repository.getById(id))
    }

    suspend fun loadExercise(id: Long): Exercise? = repository.getById(id)

    fun parseSteps(stepsJson: String): List<String> = converters.toStepList(stepsJson)
}
