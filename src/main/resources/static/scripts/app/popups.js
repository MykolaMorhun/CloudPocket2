
/***********************************************/
// upload file popup
var upload_file_popup_container;
var upload_file_popup_close_button;
var upload_file_popup_progress_bar_bar;
var upload_file_popup_progress_bar_percent;
var upload_file_popup_status;
var upload_file_popup_filename;

var upload_file_popup_auto_close_descriptor;

$(document).ready(function() {
    find_popups_elements();
    bind_popups_events();
});

function find_popups_elements() {
    upload_file_popup_container = $('#upload-file-popup-container');
    upload_file_popup_close_button = $('#upload-file-popup-close-button');
    upload_file_popup_progress_bar_bar = $('#upload-file-popup-progress-bar-bar');
    upload_file_popup_progress_bar_percent = $('#upload-file-popup-progress-bar-percent');
    upload_file_popup_status = $('#upload-file-popup-status');
    upload_file_popup_filename = $('#upload-file-popup-filename');
}

function bind_popups_events() {
    upload_file_popup_close_button.bind('click', on_upload_file_popup_close);
}

function on_upload_file_popup_close() {
    upload_file_popup_container.fadeOut();
}

function upload_file_popup_update_progress(percents) {
    var percents_value = percents + '%';
    upload_file_popup_progress_bar_bar.width(percents_value);
    upload_file_popup_progress_bar_percent.html(percents_value);
    upload_file_popup_status.text('uploading');
}

function upload_file_popup_reset(filename) {
    upload_file_popup_container.fadeIn();
    upload_file_popup_filename.text(filename);
    upload_file_popup_status.text('waiting');
    upload_file_popup_progress_bar_bar.css('background-color', '#B4F5B4');
    upload_file_popup_progress_bar_bar.width(0);
    upload_file_popup_progress_bar_percent.html('0%');
    clearTimeout(upload_file_popup_auto_close_descriptor);
}

function upload_file_popup_error() {
    upload_file_popup_status.text('failed');
    upload_file_popup_progress_bar_bar.css('background-color', '#EA8585');
}

function upload_file_popup_success() {
    upload_file_popup_update_progress(100);
    upload_file_popup_status.text('success');
    upload_file_popup_auto_close_descriptor = setTimeout(function () {
        upload_file_popup_container.fadeOut(1000);
    }, 5000);
}
