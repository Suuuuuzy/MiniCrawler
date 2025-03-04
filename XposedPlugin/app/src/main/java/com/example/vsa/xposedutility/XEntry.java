package com.example.vsa.xposedutility;

import com.example.vsa.xposedutility.tests.Wechat7020;
import com.example.vsa.xposedutility.tests.WechatMiniAppsDownloader;
import com.example.vsa.xposedutility.tests.WechatSearchToken;
import com.example.vsa.xposedutility.tests.TestHook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
// jianjia: this should be the entry file

public class XEntry implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("xloading.. " + lpparam.packageName +" -- " + Utilities.getpidname());
        
        new TestHook().hook(lpparam);
        XposedBridge.log("after xloading.. " + lpparam.packageName +" -- " + Utilities.getpidname());

//        new WechatSearchToken().hook(lpparam);
        new Wechat7020().hook(lpparam);
        new WechatMiniAppsDownloader().hook(lpparam);

    }
}
