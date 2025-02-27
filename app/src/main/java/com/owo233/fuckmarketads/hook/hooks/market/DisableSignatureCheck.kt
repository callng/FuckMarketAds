package com.owo233.fuckmarketads.hook.hooks.market

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.owo233.fuckmarketads.utils.init.HookRegister

object DisableSignatureCheck: HookRegister() {
    override fun init() {
        val appInfoCls = loadClass("com.xiaomi.market.model.AppInfo")
        val localAppInfoCls = loadClass("com.xiaomi.market.model.LocalAppInfo")

        localAppInfoCls.methodFinder()
            .filterByName("isInstalledByMarket") // 别问,问就是自家商店安装的APP
            .filterByParamCount(0)
            .filterByReturnType(Boolean::class.java)
            .first().createHook {
                returnConstant(true)
            }

        appInfoCls.methodFinder()
            .filterByName("maybeStolenApp") // 禁用被标记为盗版APP,让被修改签名的APP重新显示
            .filterByParamCount(0)
            .filterByReturnType(Boolean::class.java)
            .first().createHook {
                returnConstant(false)
            }

        appInfoCls.methodFinder()
            .filterByName("isSignatureInconsistent") // 禁用签名不一致检查
            .filterByParamCount(0)
            .filterByReturnType(Boolean::class.java)
            .first().createHook {
                returnConstant(false)
            }

        appInfoCls.methodFinder()
            .filterByName("isInconsistentUpdate") // 禁用更新不一致检查
            .filterByParamCount(0)
            .filterByReturnType(Boolean::class.java)
            .first().createHook {
                returnConstant(false)
            }

        appInfoCls.methodFinder()
            .filterByName("isDownloadDisable") // 允许下载被禁用的APP
            .filterByParamCount(0)
            .filterByReturnType(Boolean::class.java)
            .first().createHook {
                returnConstant(false)
            }

        appInfoCls.methodFinder()
            .filterByName("shouldHideAutoUpdate") // 显示被隐藏的更新APP
            .filterByParamCount(0)
            .filterByReturnType(Boolean::class.java)
            .first().createHook {
                returnConstant(false)
            }
    }
}