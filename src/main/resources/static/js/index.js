$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	// 获取标题和正文内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	// 发送ajax请求，保存帖子
	$.ajax({
		url: "http://localhost:8080/publishPost",
		data: {"title": title, "content": content},
		type: "post",
		success: function(data) {
			data = $.parseJSON(data);
			// 显示提示信息
			$("#hintBody").text(data.msg);
			// 显示提示信息框
			$("#hintModal").modal("show");
			// 2秒后，隐藏提示框
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 如果成功，刷新页面
				if (data.code == 200) {
					window.location.reload();
				}
			}, 2000);
		}
	});

}