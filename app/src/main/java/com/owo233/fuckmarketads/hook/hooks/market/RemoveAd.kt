package com.owo233.fuckmarketads.hook.hooks.market

import com.owo233.fuckmarketads.utils.init.HookRegister
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHooks
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import java.lang.reflect.Modifier

object RemoveAd : HookRegister() {

    private val listAppComponentClass by lazy {
        loadClass("com.xiaomi.market.common.component.componentbeans.ListAppComponent")
    }

    private val searchHistoryComponentClass by lazy {
        loadClass("com.xiaomi.market.common.component.componentbeans.SearchHistoryComponent")
    }

    private val pageCollapseStateExpand by lazy {
        loadClass("com.xiaomi.market.ui.UpdateListRvAdapter\$PageCollapseState")
            .methodFinder()
            .filterByName("valueOf")
            .filterByParamTypes(String::class.java)
            .filterByModifiers(Modifier.STATIC)
            .first().invoke(null, "Expand")
    }

    override fun init() {
        loadClass("com.xiaomi.market.common.network.retrofit.response.bean.AppDetailV3")
            .methodFinder().filter {
                name in setOf(
                    "isBrowserMarketAdOff",
                    "isBrowserSourceFileAdOff",
                    "supportShowCompat64bitAlert",
                )
            }.toList().createHooks {
                returnConstant(true)
            }

        loadClass("com.xiaomi.market.common.network.retrofit.response.bean.AppDetailV3")
            .methodFinder().filter {
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

        loadClass("com.xiaomi.market.ui.splash.DetailSplashAdManager")
            .methodFinder().filter {
                name in setOf(
                    "canRequestSplashAd",
                    "isRequesting",
                    "isOpenFromMsa",
                )
            }.toList().createHooks {
                returnConstant(false)
            }

        loadClass("com.xiaomi.market.ui.splash.DetailSplashAdManager")
            .methodFinder()
            .filterByName("tryToRequestSplashAd")
            .first().createHook {
                returnConstant(null)
            }

        loadClass("com.xiaomi.market.ui.splash.SplashManager")
            .methodFinder().filter {
                name in setOf(
                    "canShowSplash",
                    "needShowSplash",
                    "needRequestFocusVideo",
                    "isPassiveSplashAd",
                )
            }.toList().createHooks {
                returnConstant(false)
            }

        loadClass("com.xiaomi.market.ui.splash.SplashManager")
            .methodFinder().filter {
                name in setOf(
                    "tryAdSplash",
                    "trySplashWhenApplicationForeground"
                )
            }.toList().createHooks {
                returnConstant(null)
            }

        loadClass("com.xiaomi.market.business_ui.main.MarketTabActivity")
            .methodFinder()
            .filterByName("trySplash")
            .first().createHook {
                returnConstant(null)
            }

        // 搜索建议页面
        loadClass("com.xiaomi.market.business_ui.search.NativeSearchSugFragment")
            .methodFinder()
            .filterByName("getRequestParams")
            .first().createHook {
                after {
                    @Suppress("UNCHECKED_CAST")
                    val baseParametersForH5ToNative = (it.result as? Map<String, Any?>)?.toMutableMap() ?: return@after
                    baseParametersForH5ToNative.put("adFlag", 0)
                    it.result = baseParametersForH5ToNative
                }
            }

        // 搜索结果页面
        loadClass("com.xiaomi.market.business_ui.search.NativeSearchResultFragment")
            .methodFinder()
            .filterByName("parseResponseData")
            .first().createHook {
                after {
                    @Suppress("UNCHECKED_CAST")
                    val parsedComponents = (it.result as? List<Any>)?.toMutableList() ?: return@after
                    parsedComponents.retainAll { component ->
                        listAppComponentClass.isInstance(component)
                    }
                    it.result = parsedComponents
                }
            }

        // 搜索页面
        loadClass("com.xiaomi.market.business_ui.search.NativeSearchGuideFragment")
            .methodFinder()
            .filterByName("parseResponseData")
            .first().createHook {
                after {
                    @Suppress("UNCHECKED_CAST")
                    val parsedComponents = (it.result as? List<Any>)?.toMutableList() ?: return@after
                    parsedComponents.retainAll { component ->
                        searchHistoryComponentClass.isInstance(component)
                    }
                    it.result = parsedComponents
                }
            }

        // 搜索页面
        loadClass("com.xiaomi.market.business_ui.search.NativeSearchGuideFragment")
            .methodFinder()
            .filterByName("isLoadMoreEndGone")
            .first().createHook {
                returnConstant(true)
            }

        // 更新页面
        loadClass("com.xiaomi.market.ui.UpdateListRvAdapter")
            .constructorFinder()
            .forEach { constructor->
                constructor.createHook {
                    after {
                        val thisObj = it.thisObject
                        thisObj.javaClass.getDeclaredField("forceExpanded").set(thisObj, true)
                        thisObj.javaClass.getDeclaredField("foldButtonVisible").set(thisObj, false)
                        thisObj.javaClass.getDeclaredField("pageCollapseState").set(thisObj, pageCollapseStateExpand)
                    }
                }
            }

        // 更新页面
        loadClass("com.xiaomi.market.ui.UpdateListRvAdapter")
            .methodFinder()
            .filterByName("generateRecommendGroupItems")
            .first().createHook {
                returnConstant(null)
            }

        // 下载页面
        loadClass("com.xiaomi.market.ui.DownloadListFragment")
            .methodFinder()
            .filterByName("parseRecommendGroupResult")
            .first().createHook {
                returnConstant(null)
            }
    }
}
