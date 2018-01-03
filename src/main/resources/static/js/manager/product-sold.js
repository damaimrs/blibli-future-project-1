var page = 1;
var pageSize = 10;
var sort = 1;
var nameSort = 1;
var priceSort = 3;
var waitingTimeSort = 5;

var baseUrl = 'http://localhost:9090/';
var baseApiUrl = baseUrl + 'api/';
var menuCount;
var menuCategoryData;
var menusFilterBy = 0;
var searchText;
var menuStatus = [];

var numRow = 0;

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
        url: baseApiUrl + 'menu/count', //url yang dituju, ada pada menuAPI
        type: 'GET', // Tipe pengaksesan url
        async: false,
        data: {
            userId: userId
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            menuCount = data.data;  //data yang didapatkan
        },
        error: function (error) {
            console.log(error);
        }
    });
};

//fungsi untuk mendapatkan dan menampilkan kategori menu, untuk menampilkan menu kategori saja bukan untuk menampilkan data menu yang difilter berdasarkan kategori menu
var getMenuCategories = function () {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'menu-category/all', //url yang dituju, ada pada menuCatogoryAPI
        type: 'GET', // Tipe pengaksesan url
        async: false,
        data: {
            userId: userId
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            //menyimpan isi dari id menus-filter-by pada variabel selectMenusFIlterBy
            var selectMenusFilterBy = $('#menus-filter-by');

            selectMenusFilterBy.find('option').remove();
            //menyimpan data pada variabel menuCategoryData
            menuCategoryData = data.data;

            //jika tidak ada filter yang digunakan
            if (menusFilterBy === 0)
                //set yang diselect pada filter All
                selectMenusFilterBy.append('<option value="0" selected="selected">All</option>');
            else
                selectMenusFilterBy.append('<option value="0">All</option>');

            for (var i = 0; i < menuCategoryData.length; i++) {
                var menuCategoryId = menuCategoryData[i].menuCategoryId;
                //set filter yang di select pada menu kategori tertentu
                if (menusFilterBy === menuCategoryId)
                    selectMenusFilterBy.append('<option value="' + menuCategoryId + '" selected="selected">' + menuCategoryData[i].menuCategoryName + '</option>');
                else
                    selectMenusFilterBy.append('<option value="' + menuCategoryId + '">' + menuCategoryData[i].menuCategoryName + '</option>');
            }
        },
        error: function (error) {
            console.log(error);
        }
    });
};

//fungsi yang digunakan untuk filter
var changeFilterBy = function () {
    //mendapatkan isi dari id tertentu disimpan dalam menusFilterBy
    menusFilterBy = parseInt($('#menus-filter-by').val());
    //ke fungsi move page
    movePage(page, pageSize, sort);
};

/**
 * Fungsi yang digunakan untuk mengambil menu (berdasarkan search, filter, paging dll)
 */
var getMenuSolds = function (page, pageSize, sort) {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'purchaseitem/menusold',  //url yang dituju, ada pada purchaseItemAPI
        type: 'GET', // Tipe pengaksesan url
        data: {
            userId: userId,
            filterBy: menusFilterBy,
            searchText: searchText,
            page: page,
            pageSize: pageSize,
            sort: sort
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            //menyimpan id pada variabel tableTBody
            var tableTBody = $('#menus-table-body');
            tableTBody.find('tr').remove();
            //memasukkan data yang didapatkan pada tabel
            for (var i = 0; i < data.data.length; i++) {
                numRow++;
                //menuCategorySelect diset jadi ''
                var menuCategorySelect = '';
                //menuStatusSelect diset jadi ''
                var menuStatusSelect = '';
                //mengambil menuId
                var menuId = data.data[i].menu.menuId;
                var j;
                //untuk menampilkan menu kategori
                for (j = 0; j < menuCategoryData.length; j++) {
                    //jika menu kategori = menu kategori yg ada di data maka statusnya = selected
                    if (menuCategoryData[j].menuCategoryId === data.data[i].menu.menuCategory.menuCategoryId)
                        menuCategorySelect += '<option value="' + menuCategoryData[j].menuCategoryId + '" selected="selected">' + menuCategoryData[j].menuCategoryName + '</option>';
                    else
                        menuCategorySelect += '<option value="' + menuCategoryData[j].menuCategoryId + '">' + menuCategoryData[j].menuCategoryName + '</option>';
                }
                //untuk menampilkan menuStatus
                for (j = 0; j < menuStatus.length; j++) {
                    //jika menuStatus = menuStatus yg ada di data maka statusnya = selected
                    if (menuStatus[j] === data.data[i].menu.menuStatus)
                        menuStatusSelect += '<option value="' + menuStatus[j] + '" selected="selected">' + menuStatus[j] + '</option>';
                    else
                        menuStatusSelect += '<option value="' + menuStatus[j] + '">' + menuStatus[j] + '</option>';
                }

                tableTBody.append(
                    '<tr id="menus-row-' + menuId + '">' +
                        '<td>' + (numRow) + '</td>' +
                        '<td>' + data.data[i].menu.menuName + '</td>' +
                        '<td>' + data.data[i].menu.menuCategory.menuCategoryName + '</td>' +
                        '<td>' + 'Rp. ' + data.data[i].menu.menuPrice + ',- ' + '</td>' +
                        '<td>' + data.data[i].menu.menuWaitingTime + ' Menit ' + '</td>' +
                        '<td>' + data.data[i].quantity + '</td>' +
                        '<td>' +
                            '<div class="btn-group">' +
                                '<button class="btn btn-info" data-toggle="modal" data-target="#detailModal-' + menuId + '">Detail</button>' +
                                '<div class="modal fade" id="detailModal-' + menuId + '" role="dialog">' +
                                    '<div class="modal-dialog">' +
                                        '<div class="modal-content">' +
                                            '<div class="modal-header">' +
                                                '<button type="button" class="close" data-dismiss="modal">&times;</button>' +
                                                '<h3 class="modal-title">Detail Menu</h3>' +
                                            '</div>' +
                                            '<div class="modal-body">' +
                                                '<div class="form-group">' +
                                                    '<label for="menu-menu-category-' + menuId + '">Kategori menu : </label>' +
                                                    '<select class="form-control" id="menu-menu-category-' + menuId + '" disabled="disabled">' +
                                                        menuCategorySelect +
                                                    '</select>' +
                                                '</div>' +
                                                '<div class="form-group">' +
                                                    '<label for="menu-name-' + menuId + '">Nama : </label>' +
                                                    '<input id="menu-name-' + menuId + '" placeholder="Masukkan nama menu.." class="form-control" value="' + data.data[i].menu.menuName + '" disabled="disabled" />' +
                                                '</div>' +
                                                '<div class="form-group">' +
                                                    '<label for="menu-price-' + menuId + '">Harga (Rupiah) : </label>' +
                                                    '<input id="menu-price-' + menuId + '" type="number" placeholder="Masukkan harga menu.." class="form-control" value="' + data.data[i].menu.menuPrice + '" disabled="disabled" />' +
                                                '</div>' +
                                                '<div class="form-group">' +
                                                    '<label for="menu-status-' + menuId + '">Status : </label>' +
                                                    '<select class="form-control" id="menu-status-' + menuId + '" disabled="disabled">' +
                                                        menuStatusSelect +
                                                    '</select>' +
                                                '</div>' +
                                                '<div class="form-group">' +
                                                    '<label for="menu-waiting-time-' + menuId + '">Waktu tunggu (Menit) : </label>' +
                                                    '<input id="menu-waiting-time-' + menuId + '" type="number" placeholder="Masukkan harga menu.." class="form-control" value="' + data.data[i].menu.menuWaitingTime + '" disabled="disabled" />' +
                                                '</div>' +
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

//fungsi untuk search menu
var searchMenus = function () {
    event.preventDefault();
    //mendapatkan isi dari id menus-search-text dan disimpan pada variabel searchText
    searchText = $('#menus-search-text').val();
    //jika isi searchText tidak ada maka searchText diset menjadi undefined
    if (searchText === '')
        searchText = undefined;
    //ke fungsi movePage
    movePage(page, pageSize, sort);
};

//fungsi untuk mengatur pagination, menampilkan dan menyembunyikan tombol next dan back pagig
var movePage = function (newPage, pageSize, sort) {
    //fungsi untuk menghitung jumlah menu
    getMenuCount();

    if(page == 1){
        numRow = 0;
    }
    var menuPage = Math.ceil(menuCount / 10);

    if (newPage > menuPage)
        newPage = page;

    if (newPage > 1)
        $('#pagination-back').show();
    else
        $('#pagination-back').hide();

    if (newPage < menuPage)
        $('#pagination-next').show();
    else
        $('#pagination-next').hide();

    $('#pagination-page').text(newPage);
    $('#pagination-page-end').text(menuPage);

    //fungsi untuk mendapatkan dan menampilkan kategori menu, hanya untuk menampilkan! untuk filter datanya ada di getMenuSolds
    getMenuCategories();
    //fungsi untuk mendapatkan dan menampilkan jumlah item menu terjual
    getMenuSolds(newPage, pageSize, sort);
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

//fungsi untuk sorting berdasarkan nama
var changeSortName = function () {
    var isAsc = nameSort === 1;

    if (isAsc) {
        nameSort = 2;
        sort = 2;

        $('#menus-table-menu-name-icon').attr('src', baseUrl + 'icon/sort-down.png');
    } else {
        nameSort = 1;
        sort = 1;

        $('#menus-table-menu-name-icon').attr('src', baseUrl + 'icon/sort-up.png');
    }

    movePage(page, pageSize, sort);
};

//fungsi untuk sorting berdasarkan harga
var changeSortPrice = function () {
    var isAsc = priceSort === 3;

    if (isAsc) {
        priceSort = 4;
        sort = 4;

        $('#menus-table-menu-price-icon').attr('src', baseUrl + 'icon/sort-down.png');
    } else {
        priceSort= 3;
        sort = 3;

        $('#menus-table-menu-price-icon').attr('src', baseUrl + 'icon/sort-up.png');
    }

    movePage(page, pageSize, sort);
};

//fungsi untuk sorting berdasarkan waiting time
var changeSortWaitingTime = function () {
    var isAsc = waitingTimeSort === 5;

    if (isAsc) {
        waitingTimeSort = 6;
        sort = 6;

        $('#menus-table-menu-waiting-time-icon').attr('src', baseUrl + 'icon/sort-down.png');
    } else {
        waitingTimeSort= 5;
        sort = 5;

        $('#menus-table-menu-waiting-time-icon').attr('src', baseUrl + 'icon/sort-up.png');
    }

    movePage(page, pageSize, sort);
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
    menuStatus = [];
    //ambil menuStatus dari dropdown biar bisa diolah di js
    $('#menu-status').find('option').each(function (index) {
        menuStatus[index] = $(this).val();
    });

    movePage(page, pageSize, sort);
});