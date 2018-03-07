package xyz.nesting.payment.model.pingxx;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.pingplusplus.model.Refund;

import xyz.nesting.common.protocol.ZcAbstractToString;

/**
 * 创建refund成功model
 * @author nesting
 *
 */
@Document(collection="pingxx_webhook_refund_succeeded")
public class MgWebhookRefundSucceeded extends ZcAbstractToString{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3126654414293133456L;

	@Id
	private String id;
	
	private Date createAt;
	
	private Refund refund;

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

	public static MgWebhookRefundSucceeded build(Refund refund) {
		MgWebhookRefundSucceeded mgRefund = new MgWebhookRefundSucceeded();
		mgRefund.setRefund(refund);
		mgRefund.setCreateAt(new Date());
		return mgRefund;
	}
	
	
}
