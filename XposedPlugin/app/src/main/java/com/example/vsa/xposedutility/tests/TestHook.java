package com.example.vsa.xposedutility.tests;


import android.util.Log;

import com.example.vsa.xposedutility.Utilities;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashSet;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class TestHook {
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        String TAG = TestHook.class.getSimpleName();
        Log.d(TAG,"test hook");
        XposedBridge.log("see name2.." + Utilities.getpidname());
    }
}
