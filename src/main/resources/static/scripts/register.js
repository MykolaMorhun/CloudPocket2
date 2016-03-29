
$(document).ready(function() {
    document.getElementById("password").onkeyup = validatePassword;
    document.getElementById("confirm-password").onkeyup = validatePassword;
});

function validatePassword() {
    var password1 = document.getElementById("password").value;
    var password2 = document.getElementById("confirm-password").value;
    if(password1 != password2) {
        document.getElementById("confirm-password").setCustomValidity("Passwords do not match.");
    } else {
        document.getElementById("confirm-password").setCustomValidity(""); //empty string means no validation error
    }
}