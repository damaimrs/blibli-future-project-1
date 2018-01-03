var page = 1;
var pageSize = 10;
var sort = 1;
var nameSort = 1;
var menuCategorySort = 3;
var priceSort = 5;
var waitingTimeSort = 7;
var statusSort = 9;

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

//fungsi yang digunakan untuk menambah atau mengedit menu
var addMenu = function (data) {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'menu/' + userId, //url yang akan dituju, ada pada menuAPI
        type: 'POST',
        contentType: 'application/json',
        //mengubah data jadi format JSON
        data: JSON.stringify(data),
        dataType: 'json',
        success: function (data) {
            alert(data.message);

            if (data.data === 1) {
                movePage(page, pageSize, sort);
            }
        },
        error: function (error) {
            console.log(error);
        }
    });
};

//fungsi yang digunakan untuk submit form add menu
var submitFormAdd = function () {
    event.preventDefault();
    //menyimpan id pada variabel
    var menuNameInput = $('#menu-name');
    var menuPriceInput = $('#menu-price');
    var menuWaitingTimeInput = $('#menu-waiting-time');
    var menuStatusInput = $('#menu-status');
    //membuat object
    var data = {};
    //menyimpan isi dari id ke dalam object
    data['menuName'] = menuNameInput.val(); // Mengambil value dari input nama role
    data['menuPrice'] = menuPriceInput.val();
    data['menuWaitingTime'] = menuWaitingTimeInput.val();
    data['menuStatus'] = menuStatusInput.val();
    data['menuCategory'] = {
        'menuCategoryId': $('#menu-menu-category').val()
    };

    //mengganti isi dari id menjadi ''
    menuNameInput.val('');
    menuPriceInput.val('');
    menuWaitingTimeInput.val('');

    $('#addModal').modal('toggle');
    //pemanggilan fungsi addMenu dengan memparsing data
    addMenu(data);
};

//fungsi yang digunakan untuk mensubmit form edit menu
var submitFormEdit = function (menuId) {
    event.preventDefault();
    //membuat variabel untuk menyimpan id
    var menuNameInput = $('#menu-name-' + menuId);
    var menuPriceInput = $('#menu-price-' + menuId);
    var menuWaitingTimeInput = $('#menu-waiting-time-' + menuId);
    var menuStatusInput = $('#menu-status-' + menuId);
    //membuat object
    var data = {};
    //menyimpan isi dari id ke dalam object
    data['menuId'] = menuId;
    data['menuName'] = menuNameInput.val(); // Mengambil value dari input nama role
    data['menuPrice'] = menuPriceInput.val();
    data['menuWaitingTime'] = menuWaitingTimeInput.val();
    data['menuStatus'] = menuStatusInput.val();
    data['menuCategory'] = {
        'menuCategoryId': $('#menu-menu-category-' + menuId).val()
    };

    $('#editModal-' + menuId).modal('toggle');
    //melakukan add pada database
   addMenu(data);
};

//fungsi yang digunakan untuk mengambil jumlah menu
var getMenuCount = function () {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'menu/count', // Url yang ingin diakses
        type: 'GET', // Tipe pengaksesan url
        async: false,
        data: {
            userId: userId
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            menuCount = data.data;
        },
        error: function (error) {
            console.log(error);
        }
    });
};

//fungsi yang digunakan untuk mendapatkan kategori menu
var getMenuCategories = function () {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'menu-category/all', //url yang dituju, ada pada menuCategoryAPI
        type: 'GET', // Tipe pengaksesan url
        async: false,
        data: {
            userId: userId
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            //untuk menuCategori saat add menu atau edit menu
            var selectMenuCategories = $('#menu-menu-category');
            //untuk menuCategori di filter
            var selectMenusFilterBy = $('#menus-filter-by');
            var i;

            selectMenuCategories.find('option').remove();
            selectMenusFilterBy.find('option').remove();

            menuCategoryData = data.data;

            //menampilkan seluruh menuCategory pada select saat add atau edit
            for (i = 0; i < menuCategoryData.length; i++) {
                selectMenuCategories.append('<option value="' + menuCategoryData[i].menuCategoryId + '">' + menuCategoryData[i].menuCategoryName + '</option>');
            }
            //menampilkan menuCategory di filter jika tidak dipilih filter apapun (All)
            if (menusFilterBy === 0)
                selectMenusFilterBy.append('<option value="0" selected="selected">All</option>');
            else
                selectMenusFilterBy.append('<option value="0">All</option>');

            for (i = 0; i < menuCategoryData.length; i++) {
                var menuCategoryId = menuCategoryData[i].menuCategoryId;
                //jika ada filter yang dipilih
                if (menusFilterBy === menuCategoryId)
                    selectMenusFilterBy.append('<option value="' + menuCategoryId + '" selected="selected">' + menuCategoryData[i].menuCategoryName + '</option>');
                else
                    selectMenusFilterBy.append('<option value="' + menuCategoryId + '">' + menuCategoryData[i].menuCategoryName + '</option>');
            }
                //menampilkan status menu
            for (i = 0; i < menuStatus.length; i++) {
                var menuStatusId = -(i + 1);

                if (menusFilterBy === menuStatusId)
                    selectMenusFilterBy.append('<option value="' + menuStatusId + '" selected="selected">' + menuStatus[i] + '</option>');
                else
                    selectMenusFilterBy.append('<option value="' + menuStatusId + '">' + menuStatus[i] + '</option>');
            }
        },
        error: function (error) {
            console.log(error);
        }
    });
};

//fungsi yang digunakan untuk filter menu berdasarkan kategori/ketersediaan
var changeFilterBy = function () {
    menusFilterBy = parseInt($('#menus-filter-by').val());
    movePage(page, pageSize, sort);
};

//fungsi yang digunakan untuk mendapatkan menu dan ditampilkan pada halaman menu
var getMenus = function (page, pageSize, sort) {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'menu',   //url yang dituju, ada pada menuAPI
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
            var tableTBody = $('#menus-table-body');
            tableTBody.find('tr').remove();

            for (var i = 0; i < data.data.length; i++) {
                numRow = numRow + 1;
                var menuCategorySelect = '';
                var menuStatusSelect = '';
                var menuId = data.data[i].menuId;
                var j;
                var menuPrice = data.data[i].menuPrice;
                menuPrice = menuPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".");

                //untuk menampilkan kategori edit
                for (j = 0; j < menuCategoryData.length; j++) {
                    if (menuCategoryData[j].menuCategoryId === data.data[i].menuCategory.menuCategoryId)
                        menuCategorySelect += '<option value="' + menuCategoryData[j].menuCategoryId + '" selected="selected">' + menuCategoryData[j].menuCategoryName + '</option>';
                    else
                        menuCategorySelect += '<option value="' + menuCategoryData[j].menuCategoryId + '">' + menuCategoryData[j].menuCategoryName + '</option>';
                }

                //untuk menampilkan status menu pada saat edit
                for (j = 0; j < menuStatus.length; j++) {
                    if (menuStatus[j] === data.data[i].menuStatus)
                        menuStatusSelect += '<option value="' + menuStatus[j] + '" selected="selected">' + menuStatus[j] + '</option>';
                    else
                        menuStatusSelect += '<option value="' + menuStatus[j] + '">' + menuStatus[j] + '</option>';
                }
                //menampilkan tabel menu
                tableTBody.append(
                    '<tr id="menus-row-' + menuId + '">' +
                        '<td>' + (numRow) + '</td>' +
                        '<td>' + data.data[i].menuName + '</td>' +
                        '<td>' + data.data[i].menuCategory.menuCategoryName + '</td>' +
                        '<td>' + 'Rp. ' + menuPrice + ',- ' + '</td>' +
                        '<td>' + data.data[i].menuWaitingTime + ' Menit ' + '</td>' +
                        '<td>' + data.data[i].menuStatus + '</td>' +
                        '<td>' +
                            '<div class="btn-group">' +
                                '<button class="btn btn-warning" data-toggle="modal" data-target="#editModal-' + menuId + '">Edit</button>' +
                                '<button class="btn btn-danger" onclick="deleteMenu(' + menuId + ')">' +
                                    'Delete' +
                                '</button>' +
                                '<div class="modal fade" id="editModal-' + menuId + '" role="dialog">' +
                                    '<div class="modal-dialog">' +
                                        '<div class="modal-content">' +
                                            '<div class="modal-header">' +
                                                '<button type="button" class="close" data-dismiss="modal">&times;</button>' +
                                                '<h3 class="modal-title">Edit Menu</h3>' +
                                            '</div>' +
                                            '<form id="menu-edit-form" onsubmit="submitFormEdit(' + menuId + ')">' +
                                                '<div class="modal-body">' +
                                                    '<div class="form-group">' +
                                                        '<label for="menu-menu-category-' + menuId + '">Kategori menu : </label>' +
                                                        '<select class="form-control" id="menu-menu-category-' + menuId + '" required="required">' +
                                                            menuCategorySelect +
                                                        '</select>' +
                                                    '</div>' +
                                                    '<div class="form-group">' +
                                                        '<label for="menu-name-' + menuId + '">Nama : </label>' +
                                                        '<input id="menu-name-' + menuId + '" placeholder="Masukkan nama menu.." class="form-control" value="' + data.data[i].menuName + '" required="required" />' +
                                                    '</div>' +
                                                    '<div class="form-group">' +
                                                        '<label for="menu-price-' + menuId + '">Harga (Rupiah) : </label>' +
                                                        '<input id="menu-price-' + menuId + '" type="number" placeholder="Masukkan harga menu.." class="form-control" value="' + data.data[i].menuPrice + '" required="required" />' +
                                                    '</div>' +
                                                    '<div class="form-group">' +
                                                        '<label for="menu-status-' + menuId + '">Status : </label>' +
                                                        '<select class="form-control" id="menu-status-' + menuId + '" required="required">' +
                                                            menuStatusSelect +
                                                        '</select>' +
                                                    '</div>' +
                                                    '<div class="form-group">' +
                                                        '<label for="menu-waiting-time-' + menuId + '">Waktu tunggu (Menit) : </label>' +
                                                        '<input id="menu-waiting-time-' + menuId + '" type="number" placeholder="Masukkan harga menu.." class="form-control" value="' + data.data[i].menuWaitingTime + '" required="required" />' +
                                                    '</div>' +
                                                '</div>' +
                                                '<div class="modal-footer">' +
                                                    '<button id="btn-edit" type="submit" class="btn btn-info">Edit</button>' +
                                                '</div>' +
                                            '</form>' +
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

var deleteMenu = function (menuId) {
    var userIdLogin = $('#user-id').val(); // Mengambil user id dari input
    var data = {};
    data['userId'] = userIdLogin; // Mengambil value dari input nama user

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'menu/' + menuId, //yang diget diatas dibawa ke /login di controller
        type: 'DELETE',
        contentType: 'application/json',
        data: JSON.stringify(data),
        dataType: 'json',
        success: function (data) {
            alert(data.message);

            if (data.data === 1) {
                movePage(page, pageSize, sort);
            }
        },
        error: function (error) {
            console.log(error);
        }
    });
};

//fungsi yang digunakan untuk mendapatkan isi dari form search lalu dilakukan pencarian menu berdasarkan isi dari form tersebut
var searchMenus = function () {
    event.preventDefault();

    searchText = $('#menus-search-text').val();

    if (searchText === '')
        searchText = undefined;

    movePage(page, pageSize, sort);
};

//fungsi untuk pagination, menampilkan next dan back button
var movePage = function (newPage, pageSize, sort) {

    getMenuCount();
    if(page == 1)
    {
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

    getMenuCategories();
    getMenus(newPage, pageSize, sort);
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

//fungsi yang digunakan untuk sorting berdasarkan nama menu
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

//fungsi yang digunakan untuk sorting berdasarkan kategori menu
var changeSortMenuCategory = function () {
    var isAsc = menuCategorySort === 3;

    if (isAsc) {
        menuCategorySort = 4;
        sort = 4;

        $('#menus-table-menu-category-icon').attr('src', baseUrl + 'icon/sort-down.png');
    } else {
        menuCategorySort= 3;
        sort = 3;

        $('#menus-table-menu-category-icon').attr('src', baseUrl + 'icon/sort-up.png');
    }

    movePage(page, pageSize, sort);
};

//fungsi yang digunakan untuk sorting berdasarkan harga
var changeSortPrice = function () {
    var isAsc = priceSort === 5;

    if (isAsc) {
        priceSort = 6;
        sort = 6;

        $('#menus-table-menu-price-icon').attr('src', baseUrl + 'icon/sort-down.png');
    } else {
        priceSort= 5;
        sort = 5;

        $('#menus-table-menu-price-icon').attr('src', baseUrl + 'icon/sort-up.png');
    }

    movePage(page, pageSize, sort);
};

//fungsi yang digunakan untuk sorting berdasarkan waiting time
var changeSortWaitingTime = function () {
    var isAsc = waitingTimeSort === 7;

    if (isAsc) {
        waitingTimeSort = 8;
        sort = 8;

        $('#menus-table-menu-waiting-time-icon').attr('src', baseUrl + 'icon/sort-down.png');
    } else {
        waitingTimeSort= 7;
        sort = 7;

        $('#menus-table-menu-waiting-time-icon').attr('src', baseUrl + 'icon/sort-up.png');
    }

    movePage(page, pageSize, sort);
};

//fungsi yang digunakan untuk sorting berdasarkan status
var changeSortStatus = function () {
    var isAsc = statusSort === 9;

    if (isAsc) {
        statusSort = 10;
        sort = 10;

        $('#menus-table-menu-status-icon').attr('src', baseUrl + 'icon/sort-down.png');
    } else {
        statusSort= 9;
        sort = 9;

        $('#menus-table-menu-status-icon').attr('src', baseUrl + 'icon/sort-up.png');
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

    $('#menu-status').find('option').each(function (index) {
        menuStatus[index] = $(this).val();
    });

    movePage(page, pageSize, sort);
});