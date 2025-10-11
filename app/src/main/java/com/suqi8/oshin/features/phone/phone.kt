package com.suqi8.oshin.features.phone

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.SimpleCondition
import com.suqi8.oshin.models.StringInput
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object phone {
    val definition = PageDefinition(
        category = "phone",
        appList = listOf("com.android.phone"),
        title = AppName("com.android.phone"),
        // 2. 定义页面的卡片列表
        items = listOf(
            // --- 第一个 Card ---
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.sms_verification_code),
                        key = "sms_verification_code"
                    )
                )
            ),
            CardDefinition(
                items = listOf(
                    StringInput(
                        title = StringResource(R.string.sms_code_keyword),
                        key = "SMSCodeRule",
                        defaultValue = "验证码|校验码|检验码|确认码|激活码|动态码|安全码|验证代码|校验代码|检验代码|激活代码|确认代码|动态代码|安全代码|登入码|认证码|识别码|短信口令|动态密码|交易码|上网密码|随机码|动态口令|驗證碼|校驗碼|檢驗碼|確認碼|激活碼|動態碼|驗證代碼|校驗代碼|檢驗代碼|確認代碼|激活代碼|動態代碼|登入碼|認證碼|識別碼|Code|code|CODE|Код|код|КОД|Пароль|пароль|ПАРОЛЬ|Kod|kod|KOD|Ma|Mã|OTP"
                    ),
                    Switch(
                        title = StringResource(R.string.show_verification_toast),
                        key = "showCodeToast"
                    ),
                    Switch(
                        title = StringResource(R.string.show_verification_notification),
                        key = "showCodeNotification"
                    ),
                    Switch(
                        title = StringResource(R.string.copy_verification_to_clipboard),
                        key = "copyCode"
                    ),
                    Switch(
                        title = StringResource(R.string.auto_input_verification_code),
                        key = "inputCode",
                        defaultValue = true
                    )
                ),
                condition = SimpleCondition(
                    dependencyKey = "sms_verification_code"
                )
            )
        )
    )
}
