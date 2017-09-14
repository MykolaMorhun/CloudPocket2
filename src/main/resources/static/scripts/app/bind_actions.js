
$(document).ready(function() {
    var toolbar_buttons = {
        home:          $('#btn-home'),
        copy:          $('#btn-copy'),
        cut:           $('#btn-cut'),
        paste:         $('#btn-paste'),
        download:      $('#btn-download'),
        delete:        $('#btn-delete'),
        compress:      $('#btn-compress'),
        uncompress:    $('#btn-uncompress'),
        rename:        $('#btn-rename'),
        add_file:      $('#btn-add_file'),
        add_folder:    $('#btn-add_folder'),
        add_structure: $('#btn-add_structure'),
        search:        $('#btn-search'),
        user_profile:  $('#btn-user_profile'),
        logout:        $('#btn-logout'),
        refresh:       $('#btn-refresh'),
        go_path:       $('#btn-go_path')
    };
    
    toolbar_buttons.home.on('click', on_home_button_click);
    toolbar_buttons.copy.on('click', on_copy_button_click);
    toolbar_buttons.cut.on('click', on_cut_button_click);
    toolbar_buttons.paste.on('click', on_paste_button_click);
    toolbar_buttons.download.on('click', on_download_button_click);
    toolbar_buttons.delete.on('click', on_delete_button_click);
    toolbar_buttons.compress.on('click', on_compress_button_click);
    toolbar_buttons.uncompress.on('click', on_uncompress_button_click);
    toolbar_buttons.rename.on('click', on_rename_button_click);
    toolbar_buttons.add_file.on('click', on_add_file_button_click);
    toolbar_buttons.add_folder.on('click', on_add_folder_button_click);
    toolbar_buttons.add_structure.on('click', on_add_structure_button_click);
    toolbar_buttons.search.on('click', on_search_button_click);
    toolbar_buttons.user_profile.on('click', on_user_profile_button_click);
    toolbar_buttons.logout.on('click', on_logout_button_click);
    toolbar_buttons.refresh.on('click', on_refresh_button_click);
    toolbar_buttons.go_path.on('click', on_go_path_button_click);
});

var files_context_menu_items

$(document).ready(function() {
    files_context_menu_items = {
        open:     $('#files-context-menu-open'),
        edit:     $('#files-context-menu-edit'),
        download: $('#files-context-menu-download'),
        delete:   $('#files-context-menu-delete'),
        rename:   $('#files-context-menu-rename'),
        details:  $('#files-context-menu-details')
    }

    files_context_menu_items.open.on('click', on_context_menu_open_click);
    files_context_menu_items.edit.on('click', on_context_menu_edit_click);
    files_context_menu_items.download.on('click', on_context_menu_download_click);
    files_context_menu_items.delete.on('click', on_context_menu_delete_click);
    files_context_menu_items.rename.on('click', on_context_menu_rename_click);
    files_context_menu_items.details.on('click', on_context_menu_details_click);
});
