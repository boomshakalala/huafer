package org.ffmpeg.android;

import android.os.Build;

/**
 * Created by Administrator on 2016/12/22.
 */
public class CpuArchHelper {

    static CpuArch getCpuArch() {
        // check if device is x86 or x86_64
        String aa = Build.CPU_ABI;
        if (Build.CPU_ABI.equals(getx86CpuAbi()) || Build.CPU_ABI.equals(getx86_64CpuAbi())) {
            return CpuArch.x86;
        }  else if (Build.CPU_ABI.equals(getArm64CpuAbi())) {
            return CpuArch.ARMv7;
        } else if(Build.CPU_ABI.equals(getArmeabiv7CpuAbi())) {
            return CpuArch.ARMv7;
        }
        return CpuArch.NONE;
    }

    static String getx86CpuAbi() {
        return "x86";
    }

    static String getx86_64CpuAbi() {
        return "x86_64";
    }

    static String getArm64CpuAbi() {
        return "arm64-v8a";
    }

    static String getArmeabiv7CpuAbi() {
        return "armeabi-v7a";
    }
}
