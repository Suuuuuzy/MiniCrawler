package com.example.vsa.xposedutility.tests;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.example.vsa.xposedutility.ICases;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureClassLoader;
import java.util.HashSet;

import dalvik.system.BaseDexClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class WechatMiniAppsDownloader implements ICases {
    static String TAG = WechatMiniAppsDownloader.class.getSimpleName();

//    public static HashSet<Object> wvs = new HashSet<Object>();

    @Override
    public void hook(final XC_LoadPackage.LoadPackageParam pparam) {
        if (!pparam.packageName.equals("com.tencent.mm")) return;

        hookAll(pparam, pparam.classLoader);
        final String packageName = pparam.packageName;
        Class[] loaderClassList = {
                BaseDexClassLoader.class,
                SecureClassLoader.class,
        };

        for (final Class loaderClass : loaderClassList) {
            XposedBridge.hookAllConstructors(loaderClass, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    ClassLoader classLoader = (ClassLoader) param.thisObject;

                    hookAll(pparam, classLoader);
                }
            });
        }
    }
    static boolean did = false;
    public void hookAll(XC_LoadPackage.LoadPackageParam pparam, ClassLoader classLoader) {

        XC_MethodHook cb  =new XC_MethodHook() {
            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                Log.d(TAG, "android.app.Activity onCreate callback---->>>" + param.method.getName()+":"+did+":"+param.thisObject.getClass().getName());

                if (did) return;
                did = true;

                Context context = (Context) param.thisObject;
                context = context.getApplicationContext();
                ComponentName receiver = new ComponentName(context, MyReceiver.class);

                IntentFilter filter = new IntentFilter("android.intent.myper");
                context.registerReceiver(new MyReceiver(), filter);
                Log.d(TAG, "---->>>:registerReceiver" );
            }
        };
//        Log.d(TAG, "---->>>findAndHookMethod in downloader onCreate: "+classLoader);
        XposedHelpers.findAndHookMethod("android.app.Activity", classLoader, "onCreate", Bundle.class, cb);
        // jianjia: do not kill?
//        String wb = "com.tencent.mm.plugin.webview.ui.tools.WebviewMpUI";
//        wb = "com.tencent.mm.plugin.webview.ui.tools.WebViewUI";
//        wb = "android.app.Activity";
//        try{
//            classLoader.loadClass(wb);
//            XposedHelpers.findAndHookMethod(wb, classLoader, "onResume", new XC_MethodHook() {
//                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
//                    String target = param.thisObject.getClass().getName();
//                    Log.d(TAG, "android.app.Activity onResume callback---->>>" + param.method.getName()+":"+target);
//                    if(target.equals("com.tencent.mm.plugin.webview.ui.tools.WebviewMpUI")
//                            || target.equals("com.tencent.mm.plugin.brandservice.ui.timeline.preload.ui.TmplWebViewMMUI")
//                            || target.equals("com.tencent.mm.plugin.appbrand.ui.AppBrandUI")
//                            || target.equals("com.tencent.mm.plugin.webview.ui.tools.MMWebViewUI")  // this found in 8049
//                            || target.equals("com.tencent.mm.plugin.webview.ui.tools.WebViewUI")
//                            || target.equals("com.tencent.mm.plugin.brandservice.ui.timeline.preload.ui.TmplWebViewToolUI") // this found in 8049
//                            || target.equals("com.tencent.mm.plugin.brandservice.ui.timeline.preload.ui.TmplWebViewTooLMpUI") // not found in 8049
//                            || target.equals("com.tencent.mm.plugin.webview.ui.tools.CustomSchemeEntryWebViewUI")  // this found in 8049
//                            || target.equals("com.tencent.mm.plugin.webview.ui.tools.fts.PreLoadWebViewUI")  // this found in 8049
//                            || target.equals("com.tencent.mm.plugin.appbrand.launching.AppBrandLaunchProxyUI")  // this found in 8049, not sure
//                    ){
//                        Log.d(TAG, "---->>>:kill");
//                        Activity act = ((Activity)param.thisObject);
//                        act.finish();
//                    }
//                }
//            });
//            Log.d(TAG, "WebviewMpUI-onCreate");
//        }catch (Exception e){
//
//        }

    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "---->>>:onReceive:start:" + Wechat7020.wvs.size());
            try {
                String app = intent.getStringExtra("appid");
                int cnt = 0;
                for (Object obj : Wechat7020.wvs) {
                    if (obj == null) continue;
                    try {
                        Method invokeHandler = obj.getClass().getMethod("invokeHandler", String.class, String.class, int.class);
                        String str = "{\"appId\":\"" + app + "\",\"extraData\":\"\",\"envVersion\":\"release\",\"scene\":1037,\"sceneNote\":\"\"}";
                        Log.d(TAG, "invokeHandler---->"+str);
                        invokeHandler.invoke(obj, new Object[]{"navigateToMiniProgram", str, 9999});
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "---->>>:navigateToMiniProgram:" + app);
                    cnt += 1;
                    if (cnt>10){
                        break;
                    }                    // jianjia: if we succeed for one time, break. Don't need to go over all wvs
                    // if break here, then we need to close the newly opened miniapp to navigate to the next one
//                    break;
                }
                Log.d(TAG, "---->>>:onReceive:end");
            } catch (Exception e) {
                Log.d(TAG, "---->>>:onReceive:e" + e.toString());
            }
        }
    }
}

/*
adb shell am broadcast -a android.intent.myper --es appid "wxccbe8c6831202aeb"
*/