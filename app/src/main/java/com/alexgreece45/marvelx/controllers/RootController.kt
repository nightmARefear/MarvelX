package com.alexgreece45.marvelx.controllers

import com.alexgreece45.marvelx.util.ResourceProvider
import com.scottyab.rootbeer.RootBeer
import javax.inject.Inject

interface RootController {
    fun isRooted(): Boolean
    fun areRootCloakingAppsInstalled(): Boolean
}

class RootControllerImpl @Inject constructor(
    resourceProvider: ResourceProvider
) : RootController {

    private val rootBeer = RootBeer(resourceProvider.getContext())

    override fun isRooted(): Boolean = rootBeer.isRooted

    override fun areRootCloakingAppsInstalled(): Boolean = rootBeer.detectRootCloakingApps(
        arrayOf(
            "com.google.android.launcher.layouts.genymotion",
            "com.bluestacks",
            "com.vphone.launcher",
            "com.bignox.app",
            "com.noshufou.android.su",
            "com.noshufou.android.su.elite",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "com.yellowes.su",
            "com.topjohnwu.magisk",
            "com.kingroot.kinguser",
            "com.kingo.root",
            "com.smedialink.oneclickroot",
            "com.zhiqupk.root.global",
            "com.alephzain.framaroot",
            "com.dimonvideo.luckypatcher",
            "com.chelpus.lackypatch",
            "com.ramdroid.appquarantine",
            "com.ramdroid.appquarantinepro",
            "com.android.vending.billing.InAppBillingService.COIN",
            "com.blackmartalpha",
            "org.blackmart.market",
            "com.android.vending.billing.InAppBillingService.LUCK",
            "com.allinone.free",
            "com.repodroid.app",
            "org.creeplays.hack",
            "com.baseappfull.fwd",
            "com.zmapp",
            "com.dv.marketmod.installer",
            "org.mobilism.android",
            "com.android.wp.net.log",
            "com.android.camera.update",
            "cc.madkite.freedom",
            "com.chelpus.luckypatcher",
            "com.devadvance.rootcloak",
            "com.devadvance.rootcloakplus",
            "de.robv.android.xposed.installer",
            "com.saurik.substrate",
            "com.zachspong.temprootremovejb",
            "com.amphoras.hidemyroot",
            "com.amphoras.hidemyrootadfree",
            "com.formyhm.hiderootPremium",
            "com.formyhm.hideroot",
            "com.solohsu.android.edxp.manager",
            "org.meowcat.edxposed.manager",
            "com.xmodgame",
            "com.cih.game_cih",
            "com.charles.lpoqasert",
            "catch_.me_.if_.you_.can_",
            "com.koushikdutta.rommanager",
            "com.koushikdutta.rommanager.license"
        )
    )
}