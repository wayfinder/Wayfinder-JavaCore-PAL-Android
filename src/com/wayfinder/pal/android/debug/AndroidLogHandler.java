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
package com.wayfinder.pal.android.debug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

import android.text.format.DateFormat;
import android.util.Log;

import com.wayfinder.pal.debug.Level;
import com.wayfinder.pal.debug.LogHandler;
import com.wayfinder.pal.debug.LogMessage;

public final class AndroidLogHandler implements LogHandler {

	private volatile BufferedWriter fileOut;
	private final long startTime = System.currentTimeMillis();
	
    /**
     * Create a file where all the debug messages will be logged.
     * The location and name of the file is specific to each platform
     *  
     * <p>
     * WARNING: Debug message will be written directly to file, this can caused
     * small delays of any further writeMessageToPlatformLog()  
     */
	public void startFileLogging() {
		if (fileOut != null) return;
    	File file = new File("/sdcard/" + "core" + DateFormat.format("yyyyMMdd-kkmmss",startTime) + ".log");
    	BufferedWriter  out;
    	try {
    		if (file.createNewFile()) {
    			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
    			out.write(new Date().toString());
    			out.newLine();
    			fileOut = out; 
    		}
    	} catch (Exception e) {
			Log.e("AndroidLogHandler", "error when start log file", e);
		}
	}
	
    public void writeMessageToPlatformLog(LogMessage message) {
        String messageStr;
        switch(message.getType()) {
        case LogMessage.TYPE_MESSAGE:
            messageStr = message.getMessage();
            break;
            
        case LogMessage.TYPE_EXCEPTION:
            messageStr = Log.getStackTraceString(message.getThrowable());
            break;
            
        default:
            messageStr = ">>>UNKNOWN LOGMESSAGE TYPE<<<";
            break;
        }
        Log.println(getLogLevelFor(message.getLevel()), 
                                   message.getMethodName(), 
                                   messageStr);
        
        if (fileOut != null) {
        	long time = (System.currentTimeMillis() - startTime)/1000;
        	try {
				fileOut.write(String.valueOf(time));
				fileOut.write('\t');
				fileOut.write(message.getLevel().toString());
				fileOut.write('\t');
				fileOut.write(message.getMethodName());
				fileOut.write(':');
				fileOut.write(messageStr);
				fileOut.newLine();
				fileOut.flush();
			} catch (Exception e) {
				fileOut = null;
				Log.e("AndroidLogHandler", "error when write to log file", e);
			}
        }
        
    }
    
    
    private static int getLogLevelFor(Level level) {
        switch(level.getIntValue()) {
        case Level.VALUE_TRACE:
            return Log.VERBOSE;
            
        case Level.VALUE_DEBUG:
            return Log.DEBUG;
                
        case Level.VALUE_INFO:
            return Log.INFO;
                
        case Level.VALUE_WARN:
            return Log.WARN;
            
        case Level.VALUE_ERROR:
            return Log.ERROR;
            
        case Level.VALUE_FATAL:
            return Log.ASSERT; // ?
        }
        return Log.VERBOSE;
    }
    
    

}
