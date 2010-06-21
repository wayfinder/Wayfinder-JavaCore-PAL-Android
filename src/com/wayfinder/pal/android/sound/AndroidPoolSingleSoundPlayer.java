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

public class AndroidPoolSingleSoundPlayer implements SoundPlayer {

	private final String filePath;
	private final int duration;
	private int soundID;
	private final PlayerSourceProvider sourceProvider;
	
	private SoundPool soundPool;
	
	int totalDuration;
	
	/**
	 * @param filePaths have at least two elements
	 * @param durations the durations for each sound the array must be the same 
	 * length as filePaths and have real data. 
	 * @param provider
	 */
	public AndroidPoolSingleSoundPlayer(String filePath, int duration, 
			PlayerSourceProvider provider) {
		if (filePath == null) {
			throw new IllegalArgumentException("The ctr parameters must be not null"); 
		}
		
		this.duration = duration;
		this.filePath = filePath;
		this.sourceProvider = provider;
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
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		if (duration <= 0) {
			throw new SoundException("Inccorect duration for " + filePath);
		}
		try {
			soundID = sourceProvider.loadSound(soundPool, filePath);
		} catch (IOException ex) {
			throw new SoundException("Cannot load sound " +filePath + ex);
		}
		if (soundID == 0) {
			throw new SoundException("Cannot load sound " +filePath);
		}

		// give enough time to prepare the sound
		// the load time for sound is from 20 to 200ms
		Thread.sleep(500);
	}
	
	public void play() throws SoundException, InterruptedException {
		if (soundPool == null) return;
		try {
			if (soundPool.play(soundID, 1, 1, 0, 0, 1) == 0) {
				//try twice as the sound maybe is not ready;
				Log.w("AndroidPoolSingleSoundPlayer.play()", "Sound " + filePath + " not ready wait 1s");
				Thread.sleep(1000);
				if (soundPool.play(soundID, 1, 1, 0, 0, 1) == 0) {
					throw new SoundException("Sound not ready " +
							filePath);
				}
			}
			Thread.sleep(duration);
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
		return "AndroidPoolSingleSoundPlayer " + filePath;
	}
}
