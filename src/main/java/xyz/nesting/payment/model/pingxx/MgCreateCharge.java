package xyz.nesting.payment.model.pingxx;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.pingplusplus.model.Charge;

import xyz.nesting.common.protocol.ZcAbstractToString;
import xyz.nesting.payment.protocol.pingxx.PingxxPayReq;

/**
 * 创建charge成功model
 * @author nesting
 *
 */
@Document(collection="pingxx_create_charge")
public class MgCreateCharge extends ZcAbstractToString{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8158042056558080242L;

	@Id
	private String id;
	
	private Date createAt;
	
	private PingxxPayReq payReq;
	
	private Charge charge;
	
	public PingxxPayReq getPayReq() {
		return payReq;
	}

	public void setPayReq(PingxxPayReq payReq) {
		this.payReq = payReq;
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

	public Charge getCharge() {
		return charge;
	}

	public void setCharge(Charge charge) {
		this.charge = charge;
	}

	public static MgCreateCharge build(PingxxPayReq payReq, Charge charge) {
		MgCreateCharge mgPayCharge = new MgCreateCharge();
		mgPayCharge.setPayReq(payReq);
		mgPayCharge.setCharge(charge);
		mgPayCharge.setCreateAt(new Date());
		return mgPayCharge;
	}
	
	
}
