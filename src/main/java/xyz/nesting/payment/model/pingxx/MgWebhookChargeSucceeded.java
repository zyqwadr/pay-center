package xyz.nesting.payment.model.pingxx;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.pingplusplus.model.Charge;

import xyz.nesting.common.protocol.ZcAbstractToString;


/**
 * 支付成功通知model
 * @author nesting
 *
 */
@Document(collection="pingxx_webhook_charge_succeeded")
public class MgWebhookChargeSucceeded extends ZcAbstractToString{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -2089661604548853450L;

	@Id
	private String id;
	
	private Date createAt;
	
	private Charge charge;

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

	public Charge getCharge() {
		return charge;
	}

	public void setCharge(Charge charge) {
		this.charge = charge;
	}

	public static MgWebhookChargeSucceeded build(Charge charge) {
		MgWebhookChargeSucceeded mgPayCharge = new MgWebhookChargeSucceeded();
		mgPayCharge.setCharge(charge);
		mgPayCharge.setCreateAt(new Date());
		return mgPayCharge;
	}
	
	
}
