
var app_state = {
    current_path: '/',
    previous_path: '/',
    files_order: 'name',
    files_reverse: false
};
// map that stores files from current directory: filename -> fileinfo
var files_map;

var current_path_input;
var files_table_container;

$(document).ready(function() {
    files_table_container = $('#files-list');
    current_path_input = $('#current-path-input');

    current_path_input.bind('keypress', function (event) {
        if(event.keyCode == 13) {
            on_go_path_button_click();
        }
    });

    on_refresh_button_click();
});

/*************************************/
// file list event handlers and processors
/*************************************/

function update_file_list() {
    list_files_request(list_files_callback,
                       app_state.current_path,
                       app_state.files_order,
                       app_state.files_reverse);
    current_path_input.val(app_state.current_path);
}

function back_dir() {
    if (app_state.current_path != '/') {
        app_state.current_path = app_state.current_path.split('/').slice(0,-2).join('/') + '/';
        update_file_list();
    }
}

function view_item() {
    var row_id = $(this).parent().attr("id");

    if (row_id === '..') {
        back_dir();
        return;
    }
    if (files_map[row_id].directory === true) {
        app_state.current_path += row_id + '/';
        update_file_list();
    } else {
        view_file();
    }
}

function view_file() {
    alert(this);
}

/*************************************/
// click button handlers
/*************************************/

function on_home_button_click() {

}

function on_copy_button_click() {

}

function on_cut_button_click() {

}

function on_paste_button_click() {

}

function on_download_button_click() {

}

function on_delete_button_click() {

}

function on_compress_button_click() {

}

function on_uncompress_button_click() {

}

function on_rename_button_click() {

}

function on_add_file_button_click() {

}

function on_add_folder_button_click() {

}

function on_add_structure_button_click() {

}

function on_search_button_click() {

}

function on_user_profile_button_click() {

}

function on_logout_button_click() {
    logout_request(logout_callback);
}

function on_refresh_button_click() {
    update_file_list();
}

function on_go_path_button_click() {
    var new_path = current_path_input.val();
    if (new_path != '') {
        app_state.current_path = new_path;
        update_file_list();
    }
}

/*************************************/
// server response handlers
/*************************************/

function list_files_callback(data) {
    if ('status' in data) {
        list_files_error(data);
        return;
    }

    if (app_state.current_path == '/') {
        files_table_container.html("");
    } else {
        files_table_container.html(get_back_folder_entry_html());
    }

    files_map = {};
    data.forEach(function (item, i) {
        files_map[item.filename] = item;

        item.get_image_src = get_image_src; // need for
        files_table_container.append(get_file_entry_html(item));
    });

    // update binding
    $('#files-explorer td.cell-filename').bind('click', view_item);
    $('#files-explorer td.cell-type-image').bind('click', view_item);
}

function logout_callback() {
    window.location='/logout';
}

/*************************************/
// error handlers
/*************************************/

function list_files_error(data) {
    switch (data.status) {
        case 404:
            alert('wrong path');
            app_state.current_path = '/';
            update_file_list();
            break;
        case 500:
            alert('server error');
            break;
        default:
            alert('Unknown error: code ' + data.status);
    }
}
