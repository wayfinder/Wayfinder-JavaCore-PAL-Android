/*******************************************************************************
 * Copyright (c) 1999-2010, Vodafone Group Services
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions 
 * are met:
 * 
 *     * Redistributions of source code must retain the above copyright 
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above 
 *       copyright notice, this list of conditions and the following 
 *       disclaimer in the documentation and/or other materials provided 
 *       with the distribution.
 *     * Neither the name of Vodafone Group Services nor the names of its 
 *       contributors may be used to endorse or promote products derived 
 *       from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 ******************************************************************************/
package com.wayfinder.pal.android.hardwareinfo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import com.wayfinder.pal.hardwareinfo.HardwareInfo;

public class AndroidHardwareInfo implements HardwareInfo {
    
    private final TelephonyManager m_telMgr;
    private final boolean m_allowToReadPhoneState;

    public AndroidHardwareInfo(Context context) {
        m_telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        // check if we can access the phone state
        final String pkgName = context.getPackageName();
        final PackageManager pm = context.getPackageManager();
        m_allowToReadPhoneState = pm.checkPermission(Manifest.permission.READ_PHONE_STATE, pkgName) == PackageManager.PERMISSION_GRANTED;
    }
    
    
    public String getIMEI() {
        if(m_allowToReadPhoneState &&
            (m_telMgr.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM)) {
            
            return checkValidString(m_telMgr.getDeviceId());
        }
        return null;
    }
    
    
    public String getIMSI() {
        if(m_allowToReadPhoneState && 
                (m_telMgr.getSimState() == TelephonyManager.SIM_STATE_READY)) {
            
            return checkValidString(m_telMgr.getSubscriberId());
        }
        return null;
    }
    
    
    public String getBlackBerryPIN() {
        return null; // not a blackberry
    }
    

    public String getBluetoothMACAddress() {
        return null; // bluetooth packages not included in android yet
    }
    

    public String getESN() {
        return null; // no CDMA device implementations yet
    }

    
    private static String checkValidString(String aStr) {
        if(aStr != null) {
            aStr = aStr.trim();
            if(aStr.length() > 0) {
                return aStr;
            }
        }
        return null;
    }
}
