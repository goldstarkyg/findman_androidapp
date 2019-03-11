package com.findmiin.business.local.manager.utils;

import android.os.AsyncTask;

public class BackgroundTaskUtils extends AsyncTask<Void, Void, Void> {
	OnTaskProgress progress = null;
	public BackgroundTaskUtils(OnTaskProgress progress) {
		this.progress = progress;
	}
	
	@Override
	protected Void doInBackground(Void... params) {			
		if( progress != null )
			progress.onProgress();
		return null;
	}	
	
	@Override
   	protected void onPostExecute(Void result) 
   	{
		super.onPostExecute(result);
		if( progress != null )
			progress.onFinished();		
   	}
	
	public interface OnTaskProgress {
	    public void onProgress();
	    public void onFinished();
	}
}
