
var app_state = {
    current_path: '/',
    files_order: 'NAME',
    files_reverse: false,

    selected_files_list: [],

    files_in_buffer: [],
    buffer_path: '',
    buffer_action: '',

    is_file_uploading: false
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

    files_orders['TYPE'] = $('#type-column');
    files_orders['NAME'] = $('#name-column');
    files_orders['SIZE'] = $('#size-column');
    files_orders['CREATION_DATE'] = $('#date-column');

    file_input = $('#file_input');
    check_all_button = $('#check-all');

    files_context_menu = $('#files_context_menu');
}

function bind_events() {
    for (var order in files_orders) {
        if(!files_orders.hasOwnProperty(order)) continue;
        files_orders[order].on('click', change_files_order);
    }

    current_path_input.on('keypress', function (event) {
        if(event.keyCode == 13) {
            on_go_path_button_click();
        }
    });
    search_input.on('keypress', function (event) {
        if(event.keyCode == 13) {
            on_search_button_click();
        }
    });

    file_input.on('change', upload_file);
    check_all_button.on('click', toggle_all_checkboxes);

    files_context_menu.on('mouseleave', hide_context_menu);
    $.each(files_context_menu_items, function(key, value) {
       value.on('click', hide_context_menu);
    });
}

/*************************************/
// files list event handlers and processors
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
    open_item(row_id);
}

function open_item(item_id) {
    if (item_id === '..') {
        back_dir();
        return;
    }
    if (files_map[item_id].directory === true) {
        app_state.current_path += item_id + '/';
        update_files_list();
    } else {
        view_file(item_id);
    }
}

function view_file(filename) {
    var url = '/file/open?';
    url += 'path=' + app_state.current_path;
    url += '&';
    url += 'filename=' + filename;
    window.open(url,'_blank');
}

function edit_file(filename) {
    var url = '/file/edit?';
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

function change_files_order(elem) {
    var newOrder = $(elem.target).attr('order');
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
    if (app_state.is_file_uploading) {
        alert("You cannot upload files simultaneously");
        return;
    }

    app_state.is_file_uploading = true;
    var filename;
    $('#upload_file_form').ajaxSubmit({
        beforeSend: function() {
            filename = $('#file_input').val().split(/[\\/]/).pop();
            upload_file_popup_reset(filename);
        },
        uploadProgress: function(event, position, total, percentComplete) {
            upload_file_popup_update_progress(percentComplete);
        },
        success: function () {
            upload_file_popup_success();
            show_notification('File \'' + filename + '\' uploaded successfully');

            update_files_list();
        },
        error: function () {
            upload_file_popup_error();
            alert('Error while sending \'' + filename + '\' file to server');
        },
        complete: function () {
            app_state.is_file_uploading = false;
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

    show_notification('Copied to buffer');
}

function on_cut_button_click() {
    if (app_state.selected_files_list.length == 0) {
        alert('Select files for cut');
        return;
    }

    app_state.buffer_path = app_state.current_path;
    app_state.buffer_action = 'cut';
    app_state.files_in_buffer = app_state.selected_files_list;

    show_notification('Now click `Paste` in destination directory.');
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
        app_state.files_in_buffer = [];
    }
}

function on_download_button_click() {
    if (app_state.selected_files_list.length === 0) {
        alert('No files selected');
        return;
    }
    if (app_state.selected_files_list.length === 1 && files_map[app_state.selected_files_list[0]].directory === false) {
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
    rename_item(app_state.selected_files_list[0]);
}

function rename_item(item_id) {
    var old_file_name = item_id;
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
// context menu handlers
/*************************************/

var files_context_menu;
var context_menu_current_item_id;

var show_context_menu = function(event) {
    event.preventDefault();

    context_menu_current_item_id = event.currentTarget.id;

    files_context_menu.css('left', event.clientX - 1); // to move mouse pointer on context menu
    files_context_menu.css('top', event.clientY - 1);

    files_context_menu.fadeIn(200);
};

function hide_context_menu() {
    files_context_menu.fadeOut(200);
}

function on_context_menu_open_click() {
    open_item(context_menu_current_item_id);
}

function on_context_menu_edit_click() {
    if (files_map[context_menu_current_item_id].directory === false) {
        edit_file(context_menu_current_item_id);
    } else {
        alert('Cannot edit a directory.');
    }
}

function on_context_menu_download_click() {
    if (files_map[context_menu_current_item_id].directory === false) {
        download_file_request(download_file_callback, app_state.current_path, context_menu_current_item_id);
    } else {
        download_structure_request(download_structure_callback, app_state.current_path, [context_menu_current_item_id]);
    }
}

function on_context_menu_delete_click() {
    var confirmation = confirm('You want to delete "' + context_menu_current_item_id + '". Are you sure?');
    if (confirmation) {
        delete_request(function(data) { context_menu_delete_callback(data, context_menu_current_item_id) },
                       app_state.current_path, [context_menu_current_item_id]);
    }
}

function on_context_menu_rename_click() {
    if (context_menu_current_item_id == '..') {
      alert('Not allowed.');
      return;
    }

    rename_item(context_menu_current_item_id);
}

function on_context_menu_details_click() {
    get_detailed_file_info_request(get_detailed_file_info_callback, app_state.current_path, context_menu_current_item_id);
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
    $('#files-explorer td.cell-filename').on('click', view_item);
    $('#files-explorer td.cell-type-image').on('click', view_item);
    $('#files-explorer input[type=checkbox]').on('click', toggle_checkbox);
    $('#files-list > tr').on("contextmenu", show_context_menu);
}

function copy_files_callback(data) {
    if ('status' in data) {
        alert('Error while copying files');
        return;
    }
    show_notification('Copied');
    update_files_list();
}

function move_files_callback(data) {
    if ('status' in data) {
        alert('Error while moving files');
        return;
    }
    show_notification('Moved');
    update_files_list();
}

function download_file_callback() { }

function download_structure_callback() { }

function delete_callback(data) {
    if ('status' in data) {
        alert('Failed to delete files');
        return;
    }
    show_notification('Deleted');
    app_state.selected_files_list.forEach(function (item) {
        document.getElementById(item).remove();
    });
    app_state.selected_files_list = [];
}

function context_menu_delete_callback(data, item_id) {
    if ('status' in data) {
        alert('Failed to delete ' + item_id);
        return;
    }
    show_notification('Deleted');
    document.getElementById(item_id).remove();
}

function compress_files_callback(data) {
    if (data == "") {
        show_notification('Compressed');
        update_files_list();
    } else {
        alert('Failed to compress files');
    }
}

function uncompress_files_callback(data) {
    if (data == "") {
        show_notification('Extracted');
        update_files_list();
    } else {
        alert('Failed to uncompress archive');
    }
}

function rename_file_callback(data) {
    if (data === undefined) {
        show_notification('Renamed');
        update_files_list();
    } else {
        alert('Failed to rename file');
    }
}

function create_directory_callback(data) {
    if (data == "") {
        show_notification('Directory created');
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

    show_notification(function (number_of_found_files) {
        switch(number_of_found_files) {
            case 0:
                return 'Nothing found';
            case 1:
                return '1 result found';
            default:
                return number_of_found_files + ' results found';
        }
    } (Object.keys(data).length));
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
    $('#files-explorer td.cell-filename').on('click', view_file_directory);
    $('#files-explorer td.cell-type-image').on('click', view_file_directory);
    
    $('div[class=squaredCheckbox]').remove();
}

function get_detailed_file_info_callback(data) {
    if ('status' in data) {
        alert('Error while getting file details.');
        return;
    }

    var info = '';
    for (var key in data) {
        info += key + ': ' + data[key] + '\n';
    }

    alert(info);
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
        case 403:
            alert('forbidden');
            break;
        case 503:
            alert('server is down');
            break;
        default:
            alert('Unknown error: code ' + data.status);
    }
}
