/**
 * 支付成功回调
 * 
 * @author woden
 *
 */
def notify = app.pay.currentNotify();


//接收到回调,打印出来
log.debug("支付回调接收:{}",notify);


db.exec("insert into PAY_DETAIL (OPEN_ID,TOTAL_FEE,TRANSACTION_ID,TRADE_NUMBER,PAY_TIME) values (?,?,?,?,?)"
,notify.openId
,notify.totalFee
,notify.transactionId
,notify.tradeNumber
,notify.timeEnd
);


return app.pay.notifySuccess();