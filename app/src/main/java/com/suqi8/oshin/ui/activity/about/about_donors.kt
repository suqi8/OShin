package com.suqi8.oshin.ui.activity.about

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import com.suqi8.oshin.ui.activity.funlistui.addline
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card

@Composable
fun about_donors(navController: NavController) {
    FunPage(
        title = stringResource(id = R.string.donors_list),
        navController = navController
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp)
        ) {
            DonorsItem(
                name = "酷安：不愧是你82739",
                donors = 40f
            )
            addline()
            DonorsItem(
                name = "*超",
                donors = 5f
            )
        }
    }
}

@Composable
internal fun DonorsItem(
    name: String,
    donors: Float
) {
    val context = LocalContext.current
    BasicComponent(
        title = name,
        summary = donors.toString()+"￥"
    )
}
