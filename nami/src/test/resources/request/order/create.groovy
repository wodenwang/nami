
def token = request.getString("token");//session key
def courseId = request.getString("courseId");//课程ID

log.info(token);

//返回支付密匙,用于发起支付

//根据入参构建param
def param = [
				total : 1,
				body : '测试支付',
				openId : session.appUser(token).openId,
				notify : '/request/ord/doc_p_pay_callback.groovy'
			];

//调用微信支付,"下支付单"
def order = app.pay.order(param);
return app.pay.signature(order.prepayId);
