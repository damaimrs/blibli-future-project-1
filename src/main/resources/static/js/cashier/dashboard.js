var baseUrl = 'http://localhost:9090/';
var baseApiUrl = baseUrl + 'api/';

var changeCursor = function (obj) {
    $('#' + obj.id).css('cursor', 'pointer');
};

//fungsi untuk mendapatkan jumlah menu terdaftar
var getMenuCount = function () {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'menu/count', // Url yang ingin diakses
        type: 'GET', // Tipe pengaksesan url
        data: {
            userId: userId // Content parameter yang dikirim saat request
        },
        success: function (data) {
            //untuk nampilin sesuatu di id menu-count
            $('#menu-count').show();
            //untuk merubah isisnya dengan data.data
            $('#menu-count').text(data.data);

            //loader yg muter" pas datanya ditampilin dia dihide
            $('#loader-menu-count').hide();
        },
        error: function (error) {
            console.log(error);
        }
    });
};

/**
 * Fungsi untuk mengambil jumlah pemebelian hari ini
 */
var getPurchaseTodayCount = function () {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'purchase/count/date', // Url yang ingin diakses
        type: 'GET', // Tipe pengaksesan url
        data: {
            userId: userId
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            $('#purchase-today-count').show();
            $('#purchase-today-count').text(data.data);

            $('#loader-purchase-today-count').hide();
        },
        error: function (error) {
            console.log(error);
        }
    });
};

//fungsi untuk mendapatkan jumlah pemesanan dengan status progress
var getPurchaseProgressCount = function () {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'purchase/count/status/PROGRESS', // Url yang ingin diakses
        type: 'GET', // Tipe pengaksesan url
        data: {
            userId: userId
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            $('#purchase-progress-count').show();
            $('#purchase-progress-count').text(data.data);

            $('#loader-purchase-progress-count').hide();
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
    getMenuCount();
    getPurchaseTodayCount();
    getPurchaseProgressCount();
});