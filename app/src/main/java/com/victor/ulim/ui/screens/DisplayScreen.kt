package com.victor.ulim.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.victor.ulim.R
import com.victor.ulim.ui.AppViewModel
import com.victor.ulim.ui.Gen
import com.victor.ulim.ui.Specialty
import com.victor.ulim.ui.StudentDraft
import com.victor.ulim.ui.components.Stagger
import com.victor.ulim.ui.components.LanguageMenu
import com.victor.ulim.ui.components.localizedStudiiLabel
import com.victor.ulim.ui.components.rememberShakeState
import com.victor.ulim.ui.components.shake
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * "Afisare" — shows the data selected in the form; "Salvare" stores it in the
 * database only when every field is completed, then offers to return to the first page.
 */
@Composable
fun DisplayScreen(
    vm: AppViewModel,
    snackbarHostState: SnackbarHostState,
    onBackHome: () -> Unit
) {
    val draft = vm.draft
    val cs = MaterialTheme.colorScheme
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val shake = rememberShakeState()
    var showSuccess by rememberSaveable { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }

    val hasData = draft.nume.isNotBlank() || draft.prenume.isNotBlank()

    Box(Modifier.fillMaxSize()) {
        LanguageMenu(
            current = vm.language,
            onSelect = vm::selectLanguage,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 12.dp)
                .zIndex(1f)
        )
        if (!hasData) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Stagger(0) {
                    Surface(
                        shape = CircleShape,
                        color = cs.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Icon(
                            Icons.Filled.ManageAccounts,
                            contentDescription = null,
                            tint = cs.onSurfaceVariant,
                            modifier = Modifier.padding(24.dp).size(48.dp)
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Stagger(1) {
                    Text(
                        text = stringResource(R.string.empty_display),
                        style = MaterialTheme.typography.bodyLarge,
                        color = cs.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Stagger(0) {
                    Text(
                        text = stringResource(R.string.display_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = cs.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Stagger(1) {
                    ProfileCard(draft)
                }

                Spacer(Modifier.height(24.dp))

                Stagger(2) {
                    Column {
                        Button(
                            onClick = {
                                if (saving) return@Button
                                if (!draft.isComplete) {
                                    scope.launch {
                                        shake.shake()
                                        snackbarHostState.showSnackbar(
                                            context.getString(R.string.incomplete_warning)
                                        )
                                    }
                                    return@Button
                                }
                                saving = true
                                scope.launch {
                                    val ok = vm.saveDraft()
                                    saving = false
                                    if (ok) {
                                        showSuccess = true
                                        delay(1100)
                                        showSuccess = false
                                        snackbarHostState.showSnackbar(
                                            context.getString(R.string.save_success)
                                        )
                                    }
                                }
                            },
                            enabled = !saving,
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .shake(shake)
                        ) {
                            Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.size(8.dp))
                            Text(
                                text = stringResource(R.string.save_button),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        if (!draft.isComplete) {
                            Text(
                                text = stringResource(R.string.incomplete_warning),
                                style = MaterialTheme.typography.labelMedium,
                                color = cs.error,
                                modifier = Modifier.padding(start = 4.dp, top = 6.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Stagger(3) {
                    OutlinedButton(
                        onClick = onBackHome,
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.back_home))
                    }
                }
                Spacer(Modifier.height(28.dp))
            }
        }

        // Animated confirmation overlay
        AnimatedVisibility(
            visible = showSuccess,
            enter = scaleIn(
                initialScale = 0.4f,
                animationSpec = spring(dampingRatio = 0.5f, stiffness = 380f)
            ) + fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = cs.primaryContainer,
                    shadowElevation = 12.dp
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = cs.onPrimaryContainer,
                        modifier = Modifier.padding(28.dp).size(56.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileCard(draft: StudentDraft) {
    val cs = MaterialTheme.colorScheme
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = 2.dp,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            if (draft.fotoUri.isNotBlank()) {
                AsyncImage(
                    model = draft.fotoUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(cs.surfaceVariant)
                )
            }
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "${draft.nume} ${draft.prenume}".trim(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = cs.onSurface
                )
                HorizontalDivider(color = cs.outlineVariant.copy(alpha = 0.5f))

                InfoRow(Icons.Filled.Person, stringResource(R.string.field_nume), draft.nume.ifBlank { stringResource(R.string.not_selected) })
                InfoRow(Icons.Filled.Person, stringResource(R.string.field_prenume), draft.prenume.ifBlank { stringResource(R.string.not_selected) })
                InfoRow(Icons.Filled.CalendarMonth, stringResource(R.string.field_data_nastere), draft.dataNastere.ifBlank { stringResource(R.string.not_selected) })
                InfoRow(
                    Icons.Filled.Wc,
                    stringResource(R.string.field_gen),
                    genLabel(draft.gen)
                )
                InfoRow(Icons.Filled.School, stringResource(R.string.field_lista_studii), localizedStudiiLabel(draft.listaStudii))
                InfoRow(
                    Icons.Filled.Work,
                    stringResource(R.string.field_specialitatea),
                    specialtyLabel(draft.specialitatea)
                )
            }
        }
    }
}

@Composable
private fun genLabel(key: String): String = when (key) {
    Gen.MASCULIN -> stringResource(R.string.gen_masculin)
    Gen.FEMININ -> stringResource(R.string.gen_feminin)
    else -> key.ifBlank { stringResource(R.string.not_selected) }
}

@Composable
private fun specialtyLabel(key: String): String = when (key) {
    Specialty.INFORMATICA -> stringResource(R.string.spec_informatica)
    Specialty.TEHNOLOGII -> stringResource(R.string.spec_tehnologii)
    else -> key.ifBlank { stringResource(R.string.not_selected) }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = cs.primary.copy(alpha = 0.8f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.size(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = cs.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = cs.onSurface,
            textAlign = TextAlign.End
        )
    }
}
