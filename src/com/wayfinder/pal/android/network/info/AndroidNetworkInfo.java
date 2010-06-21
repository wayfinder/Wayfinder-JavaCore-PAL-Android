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
package com.wayfinder.pal.android.network.info;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;

import com.wayfinder.pal.network.info.NetworkInfo;
import com.wayfinder.pal.network.info.TGPPInfo;

public final class AndroidNetworkInfo extends PhoneStateListener implements NetworkInfo {
    
    private final Context m_context;
    private final TelephonyManager m_telMgr;
    
    private int m_signalStrength;
    private ServiceState m_serviceState;
    private Android3GPPInfo m_tgppInfo;
    
    public AndroidNetworkInfo(Context c) {
        m_context = c;
        
        m_telMgr = (TelephonyManager) m_context.getSystemService(Context.TELEPHONY_SERVICE);
        m_signalStrength = SIGNAL_STRENGTH_UNKNOWN;
        m_serviceState = new ServiceState();
        m_tgppInfo = new Android3GPPInfo(m_context);

        int events = PhoneStateListener.LISTEN_SERVICE_STATE;   //doesn't need any permissions
        
        String pkgName = m_context.getPackageName();
        PackageManager pm = m_context.getPackageManager();
        
        if (pm.checkPermission(
                Manifest.permission.READ_PHONE_STATE, pkgName) == PackageManager.PERMISSION_GRANTED) {
            events |= PhoneStateListener.LISTEN_SIGNAL_STRENGTH;
        }
        
        m_telMgr.listen(this, events);
    }

    public TGPPInfo get3GPPInfo() throws IllegalStateException {
        return m_tgppInfo;
    }

    public int getNetworkWAF() {
        switch (m_telMgr.getNetworkType()) {
        // these 3 are the same thing as far as we're concerned, and 
        // Android doesn't yet support CDMA or iDEN
        case TelephonyManager.NETWORK_TYPE_EDGE:
        case TelephonyManager.NETWORK_TYPE_GPRS:
        case TelephonyManager.NETWORK_TYPE_UMTS:
            return WAF_3GPP;
            
        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            return WAF_UNKNOWN;
            
        default:
            return WAF_UNKNOWN;
        }
    }

    public int getRadioState() {
        if (m_serviceState.getState() == ServiceState.STATE_IN_SERVICE) {
            return RADIO_STATE_ON;
        }
        else if (m_serviceState.getState() == ServiceState.STATE_POWER_OFF) {
            return RADIO_STATE_OFF;
        }
        else return RADIO_STATE_UNKNOWN;
    }

    public int getRoamingState() {
        if (m_telMgr.isNetworkRoaming()) {
            return ROAMING_STATE_ROAMING;
        }
        else return ROAMING_STATE_HOME;
    }

    public int getSignalStrength() {
        return m_signalStrength;
    }

    public void onServiceStateChanged(ServiceState serviceState) {
        m_serviceState = serviceState;
    }

    /**
     * @see {@link NeighboringCellInfo#getRssi()} to see the conversion from
     * "asu" to dBm
     */
    public void onSignalStrengthChanged(int asu) {
        if (asu == NeighboringCellInfo.UNKNOWN_RSSI) {
            m_signalStrength = SIGNAL_STRENGTH_UNKNOWN;
        }
        else {
            m_signalStrength = -113 + (2 * asu);
        }
    }

	public boolean isAirplaneMode() {
		int value = Settings.System.getInt(
				m_context.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0);
		
		return (value == 1);
	}
}
