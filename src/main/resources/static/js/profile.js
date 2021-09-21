$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;
	var entityType = 3;
	var entityId = $(btn).prev().val();

	if($(btn).hasClass("btn-info")) {
		// 关注TA
		$.ajax({
			url: "http://localhost:8080/follow",
			data: {"entityType":entityType,"entityId":entityId},
			type: "POST",
			success: function (data) {
				data = $.parseJSON(data);
				if (data.code == 200) {
					// $(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
					window.location.reload();
				}
			}
		});

	} else {
		// 取消关注
		$.ajax({
			url: "http://localhost:8080/unFollow",
			data: {"entityType":entityType,"entityId":entityId},
			type: "POST",
			success: function (data) {
				data = $.parseJSON(data);
				if (data.code == 200) {
					// $(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
					window.location.reload();
				}
			}
		});

	}
}