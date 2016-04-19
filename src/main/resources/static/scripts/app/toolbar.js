
var toolbar_buttons;

$(document).ready(function() {
    toolbar_buttons = {
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
    
    toolbar_buttons.home.bind('click', on_home_button_click);
    toolbar_buttons.copy.bind('click', on_copy_button_click);
    toolbar_buttons.cut.bind('click', on_cut_button_click);
    toolbar_buttons.paste.bind('click', on_paste_button_click);
    toolbar_buttons.download.bind('click', on_download_button_click);
    toolbar_buttons.delete.bind('click', on_delete_button_click);
    toolbar_buttons.compress.bind('click', on_compress_button_click);
    toolbar_buttons.uncompress.bind('click', on_uncompress_button_click);
    toolbar_buttons.rename.bind('click', on_rename_button_click);
    toolbar_buttons.add_file.bind('click', on_add_file_button_click);
    toolbar_buttons.add_folder.bind('click', on_add_folder_button_click);
    toolbar_buttons.add_structure.bind('click', on_add_structure_button_click);
    toolbar_buttons.search.bind('click', on_search_button_click);
    toolbar_buttons.user_profile.bind('click', on_user_profile_button_click);
    toolbar_buttons.logout.bind('click', on_logout_button_click);
    toolbar_buttons.refresh.bind('click', on_refresh_button_click);
    toolbar_buttons.go_path.bind('click', on_go_path_button_click);
});
