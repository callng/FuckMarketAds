package com.owo233.fuckmarketads.hook.hooks.market

import com.owo233.fuckmarketads.utils.init.HookRegister
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHooks
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object RemoveAd : HookRegister() {
    override fun init() {
        val appDetailV3Cls = loadClass("com.xiaomi.market.common.network.retrofit.response.bean.AppDetailV3")
        val detailSplashAdManagerCls = loadClass("com.xiaomi.market.ui.splash.DetailSplashAdManager")
        val splashManagerCls = loadClass("com.xiaomi.market.ui.splash.SplashManager")

        appDetailV3Cls.methodFinder().filter {
            name in setOf(
                "isBrowserMarketAdOff",
                "isBrowserSourceFileAdOff",
                "supportShowCompat64bitAlert",
            )
        }.toList().createHooks {
            returnConstant(true)
        }

        appDetailV3Cls.methodFinder().filter {
            name in setOf(
                "isInternalAd",
                "needShowAds",
                "needShowAdsWithSourceFile",
                "showComment",
                "showRecommend",
                "showTopBanner",
                "showTopVideo",
                "equals",
                "getShowOpenScreenAd",
                "hasGoldLabel",
                "isBottomButtonLayoutType",
                "isPersonalization",
                "isTopButtonLayoutType",
                "isTopSingleTabMultiButtonType",
                "needShowGrayBtn",
                "needShowPISafeModeStyle",
                "supportAutoLoadDeepLink",
                "supportShowCompatAlert",
                "supportShowCompatChildForbidDownloadAlert",
            )
        }.toList().createHooks {
            returnConstant(false)
        }

        detailSplashAdManagerCls.methodFinder().filter {
            name in setOf(
                "canRequestSplashAd",
                "isRequesting",
                "isOpenFromMsa",
            )
        }.toList().createHooks {
            returnConstant(false)
        }

        detailSplashAdManagerCls.methodFinder()
            .filterByName("tryToRequestSplashAd")
            .first().createHook {
                returnConstant(null)
            }

        splashManagerCls.methodFinder().filter {
            name in setOf(
                "canShowSplash",
                "needShowSplash",
                "needRequestFocusVideo",
                "isPassiveSplashAd",
            )
        }.toList().createHooks {
            returnConstant(false)
        }
    }
}