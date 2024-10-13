package io.github.suqi8.opatch.ui.miuix

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendModeColorFilter
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Checkbox
import top.yukonga.miuix.kmp.basic.InputField
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SearchBar
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Switch
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.CheckboxLocation
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperCheckbox
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.Search
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog

@Composable
fun MainPage() {
    var miuixSearchValue by remember { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    val showDialog2 = remember { mutableStateOf(false) }
    var checkbox by remember { mutableStateOf(false) }
    var checkboxTrue by remember { mutableStateOf(true) }
    var switch by remember { mutableStateOf(false) }
    var switchTrue by remember { mutableStateOf(true) }
    val dropdownOptions = listOf("Option 1", "Option 2", "Option 3", "Option 4")
    val dropdownSelectedOption = remember { mutableIntStateOf(0) }
    val dropdownSelectedOptionRight = remember { mutableIntStateOf(1) }
    var miuixSuperCheckbox by remember { mutableStateOf("State: false") }
    var miuixSuperCheckboxState by remember { mutableStateOf(false) }
    var miuixSuperRightCheckbox by remember { mutableStateOf("false") }
    var miuixSuperRightCheckboxState by remember { mutableStateOf(false) }
    var miuixSuperSwitch by remember { mutableStateOf("false") }
    var miuixSuperSwitchState by remember { mutableStateOf(false) }
    var miuixSuperSwitchAnimState by remember { mutableStateOf(false) }
    var buttonText by remember { mutableStateOf("Cancel") }
    var submitButtonText by remember { mutableStateOf("Submit") }
    var clickCount by remember { mutableIntStateOf(0) }
    var submitClickCount by remember { mutableIntStateOf(0) }
    val focusManager = LocalFocusManager.current
    var text1 by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf(TextFieldValue("")) }
    var progress by remember { mutableFloatStateOf(0.5f) }
    val progressDisable by remember { mutableFloatStateOf(0.5f) }

    Scaffold {
        LazyColumn {
            item {
                SearchBar(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    inputField = {
                        InputField(
                            query = miuixSearchValue,
                            onQueryChange = { miuixSearchValue = it },
                            onSearch = { expanded = false },
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                            label = "Search",
                            leadingIcon = {
                                Image(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    imageVector = MiuixIcons.Search,
                                    colorFilter = BlendModeColorFilter(
                                        MiuixTheme.colorScheme.onSurfaceContainer,
                                        BlendMode.SrcIn
                                    ),
                                    contentDescription = "Search"
                                )
                            },
                        )
                    },
                    outsideRightAction = {
                        Text(
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .clickable(
                                    interactionSource = null,
                                    indication = null
                                ) {
                                    expanded = false
                                    miuixSearchValue = ""
                                },
                            text = "Cancel",
                            color = MiuixTheme.colorScheme.primary
                        )
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    Column(
                        Modifier.fillMaxSize()
                    ) {
                        repeat(4) { idx ->
                            val resultText = "Suggestion $idx"
                            BasicComponent(
                                title = resultText,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                onClick = {
                                    miuixSearchValue = resultText
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            if (!expanded) {
                item(
                    key = "text"
                ) {
                    SmallTitle(text = "Basic")
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp)
                    ) {
                        BasicComponent(
                            title = "Title",
                            summary = "Summary",
                            leftAction = {
                                Text(text = "Left")
                            },
                            rightActions = {
                                Text(text = "Right1")
                                Spacer(Modifier.width(6.dp))
                                Text(text = "Right2")
                            },
                            onClick = {},
                            enabled = true
                        )
                        BasicComponent(
                            title = "Title",
                            summary = "Summary",
                            leftAction = {
                                Text(
                                    text = "Left",
                                    color = MiuixTheme.colorScheme.disabledOnSecondaryVariant
                                )
                            },
                            rightActions = {
                                Text(
                                    text = "Right1",
                                    color = MiuixTheme.colorScheme.disabledOnSecondaryVariant
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Right2",
                                    color = MiuixTheme.colorScheme.disabledOnSecondaryVariant
                                )
                            },
                            enabled = false
                        )
                    }

                    SmallTitle(text = "Arrow & Dialog")
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp)
                    ) {
                        SuperArrow(
                            leftAction = {
                                Box(
                                    contentAlignment = Alignment.TopStart,
                                ) {
                                    Image(
                                        colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onBackground),
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Person",
                                    )
                                }
                            },
                            title = "Arrow",
                            summary = "Click to show Dialog 1",
                            onClick = {
                                showDialog.value = true
                            }
                        )

                        SuperArrow(
                            title = "Arrow",
                            summary = "Click to show Dialog 2",
                            onClick = {
                                showDialog2.value = true
                            }
                        )

                        SuperArrow(
                            title = "Disabled Arrow",
                            onClick = {},
                            enabled = false
                        )
                    }

                    SmallTitle(text = "Checkbox")
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Checkbox(
                                modifier = Modifier,
                                checked = checkbox,
                                onCheckedChange = { checkbox = it }

                            )
                            Checkbox(
                                modifier = Modifier.padding(start = 8.dp),
                                checked = checkboxTrue,
                                onCheckedChange = { checkboxTrue = it }
                            )
                            Checkbox(
                                modifier = Modifier.padding(start = 8.dp),
                                enabled = false,
                                checked = false,
                                onCheckedChange = { }

                            )
                            Checkbox(
                                modifier = Modifier.padding(start = 8.dp),
                                enabled = false,
                                checked = true,
                                onCheckedChange = { }
                            )
                        }

                        SuperCheckbox(
                            checkboxLocation = CheckboxLocation.Right,
                            title = "Checkbox",
                            checked = miuixSuperRightCheckboxState,
                            rightActions = {
                                Text(
                                    modifier = Modifier.padding(end = 6.dp),
                                    text = miuixSuperRightCheckbox,
                                    color = MiuixTheme.colorScheme.onSurfaceVariantActions
                                )
                            },
                            onCheckedChange = {
                                miuixSuperRightCheckboxState = it
                                miuixSuperRightCheckbox = "$it"
                            },
                        )

                        SuperCheckbox(
                            title = "Checkbox",
                            summary = miuixSuperCheckbox,
                            checked = miuixSuperCheckboxState,
                            onCheckedChange = {
                                miuixSuperCheckboxState = it
                                miuixSuperCheckbox = "State: $it"
                            },
                        )

                        SuperCheckbox(
                            title = "Disabled Checkbox",
                            checked = true,
                            enabled = false,
                            onCheckedChange = {},
                        )
                    }

                    SmallTitle(text = "Switch")
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Switch(
                                checked = switch,
                                onCheckedChange = { switch = it }
                            )
                            Switch(
                                modifier = Modifier.padding(start = 6.dp),
                                checked = switchTrue,
                                onCheckedChange = { switchTrue = it }
                            )
                            Switch(
                                modifier = Modifier.padding(start = 6.dp),
                                enabled = false,
                                checked = false,
                                onCheckedChange = { }
                            )
                            Switch(
                                modifier = Modifier.padding(start = 6.dp),
                                enabled = false,
                                checked = true,
                                onCheckedChange = { }
                            )
                        }

                        SuperSwitch(
                            title = "Switch",
                            summary = "Click to expand a Switch",
                            checked = miuixSuperSwitchAnimState,
                            onCheckedChange = {
                                miuixSuperSwitchAnimState = it
                            },
                        )

                        AnimatedVisibility(
                            visible = miuixSuperSwitchAnimState,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            SuperSwitch(
                                title = "Switch",
                                checked = miuixSuperSwitchState,
                                rightActions = {
                                    Text(
                                        modifier = Modifier.padding(end = 6.dp),
                                        text = miuixSuperSwitch,
                                        color = MiuixTheme.colorScheme.onSurfaceVariantActions
                                    )
                                },
                                onCheckedChange = {
                                    miuixSuperSwitchState = it
                                    miuixSuperSwitch = "$it"
                                },
                            )
                        }

                        SuperSwitch(
                            title = "Disabled Switch",
                            checked = true,
                            enabled = false,
                            onCheckedChange = {},
                        )
                    }

                    SmallTitle(text = "Dropdown")

                    Card(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp)
                    ) {
                        SuperDropdown(
                            title = "Dropdown",
                            summary = "Popup near click",
                            items = dropdownOptions,
                            selectedIndex = dropdownSelectedOption.intValue,
                            onSelectedIndexChange = { newOption -> dropdownSelectedOption.intValue = newOption },
                        )

                        SuperDropdown(
                            title = "Dropdown",
                            summary = "Popup always on right",
                            alwaysRight = true,
                            items = dropdownOptions,
                            selectedIndex = dropdownSelectedOptionRight.intValue,
                            onSelectedIndexChange = { newOption -> dropdownSelectedOptionRight.intValue = newOption },
                        )

                        SuperDropdown(
                            title = "Disabled Dropdown",
                            items = listOf("Option 3"),
                            selectedIndex = 0,
                            onSelectedIndexChange = {},
                            enabled = false
                        )
                    }

                }
                item(
                    key = "other"
                ) {
                    SmallTitle(text = "Button")
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            modifier = Modifier.weight(1f),
                            text = buttonText,
                            onClick = {
                                clickCount++
                                buttonText = "Click: $clickCount"
                            }
                        )
                        Spacer(Modifier.width(12.dp))
                        Button(
                            modifier = Modifier.weight(1f),
                            text = submitButtonText,
                            submit = true,
                            onClick = {
                                submitClickCount++
                                submitButtonText = "Click: $submitClickCount"
                            }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            modifier = Modifier.weight(1f),
                            text = "Disabled",
                            submit = false,
                            enabled = false,
                            onClick = {}
                        )
                        Spacer(Modifier.width(12.dp))
                        Button(
                            modifier = Modifier.weight(1f),
                            text = "Disabled",
                            submit = true,
                            enabled = false,
                            onClick = {}
                        )
                    }

                    SmallTitle(text = "TextField")
                    TextField(
                        value = text1,
                        onValueChange = { text1 = it },
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )

                    TextField(
                        value = text2,
                        onValueChange = { text2 = it },
                        backgroundColor = MiuixTheme.colorScheme.secondaryContainer,
                        label = "Text Field",
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )

                    SmallTitle(text = "Slider")
                    Slider(
                        progress = progress,
                        onProgressChange = { newProgress -> progress = newProgress },
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp),
                    )

                    Slider(
                        progress = progressDisable,
                        onProgressChange = {},
                        enabled = false,
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp),
                    )

                    SmallTitle(text = "Card")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp),
                        color = MiuixTheme.colorScheme.primaryVariant,
                        insideMargin = DpSize(16.dp, 16.dp)
                    ) {
                        Text(
                            color = MiuixTheme.colorScheme.onPrimary,
                            text = "Card",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp),
                        insideMargin = DpSize(16.dp, 16.dp)
                    ) {
                        Text(
                            color = MiuixTheme.colorScheme.onSurface,
                            text = "Card\nCardCard\nCardCardCard",
                            style = MiuixTheme.textStyles.paragraph
                        )
                    }
                }
            }
        }
    }

    Dialog(showDialog)
    Dialog2(showDialog2)
}

@Composable
fun Dialog(showDialog: MutableState<Boolean>) {
    if (!showDialog.value) return
    val value = remember { mutableStateOf("") }
    SuperDialog(
        title = "Dialog 1",
        summary = "Summary",
        show = showDialog,
        onDismissRequest = {
            dismissDialog(showDialog)
        }
    ) {
        TextField(
            modifier = Modifier.padding(bottom = 16.dp),
            value = value.value,
            maxLines = 1,
            onValueChange = { value.value = it }
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                modifier = Modifier.weight(1f),
                text = "Cancel",
                onClick = {
                    dismissDialog(showDialog)
                }
            )
            Spacer(Modifier.width(20.dp))
            Button(
                modifier = Modifier.weight(1f),
                text = "Confirm",
                submit = true,
                onClick = {
                    dismissDialog(showDialog)
                }
            )
        }
    }
}

@Composable
fun Dialog2(showDialog: MutableState<Boolean>) {
    if (!showDialog.value) return
    val dropdownOptions = listOf("Option 1", "Option 2")
    val dropdownSelectedOption = remember { mutableIntStateOf(0) }
    var miuixSuperSwitchState by remember { mutableStateOf(false) }
    SuperDialog(
        title = "Dialog 2",
        backgroundColor = MiuixTheme.colorScheme.background,
        show = showDialog,
        onDismissRequest = {
            dismissDialog(showDialog)
        }
    ) {
        Card {
            SuperDropdown(
                title = "Dropdown",
                items = dropdownOptions,
                selectedIndex = dropdownSelectedOption.intValue,
                onSelectedIndexChange = { newOption -> dropdownSelectedOption.intValue = newOption },
                defaultWindowInsetsPadding = false
            )
            SuperSwitch(
                title = "Switch",
                checked = miuixSuperSwitchState,
                onCheckedChange = {
                    miuixSuperSwitchState = it
                }
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                modifier = Modifier.weight(1f),
                text = "Cancel",
                onClick = {
                    dismissDialog(showDialog)
                }
            )
            Spacer(Modifier.width(20.dp))
            Button(
                modifier = Modifier.weight(1f),
                text = "Confirm",
                submit = true,
                onClick = {
                    dismissDialog(showDialog)
                }
            )
        }
    }
}
