var baseUrl = 'http://localhost:9090/';
var isEmailOk = false;
var validateUserEmail = function (userEmail) { // Fungsi untuk mendeteksi apakah email yang diinputkan sesuai dengan kriteria email
    var reg = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
    isEmailOk = reg.test(userEmail.value);

    $('#btn-submit').prop('disabled', !isEmailOk);
};

var login = function () {
    event.preventDefault();

    var data = {};

    data['userEmail'] = $('#user-email').val(); // Mengambil value dari input user-email
    data['userPassword'] = $('#user-password').val(); // Mengambil value dari input user-password

    $('#btn-submit').prop('disabled', true);

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseUrl + 'login', //yang diget diatas dibawa ke /login di controller
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        dataType: 'json',
        success: function (data) {
            alert(data.message);

            if (data.data !== null)
                location.assign(baseUrl);
            else
                $('#btn-submit').prop('disabled', false);
        },
        error: function (error) {
            console.log(error);
            $('#btn-submit').prop('disabled', false);
        }
    });
};