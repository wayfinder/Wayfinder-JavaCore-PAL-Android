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
package com.wayfinder.pal.android.network.http;

import java.net.InetSocketAddress;
import java.net.Proxy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


/**
 * Configuration settings for the network handling on Java Standard Edition
 * 
 * This class is thread-safe.
 */
final class HttpConfiguration implements HttpConfigurationInterface {
    
    private Proxy m_proxy;
    private boolean m_keepAlive;
    
    final private ConnectivityManager m_cm;
    
    HttpConfiguration(Context context) {
        m_keepAlive = true;
        m_proxy = createProxyFromCarrierSettings(context);
        m_cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
    
    
    //-------------------------------------------------------------------------
    // proxy
    
    
    /**
     * Sets the {@link Proxy} object used for all http connections made by the
     * Core when using mobile network
     * 
     * @param proxy The {@link Proxy} object to use or null if no proxy should
     * be used
     */
    public synchronized void setProxyForMobileNetwork(Proxy proxy) {
    	if (proxy == null) {
    		m_proxy = Proxy.NO_PROXY;
    	} else {
    		m_proxy = proxy;
    	}
    }
    
    
    /* (non-Javadoc)
     * @see com.wayfinder.pal.android.network.http.HttpConfigurationInterface#getProxy()
     */
    public synchronized Proxy getProxy() {
    	NetworkInfo netInfo = m_cm.getActiveNetworkInfo();
    	//check is current network is mobile to avoid using the carrier proxy 
    	//when using WiFi
    	if (netInfo != null &&  netInfo.getType() == ConnectivityManager.TYPE_MOBILE) { 
    		return m_proxy;
    	} else {
    		return Proxy.NO_PROXY;
    	}
    }
    
    
    /**
     * Sets the currently used proxy to the one specified for the carrier
     * 
     * @param context The context in which to obtain the proxy
     */
    public static synchronized Proxy createProxyFromCarrierSettings(Context context) {
        /*
         * the default APN is used and we should use the default
         * proxy which is normally NO_PROXY.
         *
         * But for testing on Magic you can enter a default access
         * point with proxy. The type of that proxy must be
         * "default" or not set at all. If the type is set to
         * "httpproxy" (as on Click) the access point will not be
         * used and the proxy settings are not returned.
         *
         * On Click, this will return the settings from the
         * default access point even after we are in state
         * CONNECTED.
         *
         * AFAIK there is no UI on the phone to set a proxy for
         * a certain app only so this and
         * android.net.Proxy.getDefaultHost() will always return
         * the same thing. Presumably it is possible to add
         * settings in the sql database to have a special proxy
         * and that that or a similar mechanism exists on the
         * emulator. We use the "current" proxy to allow for
         * maximum flexibility.
         *
         */
        String androidProxyHost =  android.net.Proxy.getHost(context);
        int androidProxyPort = android.net.Proxy.getPort(context);
        Proxy proxy = createProxy(androidProxyHost, androidProxyPort);
        Log.i("HttpConfiguration.setProxyFromCarrierSettings", 
                "Using proxy from android.net.Proxy: " + proxy);
        return proxy;
    }
    
    
    /**
     * If host is null or empty string, return Proxy.NO_PROXY.
     *
     * @param host the host name of the proxy.
     * @param port the port on which the proxy listens.
     * @return
     */
    private static Proxy createProxy(String host, int port) {
        Log.d("createProxy()", host + ":" + port);
        Proxy proxy;

        if (host == null || host.length() < 1) {
            proxy = Proxy.NO_PROXY;
        } else {
        	/*
        	 * Some carriers unfortunately miss the A record in the DNS request,
        	 * which causes the lookup to fail. Workaround is to create the
        	 * address unresolved
        	 */
            proxy = new Proxy(Proxy.Type.HTTP,
                    InetSocketAddress.createUnresolved(host, port));
        }

        Log.d("createProxy", "returning proxy " + proxy);
        return proxy;
    }
    
    
    //-------------------------------------------------------------------------
    // keep-alive
    
    
    /**
     * Setting for if the implementation should allow the underlying sockets of
     * the http connections to stay alive for reuse later.
     * <p>
     * This setting is <code>true</code> by default
     * 
     * @param keepAlive true if underlying connections should be kept alive
     */
    public synchronized void setKeepAlive(boolean keepAlive) {
        m_keepAlive = keepAlive;
    }
    
    
    /* (non-Javadoc)
     * @see com.wayfinder.pal.android.network.http.HttpConfigurationInterface#isKeepAlive()
     */
    public synchronized boolean isKeepAlive() {
        return m_keepAlive;
    }
}
