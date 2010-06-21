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
package com.wayfinder.pal.android.graphics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.Config;

import com.wayfinder.pal.graphics.WFFont;
import com.wayfinder.pal.graphics.WFGraphicsFactory;
import com.wayfinder.pal.graphics.WFImage;

public final class AndroidGraphicsFactory implements WFGraphicsFactory {
	
	private float m_density;
    
    public AndroidGraphicsFactory(float density) {
    	m_density = density;
    }
    
    /**
     * Create a WFImage from a Bitmap image. 
     * 
     * @param bit 
     * @return a WFImage
     */
    public WFImage createWFImage(Bitmap bit) {
        return new AndroidImage(bit);
    }
    
    /*
     * (non-Javadoc)
     * @see com.wayfinder.pal.graphics.WFGraphicsFactory#createWFImage(byte[], int, int)
     */
    public WFImage createWFImage(byte[] aBuf, int aOffset, int aLength) {
        Bitmap b = BitmapFactory.decodeByteArray(aBuf, aOffset, aLength);
        return new AndroidImage(b);
    }

    /*
     * (non-Javadoc)
     * @see com.wayfinder.pal.graphics.WFGraphicsFactory#createWFImage(int, int)
     */
    public WFImage createWFImage(int aWidth, int aHeight) {
        Bitmap b = Bitmap.createBitmap(aWidth, aHeight, Config.ARGB_8888);
        b.eraseColor(Color.WHITE);        
        return new AndroidImage(b);
    }

    /*
     * (non-Javadoc)
     * @see com.wayfinder.pal.graphics.WFGraphicsFactory#createWFImage(int, int, int)
     */
    public WFImage createWFImage(int aWidth, int aHeight, int aColor) {
        Bitmap b = Bitmap.createBitmap(aWidth, aHeight, Config.ARGB_8888);
        b.eraseColor(aColor);
        return new AndroidImage(b);
    }

    /*
     * (non-Javadoc)
     * @see com.wayfinder.pal.graphics.WFGraphicsFactory#createWFImage(java.lang.String)
     */
    public WFImage createWFImage(String aResourceName) {
        Bitmap b = BitmapFactory.decodeFile(aResourceName);
        return new AndroidImage(b);
    }

    /*
     * (non-Javadoc)
     * @see com.wayfinder.pal.graphics.WFGraphicsFactory#createWFImage(int[], int, int, boolean)
     */
    public WFImage createWFImage(int[] aRgb, int aWidth, int aHeight, boolean aProcessAlpha) {
        Bitmap b = Bitmap.createBitmap(aRgb, aWidth, aHeight, Config.ARGB_8888);
        return new AndroidImage(b);
    }

    /*
     * (non-Javadoc)
     * @see com.wayfinder.pal.graphics.WFGraphicsFactory#getWFFont(int, int)
     */
    public WFFont getWFFont(int aSize, int aStyle)
            throws IllegalArgumentException {
        return new AndroidFont(aSize, aStyle, m_density);
    }
}
