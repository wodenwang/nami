(function() {
	var a = request.getString("a") || 'none';
	var b = request.getInteger("b") || 0;

	var obj = {
		a : 1
	};
	obj.list = [ {
		abc : 11,
		fdfdfd : 22
	}, {
		aa : 1,
		dd : 'dd'
	} ];
	return obj;
})();