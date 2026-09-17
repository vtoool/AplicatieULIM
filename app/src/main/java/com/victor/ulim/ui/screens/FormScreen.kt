package com.victor.ulim.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.victor.ulim.R
import com.victor.ulim.ui.AppViewModel
import com.victor.ulim.ui.Gen
import com.victor.ulim.ui.Studii
import com.victor.ulim.ui.Specialty
import com.victor.ulim.ui.components.FieldErrorText
import com.victor.ulim.ui.components.SectionHeader
import com.victor.ulim.ui.components.Stagger
import com.victor.ulim.ui.components.localizedStudiiLabel
import com.victor.ulim.ui.components.rememberShakeState
import com.victor.ulim.ui.components.shake
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * "Formular" — nume, prenume, data nașterii (datapicker), foto, lista studii
 * (completable list), gen (radio, masculin implicit), specialitatea (radio).
 */
@Composable
fun FormScreen(
    vm: AppViewModel,
    snackbarHostState: SnackbarHostState,
    onContinue: () -> Unit
) {
    val draft = vm.draft
    val cs = MaterialTheme.colorScheme
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val shake = rememberShakeState()

    var showErrors by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var dropdownExpanded by rememberSaveable { mutableStateOf(false) }

    val numeError = showErrors && draft.nume.isBlank()
    val prenumeError = showErrors && draft.prenume.isBlank()
    val studiiError = showErrors && draft.listaStudii.isBlank()
    val specError = showErrors && draft.specialitatea.isBlank()
    val fotoError = showErrors && draft.fotoUri.isBlank()

    // Strings captured at screen scope: popups and dialogs resolve their own
    // window's locale, so they must receive pre-resolved text.
    val okLabel = stringResource(R.string.date_ok)
    val cancelLabel = stringResource(R.string.date_cancel)
    val selectDateLabel = stringResource(R.string.date_select)
    val studiiOptions = listOf(
        Studii.SUPERIOARE to stringResource(R.string.studii_superioare),
        Studii.MEDII to stringResource(R.string.studii_medii)
    )

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { vm.setPhoto(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Stagger(0) {
            Text(
                text = stringResource(R.string.form_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = cs.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        // --- Foto ---
        Stagger(1) {
            Column(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(cs.primaryContainer.copy(alpha = 0.5f))
                            .border(
                                width = 2.dp,
                                color = cs.primary.copy(alpha = if (fotoError) 1f else 0.3f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (draft.fotoUri.isNotBlank()) {
                            AsyncImage(
                                model = draft.fotoUri,
                                contentDescription = stringResource(R.string.field_foto),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(84.dp)
                            )
                        } else {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = null,
                                tint = cs.onSurfaceVariant,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                    Spacer(Modifier.size(16.dp))
                    TextButton(
                        onClick = {
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    ) {
                        Icon(Icons.Filled.AddAPhoto, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.size(8.dp))
                        Text(
                            if (draft.fotoUri.isBlank()) stringResource(R.string.add_photo)
                            else stringResource(R.string.change_photo)
                        )
                    }
                }
                if (fotoError) FieldErrorText(stringResource(R.string.field_required))
            }
        }

        // --- Nume / Prenume ---
        Stagger(2) {
            OutlinedTextField(
                value = draft.nume,
                onValueChange = { vm.updateDraft { d -> d.copy(nume = it) } },
                label = { Text(stringResource(R.string.field_nume)) },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                isError = numeError,
                supportingText = if (numeError) {
                    { Text(stringResource(R.string.field_required), color = cs.error) }
                } else null,
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.height(12.dp))
        Stagger(3) {
            OutlinedTextField(
                value = draft.prenume,
                onValueChange = { vm.updateDraft { d -> d.copy(prenume = it) } },
                label = { Text(stringResource(R.string.field_prenume)) },
                isError = prenumeError,
                supportingText = if (prenumeError) {
                    { Text(stringResource(R.string.field_required), color = cs.error) }
                } else null,
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // --- Data nașterii (datapicker) ---
        Spacer(Modifier.height(12.dp))
        Stagger(4) {
            Box(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = draft.dataNastere,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.field_data_nastere)) },
                    trailingIcon = {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                )
                // The whole field opens the picker
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { showDatePicker = true }
                )
            }
        }

        // --- Lista studii (completable dropdown) ---
        Spacer(Modifier.height(12.dp))
        Stagger(5) {
            Box(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = if (draft.listaStudii.isNotBlank()) localizedStudiiLabel(draft.listaStudii) else "",
                    onValueChange = { vm.updateDraft { d -> d.copy(listaStudii = it) } },
                    label = { Text(stringResource(R.string.field_lista_studii)) },
                    leadingIcon = { Icon(Icons.Filled.School, contentDescription = null) },
                    isError = studiiError,
                    supportingText = if (studiiError) {
                        { Text(stringResource(R.string.field_required), color = cs.error) }
                    } else null,
                    trailingIcon = {
                        IconButton(onClick = { dropdownExpanded = true }) {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                        }
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    studiiOptions.forEach { (key, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                vm.updateDraft { d -> d.copy(listaStudii = key) }
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // --- Gen (radio, masculin implicit) ---
        Spacer(Modifier.height(12.dp))
        Stagger(6) {
            RadioGroupCard(
                title = stringResource(R.string.field_gen),
                options = listOf(
                    Triple(Icons.Filled.Male, stringResource(R.string.gen_masculin), Gen.MASCULIN),
                    Triple(Icons.Filled.Female, stringResource(R.string.gen_feminin), Gen.FEMININ)
                ),
                selected = draft.gen,
                onSelect = { key -> vm.updateDraft { d -> d.copy(gen = key) } }
            )
        }

        // --- Specialitatea (radio) ---
        Spacer(Modifier.height(12.dp))
        Stagger(7) {
            Column(Modifier.fillMaxWidth()) {
                RadioGroupCard(
                    title = stringResource(R.string.field_specialitatea),
                    options = listOf(
                        Triple(Icons.Filled.Computer, stringResource(R.string.spec_informatica), Specialty.INFORMATICA),
                        Triple(Icons.Filled.PrecisionManufacturing, stringResource(R.string.spec_tehnologii), Specialty.TEHNOLOGII)
                    ),
                    selected = draft.specialitatea,
                    onSelect = { key -> vm.updateDraft { d -> d.copy(specialitatea = key) } },
                    isError = specError
                )
                if (specError) FieldErrorText(stringResource(R.string.field_required))
            }
        }

        Spacer(Modifier.height(24.dp))
        Stagger(8) {
            Button(
                onClick = {
                    if (draft.isComplete) {
                        onContinue()
                    } else {
                        showErrors = true
                        scope.launch {
                            shake.shake()
                            snackbarHostState.showSnackbar(
                                context.getString(R.string.incomplete_warning)
                            )
                        }
                    }
                },
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shake(shake)
            ) {
                Text(
                    text = stringResource(R.string.continue_button),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(Modifier.height(28.dp))
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            yearRange = 1900..2100,
            initialSelectedDateMillis = remember { System.currentTimeMillis() }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            vm.updateDraft { d -> d.copy(dataNastere = formatDate(millis)) }
                        }
                        showDatePicker = false
                    }
                ) { Text(okLabel) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(cancelLabel)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = selectDateLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    )
                }
            )
        }
    }
}

private val utcDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

/** DatePicker millis are UTC-based — format them in UTC to avoid off-by-one days. */
private fun formatDate(millis: Long): String = utcDateFormat.format(Date(millis))

@Composable
private fun RadioGroupCard(
    title: String,
    options: List<Triple<ImageVector, String, String>>, // icon, label, key
    selected: String,
    onSelect: (String) -> Unit,
    isError: Boolean = false
) {
    val cs = MaterialTheme.colorScheme
    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            SectionHeader(title, Modifier.padding(top = 0.dp))
            options.forEach { (icon, label, key) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .selectable(
                            selected = selected == key,
                            role = Role.RadioButton,
                            onClick = { onSelect(key) }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = selected == key, onClick = null)
                    Spacer(Modifier.size(4.dp))
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = if (selected == key) cs.primary else cs.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (selected == key) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isError && selected.isBlank()) cs.error else cs.onSurface
                    )
                }
            }
        }
    }
}
