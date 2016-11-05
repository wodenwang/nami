var a = request.getInteger("a");
var b = request.getString("b");

var _fun = function() {
	// 直接组装成对象返回
	return {
		a : a,
		b : b
	}
}

_fun();