package com.suqi8.oshin.features.appdetail

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object appdetail {
    val definition = PageDefinition(
        category = "appdetail",
        appList = listOf("com.oplus.appdetail"),
        title = AppName("com.oplus.appdetail"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    /*Switch(
                        title = StringResource(R.string.remove_recommendations),
                        key = "remove_recommendations"
                    ),
                    Switch(
                        title = StringResource(R.string.remove_installation_frequency_popup),
                        key = "remove_installation_frequency_popup"
                    ),
                    Switch(
                        title = StringResource(R.string.remove_attempt_installation_popup),
                        key = "remove_attempt_installation_popup"
                    ),*/
                    Switch(
                        title = StringResource(R.string.remove_version_check),
                        key = "remove_version_check"
                    ),
                    Switch(
                        title = StringResource(R.string.remove_security_check),
                        key = "remove_security_check"
                    )
                )
            )
        )
    )
}
