
var toolbar_buttons;
var buttons;
var form_elements;

var current_user_data;

var user_data_updated = false;

$(document).ready(function() {
    find_elements();
    bind_events();

    fill_form_with_actual_user_data();
});

function find_elements() {
    toolbar_buttons = {
        home:     $('#btn-home'),
        profile:  $('#btn-user_profile'),
        settings: $('#btn-settings'),
        logout:   $('#btn-logout'),
        
        delete_account: $('#btn-delete_account') 
    };
    buttons = {
        save: $('#save_button'),
        back: $('#back_button')
    };
    form_elements = {
        login_input:     $('#login'),
        email_input:     $('#email'),
        password1_input: $('#password1'),
        password2_input: $('#password2')
    };
}

function bind_events() {
    toolbar_buttons.home.bind('click', on_home_tool_button_click);
    toolbar_buttons.profile.bind('click', on_profile_button_click);
    toolbar_buttons.settings.bind('click', on_settings_tool_button_click);
    toolbar_buttons.logout.bind('click', on_logout_tool_button_click);
    toolbar_buttons.delete_account.bind('click', on_delete_account_tool_button_click);

    buttons.back.bind('click', on_back_button_click);
    buttons.save.bind('click', on_save_button_click);
}

function fill_form_with_actual_user_data() {
    read_user_data();

    form_elements.login_input.val(current_user_data.login);
    form_elements.login_input.prop('disabled', true);
    form_elements.email_input.val(current_user_data.email);
    form_elements.password1_input.val('');
    form_elements.password2_input.val('');
}

function read_user_data() {
    get_user_info_request(read_user_data_callback, false);
}

function save_user_data() {
    var new_email = form_elements.email_input.val();
    var new_password = form_elements.password1_input.val();
    var new_password_confirm = form_elements.password2_input.val();

    if (! form_elements.email_input[0].validity.valid) {
        alert('e-mail is not valid');
        return;
    }
    if (new_password != new_password_confirm) {
        alert('passwords do not match');
        return;
    }

    if (current_user_data.email == new_email) {
        new_email = null;
    }
    if (new_password == '') {
        new_password = null;
    }
    if (new_email == null && new_password == null) {
        alert('nothing to update');
        return;
    }
    update_user_info_request(update_user_data_callback, current_user_data.login, new_password, new_email, false);
}

/*************************************/
// events handlers
/*************************************/

function on_home_tool_button_click() {
    window.open("/storage/","_self");
}

function on_profile_button_click() {
    window.open("/profile/user/","_self");
}

function on_settings_tool_button_click() {
    // TODO implement user settings e.g. replace files on copy or not
}

function on_logout_tool_button_click() {
    logout_request(logout_callback);
}

function on_delete_account_tool_button_click() {
    var confirmation = prompt("You want to delete this account. " +
        "It will delete ALL your files and data.\n" +
        "To confirm deletion input your login:");
    if (confirmation !== null) {
        if (current_user_data.login === confirmation) {
            delete_account_request(delete_account_callback, current_user_data.login);
        } else {
            alert('Wrong login. Canceled');
        }
    }
}

function on_back_button_click() {
    on_home_tool_button_click();
}

function on_save_button_click() {
    save_user_data();
    if (user_data_updated) {
        fill_form_with_actual_user_data();
        user_data_updated = false;
    }
}

/*************************************/
// callbacks
/*************************************/

function read_user_data_callback(userdata) {
    current_user_data = userdata;
}

function update_user_data_callback() {
    user_data_updated = true;
}

function logout_callback() {
    window.location='/logout';
}

function delete_account_callback() {
    on_logout_tool_button_click();
}
