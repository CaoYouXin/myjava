/**
 * Created by cls on 15-3-27.
 */

function goodsRow( goods ) {
    return '<tr><td><input type="checkbox" class="styled" /></td><td>'
        + goods.code + '</td><td>' + goods.name + '</td><td>'
        + goods.color + '</td><td>' + goods.size + '</td></tr>';
}

$(function() {
    $("#styled").uniform({ radioClass: 'choice' });

    $("#data-table").find("input").first().click(function () {
        if ($(this).prop('checked')) {
            $('.checker').find('span').not(':first').addClass('checked');
        } else {
            $('.checker').find('span').not(':first').removeClass('checked');
        }
    });
});