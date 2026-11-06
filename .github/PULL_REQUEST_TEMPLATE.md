<!--
感谢贡献！中英文都可以，不适用的项直接删掉。
Thanks for contributing! Chinese or English is fine. Delete anything that doesn't apply.
-->

## 改了什么 / What changed

<!--
做了什么、为什么。修 Hook 时写清宿主哪个版本改了什么、根因是什么。
What and why. For a hook fix, say which host version changed what, and the root cause.
-->


## 关联 Issue / Related issues

<!--
例如 Closes #123。修崩溃或失效时附上错误指纹 OSH-XXXXXXXXXXXX，方便合并同指纹的 Issue。
e.g. Closes #123. For crash or regression fixes, add the error fingerprint OSH-XXXXXXXXXXXX so issues with the same fingerprint can be closed together.
-->


## 类型 / Type

- [ ] 🐛 修复：崩溃 / 更新后失效 / 没生效 · Fix: crash / regression / not working
- [ ] ✨ 新功能或移植 · New or ported feature
- [ ] ⚡️ 性能或资源占用 · Performance or resource usage
- [ ] ♻️ 重构 · Refactor
- [ ] 🌐 翻译 · Translation
- [ ] 📝 文档或其他 · Docs or other

## 测试环境 / Tested on

<!-- 改 Hook 时必填 / Required for hook changes -->

- 设备与系统 / Device & ROM: <!-- 例如 e.g. OnePlus 13 · ColorOS 16.0.5.1002 -->
- 目标应用与版本 / Target app & version: <!-- 例如 e.g. com.android.systemui 16.99.12 -->
- 「模块日志」里没有新增 OShin 错误或警告 / No new OShin errors or warnings in Module logs: <!-- 是 / yes -->

## 自检 / Checklist

- [ ] 本地构建通过（`./gradlew assembleDebug`）· Builds locally
- [ ] 已在真机上验证 · Verified on a real device
- [ ] 新功能的页面 `category`、prefs 名、`key`、类型与默认值和 Hook 读取一致，目标包已在 `FeatureRegistry` 和 `OShinHookRegistry` 注册 · New features: `category`, prefs name, `key`, type and default match the Hook side; target packages are registered in `FeatureRegistry` and `OShinHookRegistry`
- [ ] Hook 落在 `onDraw`、`onLayout`、`onMeasure` 等高频回调时，不重复创建 Bitmap、blur、线程等资源 · No repeated creation of Bitmaps, blurs, threads, etc. in high-frequency callbacks such as `onDraw`, `onLayout`, `onMeasure`
- [ ] 新文案只加在默认的 `values/strings.xml` · New strings only in the default `values/strings.xml`
- [ ] 没有改动 README「开源说明」要求保留的内容（关于页的感谢、作者信息与题外话，友盟 SDK，中国地区启动验证）· Didn't alter what the README's Open Source Disclaimer asks to keep (thanks, author info and side note on the About page, the Umeng SDK, the China-region launch verification)

## 截图或录屏（选填）/ Screenshots (optional)

<!-- 改界面时强烈建议附上 / Highly recommended for UI changes -->
