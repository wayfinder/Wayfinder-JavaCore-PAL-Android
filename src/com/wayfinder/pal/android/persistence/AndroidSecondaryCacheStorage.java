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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.wayfinder.pal.persistence.SecondaryCacheStorage;

public class AndroidSecondaryCacheStorage implements SecondaryCacheStorage {
	
	private static final String MAPCACHE_FOLDER_NAME = "mapcache/";
	
	private File m_File;
	
	public AndroidSecondaryCacheStorage(String path, String name) throws IOException {
		//TODO use WFFile instead
		m_File = new File(path+MAPCACHE_FOLDER_NAME+name);
		
		if(!m_File.exists()) {
			m_File.getParentFile().mkdirs();
			if(!m_File.createNewFile())
				throw new IOException("Unable to create "+path);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.wayfinder.pal.persistence.SecondaryCacheStorage#getMaxPageSize()
	 */
	public int getMaxPageSize() {
		return 64000;
	}

	/*
	 * (non-Javadoc)
	 * @see com.wayfinder.pal.persistence.SecondaryCacheStorage#close()
	 */
	public boolean close() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.wayfinder.pal.persistence.SecondaryCacheStorage#getDataInputStream()
	 */
	public DataInputStream getDataInputStream() throws IOException {
		FileInputStream in = new FileInputStream(m_File);
		return new DataInputStream(in);
	}

	/*
	 * (non-Javadoc)
	 * @see com.wayfinder.pal.persistence.SecondaryCacheStorage#size()
	 */
	public int size() {
		return (int)m_File.length();
	}

	/*
	 * (non-Javadoc)
	 * @see com.wayfinder.pal.persistence.SecondaryCacheStorage#writeToStorage(byte[], int, int)
	 */
	public boolean writeToStorage(byte[] data, int offset, int length) throws IOException {		
		FileOutputStream out = new FileOutputStream(m_File);
		out.write(data, offset, length);
		out.close();		
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.wayfinder.pal.persistence.SecondaryCacheStorage#delete()
	 */
	public boolean delete() {
		return m_File.delete();
	}

}
