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
package com.wayfinder.pal.android.concurrency;

import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.wayfinder.pal.concurrency.ConcurrencyLayer;

public class AndroidConcurrencyLayer implements ConcurrencyLayer {

    private final ThreadFactory m_threadFactory;
    
    public AndroidConcurrencyLayer() {
        m_threadFactory = Executors.defaultThreadFactory();
    }
    

    public int getCurrentNbrOfThreads() {
        return Thread.activeCount();
    }

    public int getMaxNumberOfThreadsForPlatform() {
        return THREAD_LIMIT_UNLIMITED;
    }

    /* (non-Javadoc)
     * @see com.wayfinder.pal.concurrency.ConcurrencyLayer#startNewThread(java.lang.Runnable, java.lang.String)
     */
    public Thread startNewDaemonThread(Runnable run, String threadName) {
        Thread t = m_threadFactory.newThread(run);
        if(threadName != null) {
            t.setName(threadName);
        }
        t.setDaemon(true);
        t.start();
        return t;
    }

    public Timer startNewDaemonTimer() {
        return new Timer(true);
    }

}
