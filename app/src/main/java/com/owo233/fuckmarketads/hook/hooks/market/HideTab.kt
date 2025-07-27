package com.owo233.fuckmarketads.hook.hooks.market

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.FieldFinder.`-Static`.fieldFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.owo233.fuckmarketads.utils.init.HookRegister

object HideTab : HookRegister() {
    override fun init() {
        val tabInfoCls = loadClass("com.xiaomi.market.model.TabInfo")

        val tabTagField = tabInfoCls.fieldFinder()
            .filterByName("tag")
            .filterByType(String::class.java)
            .firstOrNull()

        tabInfoCls.methodFinder()
            .filterByName("fromJSON")
            .filterByParamCount(1)
            .first().createHook {
                after { param ->
                    val list = (param.result as List<*>).toMutableList()
                    list.removeAll {
                        val tag = tabTagField?.get(it)?.toString() ?: return@removeAll true
                        /**
                         * native_market_mine // 我的
                         * native_market_home // 主页
                         * native_market_video // 视频号，在4.99.0或之前的某个版本开始没了
                         * native_market_agent // shit 智能体
                         * native_app_assemble // shit 应用号
                         * native_market_game // 游戏
                         * native_market_rank // 榜单
                         */
                        when {
                            tag.startsWith("native_app_assemble") -> true
                            tag.startsWith("native_market_game") -> true
                            tag.startsWith("native_market_rank") -> true
                            tag.startsWith("native_market_video") -> true
                            tag.startsWith("native_market_agent") -> true
                            else -> false
                        }
                    }
                    param.setResult(list)
                }
            }
    }
}
