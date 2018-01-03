var page = 1;
var pageSize = 10;
var sort = 1;
var nameSort = 1;

var baseUrl = 'http://localhost:9090/';
var baseApiUrl = baseUrl + 'api/';
var menuCategoryCount = 1;
var searchText;

var numRow = 0;

var changeCursor = function (obj) {
    $('#' + obj.id).css('cursor', 'pointer');
};

//fungsi yang digunakan untuk menambah atau mengedit kategori menu
var addMenuCategory = function (data) {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'menu-category/' + userId, //url yang dituju, ada pada menuCategoryAPI
        type: 'POST',
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

//fungsi yang digunakan untuk mensubmit form add
var submitFormAdd = function () {
    event.preventDefault();
    //menyimpan id pada variabel
    var menuCategoryNameInput = $('#menu-category-name');
    //membuat object
    var data = {};
    //menyimpan isi dari id ke dalam objek
    data['menuCategoryName'] = menuCategoryNameInput.val(); // Mengambil value dari input nama role
    //mengganti isi dari id menjadi ''
    menuCategoryNameInput.val('');
    $('#addModal').modal('toggle');
    //memanggil fungsi addCategory
    addMenuCategory(data);
};

//fungsi yang digunakan untuk mensubmit form edit kategori menu
var submitFormEdit = function (menuCategoryId) {
    event.preventDefault();
    //membuat objek
    var data = {};
    //menyimpan isi dari id ke dalam objek
    data['menuCategoryId'] = menuCategoryId;
    data['menuCategoryName'] = $('#menu-category-name-' + menuCategoryId).val(); // Mengambil value dari input nama role

    $('#editModal-' + menuCategoryId).modal('toggle');
    //memanggil fungsi addMenuCategory
    addMenuCategory(data);
};

//fungsi yang digunakan untuk mendapatkan jumlah menu kategori
var getCategoryMenuCount = function () {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'menu-category/count', //url yang dituju, ada pada menuCategoryAPI
        type: 'GET', // Tipe pengaksesan url
        async: false,
        data: {
            userId: userId
        },
        success: function (data) {
            menuCategoryCount = data.data;
        },
        error: function (error) {
            console.log(error);
        }
    });
};

//fungsi yang digunakan untuk mendapatkan kategori menu dari database dan ditampilkan
var getMenuCategories = function (page, pageSize, sort) {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'menu-category',  //url yang dituju, ada pada menuCategoryAPI
        type: 'GET', // Tipe pengaksesan url
        data: {
            userId: userId,
            searchText: searchText, //isi dari searchText kalau melakukan search
            page: page,             //halaman ke
            pageSize: pageSize,     //jumlah data per halaman
            sort: sort              //sorting
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            var tableTBody = $('#menu-category-table-body');
            tableTBody.find('tr').remove();

            //memnampilkan data yang didapat
            for (var i = 0; i < data.data.length; i++) {
                numRow++;
                var menuCategoryId = data.data[i].menuCategoryId;
                //memasukkan data pada table
                tableTBody.append(
                    '<tr id="menu-categories-row-' + menuCategoryId + '">' +
                        '<td>' + (numRow) + '</td>' +
                        '<td>' + data.data[i].menuCategoryName + '</td>' +
                        '<td>' +
                            '<div class="btn-group">' +
                                '<button class="btn btn-warning" data-toggle="modal" data-target="#editModal-' + menuCategoryId + '">Edit</button>' +
                                '<button class="btn btn-danger" onclick="deleteMenuCategory(' + menuCategoryId + ')">' +
                                    'Delete' +
                                '</button>' +
                                '<div class="modal fade" id="editModal-' + menuCategoryId + '" role="dialog">' +
                                    '<div class="modal-dialog">' +
                                        '<div class="modal-content">' +
                                            '<div class="modal-header">' +
                                                '<button type="button" class="close" data-dismiss="modal">&times;</button>' +
                                                '<h3 class="modal-title">Edit Kategori Menu</h3>' +
                                            '</div>' +
                                            '<form id="menu-category-edit-form" onsubmit="submitFormEdit(' + menuCategoryId + ')">' +
                                                '<div class="modal-body">' +
                                                    '<div class="form-group">' +
                                                        '<label for="menu-category-name-' + menuCategoryId + '">Nama Kategori Menu : </label>\n' +
                                                        '<input id="menu-category-name-' + menuCategoryId + '" placeholder="Masukkan nama kategori menu.." class="form-control" value="' + data.data[i].menuCategoryName + '" required="required" />' +
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

//fungsi yang digunakan untuk menghapus kategori menu
var deleteMenuCategory = function (menuCategoryId) {
    var userId = $('#user-id').val(); // Mengambil user id dari input
    var data = {};

    data['userId'] = userId; // Mengambil value dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'menu-category/' + menuCategoryId, //url yang dituju, ada pada menuCategoryAPI
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

//fungsi untuk search pada menu kategori
var searchMenuCategories = function () {
    event.preventDefault();
    //mendapatkan isi dari id dan disimpan pada variabel tertentu
    searchText = $('#menu-categories-search-text').val();

    if (searchText === '')
        searchText = undefined;
    //ke fungsi move page untuk menampilkan hasil search
    movePage(page, pageSize, sort);
};

//fungsi untuk menampilkan button next dan back pada pagination
var movePage = function (newPage, pageSize, sort) {
    //mendapatkan jumlah menu kategori
    getCategoryMenuCount();

    if(page == 1) {
        numRow = 0;
    }
    //menghitung jumlah halaman pada menu kategori (untuk pagination)
    var menuCategoryPage = Math.ceil(menuCategoryCount / 10);

    if (newPage > menuCategoryPage)
        newPage = page;

    if (newPage > 1)
        $('#pagination-back').show();
    else
        $('#pagination-back').hide();

    if (newPage < menuCategoryPage)
        $('#pagination-next').show();
    else
        $('#pagination-next').hide();

    $('#pagination-page').text(newPage);
    $('#pagination-page-end').text(menuCategoryPage);

    //fungsi untuk mendapatkan kategori menu dari database dan menampilkan
    getMenuCategories(newPage, pageSize, sort);
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
    page = parseInt((menuCategoryCount / 10) + 1);
    movePage(page, pageSize, sort);
};

//fungsi untuk sorting kategori menu
var changeSort = function () {
    var isAsc = nameSort === 1;

    if (isAsc) {
        nameSort = 2;
        sort = 2;

        $('#menu-categories-table-role-name-icon').attr('src', baseUrl + 'icon/sort-down.png');
    } else {
        nameSort = 1;
        sort = 1;

        $('#menu-categories-table-role-name-icon').attr('src', baseUrl + 'icon/sort-up.png');
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
    movePage(page, pageSize, sort);
});