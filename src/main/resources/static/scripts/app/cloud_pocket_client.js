
function list_files_request(callback, path, order, reverse) {
    $.ajax({
        url: '/api/files/list',
        data: {
            path: path,
            order: order,
            isReverse: reverse
        },
        type: 'GET',
        success: function(response) {
            callback(response);
        },
        error: function (response) {
            callback(response);
        }
    });
}

function copy_files_request(callback, path_from, path_to, files) {
    $.ajax({
        url: '/api/files/copy',
        data: {
            pathFrom: path_from,
            pathTo: path_to,
            files: files.join(','),
            isReplaceIfExist: false
        },
        type: 'POST',
        success: function(response) {
            callback(response);
        },
        error: function (response) {
            callback(response);
        }
    });
}

function move_files_request(callback, path_from, path_to, files) {
    $.ajax({
        url: '/api/files/move',
        data: {
            pathFrom: path_from,
            pathTo: path_to,
            files: files.join(','),
            isReplaceIfExist: false
        },
        type: 'PUT',
        success: function(response) {
            callback(response);
        },
        error: function (response) {
            callback(response);
        }
    });
}

function download_file_request(callback, path, file) {
    /*$.ajax({
        url: '/api/files/download/file',
        data: {
            path: path,
            file: file,
            inline: false
        },
        type: 'GET',
        success: function(response) {
            callback(response);
        },
        error: function (response) {
            callback(response);
        }
    });*/

    window.location = '/api/files/download/file' + '?' + $.param({'path': path, 'file': file});
    callback();
}

function download_structure_request(callback, path, files) {
    var url = '/api/files/download/archive' + '?' + $.param({'path': path, 'files': files.join(',')});
    window.open(url,'_blank');
    callback();
}

function delete_request(callback, path, files) {
    $.ajax({
        url: '/api/files/delete' + '?' + $.param({'path': path, 'files': files.join(',')}),
        type: 'DELETE',
        success: function(response) {
            callback(response);
        },
        error: function (response) {
            callback(response);
        }
    });
}

function compress_files_request(callback, path, files) {
    $.ajax({
        url: '/api/files/compress',
        data: {
            path: path,
            files: files.join(',')
        },
        type: 'POST',
        success: function (response) {
            callback(response);
        },
        error: function (response) {
            callback(response);
        }
    });
}

function uncompress_files_request(callback, path, archive) {
    $.ajax({
        url: '/api/files/uncompress',
        data: {
            path: path,
            archiveName: archive,
            archiveType: 'ZIP',
            extractIntoSubdirectory: true
        },
        type: 'POST',
        success: function (response) {
            callback(response);
        },
        error: function (response) {
            callback(response);
        }
    });
}

function rename_file_request(callback, path, old_name, new_name) {
    $.ajax({
        url: '/api/files/rename',
        data: {
            path: path,
            oldName: old_name,
            newName: new_name
        },
        type: 'PUT',
        success: function (response) {
            callback(response);
        },
        error: function (response) {
            callback(response);
        }
    });
}

function create_directory_request(callback, path, name) {
    $.ajax({
        url: '/api/files/create/folder',
        data: {
            path: path,
            name: name
        },
        type: 'POST',
        success: function (response) {
            callback(response);
        },
        error: function (response) {
            callback(response);
        }
    });
}

function search_files_request(callback, path, pattern) {
    $.ajax({
        url: '/api/files/search',
        data: {
            path: path,
            namePattern: pattern
        },
        type: 'GET',
        success: function (response) {
            callback(response);
        },
        error: function (response) {
            callback(response);
        }
    });
}

function logout_request(callback) {
    $.get("/logout", function(response) {
        callback(response)
    });
}

