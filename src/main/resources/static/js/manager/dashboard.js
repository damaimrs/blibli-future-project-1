var baseUrl = 'http://localhost:9090/';
var baseApiUrl = baseUrl + 'api/';

var changeCursor = function (obj) {
    $('#' + obj.id).css('cursor', 'pointer');
};

/**
 * Fungsi untuk mengambil jumlah menu
 */
var getMenuCount = function () {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'menu/count', // url yang dituju ada pada menuAPI
        type: 'GET', // Tipe pengaksesan url
        data: {
            userId: userId
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            $('#menu-count').show();
            $('#menu-count').text(data.data);

            $('#loader-menu-count').hide();
        },
        error: function (error) {
            console.log(error);
        }
    });
};

/**
 * Fungsi untuk mengambil jumlah menu category
 */
var getMenuCategoryCount = function () {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'menu-category/count', //url yang dituju, ada pada menuCategoryAPI
        type: 'GET', // Tipe pengaksesan url
        data: {
            userId: userId
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            $('#menu-category-count').show();
            $('#menu-category-count').text(data.data);

            $('#loader-menu-category-count').hide();
        },
        error: function (error) {
            console.log(error);
        }
    });
};

/**
 * Fungsi untuk mengambil jumlah pembelian
 */
var getPurchasePaidCount = function () {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'purchase/count/status/PAID', //url yang dituju, ada pada purchaseAPI
        type: 'GET', // Tipe pengaksesan url
        data: {
            userId: userId
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            $('#purchase-paid-count').show();
            $('#purchase-paid-count').text(data.data);

            $('#loader-purchase-paid-count').hide();
        },
        error: function (error) {
            console.log(error);
        }
    });
};

//fungsi untuk mendapatkan total pendapatan dari seluruh pembelian
var getTotalPurchaseTotal = function () {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'purchase/total', //url yang dituju ada pada purchaseAPI
        type: 'GET', // Tipe pengaksesan url
        data: {
            userId: userId
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            $('#purchase-total-count').show();
            $('#purchase-total-count').text(data.data);

            $('#loader-purchase-total-count').hide();
        },
        error: function (error) {
            console.log(error);
        }
    });
};

//fungsi untuk mendapatkan quantity total produk terjual
var getTotalPurchaseItemQuantity = function () {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'purchaseitem/quantity/total', //url yang dituju, ada pada purchaseItemAPI
        type: 'GET', // Tipe pengaksesan url
        data: {
            userId: userId
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            $('#product-sold-count').show();
            $('#product-sold-count').text(data.data);

            $('#loader-product-sold-count').hide();
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
    getMenuCategoryCount();
    getPurchasePaidCount();
    getTotalPurchaseTotal();
    getTotalPurchaseItemQuantity();
});