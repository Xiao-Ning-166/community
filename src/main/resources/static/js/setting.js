$(function() {
    $("#uploadByQiNiu").submit(upload);
});

function upload() {
    $.ajax({
        url: "http://upload-z2.qiniup.com",
        type: "post",
        processData: false,
        contentType: false,
        data: new FormData($("#uploadByQiNiu")[0]),
        success: function (data) {
            if (data && data.code == 200) {
                // 发送请求，更新头像
                $.ajax({
                    url: "http://localhost:8080/header/url",
                    type: "post",
                    data: {"fileName": $("input[name='key']").val()},
                    success: function (data) {
                        data = $.parseJSON(data);
                        if (data.code == 200) {
                            window.location.reload();
                        } else {
                            alert("头像更新失败！！！");
                        }
                    }
                });
            } else {
                alert("图片上传失败！！！");
            }
        }
    });

    // 阻止触发表单的提交事件
    return false;
}