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
package com.wayfinder.pal.android.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import android.content.Context;
import android.content.res.Configuration;

import com.wayfinder.pal.util.StringCollator;
import com.wayfinder.pal.util.UtilFactory;

public class AndroidUtilFactory implements UtilFactory{

    private final Context m_context;
    public AndroidUtilFactory(Context c) {
        m_context = c;
    }
	/*
	 * (non-Javadoc)
	 * @see com.wayfinder.pal.util.UtilFactory#openGZIPInputStream(java.io.InputStream)
	 */
	public InputStream openGZIPInputStream(InputStream stream) throws IOException {
		return new GZIPInputStream(stream);
	}

	/*
	 * (non-Javadoc)
	 * @see com.wayfinder.pal.util.UtilFactory#openGZIPOutputStream(java.io.OutputStream)
	 */
	public OutputStream openGZIPOutputStream(OutputStream stream) throws IOException {
		return new GZIPOutputStream(stream);
	}
	
    /* (non-Javadoc)
     * @see com.wayfinder.pal.util.UtilFactory#getStringCollator()
     */
    public StringCollator getStringCollator(int collationStrength) {
        Configuration conf = m_context.getResources().getConfiguration();
        if (conf.locale != null) {
            return AndroidStringCollator.get(conf.locale, collationStrength);
        }
        return AndroidStringCollator.get(collationStrength);
    }
    
}
