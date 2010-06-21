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

import java.text.Collator;
import java.util.Locale;

import com.wayfinder.pal.util.StringCollator;

/**
 *
 */
public class AndroidStringCollator implements StringCollator {
    
    private static AndroidStringCollator INSTANCE = null;
    
    private Locale m_loc;
    private Collator m_collator;
    
    /**
     * Obtains a {@link StringCollator} set-up to use the default {@link Locale}
     * of the system for string comparisons
     * 
     * @param collationStrength the comparison level, one of 
     * {@link StringCollator#STRENGTH_PRIMARY}, 
     * {@link StringCollator#STRENGTH_SECONDARY}, or
     * {@link StringCollator#STRENGTH_TERTIARY}
     * 
     * @return a {@link StringCollator}
     */
    public static AndroidStringCollator get(int collationStrength) {
        return get(Locale.getDefault(), collationStrength);
    }
    
    /**
     * Obtains a {@link StringCollator} set-up to use the specified {@link Locale} for
     * string comparisons.
     * 
     * @param loc the {@link Locale}
     * @param collationStrength the comparison level, one of 
     * {@link StringCollator#STRENGTH_PRIMARY}, 
     * {@link StringCollator#STRENGTH_SECONDARY}, or
     * {@link StringCollator#STRENGTH_TERTIARY}
     * 
     * @return a {@link StringCollator}
     */
    public static AndroidStringCollator get(Locale loc, int collationStrength) {
        if (INSTANCE == null
                || !INSTANCE.m_loc.equals(loc)
                || INSTANCE.m_collator.getStrength() != mapCollationStrength(collationStrength)) {
            INSTANCE = new AndroidStringCollator(loc, collationStrength);
        }
        return INSTANCE;
    }
    
    private AndroidStringCollator(Locale locale, int collationStrength) {
        m_loc = locale;
        m_collator = Collator.getInstance(locale);
        m_collator.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
        m_collator.setStrength(mapCollationStrength(collationStrength));
    }

    /* (non-Javadoc)
     * @see com.wayfinder.pal.util.StringCollator#compare(java.lang.String, java.lang.String)
     */
    public int compare(String str1, String str2) {
        return m_collator.compare(str1, str2);
    }

    /**
     * Maps the strength values from the Core PAL interface to the ones in 
     * {@link Collator}. The level {@link Collator#IDENTICAL} is not used.
     * The default level is {@link Collator#SECONDARY}.
     * 
     * @param palValue the value from the PAL interface
     * @return the corresponding strength constant from {@link Collator}
     */
    private static int mapCollationStrength(int palValue) {
        switch (palValue) {
        case StringCollator.STRENGTH_PRIMARY:
            return Collator.PRIMARY;
            
        case StringCollator.STRENGTH_SECONDARY:
            return Collator.SECONDARY;
            
        case StringCollator.STRENGTH_TERTIARY:
            return Collator.TERTIARY;
            
        default:
            return Collator.SECONDARY;
        }
    }
}
