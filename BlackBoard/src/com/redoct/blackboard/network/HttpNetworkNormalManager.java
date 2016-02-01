package com.redoct.blackboard.network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpNetworkNormalManager {

	private String TAG = "HttpNetworkManager";

	private HttpNetworkNormalManager() {

		mExecutorService = Executors.newFixedThreadPool(5);
	}

	private static HttpNetworkNormalManager httpNetworkNormalManager = new HttpNetworkNormalManager();

	public static HttpNetworkNormalManager getInstance() {

		return httpNetworkNormalManager;
	}

	private ExecutorService mExecutorService;

	public void addRunningtask(final IHttpTask runningTask) {

		runningTask.before();

		mExecutorService.execute(new Runnable() {

			@Override
			public void run() {

				justTodo(runningTask);
			}
		});
	}

	protected void justTodo(IHttpTask runningTask) {

		if (runningTask != null) {

			String content = runningTask.justTodo();

			InternetUtil mInternetUtil = new InternetUtil();
			mInternetUtil.setOnHttpListener(runningTask);

			if (runningTask.getMethod() == IHttpTask.GET) {
				mInternetUtil
						.getContentFromServer(
								runningTask.getUrl(),
								"",
								runningTask.ifNeedSession());

			} else if (runningTask.getMethod() == IHttpTask.POST) {

				mInternetUtil
						.postContentToServer(
								runningTask.getUrl(),
								"",
								content, runningTask.ifNeedSession());

			}else if(runningTask.getMethod() == IHttpTask.PUT){
				mInternetUtil.putContentToServer(
						runningTask.getUrl(),
						"",
						content, runningTask.ifNeedSession());
				
			}else if(runningTask.getMethod() == IHttpTask.DELETE){
				mInternetUtil.deleteContentFromServer(
						runningTask.getUrl(),
						"",
						content, runningTask.ifNeedSession());
			}

		}

	}

}
