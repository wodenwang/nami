//definde
function main() {
	var a = request.getString("a") || 'none';
	var b = request.getInteger("b") || 0;

	return {
		a : a,
		b : b
	}
}

// invoke
main();