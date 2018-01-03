var baseUrl = 'http://localhost:9090/';
var baseApiUrl = baseUrl + 'api/';
var user;

var changeCursor = function (obj) {
    $('#' + obj.id).css('cursor', 'pointer');
};

var getUserInfo = function () {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'user/' + userId,
        type: 'GET', // Tipe pengaksesan url
        async: false,
        data: {
            userIdLogin: userId
        },
        success: function (data) {
            user = data.data;

            $('#manager-email').text(user.userEmail);

            $('#user-name').val(user.userName);
            $('#user-email').val(user.userEmail);
            $('#user-phone').val(user.userPhone);
            $('#user-address').val(user.userAddress);
            $('#user-role').val(user.role.roleName);
        },
        error: function (error) {
            console.log(error);
        }
    });
};

var addUser = function (data) {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'user/' + userId,
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        dataType: 'json',
        success: function (data) {
            alert(data.message);

            if (data.data === 1) {
                getUserInfo();
            }
        },
        error: function (error) {
            console.log(error);
        }
    });
};

var submitFormEdit = function () {
    event.preventDefault();

    user.userName = $('#user-name').val();
    user.userEmail = $('#user-email').val();
    user.userPassword = $('#user-password').val();
    user.userPhone = $('#user-phone').val();
    user.userAddress = $('#user-address').val();

    addUser(user);
};

var logout = function () {

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseUrl + 'logout',
        type: 'POST', // Tipe pengaksesan url
        success: function (data) {
            alert(data.message);

            if (data.data === 1) {
                location.assign(baseUrl);
            }
        },
        error: function (error) {
            console.log(error);
        }
    });
};

/**
 * Fungsi saat document telah siap
 */
$(document).ready(function () {
    getUserInfo();
});