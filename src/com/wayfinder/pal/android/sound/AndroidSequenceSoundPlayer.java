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
 * 
 * @deprecated as the listeners on MediaPlayer are called from the main event 
 * queue this can cause delays between sounds on heavy loads  
 */
public class AndroidSequenceSoundPlayer implements SoundPlayer, OnErrorListener, OnCompletionListener {

	private final String[] filePaths;
	private final PlayerSourceProvider sourceProvider;
	
	private MediaPlayer playerPlay;
	private MediaPlayer playerPrepare;
	
	boolean playing = false;
	
	private SoundException exceptionWhilePLay;
	
	int duration;
	
	/**
	 * 
	 * @param filePaths have at least two elements
	 * @param provider
	 */
	public AndroidSequenceSoundPlayer(String[] filePaths, 
			PlayerSourceProvider provider) {
		this.filePaths = filePaths;
		this.sourceProvider = provider;
	}

	/**
	 * this will return a relative time as the total duration is unknown until 
	 * we prepare all files
	 */
	public int getDuration() {
		return duration; 
	}

	public void prepare() throws SoundException {
		if (playerPlay != null || playerPrepare != null) return;
		
		playerPlay = new MediaPlayer();
		playerPrepare = new MediaPlayer();

		//prepare first sound only;
		duration = 0;
		prepareSingle(playerPrepare, filePaths[0]);
		prepareSingle(playerPlay, filePaths[1]);
	}
	
	public void play() throws SoundException, InterruptedException {
		if (playerPlay == null || playerPrepare == null) return;

		playerPlay.setOnErrorListener(this);
		playerPlay.setOnCompletionListener(this);
		playerPrepare.setOnErrorListener(this);
		playerPrepare.setOnCompletionListener(this);

		setStartPlaying();
		playerPrepare.start();
		waitToFinishPlay();
		if (exceptionWhilePLay != null)
			throw exceptionWhilePLay;
		try {
			for (int i = 2; i < filePaths.length; i++) {
				setStartPlaying();
				playerPlay.start();
				prepareSingle(playerPrepare, filePaths[i]);
				waitToFinishPlay();
				if (exceptionWhilePLay != null)
					throw exceptionWhilePLay;

				MediaPlayer w = playerPlay;
				playerPlay = playerPrepare;
				playerPrepare = w;
			}

			// play last file
			setStartPlaying();
			playerPlay.start();
			waitToFinishPlay();
			if (exceptionWhilePLay != null)
				throw exceptionWhilePLay;
		} finally {
			unprepare();
		}
	}
	
	private void prepareSingle(MediaPlayer player, String filePath) throws SoundException {
		try {
			player.reset();
			sourceProvider.setDataSource(player, filePath);	
			player.prepare();
			duration += player.getDuration();
		} catch (IOException e) {
			throw new SoundException("Could not prepare sound " + filePath, e);
		}
	}

	private synchronized void setStartPlaying() {
		playing = true;
	}
	
	private synchronized void notifyFinishPlayig() {
		playing = false;
		notifyAll();
	}
	
	private synchronized void waitToFinishPlay() throws InterruptedException {
		while (playing) {
			wait();
		}
	}	
	
	public boolean onError(MediaPlayer mp, int what, int extra) {
		exceptionWhilePLay = new SoundException("Play error " + what +":" + extra); 
		notifyFinishPlayig();
		return true;//the onCompletion will not be signaled
	}

	public void onCompletion(MediaPlayer mp) {
		notifyFinishPlayig();
	}

	public void unprepare() {
		if (playerPlay == null || playerPrepare == null) return;
		
		playerPlay.release();
		playerPlay = null;
		playerPrepare.release();
		playerPrepare = null;
	}
	
	public String toString() {
		return "AndroidSequenceSoundPlayer " + filePaths.length + " : " +filePaths[0];
	}
}
