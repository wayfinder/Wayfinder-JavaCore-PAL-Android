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
package com.wayfinder.pal.android.network;

import android.content.Context;

import com.wayfinder.pal.android.network.http.AndroidHttpClient;
import com.wayfinder.pal.android.network.info.AndroidNetworkInfo;
import com.wayfinder.pal.network.NetworkLayer;
import com.wayfinder.pal.network.http.HttpClient;
import com.wayfinder.pal.network.info.NetworkInfo;

/**
 * Android implementation of NetworkLayer.
 */
public final class AndroidNetworkLayer implements NetworkLayer {
    
    private final Context m_context;
    private final AndroidHttpClient m_httpClient;
    private final NetworkInfo m_networkInfo;
    
    public AndroidNetworkLayer(Context c) {
        m_context = c;
        m_httpClient = new AndroidHttpClient(c);
        m_networkInfo = new AndroidNetworkInfo(c);
    }
    
    
    public HttpClient getHttpClient() {
        return m_httpClient;
    }
    

    public NetworkInfo getNetworkInfo() {
        return m_networkInfo;
    }
}
