package com.owo233.fuckmarketads.hook.hooks.market

import com.owo233.fuckmarketads.utils.init.HookRegister
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHooks
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import java.lang.reflect.Field

object RemoveAd : HookRegister() {

    private val listAppComponentClass by lazy {
        loadClass("com.xiaomi.market.common.component.componentbeans.ListAppComponent")
    }

    private val searchHistoryComponentClass by lazy {
        loadClass("com.xiaomi.market.common.component.componentbeans.SearchHistoryComponent")
    }

    private val pageCollapseStateExpand by lazy {
        loadClass("com.xiaomi.market.ui.UpdateListRvAdapter\$PageCollapseState")
            .getDeclaredField("Expand").apply {
                isAccessible = true
            }.get(null)
    }

    private fun getDetailType(fieldName: String): Any? {
        return loadClass("com.xiaomi.market.business_ui.detail.DetailType")
            .getDeclaredField(fieldName).apply {
                isAccessible = true
            }.get(null)
    }

    private val detailTypeV3 by lazy { getDetailType("V3") }
    private val detailTypeV4 by lazy { getDetailType("V4") }

    private fun getDeclaredFieldRecursive(
        clazz: Class<*>,
        fieldName: String,
        isOnlySuperClass: Boolean = false
    ): Field? {
        var current = if (isOnlySuperClass) clazz.superclass else clazz
        while (current != null && current != Any::class.java) {
            try {
                return current.getDeclaredField(fieldName).apply {
                    isAccessible = true
                }
            } catch (_: NoSuchFieldException) {
                current = current.superclass
            }
        }
        return null
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
            .methodFinder().filter {
                name in setOf(
                    "tryShowRecommend",
                    "tryShowRecallReCommend",
                    "trySplash",
                    "fetchSearchHotList"
                )
            }.toList().createHooks {
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
                        thisObj.javaClass.getDeclaredField("forceExpanded").apply {
                            isAccessible = true
                        }.set(thisObj, true)
                        thisObj.javaClass.getDeclaredField("foldButtonVisible").apply {
                            isAccessible = true
                        }.set(thisObj, false)
                        thisObj.javaClass.getDeclaredField("pageCollapseState").apply {
                            isAccessible = true
                        }.set(thisObj, pageCollapseStateExpand)
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

        // 应用详情页面
        loadClass("com.xiaomi.market.ui.detail.BaseDetailActivity")
            .methodFinder()
            .filterByName("initParams")
            .first().createHook {
                after {
                    val thisObject = it.thisObject
                    val field = getDeclaredFieldRecursive(thisObject.javaClass, "detailType")
                        ?: return@after

                    val currentValue = field.get(thisObject)
                    if (currentValue == detailTypeV3) {
                        field.set(thisObject, detailTypeV4)
                    }
                }
            }
    }
}
