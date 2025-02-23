package com.owo233.fuckmarketads.hook.apps

import com.owo233.fuckmarketads.hook.hooks.market.RemoveAd
import com.owo233.fuckmarketads.utils.init.AppRegister
import de.robv.android.xposed.callbacks.XC_LoadPackage

object Market : AppRegister() {
    override val packageName: String = "com.xiaomi.market"
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        autoInitHooks(
            lpparam,
            RemoveAd // 移除广告
            )
    }
}