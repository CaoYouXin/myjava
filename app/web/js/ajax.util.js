/**
 * Created by cls on 15-3-27.
 */
$(function() {
    $(document).ajaxComplete(function( event, xhr ) {
        console.log( "The status is " + xhr.status + ", result is " + xhr.responseText );
        var json = jQuery.parseJSON(xhr.responseText);
        if ( json.exp !== undefined ) {
            if ( json.exp === '500' ) {
                window.self.location = "/500.html";
            } else if ( json.exp === '503' ) {
                alert( '权限不够' );
            } else if ( json.exp === 'null' ) {
                alert( 'app返回空值' );
            } else if ( json.exp === 'nonsense' ) {
                alert( '所调之命令不存在' );
            } else if ( json.exp === 'empty' ) {
                alert( '没有符合要求的数据' );
            }
        }
    });
});