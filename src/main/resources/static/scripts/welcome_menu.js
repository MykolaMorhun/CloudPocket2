$(document).ready(function() {
    var current_page = window.location.pathname;
    current_page = current_page.substr(1, current_page.length);
    $("#" + current_page).addClass("active");
});