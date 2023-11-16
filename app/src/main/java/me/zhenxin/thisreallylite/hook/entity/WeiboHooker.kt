package me.zhenxin.thisreallylite.hook.entity

import android.view.View
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.loggerD
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.ViewClass
import com.highcapable.yukihookapi.hook.type.java.BooleanClass
import com.highcapable.yukihookapi.hook.type.java.IntClass
import com.highcapable.yukihookapi.hook.type.java.MapClass
import com.highcapable.yukihookapi.hook.type.java.ObjectsClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import de.robv.android.xposed.XposedHelpers


/**
 *
 *
 * @author 真心
 * @since 2022/11/25 13:40
 * @email qgzhenxin@qq.com
 */
object WeiboHooker : YukiBaseHooker() {
    // 轻享版/极速版 基础包名一致
    private const val pkgName = "com.weico.international"

    override fun onHook() {
        // 去除开屏广告
        "$pkgName.activity.LogoActivity".hook {
            injectMember {
                method {
                    name = "doWhatNext"
                    emptyParam()
                }
                afterHook {
                    val type = result<String>()
                    if (type == "AD") {
                        result = "main"
                    }
                }
            }
            injectMember {
                method {
                    name = "triggerPermission"
                    param(BooleanClass)
                }
                replaceAny {
                    return@replaceAny true
                }
            }
        }

        // 去除信息流广告
        "$pkgName.api.RxApiKt".hook {
            injectMember {
                method {
                    name = "queryUveAdRequest\$lambda\$156"
                    param(MapClass)
                }
                beforeHook {
                    result = ""
                }
            }
            injectMember {
                method {
                    name = "queryUveAdRequest\$lambda\$157"
                    param(findClass("kotlin.jvm.functions.Function1"))
                    param(ObjectsClass)
                }
                beforeHook {
                    result = ""
                }
            }
            injectMember {
                method {
                    name = "queryUveAdRequest\$lambda\$158"
                    param(findClass("kotlin.jvm.functions.Function1"))
                    param(ObjectsClass)
                }
                beforeHook {
                    result = ArrayList<Any>()
                }
            }
        }

        // Utility
        "$pkgName.utility.KotlinExtendKt".hook {
            injectMember {
                method {
                    name = "isWeiboUVEAd"
                    param(findClass("$pkgName.model.sina.Status"))
                }
                beforeHook {
                    result = false
                }
            }
            injectMember {
                method {
                    name = "isVideoLogEnable"
                    param(findClass("$pkgName.model.sina.Status"))
                }
                beforeHook {
                    result = false
                }
            }
        }
        "$pkgName.utility.KotlinUtilKt".hook {
            injectMember {
                method {
                    name = "findUVEAd"
                    param(findClass("$pkgName.model.sina.PageInfo"))
                }
                beforeHook {
                    result = null
                }
            }
        }

        // ProcessMonitor
//        "$pkgName.manager.ProcessMonitor".hook {
//            injectMember {
//                method {
//                    name = "displayAd"
//                    param(LongClass)
//                    param(ActivityClass)
//                    param(BooleanClass)
//                }
//                replaceAny {
//                    return@replaceAny true
//                }
//            }
//        }

        // 其他广告
        "$pkgName.activity.v4.Setting".hook {
            injectMember {
                method {
                    name = "loadBoolean"
                    param(StringClass)
                }
                beforeHook {
                    val key = args[0] as String
                    when {
                        key == "BOOL_UVE_FEED_AD" -> resultFalse()
                        key.startsWith("BOOL_AD_ACTIVITY_BLOCK_") -> resultTrue()
                    }
                }
            }
            injectMember {
                method {
                    name = "loadInt"
                    param(StringClass)
                }
                beforeHook {
                    when (args[0] as String) {
                        "ad_interval" -> result = Int.MAX_VALUE
                        "display_ad" -> result = 0
                    }
                }
            }
            injectMember {
                method {
                    name = "loadStringSet"
                    param(StringClass)
                }
                beforeHook {
                    if (args[0] as String == "CYT_DAYS") {
                        result = setOf<String>()
                    }
                }
            }
            injectMember {
                method {
                    name = "loadString"
                    param(StringClass)
                }
                beforeHook {
                    if (args[0] as String == "video_ad") {
                        result = ""
                    }
                }
            }
        }

        //强制暗黑模式
//        "com.skin.loader.SkinManager".hook {
//            injectMember {
//                method {
//                    name = "isDarkMode"
//                    emptyParam()
//                }
//                afterHook {
//                    result = true
//                }
//            }
//            injectMember {
//                method {
//                    name = "getDarkModeStatus"
//                    param(ContextClass)
//                }
//                afterHook {
//                    result = true
//                }
//            }
//        }

        //隐藏首页右下角加号
        "com.weico.international.ui.maintab.MainTabFragment".hook {
            injectMember {
                method {
                    name = "onViewCreated"
                    param(ViewClass)
                    param(BundleClass)
                }
                afterHook {
                    loggerD("wxhook", "before hook")
                    val btn =
                        XposedHelpers.findField(instanceClass, "mIndexFab") as FloatingActionButton
                    btn.visibility = View.INVISIBLE
                }
            }
        }
        "com.google.android.material.floatingactionbutton.FloatingActionButton".hook {
            injectMember {
                method {
                    name = "setVisibility"
                    param(IntClass)
                }
                beforeHook {
                    args[0] = View.INVISIBLE
                }
            }
        }
    }
}
