package xyz.nesting.payment.model.pingxx;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.pingplusplus.model.Refund;

import xyz.nesting.common.protocol.ZcAbstractToString;
import xyz.nesting.payment.protocol.pingxx.PingxxRefundReq;

/**
 * 创建refund成功model
 * @author nesting
 *
 */
@Document(collection="pingxx_create_refund")
public class MgCreateRefund extends ZcAbstractToString{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1850851743897439306L;

	@Id
	private String id;
	
	private Date createAt;
	
	private PingxxRefundReq refundReq;
	
	private Refund refund;
	
	public PingxxRefundReq getRefundReq() {
		return refundReq;
	}

	public void setRefundReq(PingxxRefundReq refundReq) {
		this.refundReq = refundReq;
	}

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


	public Refund getRefund() {
		return refund;
	}

	public void setRefund(Refund refund) {
		this.refund = refund;
	}

	public static MgCreateRefund build(PingxxRefundReq refundReq, Refund refund) {
		MgCreateRefund mgRefund = new MgCreateRefund();
		mgRefund.setRefundReq(refundReq);
		mgRefund.setRefund(refund);
		mgRefund.setCreateAt(new Date());
		return mgRefund;
	}
	
	
}
