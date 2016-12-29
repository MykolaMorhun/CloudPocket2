// Applies custom background to page.
$(document).ready(function() {
    apply_custom_background();
});

function apply_custom_background() {
    var extensions = ['png', 'jpg', 'gif', 'jpeg'];
    load_custom_background_image(extensions, apply_background_callback);
}

function load_custom_background_image(extensions_arr, callback) {
    var extension = extensions_arr.shift();
    var img_url = get_next_image_url(extension);
    $.ajax({
        type: "GET",
        url: img_url
    }).done(function(image){
        image = new Image();
        image.onload = function() { callback(image); }
        image.src = img_url;
    }).fail(function(){
        if (extensions_arr.length > 0) {
            // try next image
            load_custom_background_image(extensions_arr, callback);
        }
    });
}

function get_next_image_url(extension) {
    return "/api/files/download/file?path=/&file=background." + extension + "&inline=true";
}

var apply_background_callback =
function apply_background(image) {
    if (image != null) {
        var page_body = $('body');
        page_body.css('background-image', 'url(' + image.src + ')');
        page_body.removeClass('default-background');
        if (is_texture(image)) {
            page_body.addClass('custom-background-texture');
        } else {
            page_body.addClass('custom-background-image');
        }
    }
}

function is_texture(image) {
    return image.height <= 512;
}
