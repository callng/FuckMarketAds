package com.owo233.fuckmarketads.hook.hooks.market

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.owo233.fuckmarketads.utils.init.HookRegister

object SettingsUtils: HookRegister() {
    override fun init() {
        val settingsUtilsCls = loadClass("com.xiaomi.market.util.SettingsUtils")

        settingsUtilsCls.methodFinder().filter { // 禁用商店自动更新
            name == "autoUpdateMarket"
            returnType == Boolean::class.java
        }.first().createHook {
            returnConstant(false)
        }

        settingsUtilsCls.methodFinder().filter { // 禁用静默安装
            name == "shouldSilentInstall"
            returnType == Boolean::class.java
        }.first().createHook {
            returnConstant(false)
        }

        settingsUtilsCls.methodFinder().filter { // 启用调试模式
            name == "debugCheckEnable"
            returnType == Boolean::class.java
        }.first().createHook {
            returnConstant(true)
        }

        settingsUtilsCls.methodFinder().filter { // 启用调试模式
            name == "enableDebugCheck"
            returnType == Boolean::class.java
        }.first().createHook {
            returnConstant(false)
        }

        settingsUtilsCls.methodFinder().filter { // 禁用应用安全检测
            name == "isSupportAppSecurityCheck"
            returnType == Boolean::class.java
        }.first().createHook {
            returnConstant(false)
        }

        settingsUtilsCls.methodFinder().filter { // 跳过WebViewHost检测
            name == "shouldSkipWebViewHostCheck"
            returnType == Boolean::class.java
        }.first().createHook {
            returnConstant(true)
        }

        settingsUtilsCls.methodFinder().filter { // 禁用WiFi自动更新
            name == "shouldAutoUpdateViaWifi"
            returnType == Boolean::class.java
        }.first().createHook {
            returnConstant(false)
        }

        settingsUtilsCls.methodFinder().filter { // 禁用WiFi自动下载
            name == "shouldAutoDownloadViaWifi"
            returnType == Boolean::class.java
        }.first().createHook {
            returnConstant(false)
        }
    }
}