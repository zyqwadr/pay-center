package com.tencent.protocol.pay_protocol;

/**
 * {"access_token":"82jqxGK2rOsSjT1wlCLKTxsEOmaqmeBDSSEpjx9GNXofuVPBr7_5KTmG2Q5YR5mPX2XqGpaR1DtZoqPiThO703dIs8YbBEH4CYSPKzZekHU",
 * "expires_in":7200,
 * "refresh_token":"7k1j9-Ddzaypo9vfnkGOVeAe1y8vCeFBN6YKfRfPc0_p9juEdAGvolpoD8d5167XVSEQiAHQkf4tUuz6cpGmAbJSu3yVOjwiAp9B-V9s7rI",
 * "openid":"oF9zhvnDdZl33u-KkuRwoe00ETZg",
 * "scope":"snsapi_userinfo"}
 * @author nesting
 *
 */
public class GetOpenidResData {
	private String access_token;
	private String expires_in;
	private String refresh_token;
	private String openid;
	private String scope;
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}
	public String getRefresh_token() {
		return refresh_token;
	}
	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	
}
