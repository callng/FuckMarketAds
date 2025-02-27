package com.owo233.fuckmarketads.hook.apps

import com.owo233.fuckmarketads.hook.hooks.market.DisableSignatureCheck
import com.owo233.fuckmarketads.hook.hooks.market.HideAppSecurity
import com.owo233.fuckmarketads.hook.hooks.market.HideTab
import com.owo233.fuckmarketads.hook.hooks.market.RemoveAd
import com.owo233.fuckmarketads.hook.hooks.market.SettingsUtils
import com.owo233.fuckmarketads.utils.init.AppRegister
import de.robv.android.xposed.callbacks.XC_LoadPackage

object Market : AppRegister() {
    override val packageName: String = "com.xiaomi.market"
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        autoInitHooks(
            lpparam,
            RemoveAd, // 移除广告
            HideTab, // 隐藏tab
            HideAppSecurity, // 隐藏应用安全检测
            DisableSignatureCheck, // 禁用本地APP签名校验
            SettingsUtils // 更改默认设置
            )
    }
}