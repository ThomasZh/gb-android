package com.redoct.blackboard.network;

import org.apache.http.Header;

import android.os.Handler;
import android.os.Message;

import com.redoct.blackboard.network.InternetUtil.OnHttpListener;

public abstract class IHttpTask implements OnHttpListener {
	
	public static int MODE_BEFORE=0;
	public static int MODE_FIRST=1;
	
	public static String HTTP_URL="";

	private final int MESSAGE_LOAD_FAILED = 0;
	private final int MESSAGE_LOAD_SUCCESS = 1;
	public static int GET = 0;
	public static int POST = 1;
	public static int DELETE = 2;
	public static int PUT = 3;

	public abstract int getMethod();

	public abstract String getUrl();

	public abstract boolean ifNeedSession();
	
	//private boolean isNeedLoadBadgeNumber;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case MESSAGE_LOAD_FAILED:

				if (msg.obj instanceof ResponseContentContainer) {
					int errorCode = msg.arg1;
					ResponseContentContainer responseContent = (ResponseContentContainer) msg.obj;

					failure(errorCode, responseContent.getBody(),responseContent.getHeaders());

					complete();
					
					/*if(TextUtils.isEmpty(Constant.HTTP_COMMON_URL)||errorCode==401||errorCode==Constant.RESULT_CODE_TIME_OUT){
						long curTime=System.currentTimeMillis();
						if(curTime-iClubApplication.lastAotuLoginTime>30*1000){
							
							Log.e("zyf","开始做自动登录.......");
							
							iClubApplication.lastAotuLoginTime=curTime;
							connectGateKeeper();
						}else {
							Log.e("zyf","30以内不做重复做自动登录.......");
						}
					}*/
				} else {
					int errorCode = msg.arg1;
					String responseContent = (String) msg.obj;

					failure(errorCode, responseContent);

					complete();
					
				}

				break;
			case MESSAGE_LOAD_SUCCESS:
				if (msg.obj instanceof ResponseContentContainer) {
					ResponseContentContainer responseContent2 = (ResponseContentContainer) msg.obj;

					callback(responseContent2.getBody(),responseContent2.getHeaders());

					complete();
				} else {
					String responseContent2 = (String) msg.obj;

					callback(responseContent2);

					complete();
				}

				break;

			default:
				break;
			}
		}

	};

	private void sendMessage(int what, int errorCode, Object obj) {

		Message msg = new Message();
		msg.what = what;
		msg.arg1 = errorCode;
		msg.obj = obj;

		mHandler.sendMessage(msg);
	}

	public boolean isRunning = false;

	public void before() {

	}

	public void callback(String responseContent) {

	}

	public void failure(int errorCode, String responseContent) {

	}

	public void callback(String responseContent, Header[] headers) {

	}

	public void failure(int errorCode, String responseContent, Header[] headers) {

	}

	public void complete() {

		isRunning = false;
	}

	// 组装好要post的cotent
	public String justTodo() {

		isRunning = true;

		return "";
	}

	@Override
	public void onResult(int resultCode, String result) {

		if (resultCode == 200) { // 连接服务器成功

			sendMessage(MESSAGE_LOAD_SUCCESS, 200, result);

		} else {

			sendMessage(MESSAGE_LOAD_FAILED, resultCode, result);
		}
	}

	@Override
	public void onResult(int resultCode, String result, Header[] headers) {

		ResponseContentContainer container = new ResponseContentContainer();
		container.setBody(result);
		container.setHeaders(headers);
		if (resultCode == 200) { // 连接服务器成功

			sendMessage(MESSAGE_LOAD_SUCCESS, 200, container);

		} else {

			sendMessage(MESSAGE_LOAD_FAILED, resultCode, container);
		}
	}

	private class ResponseContentContainer {

		private String body;
		private Header[] headers;

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

		public Header[] getHeaders() {
			return headers;
		}

		public void setHeaders(Header[] headers) {
			this.headers = headers;
		}
	}
	
	/*private void connectGateKeeper(){
    	
    	ServerConnectTask connetTask = new ServerConnectTask() {
			@Override
			public void before() {
				super.before();
			}

			@Override
			public void callback(String responseContent, Header[] headers) {
				super.callback(responseContent);
				
				try {
					JSONObject object = new JSONObject(responseContent);
					String ip = object.optString("stpIp");
					String port = object.optString("stpPort");
					ServiceConfig.token = object.optString("gateToken");

					Constant.HTTP_COMMON_URL = "http://" + ip + ":" + port;
					
					UserInfoSpUtils.saveStpUrl(iClubApplication.mContext, Constant.HTTP_COMMON_URL);
					
					Log.e("zyf", "gate back url: " + UserInfoSpUtils.getStpUrl(iClubApplication.mContext));
					
					login();
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void complete() {
				// TODO Auto-generated method stub
				super.complete();
			}

			@Override
			public void failure(int errorCode, String responseContent,
					Header[] headers) {
				super.failure(errorCode, responseContent);
				Log.e("zyf", "get gate keeper failed......");
			}
		};
		HttpNetworkNormalManager.getInstance().addRunningtask(connetTask);
    }
	
	private String loginName,loginPsw;

    private void login() {

    	loginName=UserInfoSpUtils.getLoginName(iClubApplication.mContext);
    	loginPsw=UserInfoSpUtils.getLoginPsw(iClubApplication.mContext);
    	UserLoginTask login = new UserLoginTask(loginName, loginPsw) {
    		
            @Override
            public void before() {
                super.before();
            }

            @Override
            public void callback(String responseContent, Header[] headers) {
                super.callback(responseContent, headers);
                JSONObject response = null;
                try {
                    response = new JSONObject(responseContent);
                    
                    UserInfoSpUtils.saveLoginName(iClubApplication.mContext,loginName);
                    UserInfoSpUtils.saveLoginPsw(iClubApplication.mContext, loginPsw);
                    UserInfoSpUtils.saveIsThreeLogin(iClubApplication.mContext,false);

                    UserInfoSpUtils.saveAccountId(iClubApplication.mContext,response.optString("accountId"));
                    UserInfoSpUtils.saveSessionId(iClubApplication.mContext,response.optString("sessionToken"));
                    
                    Log.e("zyf", "自动登录成功......");
                    
                    if(isNeedLoadBadgeNumber){
                    	Log.e("zyf", "内存被回收，需要重新获取BadgeNumber。。。。");
                    	isNeedLoadBadgeNumber=false;
                        getBadgeNumber();
                    }else {
                    	Log.e("zyf", "不不不需要重新获取BadgeNumber。。。。");
					}
                    
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                
            }


            @Override
            public void failure(int errorCode, String responseContent, Header[] headers) {
                super.failure(errorCode, responseContent, headers);

            }

            @Override
            public void complete() {
                // TODO Auto-generated method stub
                super.complete();
            }

        };
        HttpNetworkNormalManager.getInstance().addRunningtask(login);

    }
    
    public void getBadgeNumber() {

        BadgeNumberQueryTask mBadgeNumberQueryTask = new BadgeNumberQueryTask() {

            @Override
            public void before() {
                super.before();
            }

            @Override
            public void callback(String responseContent, Header[] headers) {
                super.callback(responseContent, headers);

                Log.e("zyf", "BadgeNumber: " + responseContent);

                try {

                    JSONObject totalJsonObject = new JSONObject(responseContent);
                    iClubApplication.mBadgeNumberInfo.setSymbolNum(totalJsonObject.optInt("symbolNum"));
                    iClubApplication.mBadgeNumberInfo.setFriendInvitationNum(totalJsonObject.optInt("friendInvitationNum"));
                    iClubApplication.mBadgeNumberInfo.setActivityNum(totalJsonObject.optInt("activityNum"));
                    iClubApplication.mBadgeNumberInfo.setSingleChatNum(totalJsonObject.optInt("singleChatNum"));
                    iClubApplication.mBadgeNumberInfo.setGroupChatNum(totalJsonObject.optInt("groupChatNum"));
                    iClubApplication.mBadgeNumberInfo.setActivityAnnouncementNum(totalJsonObject.optInt("activityAnnouncementNum"));
                    if (iClubApplication.mBadgeNumberInfo.getActivityNum() > 0 ) {
                        MessageTransfor messageTransfor = new MessageTransfor();
                        messageTransfor.setType(610);
                        messageTransfor.setIsShow(true);
                        messageTransfor.getTargetClassName().add("MainActivity");   //指定广播的类
                        messageTransfor.getTargetClassName().add("MySelfFragmentContainerFragment");
                        messageTransfor.getTargetClassName().add("MessageAcitvity");
                        messageTransfor.setMsgId(UUID.randomUUID().toString());
                        JpushReceiver.temMsgList.add(messageTransfor);
                        EventBus.getDefault().post(messageTransfor);
                    }    if (iClubApplication.mBadgeNumberInfo.getSingleChatNum()> 0 ) {
                        MessageTransfor messageTransfor = new MessageTransfor();
                        messageTransfor.setChatType(121);
                        messageTransfor.setIsShow(true);
                        messageTransfor.getTargetClassName().add("MainActivity");   //指定广播的类
                        messageTransfor.getTargetClassName().add("MySelfFragmentContainerFragment");
                        messageTransfor.getTargetClassName().add("MessageAcitvity");
                		messageTransfor.getTargetClassName().add("ChatConusltListActivity");
                        messageTransfor.setMsgId(UUID.randomUUID().toString());
                        JpushReceiver.temMsgList.add(messageTransfor);
                        EventBus.getDefault().post(messageTransfor);
                    } if (iClubApplication.mBadgeNumberInfo.getGroupChatNum()>0 ) {
                        MessageTransfor messageTransfor = new MessageTransfor();
                        messageTransfor.setChatType(123);
                        messageTransfor.setIsShow(true);
                        messageTransfor.getTargetClassName().add("MainActivity");   //指定广播的类
                        messageTransfor.getTargetClassName().add("MySelfFragmentContainerFragment");
                        messageTransfor.getTargetClassName().add("MessageAcitvity");
                		messageTransfor.getTargetClassName().add("ChatConusltListActivity");
                        messageTransfor.setMsgId(UUID.randomUUID().toString());
                        JpushReceiver.temMsgList.add(messageTransfor);
                        EventBus.getDefault().post(messageTransfor);
                    }if (iClubApplication.mBadgeNumberInfo.getFriendInvitationNum()>0 ) {
                    	MainActivity.receiveFriends receiveFriends = new MainActivity.receiveFriends();
						receiveFriends.setIsShow(true);
						EventBus.getDefault().post(receiveFriends);
                    }

                    //发送广播通知
                    Intent intent = new Intent(Constant.ACTION_BADGE_NUMBER_UPDATE);
                    iClubApplication.mContext.sendBroadcast(intent);

                } catch (Exception e) {

                }
            }

            @Override
            public void failure(int errorCode, String responseContent,
                                Header[] headers) {
                super.failure(errorCode, responseContent, headers);
            }

            @Override
            public void complete() {
                super.complete();
            }
        };
        HttpNetworkNormalManager.getInstance().addRunningtask(mBadgeNumberQueryTask);
    }*/

}
