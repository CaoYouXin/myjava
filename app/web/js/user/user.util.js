/**
 * Created by cls on 15-3-26.
 */

var userData = {};

function getUserName() {
    var username = $.cookie("un");
    if (username) {
        //console.info("username = " + username);
        return username;
    }
    return null;
}

function setLastCmd(url, formData) {
    userData.url = url;
    userData.formData = formData;
}

function getLastUrl() {
    return userData.url;
}

function getLastFormData() {
    return userData.formData;
}