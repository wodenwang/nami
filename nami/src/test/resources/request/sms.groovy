
def mobile = request.getString("mobile");
if(!mobile){
	mobile = "18988888320";
}

sms.code(mobile);

return [OK:true];