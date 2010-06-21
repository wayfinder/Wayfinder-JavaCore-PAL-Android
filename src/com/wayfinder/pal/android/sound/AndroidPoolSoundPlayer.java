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

import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.wayfinder.pal.sound.SoundException;
import com.wayfinder.pal.sound.SoundPlayer;

public class AndroidPoolSoundPlayer implements SoundPlayer {

	private final String[] filePaths;
	private final int[] durations;
	private final int[] soundIDs;
	private final PlayerSourceProvider sourceProvider;
	
	private SoundPool soundPool;
	
	int totalDuration;
	
	/**
	 * @param filePaths have at least two elements
	 * @param durations the durations for each sound the array must be the same 
	 * length as filePaths and have real data. 
	 * @param provider
	 */
	public AndroidPoolSoundPlayer(String[] filePaths, int[] durations, 
			PlayerSourceProvider provider) {
		if (filePaths == null || durations == null) {
			throw new IllegalArgumentException("The ctr parameters must be not null"); 
		}
		
		if (filePaths.length != durations.length) {
			throw new IllegalArgumentException("The arrays must have same size"); 
		}
		
		this.durations = durations;
		this.filePaths = filePaths;
		this.sourceProvider = provider;
		this.soundIDs = new int[filePaths.length];
	}

	/**
	 * this will return a relative time as the total duration is unknown until 
	 * we prepare all files
	 */
	public int getDuration() {
		return totalDuration; 
	}

	public void prepare() throws SoundException, InterruptedException {
		if (soundPool != null) return;
		soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		for(int i = 0; i < filePaths.length; i++) {
			if (durations[i] <= 0) {
				throw new SoundException("Inccorect duration for " + filePaths[i]);
			}
			try {
				soundIDs[i] = sourceProvider.loadSound(soundPool, filePaths[i]);
			} catch (IOException ex) {
				throw new SoundException("Cannot load sound " +filePaths[i] + ex);
			}
			if (soundIDs[i] == 0) {
				throw new SoundException("Cannot load sound " +filePaths[i]);
			}
			totalDuration += durations[i];
		}
		// give enough time to prepare the first sound
		// the load time for sound is from 20 to 200ms
		Thread.sleep(500);
	}
	
	public void play() throws SoundException, InterruptedException {
		if (soundPool == null) return;
		try {
			for (int i=0; i < soundIDs.length; i++) {
				if (soundPool.play(soundIDs[i], 1, 1, 0, 0, 1) == 0) {
					if (i==0) {
						Log.w("AndroidPoolSingleSoundPlayer.play()", "Sound " + filePaths[i] + " not ready wait 1s");						
						Thread.sleep(1000);
						if (soundPool.play(soundIDs[i], 1, 1, 0, 0, 1) == 0) {
							throw new SoundException("First sound not ready " +
									filePaths[i]);
						}
					} else {
						throw new SoundException("Sound not ready " +
								filePaths[i]);
					}
				}
				Thread.sleep(durations[i]);
			}
		} finally {
			unprepare();
		}
	}
	
	public void unprepare() {
		if (soundPool == null) return;
		soundPool.release();
		soundPool = null;
	}
	
	public String toString() {
		return "AndroidPoolSoundPlayer " + filePaths.length + " : " +filePaths[0];
	}
}
