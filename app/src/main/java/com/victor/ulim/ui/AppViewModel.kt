package com.victor.ulim.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.victor.ulim.data.local.StudentEntity
import com.victor.ulim.data.local.UlimDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlinx.coroutines.launch

/** Canonical keys stored in the database, mapped to localized labels in the UI. */
object Gen {
    const val FEMININ = "feminin"
    const val MASCULIN = "masculin"
}

object Specialty {
    const val INFORMATICA = "informatica"
    const val TEHNOLOGII = "tehnologii"
}

object Studii {
    const val SUPERIOARE = "superioare"
    const val MEDII = "medii"
}

/** Current form data while it is being completed. */
data class StudentDraft(
    val nume: String = "",
    val prenume: String = "",
    val dataNastere: String = "",
    val gen: String = Gen.MASCULIN, // "masculin implicit"
    val listaStudii: String = "",
    val specialitatea: String = "",
    val fotoUri: String = ""
) {
    val isComplete: Boolean
        get() = nume.isNotBlank() &&
            prenume.isNotBlank() &&
            dataNastere.isNotBlank() &&
            gen.isNotBlank() &&
            listaStudii.isNotBlank() &&
            specialitatea.isNotBlank() &&
            fotoUri.isNotBlank()
}

class AppViewModel(app: Application) : AndroidViewModel(app) {

    private val appContext: Context = app
    private val db = UlimDatabase.get(app)

    // --- Language (ro default; switchable in-app: ro / ru / en) ---

    var language: String by mutableStateOf(
        appContext.getSharedPreferences("ulim_prefs", Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, LANG_RO) ?: LANG_RO
    )
        private set

    fun selectLanguage(code: String) {
        language = code
        appContext.getSharedPreferences("ulim_prefs", Context.MODE_PRIVATE)
            .edit().putString(KEY_LANGUAGE, code).apply()
    }

    // --- Login ---

    var loggedIn: Boolean by mutableStateOf(false)
        private set

    suspend fun login(username: String, password: String): Boolean {
        val user = db.userDao().find(username.trim(), password)
        return if (user != null) {
            loggedIn = true
            true
        } else {
            false
        }
    }

    // --- Form state ---

    var draft: StudentDraft by mutableStateOf(StudentDraft())
        private set

    fun updateDraft(transform: (StudentDraft) -> StudentDraft) {
        draft = transform(draft)
    }

    /**
     * Copies the picked image into the app's private storage so it survives
     * across sessions (photo-picker content URIs are only valid for the session).
     */
    fun setPhoto(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            val app = getApplication<Application>()
            val file = File(app.filesDir, "student_photo.jpg")
            withContext(Dispatchers.IO) {
                runCatching {
                    app.contentResolver.openInputStream(uri)?.use { input ->
                        file.outputStream().use { output -> input.copyTo(output) }
                    }
                }
            }
            updateDraft { it.copy(fotoUri = Uri.fromFile(file).toString()) }
        }
    }

    // --- Last saved record (used when the form is empty) ---

    val savedStudent: StateFlow<StudentEntity?> = db.studentDao()
        .observeLatest()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        // Restore the last saved record when the app reopens, so the form and
        // the Display screen keep their data after being closed.
        viewModelScope.launch {
            val entity = db.studentDao().observeLatest().filterNotNull().firstOrNull()
            if (entity != null && draft.nume.isBlank() && draft.prenume.isBlank()) {
                draft = StudentDraft(
                    nume = entity.nume,
                    prenume = entity.prenume,
                    dataNastere = entity.dataNastere,
                    gen = entity.gen,
                    listaStudii = entity.listaStudii,
                    specialitatea = entity.specialitatea,
                    fotoUri = entity.fotoUri
                )
            }
        }
    }

    /** Saves the draft into the DB — caller guarantees the draft is complete. */
    suspend fun saveDraft(): Boolean {
        if (!draft.isComplete) return false
        db.studentDao().insert(
            StudentEntity(
                nume = draft.nume.trim(),
                prenume = draft.prenume.trim(),
                dataNastere = draft.dataNastere,
                gen = draft.gen,
                listaStudii = draft.listaStudii.trim(),
                specialitatea = draft.specialitatea,
                fotoUri = draft.fotoUri
            )
        )
        return true
    }

    fun logout() {
        loggedIn = false
    }

    companion object {
        const val KEY_LANGUAGE = "language"
        const val LANG_RO = "ro"
        const val LANG_RU = "ru"
        const val LANG_EN = "en"
    }
}
