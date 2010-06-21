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

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

import com.wayfinder.pal.sound.SoundException;
import com.wayfinder.pal.sound.SoundPlayer;

/**
 * @deprecated as this can cause playing of all sounds in the resources
 * on some devices HTC Click and SEMC Rachel firmware R1AA035 
 */
public class AndroidSoundPlayer implements SoundPlayer, OnErrorListener, OnCompletionListener {

	private final String filePath;
	private final PlayerSourceProvider sourceProvider;
	private MediaPlayer player;
	private boolean played = false; 
	private SoundException exceptionWhilePLay;
	
	public AndroidSoundPlayer(String filePath, PlayerSourceProvider sourceProvider) {
		this.filePath = filePath;
		this.sourceProvider = sourceProvider;
	}
	
	public void destroy() {
		notifyFinish();
		player.release();
	}

	public int getDuration() {
		return player.getDuration();
	}

	public void prepare() throws SoundException {
		if (player != null) return;
		
		player = new MediaPlayer();
		try {
			sourceProvider.setDataSource(player, filePath);	
			player.prepare();
		} catch (IOException e) {
			throw new SoundException("Could not prepare sound " + filePath, e);
			
		}
	}

	public boolean onError(MediaPlayer mp, int what, int extra) {
		exceptionWhilePLay = new SoundException("Play error " + what +":" + extra); 
		notifyFinish();
		return true;
	}

	public void onCompletion(MediaPlayer mp) {
		notifyFinish();
	}

	public void play() throws SoundException, InterruptedException {
		if (player == null) return;
		try {
			player.setOnCompletionListener(this);
			player.setOnErrorListener(this);
			player.start();
			waitToFinish();
		} finally {
			unprepare();
		}
		if (exceptionWhilePLay != null) throw exceptionWhilePLay;
	}

	private synchronized void waitToFinish() throws InterruptedException {
		while (!played) {
			wait();
		}
	}
	private synchronized void notifyFinish() {
		played = true;
		notifyAll();
	}

	public void unprepare() {
		if (player == null) return;
		player.release();
		player = null;
	}
	public String toString() {
		return "AndroidSoundPlayer : " +filePath;
	}
}
