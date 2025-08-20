package com.suqi8.oshin.ui.activity.com.android.phone

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.Card
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.activity.components.FunString
import com.suqi8.oshin.ui.activity.components.FunSwich
import com.suqi8.oshin.ui.activity.components.addline
import com.suqi8.oshin.utils.GetAppName

@SuppressLint("SuspiciousIndentation")
@Composable
fun phone(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.android.phone"),
        appList = listOf("com.android.phone"),
        navController = navController
    ) {
        val context = LocalContext.current
        Card {
            val sms_verification_code = remember { mutableStateOf(context.prefs("phone").getBoolean("sms_verification_code", false)) }
            FunSwich(
                title = stringResource(R.string.sms_verification_code),
                category = "phone",
                key = "sms_verification_code",
                onCheckedChange = { sms_verification_code.value = it }
            )
            AnimatedVisibility(sms_verification_code.value) {
                Column {
                    addline()
                    FunString(
                        title = stringResource(R.string.sms_code_keyword),
                        category = "phone",
                        key = "SMSCodeRule",
                        defValue = "验证码|校验码|检验码|确认码|激活码|动态码|安全码|验证代码|校验代码|检验代码|激活代码|确认代码|动态代码|安全代码|登入码|认证码|识别码|短信口令|动态密码|交易码|上网密码|随机码|动态口令|驗證碼|校驗碼|檢驗碼|確認碼|激活碼|動態碼|驗證代碼|校驗代碼|檢驗代碼|確認代碼|激活代碼|動態代碼|登入碼|認證碼|識別碼|Code|code|CODE|Код|код|КОД|Пароль|пароль|ПАРОЛЬ|Kod|kod|KOD|Ma|Mã|OTP"
                    )
                    addline()
                    FunSwich(
                        title = stringResource(R.string.show_verification_toast),
                        category = "phone",
                        key = "showCodeToast"
                    )
                    addline()
                    FunSwich(
                        title = stringResource(R.string.show_verification_notification),
                        category = "phone",
                        key = "showCodeNotification"
                    )
                    addline()
                    FunSwich(
                        title = stringResource(R.string.copy_verification_to_clipboard),
                        category = "phone",
                        key = "copyCode"
                    )
                }
            }
        }
    }
}
