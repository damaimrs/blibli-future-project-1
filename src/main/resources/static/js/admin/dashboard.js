var baseUrl = 'http://localhost:9090/';
var baseApiUrl = baseUrl + 'api/';

var changeCursor = function (obj) {
    $('#' + obj.id).css('cursor', 'pointer');
};

//fungsi untuk mendapatkan jumlah role dan ditampilkan pada dashboard admin
var getRoleCount = function () {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'role/count', //url yang akan dituju, ada pada roleAPI
        type: 'GET', // Tipe pengaksesan url
        data: {
            userId: userId
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            //untuk menampilkan data yang sudah didapatkan dari API
            $('#role-count').show();
            $('#role-count').text(data.data);
            //loader (yang muter" saat roleCount belum didapat akan dihide)
            $('#loader-role-count').hide();
        },
        error: function (error) {
            console.log(error);
        }
    });
};

//fungsi yang digunakan untuk mendapatkan jumlah user
var getUserCount = function () {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'user/count', //API yang dituju pada UserAPI
        type: 'GET', // Tipe pengaksesan url
        data: {
            userId: userId
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            //menampilkan data yang didapatkan dari API
            $('#user-count').show();
            $('#user-count').text(data.data);
            //loader dihide
            $('#loader-user-count').hide();
        },
        error: function (error) {
            console.log(error);
        }
    });
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
    getRoleCount();
    getUserCount();
});