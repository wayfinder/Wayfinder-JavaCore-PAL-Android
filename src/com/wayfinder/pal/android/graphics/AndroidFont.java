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

import com.wayfinder.pal.graphics.WFFont;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetricsInt;


final class AndroidFont implements WFFont {
    
    private final Paint iFontPaint;
    private final Typeface iFontFace;
    private final FontMetricsInt iFontMetrics;
    
    private static final int FONT_SIZE_SMALL_PIXELS = 12;
    private static final int FONT_SIZE_MEDIUM_PIXELS = 15;
    private static final int FONT_SIZE_LARGE_PIXELS = 18;
    private static final int FONT_SIZE_VERY_LARGE_PIXELS = 25;
    
    AndroidFont(int size, int style, float density) {
        
        // set info in paint object
        iFontPaint = new Paint();
        iFontPaint.setTextSize(density * (float)convertToPixelSize(size));
        
        // set typeface
        final int andStyle;
        if (style != WFFont.STYLE_PLAIN) {
            final boolean isBold = (style & WFFont.STYLE_BOLD) == WFFont.STYLE_BOLD;
            final boolean isItalic = (style & WFFont.STYLE_ITALIC) == WFFont.STYLE_ITALIC;
            final boolean isUnderlined = (style & WFFont.STYLE_UNDERLINE) == WFFont.STYLE_UNDERLINE;
            if (isBold && isItalic) {
                andStyle = Typeface.BOLD_ITALIC;
            } else if (isBold) {
                andStyle = Typeface.BOLD;
            } else if (isItalic) {
                andStyle = Typeface.ITALIC;
            } else {
                andStyle = Typeface.NORMAL;
            }
            iFontPaint.setUnderlineText(isUnderlined);
        } else {
            andStyle = Typeface.NORMAL;
        }
        
        iFontFace = Typeface.create(Typeface.SANS_SERIF, andStyle);
        iFontPaint.setTypeface(iFontFace);
        iFontPaint.setAntiAlias(true);
        iFontMetrics = iFontPaint.getFontMetricsInt();
    }
    
    
    /**
     * Used to set default font
     * @param aPaint
     */
    AndroidFont(Paint aPaint) {
        iFontPaint = new Paint(aPaint);
        iFontFace = iFontPaint.getTypeface();
        iFontMetrics = iFontPaint.getFontMetricsInt();
    }
    
    private static int convertToPixelSize(int fontSize) {
        switch (fontSize) {
	        case WFFont.SIZE_SMALL:       return FONT_SIZE_SMALL_PIXELS;
	        case WFFont.SIZE_MEDIUM:      return FONT_SIZE_MEDIUM_PIXELS;
	        case WFFont.SIZE_LARGE:       return FONT_SIZE_LARGE_PIXELS;
	        case WFFont.SIZE_VERY_LARGE:  return FONT_SIZE_VERY_LARGE_PIXELS;
        }
        throw new IllegalArgumentException("WFFont size " + fontSize + " is not a proper size");
    }

    
    public int getBaselinePosition() {
        return (int) (iFontPaint.getTextSize() - iFontMetrics.ascent);
    }

    public int getFontHeight() {
        return (int) iFontPaint.getTextSize();
    }

    public int getStringWidth(String aStr) {
        return (int) iFontPaint.measureText(aStr);
    }

    public int getStyle() {
        int wfstyle = 0;
        if(iFontFace.isBold()) {
            wfstyle |= STYLE_BOLD;
        }
        if(iFontPaint.isUnderlineText()) {
            wfstyle |= STYLE_UNDERLINE;
        }
        if(iFontFace.isItalic()) {
            wfstyle |= STYLE_ITALIC;
        }
        return wfstyle;
    }
    
    
    Paint getPaintObject() {
        return iFontPaint;
    }
}
