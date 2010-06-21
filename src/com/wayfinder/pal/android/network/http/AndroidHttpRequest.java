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

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.wayfinder.pal.network.http.HttpEntity;
import com.wayfinder.pal.network.http.HttpEntityEnclosingRequest;
import com.wayfinder.pal.network.http.HttpHost;
import com.wayfinder.pal.network.http.HttpResponse;

final class AndroidHttpRequest implements HttpEntityEnclosingRequest {
    
    private final Map<String, String> m_headers;
    private final String m_requestURI;
    private final HttpConfigurationInterface m_httpConfig;
    private HttpEntity m_entity;
    
    AndroidHttpRequest(HttpConfigurationInterface netConf, String requestURI) {
        m_httpConfig = netConf;
        m_requestURI = requestURI;
        m_headers = new HashMap<String, String>(); 
    }
    
    
    //-------------------------------------------------------------------------
    // HttpRequest ifc
    
    
    /* (non-Javadoc)
     * @see com.wayfinder.pal.network.http.HttpRequest#getRequestMethod()
     */
    public String getRequestMethod() {
        if(m_entity == null) {
            return "GET";
        }
        return "POST";
    }
    

    /* (non-Javadoc)
     * @see com.wayfinder.pal.network.http.HttpRequest#getRequestUri()
     */
    public final String getRequestUri() {
        return m_requestURI;
    }
    
    
    //-------------------------------------------------------------------------
    // HttpMessage ifc
    

    /* (non-Javadoc)
     * @see com.wayfinder.pal.network.http.HttpMessage#addHeader(java.lang.String, java.lang.String)
     */
    public final void addHeader(String name, String value) {
        m_headers.put(name, value);
    }

    
    /* (non-Javadoc)
     * @see com.wayfinder.pal.network.http.HttpMessage#containsHeader(java.lang.String)
     */
    public final boolean containsHeader(String name) {
        return m_headers.containsKey(name);
    }

    
    /* (non-Javadoc)
     * @see com.wayfinder.pal.network.http.HttpMessage#getHeader(java.lang.String)
     */
    public final String getHeader(String name) {
        return m_headers.get(name);
    }

    
    /* (non-Javadoc)
     * @see com.wayfinder.pal.network.http.HttpMessage#setHeader(java.lang.String, java.lang.String)
     */
    public final void setHeader(String name, String value) {
        if(m_headers.containsKey(name)) {
            m_headers.remove(name);
        }
        addHeader(name, value);
    }
    
    
    //-------------------------------------------------------------------------
    // HttpEntityEnclosingRequest ifc
    
    
    /* (non-Javadoc)
     * @see com.wayfinder.pal.network.http.HttpEntityEnclosingRequest#setEntity(com.wayfinder.pal.network.http.HttpEntity)
     */
    public void setEntity(HttpEntity entity) {
        m_entity = entity;
    }
    
    
    /* (non-Javadoc)
     * @see com.wayfinder.pal.network.http.HttpEntityEnclosingRequest#getEntity()
     */
    public HttpEntity getEntity() {
        return m_entity;
    }
    
    
    
    //-------------------------------------------------------------------------
    // package stuff
    
    
    /**
     * Services the request.
     * 
     * @param host The {@link HttpHost} for the target server
     * @return An {@link HttpResponse} with the reply from the server
     * @throws IOException if something went wrong during the network
     * communication
     */
    final HttpResponse handleConnection(HttpHost host) throws IOException {
        // open the connection, this will resolve the URL as well
        URL url = new URL(host.toURI() + m_requestURI);
        Proxy prox = m_httpConfig.getProxy();
        
        HttpURLConnection conn;

        if(prox == Proxy.NO_PROXY) {
            conn = (HttpURLConnection) url.openConnection();
        } else {
            conn = (HttpURLConnection) url.openConnection(prox);
        }
        
        // set timeout values in millisecs. Default values are 0, which means "infinite"...
        conn.setConnectTimeout(10000); // to establish the connection
        conn.setReadTimeout(30000); // to read the resource
        
        // auto handling of redirects
        conn.setInstanceFollowRedirects(true);
        
        // override required headers
        //m_headers.set("Connection", m_httpConfig.isKeepAlive() ? "Keep-Alive" : "Close");

        // set method and headers
        conn.setRequestMethod(getRequestMethod());
        Set<Entry<String, String>> entrySet = m_headers.entrySet();
        for (Entry<String, String> entry : entrySet) {
            conn.addRequestProperty(entry.getKey(), entry.getValue());
        }
        
        if(m_entity != null) {
            // if we have an entity attached, send that information
            // set the connection in output mode
            conn.setDoOutput(true);
            
            // send extra headers
            String encoding = m_entity.getContentEncoding();
            if(encoding != null) {
                conn.setRequestProperty("Content-Encoding", encoding);
            }
            String type = m_entity.getContentType();
            if(type != null) {
                conn.setRequestProperty("Content-Type", type);
            }
            // we haz chunk?
            if(m_entity.isChunked()) {
                final long contentLength = m_entity.getContentLength();
                if(contentLength < 0) {
                    // unknown size, use streaming chunking
                    conn.setChunkedStreamingMode(-1); // -1 = default chunk size
                } else {
                    // known size, let the impl decide
                    conn.setFixedLengthStreamingMode((int)contentLength);
                }
            }
            // establish the actual connection
            conn.connect();
            OutputStream os = null;
            try {
                os = conn.getOutputStream();
                m_entity.writeTo(os);
            } finally {
                os.close();
            }
        } else {
            // simply connect
            conn.connect();
        }

        conn.getResponseCode(); // trigger transaction
        return new AndroidHttpResponse(conn, m_httpConfig);
    }
}
