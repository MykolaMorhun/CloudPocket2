
var path;
var file;
var downloadFileUri;
var uploadFileUrl;

$(document).ready(function() {
    path = getUrlParameter('path');
    filename = getUrlParameter('filename');
    downloadFileUri = '/api/files/download/file' + '?' + $.param({'path': path, 'file': filename});
    uploadFileUrl = '/api/files/upload/file' + '?' + $.param({'path': path});

    $('#save').on('click', save_file_content);
    $('#discard').on('click', on_discard_button_click);

    load_file_content();
    $('#file-name').text(path + filename);
});

function load_file_content() {
    $.ajax({
        url: downloadFileUri,
        success: function(text) {
            $("#file-content").val(text);
        },
        error: function() {
            alert('Failed to get text file: ' + filename);
        }
    });
}

function save_file_content() {
    var newContent = $("#file-content").val();

    var formData = new FormData();
    formData.append('file', new File([new Blob([newContent])], filename));
    formData.append('another-form-field', 'some value');

    $.ajax({
        url: uploadFileUrl,
        data: formData,
        processData: false,
        contentType: false,
        type: 'POST',
        success: function () {
            show_notification('File ' + filename + ' is successfully saved.');
        },
        error: function () {
            alert('Failed to save changes.');
        }
    });
}

function on_discard_button_click() {
    if (confirm('All unsaved changes will be lost. Continue?')) {
        load_file_content();
        show_notification('File ' + filename + ' is reset to original state.');
    }
}