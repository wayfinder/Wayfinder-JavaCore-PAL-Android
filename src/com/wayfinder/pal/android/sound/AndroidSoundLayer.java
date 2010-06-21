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

import com.wayfinder.pal.sound.SoundException;
import com.wayfinder.pal.sound.SoundLayer;
import com.wayfinder.pal.sound.SoundPlayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class AndroidSoundLayer implements SoundLayer {

	private final PlayerSourceProvider provider;
	private final SoundConfiguration m_config;
	
	public AndroidSoundLayer(Context context) {
		super();
		m_config = new SoundConfiguration(SoundConfiguration.LOCATED_RES, "raw");
		if(m_config.getLocation() == SoundConfiguration.LOCATED_RES) {
			provider = new ResourcePlayerSourceProvider(context, m_config);
		} else {
			provider = new AssetsPlayerSourceProvider(context);
		}
	}

	public SoundPlayer create(String filePath, int duration) {
		return new AndroidPoolSingleSoundPlayer(filePath, duration, provider);
		//return new AndroidSoundPlayer(filePath, provider);
	}

	public SoundPlayer create(String[] filePaths, int[] durations) {
		if (filePaths.length == 0) {
			throw new IllegalArgumentException(
					"filePaths parmeters cannot be empty");
		}
		if (filePaths.length == 1) {
			return new AndroidPoolSingleSoundPlayer(filePaths[0],durations[0], provider);
			//return new AndroidSoundPlayer(filePaths[0],provider);
		}
		
		return new AndroidPoolSoundPlayer(filePaths, durations, provider);
		//return new AndroidSequenceSoundPlayer(filePaths,provider);
	}

	public int[] getDuration(String[] filePaths) throws SoundException {
		int[] result = new int[filePaths.length];
		MediaPlayer player = new MediaPlayer();
		try {	
			for (int i=0;i<filePaths.length;i++) {
				try {
					provider.setDataSource(player, filePaths[i]);
					player.prepare();
					result[i] = player.getDuration();
					Log.d("AndroidSoundLayer.getDuration()" , "h.add(\"" + filePaths[i] + "\"," + result[i]+ ");");
				} catch (IOException ex){
					result[i] = 0;
					Log.e("AndroidSoundLayer.getDuration()", "Error when preparing " + filePaths[i],  ex);
				}
				player.reset();
			}
		} finally {
			player.release();
		}
		return result;
	}
	
}
