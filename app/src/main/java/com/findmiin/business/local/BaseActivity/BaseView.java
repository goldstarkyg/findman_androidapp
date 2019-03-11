package com.findmiin.business.local.BaseActivity;



public interface BaseView {
	public static final String INTENT_EXTRA = "intent_extra";
	public static final int 	USER_ID = 0;
	
	public void initProgress();
	public void showProgress(String title, String message);
	public void changeProgress(String title, String message);
	public void hideProgress();
	public void finishView();
}
