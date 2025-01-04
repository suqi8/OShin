package io.github.suqi8.opatch.hook.android.corepatch;

import de.robv.android.xposed.XposedHelpers;

public class CorePatchForV extends CorePatchForU {
    @Override
    Class<?> getParsedPackage(ClassLoader classLoader) {
        return XposedHelpers.findClassIfExists("com.android.internal.pm.parsing.pkg.ParsedPackage", classLoader);
    }
}
