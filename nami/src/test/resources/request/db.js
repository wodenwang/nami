
function main(){
	nami.error('出错');
	return db.query("select 1");
}

main();