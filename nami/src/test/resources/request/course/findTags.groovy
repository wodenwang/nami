def list = db.query("select * from COUR_TAG order by SORT asc");

def result = [:];
result.total = list.size();
result.list = [];
for(def o:list){
	def vo = [:];
	vo._id = o.ID;
	vo.value = o.ID;
	vo.title = o.TITLE;
	result.list += vo;
}

return result;
