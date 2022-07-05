package com.vote.common.util;

/**
 * 常量类
 *
 */
public interface Constant {

	/**
	 * 权限头
	 */
	public static final String AUTHORIZATION = "Authorization";

	/**
	 * 初始密码
	 */
	public static final String INIT_PWD = "123456";
	/**
	 * 用户信息
	 */
	public static final String USER_INFO = "user_info";

	/**
	 * redis存储前缀
	 */
	public static final String PERMISSIONS = "permissions:";

	/**
	 * 投票redisKey前缀
	 */
	public static final String ACT_REDIS_KEY = "act_redis_key:";

	/**
	 * 投票redisKey前缀
	 */
	public static final String ACT_STATUS_REDIS_KEY = "act_status_redis_key:";

	class SerialCoses{
		private SerialCoses(){
		}
		/**
		 * 投票编码
		 */
		public static final String SERIAL_VOTING_CODE = "VOTING_CODE";
	}

	class DictionaryTypes{
		/**
		 * 投票状态
		 */
		//新增
		public static final String VOTING_STATUS_NEW = "10";
		//启用
		public static final String VOTING_STATUS_RUNNING = "20";
		//停用
		public static final String VOTING_STATUS_STOP = "30";
		//结束
		public static final String VOTING_STATUS_OVER = "40";

	}

}