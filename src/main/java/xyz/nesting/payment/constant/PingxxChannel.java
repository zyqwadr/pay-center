package xyz.nesting.payment.constant;

import xyz.nesting.common.exceptions.BusinessException;

public enum PingxxChannel {

	alipay("支付宝App支付"),
	wx("微信App支付");
	
	private String desc;
	
	private PingxxChannel(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	public static boolean valid(String channel) {
		for (PingxxChannel v : values()) {
			if (channel.equals(v.name())) {
				return true;
			}
		}
		BusinessException.error(ErrorCode.FAILED, "wrong channel");
		return false;
	}
}
