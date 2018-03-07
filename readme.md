## 支付中心系统
- 集成微信，支付宝，ping++支付，认证功能
- 所有请求接口通过md5请求参数加密，保证安全

### 使用技术
- spring-boot,mysql,mongdb

### 微信
- 包含微信公众号和app,支付，通知，退款，账单接口

### 支付宝
- 包含支付宝网页和app，支付，通知，退款，账单接口

### ping++
- 包含支付，通知，退款，认证接口

### 使用
- 1.修改配置application-dev.properties
- 2.下载并替换wx_certificate和pingxx_key现有key
- 3.初始化数据库表schema/construct.sql
- 4.修改ali配置com/alipay/config/AlipayConfig.java里的partner,private_key,alipay_public_key三个参数
