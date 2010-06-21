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
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


import com.wayfinder.pal.persistence.WFFileConnection;

public class AndroidFileConnection implements WFFileConnection {
	
	private File file;
	
	public AndroidFileConnection(String path) throws IOException {
		file = new File(path);	
	}
	
	private void createIfNotExist() throws IOException {
		if (!file.exists()) {
			// create the whole directory structure
			// ignore the findbugs if something could not be created 
			// we will have an exception when trying to open the file.
			file.getParentFile().mkdirs();
			file.createNewFile();
			
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.wayfinder.pal.persistence.FileConnection#close()
	 */
	public void close() throws IOException { }

	/*
	 * (non-Javadoc)
	 * @see com.wayfinder.pal.persistence.FileConnection#delete()
	 */
	public boolean delete() {
		return file.delete();
	}

	/*
	 * (non-Javadoc)
	 * @see com.wayfinder.pal.persistence.FileConnection#fileSize()
	 */
	public int fileSize() {
		return (int)file.length();
	}

	/*
	 * (non-Javadoc)
	 * @see com.wayfinder.pal.persistence.FileConnection#openDataInputStream()
	 */
	public DataInputStream openDataInputStream() throws IOException {
		FileInputStream fin = new FileInputStream(file);
		return new DataInputStream(fin);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.wayfinder.pal.persistence.FileConnection#openDataOutputStream()
	 */
	public DataOutputStream openDataOutputStream() throws IOException{
		createIfNotExist();
		FileOutputStream fout = new FileOutputStream(file);
		return new DataOutputStream(fout);
	}

	public DataOutputStream openDataOutputStream(boolean append)
			throws IOException {
		createIfNotExist();
		FileOutputStream fout = new FileOutputStream(file, true);
		return new DataOutputStream(fout);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.wayfinder.pal.persistence.FileConnection#exists()
	 */
	public boolean exists() {
		return file.exists();
	}


}
