(function() {
	var b = nami.invoke("/fun1.js", 200, 202);// 调用fun1.js,入参2

	return {
		result : b + 1
	}
})();