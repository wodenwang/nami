/**
 * 下单支付
 * 
 * @author woden
 *
 */

//获取当前用户
def user = session.appUser();

//更新用户信息
if(db.find("select OPEN_ID from PAY_USER where OPEN_ID=?",user.openId)){//找得到则更新
	db.exec("update PAY_USER set NICK_NAME=?,AVATAR_URL=?,UPDATE_TIME=? where OPEN_ID=?"
			,user.nickName
			,user.avatarUrl
			,now
			,user.openId
			);
}else{//否则新增
	db.exec("insert into PAY_USER (OPEN_ID,NICK_NAME,AVATAR_URL,UPDATE_TIME) values (?,?,?,?)"
			,user.openId
			,user.nickName
			,user.avatarUrl
			,now
			);
}

//支付下单
def total = request.getInteger("total");
def order =app.pay.order([
	openId : user.openId,
	total : total,
	body : '多谢赞赏',
	notify : nami.invoke('host.groovy')+'/request/scholes_pay/pay_callback.groovy' //回调
]);

//向客户端生成支付加密串
return [tradeNumber:order.tradeNumber,signature:app.pay.signature(order.prepayId)];