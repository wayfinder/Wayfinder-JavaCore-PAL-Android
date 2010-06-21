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
import com.wayfinder.pal.graphics.WFGraphics;
import com.wayfinder.pal.graphics.WFImage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

public final class AndroidGraphics implements WFGraphics {
    
    private final Paint m_paint;
	private final Paint m_fillPaint;
	private final Paint m_strokePaint;
	
    private Canvas m_canvas;
    
    private AndroidFont m_font;
    private int m_restoreCount;
    
    private Path m_fillTrianglePath = new Path();
    private Path m_fillPolygonPath = new Path();
    private Path m_drawPolygonPath = new Path();
    private Rect m_drawRect = new Rect();
    private Rect m_fillRect = new Rect();
    private Path m_drawPath = new Path();
	private int m_lastColor = -2;
    
	private final Matrix m_matrix = new Matrix();

	public AndroidGraphics(Bitmap bitmap) {
        this(new Canvas(bitmap));
    }
    
    public AndroidGraphics(Canvas canvas) {
        m_paint = new Paint();
        m_paint.setAntiAlias(true);
        m_fillPaint = new Paint(m_paint);
        m_fillPaint.setStyle(Paint.Style.FILL);
        m_strokePaint = new Paint(m_paint);
        m_strokePaint.setStyle(Paint.Style.STROKE);
        m_strokePaint.setStrokeCap(Paint.Cap.ROUND);
        m_font = new AndroidFont(m_paint);
        m_canvas = canvas;
    }
    
    /**
     * Sets the canvas
     * 
     * @param canvas - the canvas
     */
    public void setCanvas(Canvas canvas) {
    	m_canvas = canvas;
    }
    
    //-------------------------------------------------------------------------
    // Images
    
    
    void drawBitmap(Bitmap bitmap, int x, int y) {
        m_canvas.drawBitmap(bitmap, x, y, null);
    }
    
    public void drawImage(WFImage img, int x, int y, int anchor) {
        Object obj = img.getNativeImage();
        if(obj instanceof Bitmap) {
            Bitmap b = (Bitmap) obj;
            int width = b.getWidth();
            if ( (anchor & ANCHOR_RIGHT) == ANCHOR_RIGHT ) {
                x -= width;
            } else if ( (anchor & ANCHOR_HCENTER) == ANCHOR_HCENTER ) {
                x -= width / 2;
            }
            int height = b.getHeight();
            if ( (anchor & ANCHOR_BOTTOM) == ANCHOR_BOTTOM ) {
                y -= height;
            } else if ( (anchor & ANCHOR_VCENTER) == ANCHOR_VCENTER ) {
                y -= height / 2;
            }
            drawBitmap(b, x, y);
        }
    }
    
    
    public void drawRGB(int[] rgbData, int offset, int scanlength, int x,
            int y, int width, int height, boolean processAlpha) {
        
        m_canvas.drawBitmap(rgbData, offset, scanlength, x, y, width, height, processAlpha, m_paint);
    }
    
    
    //-------------------------------------------------------------------------
    // Lines
    
    
    public void drawLine(int x1, int y1, int x2, int y2, int thickness) {
    	final Paint paint = m_strokePaint;
        paint.setStrokeWidth(thickness);
        m_canvas.drawLine(x1, y1, x2, y2, paint);
        paint.setStrokeWidth(1);
    }
    
    public void drawConnectedLine(int x, int y, int thickness) {
    	// Used for redline navigation. 
    }

    public boolean supportsPath() {
        return true;
    }
    
    public void drawPath(int[] xCoords, int[] yCoords, int nbrCoords, int width) {
    	final Path path = m_drawPath;
        path.reset();
        path.moveTo(xCoords[0], yCoords[0]);
        for (int i = 1; i < nbrCoords; i++) {
            path.lineTo(xCoords[i], yCoords[i]);
        }
        final Paint paint = m_strokePaint;
        paint.setStrokeWidth(width);
        m_canvas.drawPath(path, paint);
        paint.setStrokeWidth(1);
    }
    
//	//-------------------------------------------------------------------------
//	// Transforms
//
//    public boolean supportsTransforms() {
//        return false;
//    }
//
//    public void setTransformMatrix(float[] matrix) {
//    	m_matrix.setValues(matrix);
//    }
//
//    private float[] m_floats = new float[128];
//    public void transform(int[] points, int length) {
//    	float[] floats = m_floats;
//    	for (int i = 0; i < length; i++) {
//    		floats[i] = points[i];
//    	}
//    	m_matrix.mapPoints(floats, 0, floats, 0, length);
//    	for (int i = 0; i < length; i++) {
//    		points[i] = (int)floats[i];
//    	}
//    }
//
//    public void fillPolygonT(int[] points, int length) {
//    	final Path path = m_fillPolygonPath;
//        path.reset();
//        path.moveTo(points[0], points[1]);
//        for (int i = 2; i < length; i += 2) {
//            path.lineTo(points[i], points[i + 1]);
//        }
//        path.close();
//        path.transform(m_matrix);
//        m_canvas.drawPath(path, m_fillPaint);
//    }

    //-------------------------------------------------------------------------
    // Shapes
    
    public boolean supportsPolygon() {
        return true;
    }
    
    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
    	final Path path = m_fillTrianglePath;
    	path.reset();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.close();
        m_canvas.drawPath(path, m_fillPaint);
    }
    
    public void fillPolygon(int[] xPts, int[] yPts, int length) {
    	final Path path = m_fillPolygonPath;
        path.reset();
        path.moveTo(xPts[0], yPts[0]);
        for (int i = 1; i < length; i++) {
            path.lineTo(xPts[i], yPts[i]);
        }
        path.close();
        m_canvas.drawPath(path, m_fillPaint);
    }
    
    public void drawPolygon(int[] xPts, int[] yPts) {
    	final Path path = m_drawPolygonPath;
        path.reset();
        path.moveTo(xPts[0], yPts[0]);
        final int len = yPts.length;
        for (int i = 1; i < len; i++) {
            path.lineTo(xPts[i], yPts[i]);
        }
        path.close();
        m_canvas.drawPath(path, m_strokePaint);
    }
    
    public void drawRect(int x, int y, int width, int height) {
    	final Rect rect = m_drawRect;
        rect.set(x, y, x + width, y + height);
        m_canvas.drawRect(rect, m_strokePaint);
    }
    
    public void fillRect(int x, int y, int width, int height) {
    	final Rect rect = m_fillRect;
        rect.set(x, y, x + width, y + height);
        m_canvas.drawRect(rect, m_fillPaint);
    }
    
    //-------------------------------------------------------------------------
    // Text
    
    
    public void drawText(String str, int x, int y, int anchor) {
        drawText(str, x, y, Integer.MAX_VALUE, anchor, null);
    }

    public void drawText(String str, int x, int y, int maxWidth, int anchor) {
        drawText(str, x, y, maxWidth, anchor, null);
    }

    public void drawText(String str, int x, int y, int maxWidth, int anchor, String suffix) {
    	Paint fontPaint = new Paint(m_font.getPaintObject());
        fontPaint.setColor(m_paint.getColor());
        
        Paint.Align al;
        if((anchor & ANCHOR_LEFT) != 0) {
            al = Paint.Align.LEFT;
        } else if((anchor & ANCHOR_RIGHT) != 0) {
            al = Paint.Align.RIGHT;
        } else if((anchor & ANCHOR_HCENTER) != 0) {
            al = Paint.Align.CENTER;
        }  else {
            throw new IllegalArgumentException("Anchor was illegal");
        }
        fontPaint.setTextAlign(al);
        
        y += fontPaint.getTextSize();
        
        final int strWidth = m_font.getStringWidth(str);        
        if(strWidth > maxWidth) {
        	if(suffix != null) {
        		maxWidth -= m_font.getStringWidth(suffix);
        		if(maxWidth <= 0)
        			throw new IllegalArgumentException("maxWidth are less then the size of the suffix!");
        		final int nbrOfChars = fontPaint.breakText(str, true, maxWidth, null);
        		final String newStr = str.substring(0, nbrOfChars).trim()+suffix;
        		m_canvas.drawText(newStr, x, y, fontPaint);        		
        	} else {
        		final int nbrOfChars = fontPaint.breakText(str, true, maxWidth, null);
        		m_canvas.drawText(str, 0, nbrOfChars, x, y, fontPaint);
        	}        	
        } else {
        	m_canvas.drawText(str, 0, str.length(), x, y, fontPaint);	
        }        
    }
    
    public void drawRotatedText(String str, int x, int y, double tanTheta) {
    	Paint fontPaint = m_font.getPaintObject();
    	fontPaint.setColor(m_paint.getColor());
    	y += fontPaint.getTextSize() / 2;
    	
    	final double ang = Math.atan(tanTheta);
    	final float angle = (float)Math.toDegrees(ang);
    	
    	m_canvas.rotate(angle, x, y);
        m_canvas.drawText(str, 0, str.length(), x-m_font.getStringWidth(str)/2, y, fontPaint);
        m_canvas.rotate(-angle, x, y);      
    }
    
    public boolean supportRotatedTexts() {
    	return true;
    }
        
    public void setFont(WFFont font) {
        if(font instanceof AndroidFont) {
            m_font = (AndroidFont) font;
        }
    }

    //-------------------------------------------------------------------------
    // Clip
    
    public int getClipHeight() {
        Rect r = new Rect();
        if(m_canvas.getClipBounds(r)) {
            return r.height();
        }
        return m_canvas.getHeight();
    }

    public int getClipWidth() {
        Rect r = new Rect();
        if(m_canvas.getClipBounds(r)) {
            return r.width();
        }
        return m_canvas.getWidth();
    }

    public int getClipX() {
        return m_canvas.getClipBounds().left;
    }

    public int getClipY() {
        return m_canvas.getClipBounds().top;
    }
    
    
    public void setClip(int x, int y, int width, int height) {
        // same drill as on BlackBerry, context stack
        m_canvas.restoreToCount(m_restoreCount);
        m_restoreCount = m_canvas.save(Canvas.CLIP_SAVE_FLAG);
        Rect r = new Rect(x, y, x + width, y + height);
        m_canvas.clipRect(r);
    }
    
    
    //-------------------------------------------------------------------------
    // Misc
    
    public void allowAntialias(boolean aAllow) {
        m_paint.setAntiAlias(aAllow);
        m_fillPaint.setAntiAlias(aAllow);
        m_strokePaint.setAntiAlias(aAllow);
    }


    public int getColor() {
        // color in android includes alpha, but the interfaces specifies
        // an alphaless value. 
        final int andColor = m_paint.getColor();
        return Color.argb(
                0,
                Color.red(andColor), 
                Color.green(andColor),
                Color.blue(andColor));
    }

    public void setColor(int color) {
    	if (color != m_lastColor) {
    		m_lastColor  = color;
            // color in android includes alpha, but the interfaces specifies
            // an alphaless value. Since the top bit usually are 0, we have to
            // convert it to a color or we get a transparent color... >_<
            final int andColor = Color.rgb(Color.red(color), 
                                           Color.green(color),
                                           Color.blue(color));
            m_paint.setColor(andColor);
            m_fillPaint.setColor(andColor);
            m_strokePaint.setColor(andColor);
    	}
    }
}
