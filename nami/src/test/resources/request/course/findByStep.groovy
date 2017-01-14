
def page = request.getInteger("page");
def tag = request.getString("tag");
def tagId = db.find("select * from COUR_TAG where TITLE = ?",tag)?.ID;
def list = db.query("select * from COUR_COURSE where TAG_ID = ? order by CREATE_TIME desc limit ?,10",tagId,page*10);

def result = [:];
result.total = list.size();
result.list = [];
for(def o:list){
	def vo = [:];
	vo._id = o.ID;
	vo.courseId = o.ID;
	vo.title = o.TITLE;
	vo.avatar = o.AVATAR;
	vo.teacher = o.TEACHER;
	vo.period = o.PERIOD;
	vo.text = o.TEXT;
	vo.subject = o.SUBJECT;
	vo.target = o.TARGET;
	vo.startTime = o.START_TIME;
	vo.created = o.CREATE_TIME;
	vo.price = o.PRICE;
	vo.score = 1;
	result.list += vo;
}

return result;
