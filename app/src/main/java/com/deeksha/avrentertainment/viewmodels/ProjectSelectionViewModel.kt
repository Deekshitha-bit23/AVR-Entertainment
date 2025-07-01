package com.deeksha.avrentertainment.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deeksha.avrentertainment.models.Project
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProjectSelectionViewModel : ViewModel() {
    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadProjects()
    }

    fun loadProjects() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val db = FirebaseFirestore.getInstance()
                val snapshot = db.collection("projects").get().await()
                val projectList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Project::class.java)?.copy(id = doc.id)
                }
                _projects.value = projectList
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
} 