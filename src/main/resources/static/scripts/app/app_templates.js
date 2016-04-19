
var back_template;
var item_template;

$(document).ready(function() {
    back_template = $('#back-entity-template').html();
    item_template = $('#file-entity-template').html();
    Mustache.parse(item_template);
});

function get_file_entry_html(fileDto) {
    return Mustache.render(item_template, fileDto);
}

function get_back_folder_entry_html() {
    return back_template;
}

var get_image_src = function() {
    if (this.directory === true) {
        return '/images/fileico/folder.png';
    } else {
        return '/images/fileico/' + this.extension + '.png';
    }
};

function unknown_extension(image) {
    image.onerror = "";
    image.src = "/images/fileico/unknown.png";
    return true;
}
