
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

var get_file_size = function() {
    return get_convenient_file_size(this.size);
}

var kilobyte = 1024;
var megabyte = kilobyte * 1024;
var gigabyte = megabyte * 1024;
function get_convenient_file_size(file_size_in_bytes) {
    if (file_size_in_bytes < kilobyte) {
        return file_size_in_bytes + ' B';
    } else if (file_size_in_bytes < megabyte) {
        return roundToHundredths(file_size_in_bytes / kilobyte) + ' KB';
    } else if (file_size_in_bytes < gigabyte) {
        return roundToHundredths(file_size_in_bytes / megabyte) + ' MB';
    } else {
        return roundToHundredths(file_size_in_bytes / megabyte) + ' GB';
    }
};

function roundToHundredths(number) {
    return Math.round(number * 100) / 100;
}

function get_file_upload_form_template() {
    return '<form id="upload_file_form" name="upload_file" action="" method="post" enctype="multipart/form-data" style="display: none">' +
               '<input type="file" name="file" id="file_input">' +
           '</form>';
}