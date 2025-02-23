package com.owo233.fuckmarketads.hook

import com.owo233.fuckmarketads.hook.apps.Market
import com.owo233.fuckmarketads.utils.init.AppRegister
import com.owo233.fuckmarketads.utils.init.EasyXposedInit
import de.robv.android.xposed.IXposedHookZygoteInit

class MainHook : EasyXposedInit() {

    override val registeredApp: Set<AppRegister> = setOf(
        Market // 应用商店
    )

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
        super.initZygote(startupParam)
    }
}