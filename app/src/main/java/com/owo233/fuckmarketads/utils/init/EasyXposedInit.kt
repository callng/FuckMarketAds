package com.owo233.fuckmarketads.utils.init

import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.LogExtensions.logexIfThrow
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage

private const val TAG = "FuckMarketAds"

abstract class EasyXposedInit: IXposedHookLoadPackage, IXposedHookZygoteInit,
    IXposedHookInitPackageResources {

    private lateinit var packageParam: XC_LoadPackage.LoadPackageParam
    private lateinit var packageResourcesParam: XC_InitPackageResources.InitPackageResourcesParam
    abstract val registeredApp: Set<AppRegister>

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {

        packageParam = lpparam!!
        registeredApp.forEach { app ->
            if (app.packageName == lpparam.packageName) {
                EzXHelper.apply {
                    initHandleLoadPackage(lpparam)
                    setLogTag(TAG)
                    setToastTag(TAG)
                }
                runCatching { app.handleLoadPackage(lpparam) }.logexIfThrow("Failed call handleLoadPackage, package: ${app.packageName}")
            }
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
        EzXHelper.initZygote(startupParam!!)
    }

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {
        packageResourcesParam = resparam!!
        registeredApp.forEach { app ->
            if (app.packageName == resparam.packageName) {
                runCatching { app.handleInitPackageResources(resparam) }.logexIfThrow("Failed call handleInitPackageResources, package: ${app.packageName}")
            }
        }
    }
}