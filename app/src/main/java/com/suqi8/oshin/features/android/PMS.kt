package com.suqi8.oshin.features.android

import com.suqi8.oshin.R
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object PMS {
    val definition = PageDefinition(
        category = "android\\package_manager_services",
        appList = listOf("android"),
        title = StringResource(R.string.package_manager_services),
        // 2. 定义页面的卡片列表
        items = listOf(
            CardDefinition(
                titleRes = R.string.common_settings,
                items = listOf(
                    Switch(
                        title = StringResource(R.string.allow_downgrade_title),
                        summary = R.string.allow_downgrade_summary,
                        key = "allow_downgrade"
                    ),
                    Switch(
                        title = StringResource(R.string.allow_signature_mismatch_on_update_title),
                        summary = R.string.allow_signature_mismatch_on_update_summary,
                        key = "allow_signature_mismatch_on_update"
                    )
                )
            ),
            CardDefinition(
                titleRes = R.string.verification_bypass_settings,
                items = listOf(
                    Switch(
                        title = StringResource(R.string.disable_jar_verifier_title),
                        summary = R.string.disable_jar_verifier_summary,
                        key = "disable_jar_verifier"
                    ),
                    Switch(
                        title = StringResource(R.string.disable_message_digest_title),
                        summary = R.string.disable_message_digest_summary,
                        key = "disable_message_digest"
                    ),
                    Switch(
                        title = StringResource(R.string.bypass_arsc_uncompressed_check_title),
                        summary = R.string.bypass_arsc_uncompressed_check_summary,
                        key = "bypass_arsc_uncompressed_check"
                    ),
                    Switch(
                        title = StringResource(R.string.bypass_min_signature_version_check_title),
                        summary = R.string.bypass_min_signature_version_check_summary,
                        key = "bypass_min_signature_version_check"
                    ),
                    Switch(
                        title = StringResource(R.string.bypass_v1_signature_errors_title),
                        summary = R.string.bypass_v1_signature_errors_summary,
                        key = "bypass_v1_signature_errors"
                    ),
                    Switch(
                        title = StringResource(R.string.allow_mismatched_split_apk_signatures_title),
                        summary = R.string.allow_mismatched_split_apk_signatures_summary,
                        key = "allow_mismatched_split_apk_signatures"
                    ),
                    Switch(
                        title = StringResource(R.string.disable_install_verification_title),
                        summary = R.string.disable_install_verification_summary,
                        key = "disable_install_verification"
                    )
                )
            ),
            CardDefinition(
                titleRes = R.string.advanced_settings,
                items = listOf(
                    Switch(
                        title = StringResource(R.string.allow_system_app_hidden_api_title),
                        summary = R.string.allow_system_app_hidden_api_summary,
                        key = "allow_system_app_hidden_api"
                    ),
                    Switch(
                        title = StringResource(R.string.allow_nonsystem_shared_uid_title),
                        summary = R.string.allow_nonsystem_shared_uid_summary,
                        key = "allow_nonsystem_shared_uid"
                    ),
                    Switch(
                        title = StringResource(R.string.pms_command_title),
                        summary = R.string.pms_command_summary,
                        key = "pms_command"
                    )
                )
            )
        )
    )
}
