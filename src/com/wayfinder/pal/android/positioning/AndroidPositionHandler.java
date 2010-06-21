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
package com.wayfinder.pal.android.positioning;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;

import com.wayfinder.pal.positioning.PositionProviderInterface;
import com.wayfinder.pal.positioning.UpdatesHandler;

/**
 * 
 *
 */
public class AndroidPositionHandler 
implements PositionProviderInterface, LocationListener {
    
    private final Context m_context;
    private final LocationManager m_locMgr;
    private final String m_type;
    private final long m_minUpdateTime;
    private final float m_minUpdateDist;
    
    private HandlerThread m_handlerThread;
    
    private UpdatesHandler m_coreHandler;
    
    /**
     * 
     * @param c Context
     * @param type  the type of provider this will handle, either 
     * {@link LocationManager#GPS_PROVIDER} or {@link LocationManager#NETWORK_PROVIDER}   
     * @param minUpdTime    update frequency in milliseconds to be passed as a parameter for
     * {@link LocationManager#requestLocationUpdates(String, long, float, LocationListener)} 
     * @param minUpdDist    update frequency in meters to be passed as a parameter for
     * {@link LocationManager#requestLocationUpdates(String, long, float, LocationListener)}
     * @param handlerThread 
     */
    public AndroidPositionHandler(Context c, String type, long minUpdTime, float minUpdDist, HandlerThread handlerThread) {
        m_context = c;
        m_locMgr = (LocationManager) m_context.getSystemService(Context.LOCATION_SERVICE);
        m_type = type;
        m_minUpdateTime = minUpdTime;
        m_minUpdateDist = minUpdDist;
        m_handlerThread = handlerThread;
    }

    public void resumeUpdates() {
        // make sure this doesn't get added several times
        m_locMgr.removeUpdates(this);
        Log.i("AndroidPositionHandler.resumeUpdates()", m_type);
        m_locMgr.requestLocationUpdates(
                m_type, m_minUpdateTime, m_minUpdateDist, this, m_handlerThread.getLooper());
        //this should leave the previous state for the UpdatesHandler, and either
        //onStatusChanged() or onLocationChanged() will change the state there
    }

    public void stopUpdates() {
        Log.i("AndroidPositionHandler.stopUpdates()", m_type);
        m_locMgr.removeUpdates(this);
        m_coreHandler.updateState(UpdatesHandler.PROVIDER_TEMPORARILY_UNAVAILABLE);
    }

    public void setUpdatesHandler(UpdatesHandler coreHandler) {
        m_coreHandler = coreHandler;
    }
    
    public void onLocationChanged(Location location) {
        Log.d("AndroidPositionHandler.onLocationChanged()", m_type+" "+location);
        //we can assume the provider is available since we get positions
        m_coreHandler.updateState(UpdatesHandler.PROVIDER_AVAILABLE);
        m_coreHandler.updatePosition(
                location.getLatitude(), 
                location.getLongitude(), 
                location.hasSpeed() ? location.getSpeed() : UpdatesHandler.VALUE_UNDEF, 
                location.hasBearing() ? location.getBearing() : UpdatesHandler.VALUE_UNDEF, 
                (float) (location.hasAltitude() ? location.getAltitude() : UpdatesHandler.VALUE_UNDEF), 
                (int) (location.hasAccuracy() ? location.getAccuracy() : UpdatesHandler.VALUE_UNDEF), 
                location.getTime());
    }

    public void onProviderDisabled(String provider) {
        m_coreHandler.updateState(UpdatesHandler.PROVIDER_OUT_OF_SERVICE);
    }

    public void onProviderEnabled(String provider) {
        m_coreHandler.updateState(UpdatesHandler.PROVIDER_TEMPORARILY_UNAVAILABLE);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("AndroidPositionHandler.onStatusChanged()", "provider: "+provider+" status: "+status+" m_type: "+m_type);
        if (provider.equals(m_type)) {
            int state = UpdatesHandler.VALUE_UNDEF;
            switch (status) {
            case LocationProvider.AVAILABLE:
                state = UpdatesHandler.PROVIDER_AVAILABLE;
                break;
                
            case LocationProvider.OUT_OF_SERVICE:
                state = UpdatesHandler.PROVIDER_OUT_OF_SERVICE;
                break;
                
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                state = UpdatesHandler.PROVIDER_TEMPORARILY_UNAVAILABLE;
                break;
            }
            
            m_coreHandler.updateState(state);
        }
    }

    /* (non-Javadoc)
     * @see com.wayfinder.pal.positioning.PositionProviderInterface#getType()
     */
    public int getType() {
        if (LocationManager.GPS_PROVIDER.equals(m_type)) {
            return PositionProviderInterface.TYPE_INTERNAL_GPS;
        }
        else if (LocationManager.NETWORK_PROVIDER.equals(m_type)) {
            return PositionProviderInterface.TYPE_NETWORK;
        }
        return PositionProviderInterface.TYPE_SIMULATOR;
    }

}
