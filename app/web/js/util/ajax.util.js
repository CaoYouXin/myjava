/**
 * Created by cls on 15-3-27.
 */
function setGlobalHandler(callback) {
    $(document).ajaxComplete(function( event, xhr ) {
        console.log( "The status is " + xhr.status + ", result is " + xhr.responseText );
        var json = jQuery.parseJSON(xhr.responseText);
        if ( json.exp !== undefined ) {
            if ( json.exp === '500' ) {
                window.self.location = "/500.html";
            } else if ( json.exp === '503' ) {
                alert( '权限不够' );
                bootbox.dialog("Custom dialog with icons being passed explicitly into <b>bootbox.dialog</b>.", [{
                    "label" : "Success!",
                    "class" : "btn-success",
                    "icon"  : "icon-ok-sign icon-white"
                }, {
                    "label" : "Danger!",
                    "class" : "btn-danger",
                    "icon"  : "icon-warning-sign icon-white"
                }, {
                    "label" : "<span>Click ME!</span>",
                    "class" : "btn-primary",
                    "icon"  : "icon-ok icon-white"
                }, {
                    "label" : "Just a button...",
                    "icon"  : "icon-picture"
                }]);
                //$.ajax(getLastUrl(), {
                //    type: "POST",
                //    data: getLastFormData(),
                //    dataType: "json",
                //    success: callback,
                //    error: function (jqXHR, textStatus, errorThrown) {
                //        alert("有问题啦！！！" + jqXHR + "！！！" + textStatus + "！！！" + errorThrown);
                //        alert("问题详情（返回状态）！！！" + jqXHR.status);
                //        alert("问题详情（返回内容）！！！" + jqXHR.responseText);
                //    }
                //});
            } else if ( json.exp === 'null' ) {
                alert( 'app返回空值' );
            } else if ( json.exp === 'nonsense' ) {
                alert( '所调之命令不存在' );
            } else if ( json.exp === 'empty' ) {
                alert( '没有符合要求的数据' );
            } else if ( json.exp === 'not login' ) {
                alert( '登录超时，请重新登录' );
                window.self.location = "/login.html";
            }
        }
    });
}
