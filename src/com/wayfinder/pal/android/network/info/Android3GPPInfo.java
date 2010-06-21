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
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import com.wayfinder.pal.network.info.NetworkException;
import com.wayfinder.pal.network.info.TGPPInfo;

public class Android3GPPInfo implements TGPPInfo {
    
    private final Context m_context;
    private final TelephonyManager m_telMgr;
    
    private boolean m_cellIdAllowed = false;
    
    private static GsmCellLocation UNKNOWN_CELL = new GsmCellLocation();
    private static String UNKNOWN = ""; 
    
    Android3GPPInfo(Context c) {
        m_context = c;
        m_telMgr = (TelephonyManager) m_context.getSystemService(
                Context.TELEPHONY_SERVICE);
        
        String pkgName = m_context.getPackageName();
        PackageManager pm = m_context.getPackageManager();
        
        if (pm.checkPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION, pkgName) == PackageManager.PERMISSION_GRANTED) {
            m_cellIdAllowed = true;
        }
    }



    /* (non-Javadoc)
     * @see com.wayfinder.pal.network.info.TGPPInfo#getCurrentMCC()
     */
    public String getCurrentMCC() throws NetworkException {
        // first 3 is MCC
    	String net = m_telMgr.getNetworkOperator();
    	if (net.length() > 4) {
    		return net.substring(0, 3);
    	} else {
    		return UNKNOWN;
    	}
    }

    /* (non-Javadoc)
     * @see com.wayfinder.pal.network.info.TGPPInfo#getCurrentMNC()
     */
    public String getCurrentMNC() throws NetworkException {
        // first 3 is MCC, the rest (2 or 3) is MNC
    	String net = m_telMgr.getNetworkOperator();
    	if (net.length() > 4) {
    		return net.substring(3);
    	} else {
    		return UNKNOWN;
    	}
    }

    /* (non-Javadoc)
     * @see com.wayfinder.pal.network.info.TGPPInfo#getHomeMCC()
     */
    public String getHomeMCC() throws NetworkException {
    	String sim = m_telMgr.getSimOperator();
    	if (sim.length() > 4) {
    		return sim.substring(0, 3);
    	} else {
    		return UNKNOWN;
    	}
        
    }

    /* (non-Javadoc)
     * @see com.wayfinder.pal.network.info.TGPPInfo#getHomeMNC()
     */
    public String getHomeMNC() throws NetworkException {
    	String sim = m_telMgr.getSimOperator();
    	if (sim.length() > 4) {
    		return sim.substring(3);
    	} else {
    		return UNKNOWN;
    	}
    }
    
    /* (non-Javadoc)
     * @see com.wayfinder.pal.network.info.TGPPInfo#getCellID()
     */
    public String getCellID() throws NetworkException {
    	int cid = getGsmCellLocation().getCid();
    	if (cid != -1) {
    		return Integer.toHexString(cid);
    	} else {
    		return UNKNOWN;
    	}
    }

    /* (non-Javadoc)
     * @see com.wayfinder.pal.network.info.TGPPInfo#getLAC()
     */
    public String getLAC() throws NetworkException {
    	int lac = getGsmCellLocation().getLac();
    	if (lac != -1) {
    		return Integer.toHexString(lac);
    	} else {
    		return UNKNOWN;
    	}
    }

    /* (non-Javadoc)
     * @see com.wayfinder.pal.network.info.TGPPInfo#getNetworkType()
     */
    public int getNetworkType() throws NetworkException {
        if (m_telMgr.getNetworkType() == TelephonyManager.NETWORK_TYPE_EDGE
                || m_telMgr.getNetworkType() == TelephonyManager.NETWORK_TYPE_GPRS) {
            return TYPE_3GPP_GPRS;
        }
        else if (m_telMgr.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS) {
            return TYPE_3GPP_UMTS;
        }
        return TYPE_3GPP_UNKNOWN;
    }

    /* (non-Javadoc)
     * @see com.wayfinder.pal.network.info.TGPPInfo#supportsCellID()
     */
    public boolean supportsCellID() {
    	return (m_cellIdAllowed && 
    		m_telMgr.getSimState() == TelephonyManager.SIM_STATE_READY &&
    		is3GPPNetwork());
    }
    
    /* (non-Javadoc)
     * @see com.wayfinder.pal.network.info.TGPPInfo#supportsLAC()
     */
    public boolean supportsLAC() {
        return supportsCellID();
    }

    /* (non-Javadoc)
     * @see com.wayfinder.pal.network.info.TGPPInfo#supportsCurrentMCC()
     */
    public boolean supportsCurrentMCC() {
        return m_telMgr.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    /* (non-Javadoc)
     * @see com.wayfinder.pal.network.info.TGPPInfo#supportsCurrentMNC()
     */
    public boolean supportsCurrentMNC() {
        return supportsCurrentMCC();
    }

    /* (non-Javadoc)
     * @see com.wayfinder.pal.network.info.TGPPInfo#supportsHomeMCC()
     */
    public boolean supportsHomeMCC() {
        return m_telMgr.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    /* (non-Javadoc)
     * @see com.wayfinder.pal.network.info.TGPPInfo#supportsHomeMNC()
     */
    public boolean supportsHomeMNC() {
        return supportsHomeMCC();
    }

    private GsmCellLocation getGsmCellLocation() {	
    	if (m_cellIdAllowed) {
    		CellLocation cellLocation = m_telMgr.getCellLocation();
    		if (cellLocation instanceof GsmCellLocation) {
        		return (GsmCellLocation)cellLocation;
    		}
    	}
    	return UNKNOWN_CELL;    	 
    }
    
    private boolean is3GPPNetwork() {
    	int type = m_telMgr.getNetworkType();
    	return (type == TelephonyManager.NETWORK_TYPE_EDGE
            || type == TelephonyManager.NETWORK_TYPE_GPRS 
        	|| type == TelephonyManager.NETWORK_TYPE_UMTS);
    }
    

}
