package com.owo233.fuckmarketads.hook.hooks.market

import android.view.View
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.owo233.fuckmarketads.utils.init.HookRegister

object HideAppSecurity : HookRegister() {
    override fun init() {
        val securityViewCls = loadClass("com.xiaomi.market.business_ui.main.mine.app_security.MineAppSecurityView")
        val settingUtilsCls = loadClass("com.xiaomi.market.util.SettingsUtils")
        val managerCls = loadClass("com.xiaomi.market.common.analytics.onetrack.ExperimentManager\$Companion")

        securityViewCls.constructorFinder()
            .forEach { constructor ->
                constructor.createHook {
                    after {
                        val instance = it.thisObject as? View ?: return@after
                        instance.visibility = View.GONE
                    }
                }
            }

        securityViewCls.methodFinder()
            .filterByName("checkShown")
            .first().createHook {
                returnConstant(false)
            }

        securityViewCls.methodFinder()
            .filterByName("checkSettingSwitch")
            .first().createHook {
                returnConstant(false)
            }

        settingUtilsCls.methodFinder()
            .filterByName("isSupportAppSecurityCheck")
            .first().createHook {
                returnConstant(false)
            }

        managerCls.methodFinder()
            .filterByName("isMineAppSecurityCheckOpen")
            .first().createHook {
                returnConstant(false)
            }
    }
}