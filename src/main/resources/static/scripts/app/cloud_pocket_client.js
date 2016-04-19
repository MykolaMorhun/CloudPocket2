
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

function logout_request(callback) {
    $.get("/logout", function(response) {
        callback(response)
    });
}

