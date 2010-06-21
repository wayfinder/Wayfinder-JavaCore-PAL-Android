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

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

public class AssetsPlayerSourceProvider implements PlayerSourceProvider {

	private final AssetManager assetMgr;
	
	public AssetsPlayerSourceProvider(Context context) {
		super();
		this.assetMgr = context.getAssets();
	}
	
	public void setDataSource(MediaPlayer player, String filePath)
			throws IOException {
		AssetFileDescriptor afd;
		
		afd = assetMgr.openFd(filePath);
		
		try {
			long len = afd.getLength();
	        if (len == AssetFileDescriptor.UNKNOWN_LENGTH) {
	        	throw new IOException("no length for fd");
	        }
	        player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), len);
		} finally {
			try {
				afd.close();
			} catch (IOException e) {
				//nothing to do
				Log.w("AssetsPlayerSourceProvider", "Error when closing FileDescriptor for "
						 + filePath, e);
			}
		}		
	}

	public int loadSound(SoundPool soundPool, String filePath) throws IOException {
		AssetFileDescriptor afd = assetMgr.openFd(filePath);
		int soundID = soundPool.load(afd, 0);
		afd.close();
		return soundID;
	}
}
