/**
 * 排行榜
 * @author woden
 */

def user = session.appUser();
def list = db.query("""
select b.*,a.TOTAL_FEE from
(
	select sum(TOTAL_FEE) TOTAL_FEE,OPEN_ID from PAY_DETAIL group by OPEN_ID
) a 
left join PAY_USER b on a.OPEN_ID = b.OPEN_ID
order by a.TOTAL_FEE desc
limit 10
""".toString());

def result = [];
def i=1;
for(def o:list){
	def vo = [:];
	vo.avatar = o.AVATAR_URL;
	vo.nickName = o.NICK_NAME;
	vo.fee = fmt.formatPrice(o.TOTAL_FEE/100);
	if(user.openId == o.OPEN_ID){
		vo.my = true;
	}
	vo.rank = i++;
	result += vo;
}

return [list:result];