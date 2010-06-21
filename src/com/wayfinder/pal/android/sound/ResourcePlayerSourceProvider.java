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
package com.wayfinder.pal.android.sound;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

public class ResourcePlayerSourceProvider implements PlayerSourceProvider {
	
	private final Context m_context;
	private final Resources m_resources;
	private final SoundConfiguration m_config;

	public ResourcePlayerSourceProvider(Context context, SoundConfiguration config) {
		super();
		m_context = context;
		m_resources = context.getResources();
		m_config = config;
	}

	
	public void setDataSource(MediaPlayer player, String filePath)
			throws IOException {
		
		//FIXME:
		// Not sure if this should be done here, but otherwise we would have to modify the syntax file :P
		// Android resources can only handle files with lower case, otherwise exceptions for all
		filePath = filePath.toLowerCase();
		
		// In reality, looking up an identifier from a filename is not recommended but if we want to load based on the
		// name it's either that or using reflection from the R file
		// Not sure which is faster to do...
		int resId = m_resources.getIdentifier(filePath, m_config.getSubfolder(), m_context.getPackageName());
		if(resId == 0) {
			String errorMsg = "Could not locate: " + filePath + " in subfolder: " + m_config.getSubfolder()
                               + " for package: " + m_context.getPackageName();
			Log.e("ResourcePlayerSourceProvider", errorMsg);
			throw new FileNotFoundException(errorMsg);
		}
		
		AssetFileDescriptor afd = m_resources.openRawResourceFd(resId);
		try {
			player.reset();
			
			long len = afd.getLength();
	        if (len == AssetFileDescriptor.UNKNOWN_LENGTH) {
	        	throw new IOException("no length for fd");
	        }
	       
	        if (len > 8000) {
	        	Log.w("ResourcePlayerSourceProvider.setDataSource()", "len too big for " +filePath);
	        }
	        
	        player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), len);
		} finally {
			try {
				afd.close();
			} catch (IOException e) {
				//nothing to do
				Log.w("ResourcePlayerSourceProvider", "Error when closing FileDescriptor for "
						 + filePath + " in subfolder: " + m_config.getSubfolder()
                         + " for package: " + m_context.getPackageName(), e);
			}
		}
		
	}


	public int loadSound(SoundPool soundPool, String filePath)
			throws IOException {
		//FIXME:
		// Not sure if this should be done here, but otherwise we would have to modify the syntax file :P
		// Android resources can only handle files with lower case, otherwise exceptions for all
		filePath = filePath.toLowerCase();
		
		// In reality, looking up an identifier from a filename is not recommended but if we want to load based on the
		// name it's either that or using reflection from the R file
		// Not sure which is faster to do...
		int resId = m_resources.getIdentifier(filePath, m_config.getSubfolder(), m_context.getPackageName());
		if(resId == 0) {
			String errorMsg = "Could not locate: " + filePath + " in subfolder: " + m_config.getSubfolder()
                               + " for package: " + m_context.getPackageName();
			Log.e("ResourcePlayerSourceProvider", errorMsg);
			throw new FileNotFoundException(errorMsg);
		}
		
		int soundID = soundPool.load(m_context, resId, 0);
		return soundID;
	}

}
