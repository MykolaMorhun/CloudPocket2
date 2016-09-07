
var tool_buttons;
var tool_separators;

$(document).ready(function() {
    tool_buttons = $('.tool-button');
    tool_separators = $('.tool-separator');
    resize();
});

$(window).resize(function() {
    resize();
});

var resize = function () {
    var page_width = get_horizontal_window_size();
    if (page_width < 1360) {
       if (page_width <= 800) {
           tool_buttons.css("width", "22px");
           tool_buttons.css("height", "22px");
           tool_separators.css("height", "26px");
        } else if (page_width <= 1024) {
           tool_buttons.css("width", "28px");
           tool_buttons.css("height", "28px");
           tool_separators.css("height", "32px");
        } else {
           tool_buttons.css("width", "36px");
           tool_buttons.css("height", "36px");
           tool_separators.css("height", "40px");
        }
    } else {
        tool_buttons.css("width", "42px");
        tool_buttons.css("height", "42px");
        tool_separators.css("height", "46px");
    }
};

function get_horizontal_window_size() {
    return window.innerWidth;
}
