package com.nlt.baseObject;

import java.io.Serializable;

public class downloadThreadInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * threadID for mutil thread download one file
	 */
	private int threadID;
	private String fileName;
	private String fileUri;
	private int startPosition;
	private int endPotition;
	private int progress;
	
		
	public downloadThreadInfo(int threadID, String fileName, String fileUri,
			int startPosition, int endPotition, int progress)
	{
		this.threadID = threadID;
		this.fileName = fileName;
		this.fileUri = fileUri;
		this.startPosition = startPosition;
		this.endPotition = endPotition;
		this.progress = progress;
	}

	public int getThreadID()
	{
		return threadID;
	}

	public void setThreadID(int threadID)
	{
		this.threadID = threadID;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getFileUri()
	{
		return fileUri;
	}

	public void setFileUri(String fileUri)
	{
		this.fileUri = fileUri;
	}

	public int getStartPosition()
	{
		return startPosition;
	}

	public void setStartPosition(int startPosition)
	{
		this.startPosition = startPosition;
	}

	public int getEndPotition()
	{
		return endPotition;
	}

	public void setEndPotition(int endPotition)
	{
		this.endPotition = endPotition;
	}

	public int getProgress()
	{
		return progress;
	}

	public void setProgress(int progress)
	{
		this.progress = progress;
	}

	public int getTheadID()
	{
		return threadID;
	}
	public void setTheadID(int theadID)
	{
		this.threadID = theadID;
	}

	@Override
	public String toString()
	{
		return "downloadThreadInfo [threadID=" + threadID + ", fileName="
				+ fileName + ", fileUri=" + fileUri + ", startPosition="
				+ startPosition + ", endPotition=" + endPotition
				+ ", progress=" + progress + "]";
	}

	
	
}
