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
import android.location.LocationManager;
import android.os.HandlerThread;

import com.wayfinder.pal.positioning.PositionProviderInterface;
import com.wayfinder.pal.positioning.PositioningLayer;

/**
 * 
 *
 */
public class AndroidPositioningLayer implements PositioningLayer {
    
    private static final long GPS_UPDATE_TIME = 500;    //ms
    private static final int GPS_UPDATE_DIST = 3;       //m
    
    private static final long CELLID_UPDATE_TIME = 60000;   //ms
    private static final int CELLID_UDPATE_DIST = 20;       //m
    
    private final Context m_context;
    
    private AndroidPositionHandler[] m_posProviders;
    
    private AndroidPositionHandler m_gpsHandler;
    
    private AndroidPositionHandler m_cellidHandler;
    
    private HandlerThread m_handlerThread;
    
    public AndroidPositioningLayer(Context c) {
        m_context = c;
        m_handlerThread = new HandlerThread("PAL.Positioning");
        
        m_gpsHandler = new AndroidPositionHandler(
                m_context, 
                LocationManager.GPS_PROVIDER, 
                GPS_UPDATE_TIME, 
                GPS_UPDATE_DIST, 
                m_handlerThread);
        
        m_cellidHandler = new AndroidPositionHandler(
                m_context, 
                LocationManager.NETWORK_PROVIDER, 
                CELLID_UPDATE_TIME, 
                CELLID_UDPATE_DIST, 
                m_handlerThread);
        
        m_posProviders = new AndroidPositionHandler[] {m_gpsHandler, m_cellidHandler};
        
        m_handlerThread.start();
    }

    /* (non-Javadoc)
     * @see com.wayfinder.pal.positioning.PositioningLayer#getLocationProviders()
     */
    public PositionProviderInterface[] getPositionProviders() {
        return m_posProviders;
    }

    /* (non-Javadoc)
     * @see com.wayfinder.pal.positioning.PositioningLayer#resumeUpdates()
     */
    public void resumeUpdates() {
        m_gpsHandler.resumeUpdates();
        m_cellidHandler.resumeUpdates();
    }

    /* (non-Javadoc)
     * @see com.wayfinder.pal.positioning.PositioningLayer#stopUpdates()
     */
    public void stopUpdates() {
        m_cellidHandler.stopUpdates();
        m_gpsHandler.stopUpdates();
    }

}
