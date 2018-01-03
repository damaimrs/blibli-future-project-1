var page = 1;
var pageSize = 10;
var sort = 1;
var dateSort = 1;
var tableSort = 3;
var waitingTimeSort = 5;
var totalSort = 7;
var cashierSort = 9;
var startDate, endDate;

var baseUrl = 'http://localhost:9090/';
var baseApiUrl = baseUrl + 'api/';
var purchaseCount;

var numRow = 0;

var changeCursor = function (obj) {
    $('#' + obj.id).css('cursor', 'pointer');
};

/**
 * Fungsi untuk mengambil jumlah pemesanan
 */
var getPurchaseCount = function () {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'purchase/count/status/PAID', // Url yang ingin diakses
        type: 'GET', // Tipe pengaksesan url
        async: false,
        data: {
            userId: userId
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            purchaseCount = data.data;
        },
        error: function (error) {
            console.log(error);
        }
    });
};

var getPurchase = function (page, pageSize, sort) {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'purchase',
        type: 'GET', // Tipe pengaksesan url
        data: {
            userId: userId,
            purchaseStatus: "PAID",
            startDate: startDate,
            endDate: endDate,
            page: page,
            pageSize: pageSize,
            sort: sort
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            var tableTBody = $('#purchases-table-body');
            tableTBody.find('tr').remove();

            for (var i = 0; i < data.data.length; i++) {
                numRow ++;
                var purchaseId = data.data[i].purchaseId;
                var purchaseDate = new Date(data.data[i].purchaseDate);
                var purchaseItems = '';

                for (var j = 0; j < data.data[i].purchaseItems.length; j++) {
                    var menuPrice = data.data[i].purchaseItems[j].menu.menuPrice;
                    menuPrice = menuPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
                    purchaseItems +=
                        '<tr>' +
                            '<td>' + (j + 1) + '</td>' +
                            '<td>' + data.data[i].purchaseItems[j].menu.menuName + '</td>' +
                            '<td>' + data.data[i].purchaseItems[j].menu.menuCategory.menuCategoryName + '</td>' +
                            '<td>' + 'Rp. ' + menuPrice + ',- ' + '</td>' +
                            '<td>' + data.data[i].purchaseItems[j].menu.menuWaitingTime + ' Menit ' + '</td>' +
                            '<td>' + data.data[i].purchaseItems[j].purchaseItemQuantity + '</td>' +
                        '</tr>';
                }

                tableTBody.append(
                    '<tr id="menus-row-' + purchaseId + '">' +
                        '<td>' + (numRow) + '</td>' +
                        '<td>' +
                            purchaseDate.getHours() + ':' + purchaseDate.getMinutes() + ':' + purchaseDate.getSeconds() + ', ' +
                            purchaseDate.getDate() + '/' + (purchaseDate.getMonth() + 1) + '/' + purchaseDate.getFullYear() +
                        '</td>' +
                        '<td>' + data.data[i].purchaseTable + '</td>' +
                        '<td>' + data.data[i].purchaseWaitingTime + ' Menit ' + '</td>' +
                        '<td>' + 'Rp. ' + data.data[i].purchaseTotal + ',- ' + '</td>' +
                        '<td>' + data.data[i].cashier.userName + '</td>' +
                        '<td>' +
                            '<div class="btn-group">' +
                                '<button class="btn btn-info" data-toggle="modal" data-target="#detailModal-' + purchaseId + '">Detail</button>' +
                                '<div class="modal fade" id="detailModal-' + purchaseId + '" role="dialog">' +
                                    '<div class="modal-dialog">' +
                                        '<div class="modal-content">' +
                                            '<div class="modal-header">' +
                                                '<button type="button" class="close" data-dismiss="modal">&times;</button>' +
                                                '<h3 class="modal-title">Detail Pembelian</h3>' +
                                                '<h6 class="modal-title">Tanggal : ' +
                                                    purchaseDate.getHours() + ':' + purchaseDate.getMinutes() + ':' + purchaseDate.getSeconds() + ', ' +
                                                    purchaseDate.getDate() + '/' + (purchaseDate.getMonth() + 1) + '/' + purchaseDate.getFullYear() +
                                                '</h6>' +
                                            '</div>' +
                                            '<div class="modal-body">' +
                                                '<div class="form-group">' +
                                                    '<label>Nomor meja : </label>' +
                                                    '<input type="number" class="form-control" value="' + data.data[i].purchaseTable + '" disabled="disabled" />' +
                                                '</div>' +
                                                '<hr />' +
                                                '<table class="table table-hover">' +
                                                    '<thead>' +
                                                        '<tr>' +
                                                            '<th>No</th>' +
                                                            '<th>Nama</th>' +
                                                            '<th>Kategori Menu</th>' +
                                                            '<th>Harga</th>' +
                                                            '<th>Waktu Tunggu</th>' +
                                                            '<th>Jumlah</th>' +
                                                            '<th></th>' +
                                                        '</tr>' +
                                                    '</thead>' +
                                                    '<tbody id="purchase-items-table-body">' +
                                                        purchaseItems +
                                                    '</tbody>' +
                                                '</table>' +
                                                '<hr />' +
                                                '<div class="form-group">' +
                                                    '<label>Waktu tunggu (Menit) : </label>' +
                                                    '<input type="number" class="form-control" value="' + data.data[i].purchaseWaitingTime + '" disabled="disabled" />' +
                                                '</div>' +
                                                '<div class="form-group">' +
                                                    '<label>Total (Rupiah) : </label>' +
                                                    '<input type="number" class="form-control" value="' + data.data[i].purchaseTotal + '" disabled="disabled" />' +
                                                '</div>' +
                                            '</div>' +
                                            '<div class="modal-footer">' +
                                                '<button id="btn-print" type="submit" class="btn btn-success" onclick="window.print();">Cetak Invoice</button>' +
                                            '</div>' +
                                        '</div>' +
                                    '</div>' +
                                '</div>' +
                            '</div>' +
                        '</td>' +
                    '</tr>'
                );
            }
        },
        error: function (error) {
            console.log(error);
        }
    });
};

var movePage = function (newPage, pageSize, sort) {
    getPurchaseCount();

    if (page === 1){
        numRow = 0;
    }

    var purchasePage = Math.ceil(purchaseCount / 10);

    if (newPage > purchasePage)
        newPage = page;

    if (newPage > 1)
        $('#pagination-back').show();
    else
        $('#pagination-back').hide();

    if (newPage < purchasePage)
        $('#pagination-next').show();
    else
        $('#pagination-next').hide();

    $('#pagination-page').text(newPage);
    $('#pagination-page-end').text(purchasePage);

    getPurchase(newPage, pageSize, sort);
};

var nextPage = function () {
    page += 1;
    movePage(page, pageSize, sort);
};

var backPage = function () {
    page -= 1;
    movePage(page, pageSize, sort);
};

var endPage = function () {
    page = parseInt((menuCount / 10) + 1);
    movePage(page, pageSize, sort);
};

var changeSortDate = function () {
    var isAsc = dateSort === 1;

    if (isAsc) {
        dateSort = 2;
        sort = 2;

        $('#purchases-table-purchase-date-icon').attr('src', baseUrl + 'icon/sort-down.png');
    } else {
        dateSort = 1;
        sort = 1;

        $('#purchases-table-purchase-date-icon').attr('src', baseUrl + 'icon/sort-up.png');
    }

    movePage(page, pageSize, sort);
};

var changeSortTable = function () {
    var isAsc = tableSort === 3;

    if (isAsc) {
        tableSort = 4;
        sort = 4;

        $('#purchases-table-purchase-table-icon').attr('src', baseUrl + 'icon/sort-down.png');
    } else {
        tableSort = 3;
        sort = 3;

        $('#purchases-table-purchase-table-icon').attr('src', baseUrl + 'icon/sort-up.png');
    }

    movePage(page, pageSize, sort);
};

var changeSortWaitingTime = function () {
    var isAsc = waitingTimeSort === 5;

    if (isAsc) {
        waitingTimeSort = 6;
        sort = 6;

        $('#purchases-table-purchase-waiting-time-icon').attr('src', baseUrl + 'icon/sort-down.png');
    } else {
        waitingTimeSort = 5;
        sort = 5;

        $('#purchases-table-purchase-waiting-time-icon').attr('src', baseUrl + 'icon/sort-up.png');
    }

    movePage(page, pageSize, sort);
};

var changeSortTotal = function () {
    var isAsc = totalSort === 7;

    if (isAsc) {
        totalSort = 8;
        sort = 8;

        $('#purchases-table-purchase-total-icon').attr('src', baseUrl + 'icon/sort-down.png');
    } else {
        totalSort = 7;
        sort = 7;

        $('#purchases-table-purchase-total-icon').attr('src', baseUrl + 'icon/sort-up.png');
    }

    movePage(page, pageSize, sort);
};

var changeSortCashier = function () {
    var isAsc = cashierSort === 9;

    if (isAsc) {
        cashierSort = 10;
        sort = 10;

        $('#purchases-table-purchase-cashier-icon').attr('src', baseUrl + 'icon/sort-down.png');
    } else {
        cashierSort = 9;
        sort = 9;

        $('#purchases-table-purchase-cashier-icon').attr('src', baseUrl + 'icon/sort-up.png');
    }

    movePage(page, pageSize, sort);
};

var onStartDateChanged = function () {
    var value = $('#start-date').val();

    if (value !== '') {
        var valueDateObj = new Date(value);
        var endDateObj = new Date(endDate);

        if (valueDateObj >= endDateObj) {
            valueDateObj.setDate(valueDateObj.getDate() + 1);

            var day = "0" + valueDateObj.getDate();
            var month = ("0" + (valueDateObj.getMonth() + 1));

            day = day.slice(-2);
            month = month.slice(-2);

            endDate = valueDateObj.getFullYear() + "-" + month + "-" + day;

            $('#end-date').val(endDate);
        }

        startDate = value;
        movePage(page, pageSize, sort);
    }
};

var onEndDateChanged = function () {
    var value = $('#end-date').val();

    if (value !== '') {
        var valueDateObj = new Date(value);
        var startDateObj = new Date(startDate);

        if (valueDateObj <= startDateObj) {
            valueDateObj.setDate(valueDateObj.getDate() - 1);

            var day = "0" + valueDateObj.getDate();
            var month = ("0" + (valueDateObj.getMonth() + 1));

            day = day.slice(-2);
            month = month.slice(-2);

            startDate = valueDateObj.getFullYear() + "-" + month + "-" + day;

            $('#start-date').val(startDate);
        }

        endDate = value;
        movePage(page, pageSize, sort);
    }
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

$(document).ready(function () {
    var date = new Date();

    var startDay = "0" + date.getDate();
    var endDay = "0" + (date.getDate() + 1);
    var month = ("0" + (date.getMonth() + 1));

    startDay = startDay.slice(-2);
    endDay = endDay.slice(-2);
    month = month.slice(-2);

    startDate = date.getFullYear() + "-" + month + "-" + startDay;
    endDate = date.getFullYear() + "-" + month + "-" + endDay;

    $('#start-date').val(startDate);
    $('#end-date').val(endDate);

    movePage(page, pageSize, sort);
});