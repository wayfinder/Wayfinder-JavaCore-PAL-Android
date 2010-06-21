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
package com.wayfinder.pal.android.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.wayfinder.pal.persistence.PersistenceLayer;
import com.wayfinder.pal.persistence.SecondaryCacheStorage;
import com.wayfinder.pal.persistence.SettingsConnection;
import com.wayfinder.pal.persistence.WFFileConnection;

public class AndroidPersistenceLayer implements PersistenceLayer {

	private final Context m_context;
	private String m_BaseFileDirectory = null;
	
	public AndroidPersistenceLayer(Context context) {
	    m_context = context;	    
	}
    
    /*
	 * (non-Javadoc)
	 * @see com.wayfinder.pal.persistence.PersistenceLayer#openFile(java.lang.String)
	 */
	public synchronized WFFileConnection openFile(String path) throws IOException {
		return new AndroidFileConnection(path);
	}

    /* (non-Javadoc)
     * @see com.wayfinder.pal.persistence.PersistenceLayer#getResourceAsStream(java.lang.String)
     */
    public InputStream getResourceAsStream(String resource) throws IOException {
    	
    	// look in resources/raw. Usual place for stuff that would be loaded
		final int index = resource.lastIndexOf('.');
		String resStr;
		if(index >= 0) {
			resStr = resource.substring(0, index);
		} else {
			resStr = resource;
		}
		Resources contextRes = m_context.getResources();
		int resId = contextRes.getIdentifier(resStr.toLowerCase(), "raw", m_context.getPackageName());
		if(resId != 0) {
			return contextRes.openRawResource(resId);
		}
		// else check the assets
		return m_context.getAssets().open(resource);
    }
    
    /*
     * (non-Javadoc)
     * @see com.wayfinder.pal.persistence.PersistenceLayer#openSettingsConnection(java.lang.String)
     */
    public SettingsConnection openSettingsConnection(String settingsType) {
    	return new AndroidSettingsConnection(settingsType, this);
    }
    
    /*
     * (non-Javadoc)
     * @see com.wayfinder.pal.persistence.PersistenceLayer#openSecondaryCacheStorage(java.lang.String)
     */
    public SecondaryCacheStorage openSecondaryCacheStorage(String name) throws IOException {
    	return new AndroidSecondaryCacheStorage(getBaseFileDirectory(), name);
    }
    
    /*
     * (non-Javadoc)
     * @see com.wayfinder.pal.persistence.PersistenceLayer#setBaseFileDirectory(java.lang.String)
     */
    public synchronized void setBaseFileDirectory(String path) {
    	if (path == null || !path.endsWith("/")) {
    		throw new IllegalArgumentException("Path must be not null and ends with '/'");
    	}
    	//don't create any directories those will be created automatically on first
    	//write operation
    	m_BaseFileDirectory = path;
    }
    
    /*
     * (non-Javadoc)
     * @see com.wayfinder.pal.persistence.PersistenceLayer#getBaseFileDirectoryPath()
     */
    public synchronized String getBaseFileDirectory() {
    	
    	if(m_BaseFileDirectory == null) {
    		m_BaseFileDirectory = m_context.getFilesDir().getAbsolutePath() + '/';
        	//don't create any directories those will be created automatically on first
        	//write operation
    	}
    	return m_BaseFileDirectory;
    }
    
    /*
     * (non-Javadoc)
     * @see com.wayfinder.pal.persistence.PersistenceLayer#listFiles(java.lang.String, java.lang.String)
     */
    public String[] listFiles(String folder, String extension) {
    	File path = new File(folder);
		return path.list(new AndroidFileNameFilter(extension));
    }
    
    private static class AndroidFileNameFilter implements FilenameFilter {
		
		private String m_Filter;
		
		public AndroidFileNameFilter(String filter) {
			m_Filter = filter;
		}

		public boolean accept(File dir, String name) {
			return name.endsWith(m_Filter);
		}		
	}
}
