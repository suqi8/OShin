package com.suqi8.oshin.hook.android.corepatch;

import java.lang.reflect.InvocationTargetException;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class CorePatchForU extends CorePatchForT {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        super.handleLoadPackage(loadPackageParam);
        var utilClass = findClass("com.android.server.pm.ReconcilePackageUtils", loadPackageParam.classLoader);
        if (utilClass != null) {
            try {
                deoptimizeMethod(utilClass, "reconcilePackages");
            } catch (Throwable ignored) {
            }
        }
        // ee11a9c (Rename AndroidPackageApi to AndroidPackage)
        findAndHookMethod("com.android.server.pm.PackageManagerServiceUtils", loadPackageParam.classLoader,
                "checkDowngrade",
                "com.android.server.pm.pkg.AndroidPackage",
                "android.content.pm.PackageInfoLite",
                new ReturnConstant(prefs, "downgrade", null));
        findAndHookMethod("com.android.server.pm.ScanPackageUtils", loadPackageParam.classLoader,
                "assertMinSignatureSchemeIsValid",
                "com.android.server.pm.pkg.AndroidPackage", int.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        if (prefs.getBoolean("authcreak", false)) {
                            param.setResult(null);
                        }
                    }
                });

        var ntService = XposedHelpers.findClassIfExists("com.nothing.server.ex.NtConfigListServiceImpl",
                loadPackageParam.classLoader);
        if (ntService != null) {
            findAndHookMethod(ntService, "isInstallingAppForbidden", String.class,
                    new ReturnConstant(prefs, "bypassBlock", false));

            findAndHookMethod(ntService, "isStartingAppForbidden", String.class,
                    new ReturnConstant(prefs, "bypassBlock", false));
        }
    }

    @Override
    Class<?> getIsVerificationEnabledClass(ClassLoader classLoader) {
        return XposedHelpers.findClass("com.android.server.pm.VerifyingSession", classLoader);
    }
}
