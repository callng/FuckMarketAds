package com.owo233.fuckmarketads.yife

import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import org.luckypray.dexkit.DexKitBridge

object DexKit {
    private lateinit var hostDir: String
    private var isInitialized = false
    val dexKitBridge: DexKitBridge by lazy {
        System.loadLibrary("dexkit")
        DexKitBridge.create(hostDir).also {
            isInitialized = true
        }
    }

    fun initDexKit(loadPackageParam: LoadPackageParam) {
        hostDir = loadPackageParam.appInfo.sourceDir
    }

    fun closeDexKit() {
        if (isInitialized) dexKitBridge.close()
    }
}