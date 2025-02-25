package com.example.vsa.xposedutility.tests;

import android.util.Log;

import com.example.vsa.xposedutility.ICases;
import com.example.vsa.xposedutility.Utilities;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.security.SecureClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import android.os.Bundle;

import dalvik.system.BaseDexClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Wechat7020 implements ICases {
    static String TAG = Wechat7020.class.getSimpleName();
    public static HashSet<Object> wvs = new HashSet<Object>();
    @Override
    public void hook(final XC_LoadPackage.LoadPackageParam pparam) {
        if (!pparam.packageName.equals("com.tencent.mm")) return;
//        Log.d(TAG, "jianjia see in 7020 hook---->>>:" + pparam.classLoader);
        hookAll(pparam, pparam.classLoader);
        final String packageName = pparam.packageName;
        Class[] loaderClassList = {
                BaseDexClassLoader.class,
                SecureClassLoader.class,
        };

        for (final Class loaderClass : loaderClassList) {
//            Log.d(TAG, "jianjia see in 7020 loop---->>>:" + loaderClass);
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
            protected void beforeHookedMethod(final MethodHookParam param) throws Throwable {
                if(!param.method.getDeclaringClass().getName().equals("com.tencent.mm.plugin.appbrand.jsapi.g0")) {
                    Log.d(TAG, "---->>>beforeHookedMethod: " + param.method.getName() + " class: " + param.method.getDeclaringClass().getName());
                    // jianjia: wechat updated and when we go back, the code is slightly different so this does not work. We grep logcat with "shopAppid" instead
                    if (param.method.getName().equals("e7") && param.method.getDeclaringClass().getName().equals("com.tencent.mm.plugin.webview.ui.tools.fts.FTSSOSHomeWebViewUI")){
                        Utilities.printParameter("e7-FTSSOSHomeWebViewUI", param);
                    }
                    else {
                        Utilities.printParameter(TAG, param);
                    }
                }

                if(param.method.getName().equals("invokeHandler") && !did){
                    did = true;

                    final Object thiobj =  param.thisObject;
                    final Method invokeHandler = thiobj.getClass().getMethod("invokeHandler",String.class, String.class, int.class);


                }
                // method: m or i0 , in class: com.tencent.mm.plugin.appbrand.appcache.w8
//                if(param.method.getDeclaringClass().getName().equals("com.tencent.mm.plugin.appbrand.appcache.w8")
//                || param.method.getDeclaringClass().getName().equals("zj")
//                ) {
//                    Log.d(TAG, "inside w8 or zj");
//
//                    if(param.method.getName().equals("i0")
//                        && param.method.getName().equals("m")
//                    ){
//                        String appid = param.args[0] + "";
//                        String version = param.args[2] + "";
//                        String[] url = (String[]) param.args[3];
//                        Log.d(TAG, "inside i0 or m in class: w8: " + appid + " " + version + " " + url);
//                    }
//
//                    if ((param.method instanceof Constructor && ((Constructor) param.method).getParameterTypes().length == 4)
//                    || (param.method instanceof Method && ((Method) param.method).getParameterTypes().length == 4)
//                    ) {
//                        String appid = param.args[0] + "";
//                        String version = param.args[2] + "";
//                        String url = param.args[3] + "";
//                        Log.d(TAG, "inside w8 or zj with 4 parameters: " + appid + " " + version + " " + url);
//                        String[] arg3 = (String[]) param.args[3];
//                        Log.d(TAG, Arrays.toString(arg3));
//                        Utilities.writeToFile("apps.txt", appid + " " + version + " " + url + "\n");
//                    }
//                }
                if(param.method.getDeclaringClass().getName().equals("com.tencent.mm.plugin.appbrand.appcache.a9")){
                    if(param.method.getName().equals("p9")){
                        String appid = param.args[0] + "";
                        String pkg_dir = param.args[2] + "";
                        Log.d(TAG, "USEFUL INFO: The appid: " + appid + " and its pkg dir: " + pkg_dir);
                    }
                    Utilities.printParameter(TAG, param);
                }

                if(param.method.getDeclaringClass().getName().equals("com.tencent.mm.plugin.appbrand.jsapi.g0")) {
//                    Log.d(TAG, "hooked invokeHandler");
                    if (!wvs.contains(param.thisObject))
                        wvs.add(param.thisObject);
//                    Log.d(TAG, "---->>>wvs in 7020: " + wvs.size());
                }

                // get the search API
                // jianjia: this might does not work because 135 is a number generated during decompilation....not a real number?
//                if(param.method.getDeclaringClass().getName().equals("com.tencent.mm.plugin.webview.ui.tools.fts.FTSSOSHomeWebViewUI")) {
//                    if(param.method.getName().equals("e7") && param.args[0]==135){
//                        Log.d(TAG, "hooked searchBar");
//                        final Object searchObj =  param.thisObject;
//                        final Method invokeHandler = searchObj.getClass().getMethod("e7", int.class, Bundle.class);
//                        Bundle argBundle = (Bundle) param.args[1];
//                        argBundle.putString("query", "Hello");
//                        Log.d(TAG, "searchBar---->"+argBundle.getString("query"));
//                        invokeHandler.invoke(searchObj, 135, argBundle);
//                    }
//                }

            }
        };



        HashSet<String> clss = new HashSet<>();

//        clss.add("com.tencent.mm.plugin.appbrand.jsapi.l");
        clss.add("com.tencent.mm.plugin.appbrand.jsapi.g0");

//        clss.add("com.tencent.mm.plugin.appbrand.appcache.ba");
//        clss.add("com.tencent.mm.plugin.appbrand.appcache.w8"); // jianjia: I gtuess it is w8.j, the 3rd parameter is the download url
//        clss.add("com.tencent.mm.plugin.appbrand.appcache.j7");
//        clss.add("com.tencent.mm.plugin.appbrand.appcache.g0");
//        clss.add("com.tencent.mm.plugin.appbrand.appcache.k7");
//        clss.add("com.tencent.mm.plugin.appbrand.appcache.m5");
//        clss.add("com.tencent.mm.plugin.appbrand.appcache.predownload");
        clss.add("com.tencent.mm.plugin.appbrand.appcache.a9");

//        clss.add("o14.q");
//        clss.add("com.tencent.mm.plugin.websearch.y");
        clss.add("com.tencent.mm.plugin.webview.ui.tools.fts.FTSSOSHomeWebViewUI");
        // jianjia: method: E5, class: com.tencent.mm.plugin.webview.ui.tools.fts.FTSSOSHomeWebViewUI - something like input onchange
        // method: e7, class: com.tencent.mm.plugin.webview.ui.tools.fts.FTSSOSHomeWebViewUI - initiates the search and returns the result of the search
        // jianjia: I can not remember how did I find this api...I might have been searching for webview.ui.tools...
        for(String cls : clss){
//            Log.d(TAG, "try hooking:" + cls);
            for(Member md:MyXposedHelper.hookAllDeclaredMethods(classLoader, cls, null, cb)) {
                Log.d(TAG, "hooked:" + md);
            }
        }


    }

}
