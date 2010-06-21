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
package com.wayfinder.pal.android.softwareinfo;

import java.util.Locale;

import android.content.Context;
import android.content.res.Configuration;

import com.wayfinder.pal.softwareinfo.SoftwareInfo;

public final class AndroidSoftwareInfo implements SoftwareInfo {
    
    private final Locale m_locale;
    
    public AndroidSoftwareInfo(Context c) {
        Configuration conf = c.getResources().getConfiguration();
        if(conf.locale != null) {
            m_locale = conf.locale;
        } else {
            m_locale = Locale.getDefault();
        }
    }
    
    
    public int getLanguageType() {
        if(m_locale == null) {
            return LANGUAGE_NONE;
        }
        return LANGUAGE_ISO_639_2;
    }
    
    
    public String getLanguage() {
        if(m_locale == null) {
            throw new IllegalStateException("Unable to determine language");
        }
        return m_locale.getISO3Language();
    }
    
    
    public int getCountryType() {
        if(m_locale == null) {
            return COUNTRY_NONE;
        }
        return COUNTRY_ISO_3166_1_ALPHA_3;
    }
    

    public String getCountry() {
        if(m_locale == null) {
            throw new IllegalStateException("Unable to determine country");
        }
        return m_locale.getISO3Country();
    }


}
