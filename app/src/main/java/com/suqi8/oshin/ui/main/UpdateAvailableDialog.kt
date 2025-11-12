package com.suqi8.oshin.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.nav.path.NavPath
import com.suqi8.oshin.ui.nav.transition.NavTransitionType
import com.suqi8.oshin.ui.nav.ui.NavStackScope
import com.suqi8.oshin.ui.softupdate.GitHubRelease
import com.suqi8.oshin.ui.softupdate.UpdateViewModel
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SuperDialog

@Composable
fun UpdateAvailableDialog(
    release: GitHubRelease?,
    navPath: NavPath,
    navStackScope: NavStackScope,
    onDismiss: () -> Unit,
    updateViewModel: UpdateViewModel
) {
    val show = remember(release) { mutableStateOf(release != null) }

    SuperDialog(
        show = show,
        title = stringResource(R.string.update_page_status_new_version),
        summary = stringResource(R.string.update_available_dialog_summary, release?.name ?: ""),
        onDismissRequest = onDismiss
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.cancel),
                onClick = {
                    onDismiss()
                    show.value = false
                }
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.update_available_dialog_now),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                onClick = {
                    onDismiss()
                    show.value = false
                    updateViewModel.setAutoDownloadFlag()
                    navPath.push(item = "software_update", navTransitionType = NavTransitionType.Zoom)
                }
            )
        }
    }
}
