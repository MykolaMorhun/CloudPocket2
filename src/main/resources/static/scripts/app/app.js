
var app_state = {
    current_path: '/',
    files_order: 'Name',
    files_reverse: false,

    selected_files_list: [],

    files_in_buffer: [],
    buffer_path: '',
    buffer_action: ''
};
// map that stores files from current directory: filename -> fileinfo
var files_map;

var files_table_container;
var current_path_input;
var search_input;
var file_input;

var files_orders = {};
var check_all_button;

$(document).ready(function() {
    document.body.innerHTML += get_file_upload_form_template();

    find_elements();
    bind_events();

    update_files_list();
});

function find_elements() {
    files_table_container = $('#files-list');
    current_path_input = $('#current-path-input');
    search_input = $('#search-input');

    files_orders['Type'] = $('#type-column');
    files_orders['Name'] = $('#name-column');
    files_orders['Size'] = $('#size-column');
    files_orders['Creation date'] = $('#date-column');

    file_input = $('#file_input');
    check_all_button = $('#check-all');
}

function bind_events() {
    for (var order in files_orders) {
        if(!files_orders.hasOwnProperty(order)) continue;
        files_orders[order].bind('click', changeFilesOrder);
    }

    current_path_input.bind('keypress', function (event) {
        if(event.keyCode == 13) {
            on_go_path_button_click();
        }
    });
    search_input.bind('keypress', function (event) {
        if(event.keyCode == 13) {
            on_search_button_click();
        }
    });

    file_input.on('change', upload_file);
    check_all_button.bind('click', toggle_all_checkboxes);
}

/*************************************/
// file list event handlers and processors
/*************************************/

function update_files_list() {
    list_files_request(list_files_callback,
                       app_state.current_path,
                       app_state.files_order,
                       app_state.files_reverse);
    current_path_input.val(app_state.current_path);
    app_state.selected_files_list = [];
}

function back_dir() {
    if (app_state.current_path != '/') {
        app_state.current_path = app_state.current_path.split('/').slice(0,-2).join('/') + '/';
        update_files_list();
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
        update_files_list();
    } else {
        view_file(row_id);
    }
}

function view_file(filename) {
    var url = '/open/file?';
    url += 'path=' + app_state.current_path;
    url += '&';
    url += 'filename=' + filename;
    window.open(url,'_blank');
}

function get_selected_files() {
    var selected_files = [];
    for (var key in files_map) {
        if(!files_map.hasOwnProperty(key)) continue;
        if (app_state.selected_files_list.indexOf(files_map[key].filename) > -1) {
            selected_files.push(files_map[key]);
        }
    }
    return selected_files;
}

function toggle_checkbox() {
    var item_id = $(this).closest('tr').attr("id");
    if (this.checked) {
        app_state.selected_files_list.push(item_id);
        document.getElementById(item_id).className = 'selected';
    } else {
        app_state.selected_files_list.splice(app_state.selected_files_list.indexOf(item_id), 1);
        document.getElementById(item_id).className = '';
    }
}

function toggle_all_checkboxes() {
    if (app_state.selected_files_list.length != 0) {
        app_state.selected_files_list = [];
        $('input[id^=checkbox-]').prop('checked', false);
        $('#files-list > tr').removeClass('selected');
    } else {
        $('input[id^=checkbox-]').prop('checked', true);
        $('#files-list > tr').addClass('selected');
        app_state.selected_files_list = [];
        for (var key in files_map) {
            if (files_map.hasOwnProperty(key)) {
                app_state.selected_files_list.push(key);
            }
        }
    }
}

function changeFilesOrder(elem) {
    var newOrder = elem.target.innerText;
    var oldOrder = app_state.files_order;
    if (newOrder === oldOrder) {
        app_state.files_reverse = ! app_state.files_reverse;
    } else {
        files_orders[oldOrder].removeClass('sort_by_it');
        files_orders[newOrder].addClass('sort_by_it');
        app_state.files_order = newOrder;
        app_state.files_reverse = false;
    }
    update_files_list();
}

function upload_file() {
    $('#upload_file_form').ajaxSubmit({
        success: function () {
            update_files_list();
        },
        error: function () {
            alert('Error while sending file to server');
        }
    });
}

function view_file_directory() {
    var full_path = $(this).parent().attr("id");
    var path_to_folder_that_contain_file = full_path.split('/').slice(0,-1).join('/') + '/';
    app_state.current_path = path_to_folder_that_contain_file;
    update_files_list();
}

/*************************************/
// click button handlers
/*************************************/

function on_home_button_click() {
    app_state.current_path = '/';
    update_files_list();
}

function on_copy_button_click() {
    if (app_state.selected_files_list.length == 0) {
        alert('Select files for copying');
        return;
    }

    app_state.buffer_path = app_state.current_path;
    app_state.buffer_action = 'copy';
    app_state.files_in_buffer = app_state.selected_files_list;
}

function on_cut_button_click() {
    if (app_state.selected_files_list.length == 0) {
        alert('Select files for cut');
        return;
    }

    app_state.buffer_path = app_state.current_path;
    app_state.buffer_action = 'cut';
    app_state.files_in_buffer = app_state.selected_files_list;
}

function on_paste_button_click() {
    if (app_state.files_in_buffer.length == 0) {
        alert('Buffer is empty');
        return;
    }
    if (app_state.buffer_path == app_state.current_path) {
        alert('Cannot use the same directory');
        return;
    }
    
    if (app_state.buffer_action == 'copy') {
        copy_files_request(copy_files_callback, app_state.buffer_path, app_state.current_path, app_state.files_in_buffer);
    } else if (app_state.buffer_action == 'cut') {
        move_files_request(move_files_callback, app_state.buffer_path, app_state.current_path, app_state.files_in_buffer);
    }
}

function on_download_button_click() {
    if (app_state.selected_files_list.length === 0) {
        alert('No files selected');
        return;
    }
    if (app_state.selected_files_list.length === 1) {
        download_file_request(download_file_callback, app_state.current_path, app_state.selected_files_list[0]);
    } else {
        download_structure_request(download_structure_callback, app_state.current_path, app_state.selected_files_list);
    }
}

function on_delete_button_click() {
    if (app_state.selected_files_list.length === 0) {
        alert('No files selected');
        return;
    }
    var confirmation = confirm('You want to delete ' + app_state.selected_files_list.length + ' files. Are you sure?');
    if (confirmation) {
        delete_request(delete_callback, app_state.current_path, app_state.selected_files_list);
    }
}

function on_compress_button_click() {
    if (app_state.selected_files_list.length == 0) {
        alert('No files selected');
        return;
    }
    compress_files_request(compress_files_callback, app_state.current_path, app_state.selected_files_list);
}

function on_uncompress_button_click() {
    if (app_state.selected_files_list.length != 1) {
        alert('Select archive');
        return;
    }
    uncompress_files_request(uncompress_files_callback, app_state.current_path, app_state.selected_files_list[0]);
}

function on_rename_button_click() {
    if (app_state.selected_files_list.length != 1) {
        alert('Select file to rename');
        return;
    }
    var old_file_name = app_state.selected_files_list[0];
    var new_file_name = prompt("New file name:", old_file_name);
    if (new_file_name !== null && new_file_name != "" && new_file_name != old_file_name) {
        rename_file_request(rename_file_callback, app_state.current_path, old_file_name, new_file_name);
    }
}

function on_add_file_button_click() {
    document.forms["upload_file"].action = '/api/files/upload/file' + '?' + $.param({'path': app_state.current_path});
    file_input.click();
    update_files_list();
}

function on_add_folder_button_click() {
    var new_directory_name = prompt("New directory:");
    if (new_directory_name !== null && new_directory_name != "") {
        create_directory_request(create_directory_callback, app_state.current_path, new_directory_name)
    }
}

function on_add_structure_button_click() {
    document.forms["upload_file"].action = '/api/files/upload/structure'+
        '?' + $.param({'path': app_state.current_path, 'skipSubfolder': true});
    file_input.click();
    update_files_list();
}

function on_search_button_click() {
    search_input.focus();
    var search_pattern = search_input.val();
    if (search_pattern != '') {
        search_files_request(search_files_callback, app_state.current_path, search_pattern);
    }
}

function on_user_profile_button_click() {
    window.open("/profile/user/","_self")
}

function on_logout_button_click() {
    logout_request(logout_callback);
}

function on_refresh_button_click() {
    update_files_list();
}

function on_go_path_button_click() {
    var new_path = current_path_input.val();
    if (new_path != '') {
        if (!new_path.endsWith('/')) {
            new_path += '/';
        }
        app_state.current_path = new_path;
        update_files_list();
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

    // add .. item if no root dir
    if (app_state.current_path == '/') {
        files_table_container.html("");
    } else {
        files_table_container.html(get_back_folder_entry_html());
    }

    files_map = {};
    data.forEach(function (item, i) {
        files_map[item.filename] = item;

        item.get_image_src = get_image_src;
        item.get_file_size = get_file_size;
        files_table_container.append(get_file_entry_html(item));
    });

    // update binding
    $('#files-explorer td.cell-filename').bind('click', view_item);
    $('#files-explorer td.cell-type-image').bind('click', view_item);
    $('#files-explorer input[type=checkbox]').bind('click', toggle_checkbox);
}

function copy_files_callback(data) {
    if ('status' in data) {
        alert('error while copying files');
        return;
    }
    update_files_list();
}

function move_files_callback(data) {
    if ('status' in data) {
        alert('error while moving files');
        return;
    }
    update_files_list();
}

function download_file_callback() { }

function download_structure_callback() { }

function delete_callback(data) {
    if ('status' in data) {
        alert('failed to delete files');
        return;
    }
    app_state.selected_files_list.forEach(function (item) {
        var elem = document.getElementById(item);
        elem.remove();
    });
    app_state.selected_files_list = [];
}

function compress_files_callback(data) {
    if (data == "") {
        update_files_list();
    } else {
        alert('failed to compress files');
    }
}

function uncompress_files_callback(data) {
    if (data == "") {
        update_files_list();
    } else {
        alert('failed to uncompress archive');
    }
}

function rename_file_callback(data) {
    if (data === undefined) {
        update_files_list();
    } else {
        alert('failed to rename file');
    }
}

function create_directory_callback(data) {
    if (data == "") {
        update_files_list();
    } else {
        alert('failed to create new directory');
    }
}

function search_files_callback(data) {
    if ('status' in data) {
        list_files_error(data);
        return;
    }
    
    current_path_input.val('Search results');
    files_table_container.html("");

    files_map = {};
    for (var full_path in data) {
        var item = data[full_path];
        item.filename = full_path;

        item.get_image_src = get_image_src;
        item.get_file_size = get_file_size;
        files_table_container.append(get_file_entry_html(item));
    }

    // set binding
    $('#files-explorer td.cell-filename').bind('click', view_file_directory);
    $('#files-explorer td.cell-type-image').bind('click', view_file_directory);
    
    $('div[class=squaredCheckbox]').remove();
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
            update_files_list();
            break;
        case 500:
            alert('server error');
            break;
        default:
            alert('Unknown error: code ' + data.status);
    }
}
