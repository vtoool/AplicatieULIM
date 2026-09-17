package com.victor.ulim.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.victor.ulim.R
import com.victor.ulim.ui.Studii

/**
 * Maps a canonical "lista studii" key ("superioare" / "medii") to its localized
 * label; free-text entries are shown as typed.
 */
@Composable
fun localizedStudiiLabel(key: String): String = when (key) {
    Studii.SUPERIOARE -> stringResource(R.string.studii_superioare)
    Studii.MEDII -> stringResource(R.string.studii_medii)
    else -> key.ifBlank { stringResource(R.string.not_selected) }
}
