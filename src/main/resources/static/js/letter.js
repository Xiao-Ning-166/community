$(function () {
    $("#sendBtn").click(send_letter);
    $(".close").click(delete_msg);
});

function send_letter() {
    $("#sendModal").modal("hide");

    // 接收私信的用户名
    var username = $("#recipient-name").val();
    // 私信内容
    var content = $("#message-text").val();

    $.ajax({
        url: "http://localhost:8080/sendLetter",
        data: {"username": username, "content": content},
        type: 'post',
        success: function (data) {
            data = $.parseJSON(data);
            // 显示结果
            $("#hintBody").text(data.msg);
            $("#hintModal").modal("show");
            setTimeout(function () {
                $("#hintModal").modal("hide");
                location.reload();
            }, 2000);
        }
    });


}

function delete_msg() {
    var letterId = $(this).attr("value");
    $.ajax({
        url: "http://localhost:8080/deleteLetter/" + letterId,
        type: 'get',
        success: function (data) {
            data = $.parseJSON(data);
            if (data.code == 200) {
                // TODO 删除数据
                $(this).parents(".media").remove();
                location.reload();
            }
        }
    });
}