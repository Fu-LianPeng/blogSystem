$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;
	var entityType=3;
	var entityId=$("#entityId").val();


	$.post(
		context_Path+"/follow",
		{"entityType":entityType,"entityId":entityId},
		function (data) {
			data=$.parseJSON(data);
			if(data.code==0){

				//if(data.followStatus) {
					// 关注TA
				//	$(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
				//} else {
					// 取消关注
				//	$(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
				//}
				window.location.reload();
			}else{
				alert(data.msg);
			}
		}
	)

}