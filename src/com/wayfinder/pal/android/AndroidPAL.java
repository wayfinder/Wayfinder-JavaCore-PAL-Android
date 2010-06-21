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
package com.wayfinder.pal.android;

import android.Manifest.permission;
import android.content.Context;

import com.wayfinder.pal.PAL;
import com.wayfinder.pal.android.concurrency.AndroidConcurrencyLayer;
import com.wayfinder.pal.android.debug.AndroidLogHandler;
import com.wayfinder.pal.android.hardwareinfo.AndroidHardwareInfo;
import com.wayfinder.pal.android.network.AndroidNetworkLayer;
import com.wayfinder.pal.android.network.http.AndroidHttpClient;
import com.wayfinder.pal.android.network.http.HttpConfigurationInterface;
import com.wayfinder.pal.android.persistence.AndroidPersistenceLayer;
import com.wayfinder.pal.android.positioning.AndroidPositioningLayer;
import com.wayfinder.pal.android.softwareinfo.AndroidSoftwareInfo;
import com.wayfinder.pal.android.util.AndroidUtilFactory;
import com.wayfinder.pal.android.sound.AndroidSoundLayer;


/**
 * Ensure that the following permissions are set in the manifest:
 * <p>
 * <ul>
 * <li>{@link permission#ACCESS_COARSE_LOCATION}</li>
 * <li>{@link permission#ACCESS_FINE_LOCATION}</li>
 * <li>{@link permission#INTERNET}</li>
 * <li>{@link permission#READ_PHONE_STATE}</li>
 * </ul>
 * 
 * @version Android 1.5r2
 */
public class AndroidPAL extends PAL {
    
    protected AndroidPAL(Context c,
            AndroidLogHandler logHandler, 
            AndroidConcurrencyLayer cLayer, 
            AndroidNetworkLayer netLayer, 
            AndroidHardwareInfo hardwareInfo,
            AndroidSoftwareInfo softInfo,
            AndroidPersistenceLayer perLayer,
            AndroidUtilFactory uFactory,
            AndroidSoundLayer soundLayer, 
            AndroidPositioningLayer posLayer) {
    	
        super(logHandler, cLayer, netLayer, hardwareInfo, softInfo, perLayer, 
        	  uFactory, soundLayer, posLayer);
    }

    
    @Override
    public void requestGC() {
        // System.gc();
    }
        

    /**
     * Ensure that the following permissions are set in the manifest:
     * <p>
     * <ul>
     * <li>{@link permission#ACCESS_COARSE_LOCATION}</li>
     * <li>{@link permission#ACCESS_FINE_LOCATION}</li>
     * <li>{@link permission#INTERNET}</li>
     * <li>{@link permission#READ_PHONE_STATE}</li>
     * </ul>
     * 
     * @param c The main {@link Context} object
     * @return The Android specific {@link PAL} implementation
     */
    public static AndroidPAL createAndroidPAL(Context c) {
        return new AndroidPAL(c,
                              new AndroidLogHandler(),
                              new AndroidConcurrencyLayer(),
                              new AndroidNetworkLayer(c),
                              new AndroidHardwareInfo(c),
                              new AndroidSoftwareInfo(c),
                              new AndroidPersistenceLayer(c),
                              new AndroidUtilFactory(c),
                              new AndroidSoundLayer(c), 
                              new AndroidPositioningLayer(c));
        
    }


    // ----------------------------------------------------------------------



    /**
     * <p>Get an interface to obtain Http configuration parameters.</p>
     * 
     * <p>The configuration will change depending on what transport (e.g.
     * WAP APN, regular internet) is utilized. The interface instance stays the
     * same. So you only need to call this method once and then call the methods
     * in  {@link HttpConfigurationInterface} before each connection attempt.
     * See the interface for more details.</p>
     * 
     * @return an instance of {@link HttpConfigurationInterface}.
     */
    public HttpConfigurationInterface getHttpConfiguration() {
        AndroidHttpClient httpclient = (AndroidHttpClient)
            getNetworkLayer().getHttpClient();

        return httpclient.getHttpConfiguration(); 
    }
}
