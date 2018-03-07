package xyz.nesting.payment.model.pingxx;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.pingplusplus.model.Identification;

import xyz.nesting.common.protocol.ZcAbstractToString;
import xyz.nesting.payment.protocol.pingxx.PingxxAuthReq;

/**
 * 创建charge成功model
 * @author nesting
 *
 */
@Document(collection="pingxx_auth")
public class MgAuth extends ZcAbstractToString{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4925170253058168153L;

	@Id
	private String id;
	
	private Date createAt;
	
	private PingxxAuthReq authReq;
	
	private Identification identification;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public PingxxAuthReq getAuthReq() {
		return authReq;
	}

	public void setAuthReq(PingxxAuthReq authReq) {
		this.authReq = authReq;
	}

	public Identification getIdentification() {
		return identification;
	}

	public void setIdentification(Identification identification) {
		this.identification = identification;
	}

	public static MgAuth build (PingxxAuthReq authReq, Identification identification) {
		MgAuth mgAuth = new MgAuth();
		mgAuth.setAuthReq(authReq);
		mgAuth.setIdentification(identification);
		return mgAuth;
	}
	
}
