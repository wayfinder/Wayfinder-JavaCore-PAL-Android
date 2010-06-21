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

import java.net.Proxy;

/**
 * <p>Interface from which request handlers get network configuration data.</p>
 * 
 * <p>Implementors must be thread-safe. Users must query the data before each
 * attempt to set-up/tear down sockets as the configuration may change depending
 * on available networks.</p>
 * 
 * <p>For example, each call to {@link java.net.URL#openConnection(java.net.Proxy)}
 * should call <code>getProxy()</code>.</p>
 */
public interface HttpConfigurationInterface {

    /**
     * <p>Returns the proxy object used for all http connections by the core.</p>
     * 
     * <p>In case no proxy is set, return <code>Proxy.NO_PROXY</code>.
     * 
     * @return A {@link Proxy} object, will never return <code>null</code>.
     */
    public abstract Proxy getProxy();

    /**
     * Checks to see if underlying sockets should be kept alive
     * 
     * @return <code>true</code> if they should
     */
    public abstract boolean isKeepAlive();
}
