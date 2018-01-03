var page = 1;
var pageSize = 10;
var sort = 1;
var nameSort = 1;

var baseUrl = 'http://localhost:9090/';
var baseApiUrl = baseUrl + 'api/';
var roleCount;
var searchText;

var numRow = 0;

var changeCursor = function (obj) {
    $('#' + obj.id).css('cursor', 'pointer');
};

var addRole = function (data) {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'role/' + userId, //url yang dituju pada roleAPI
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

var submitFormAdd = function () {
    event.preventDefault();

    var roleNameInput = $('#role-name');

    var data = {};
    data['roleName'] = roleNameInput.val(); // Mengambil value dari input nama role

    roleNameInput.val('');
    $('#addModal').modal('toggle');

    addRole(data);
};

var submitFormEdit = function (roleId) {
    event.preventDefault();

    var data = {};

    data['roleId'] = roleId;
    data['roleName'] = $('#role-name-' + roleId).val(); // Mengambil value dari input nama role

    $('#editModal-' + roleId).modal('toggle');

    addRole(data);
};

/**
 * Fungsi untuk mengambil jumlah role
 */
var getRoleCount = function () {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'role/count', // Url yang ingin diakses
        type: 'GET', // Tipe pengaksesan url
        async: false,
        data: {
            userId: userId
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            roleCount = data.data;
        },
        error: function (error) {
            console.log(error);
        }
    });
};

//fungsi untuk mendapatkan role
var getRoles = function (page, pageSize, sort) {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'role',   //API yang dituju ada pada roleAPI
        type: 'GET', // Tipe pengaksesan url
        data: {
            userId: userId,
            searchText: searchText,
            page: page,
            pageSize: pageSize,
            sort: sort
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            var tableTBody = $('#roles-table-body');
            tableTBody.find('tr').remove();

            for (var i = 0; i < data.data.length; i++) {
                //num buat ngasih nomor aja
                numRow = numRow + 1;
                var roleId = data.data[i].roleId;

                tableTBody.append(
                    '<tr id="roles-row-' + roleId + '">' +
                        '<td>' + (numRow) + '</td>' +
                        '<td>' + data.data[i].roleName + '</td>' +
                        '<td>' +
                            '<div class="btn-group">' +
                                '<button class="btn btn-warning" data-toggle="modal" data-target="#editModal-' + roleId + '">Edit</button>' +
                                '<button class="btn btn-danger" onclick="deleteRole(' + roleId + ')">' +
                                    'Delete' +
                                '</button>' +
                                '<div class="modal fade" id="editModal-' + roleId + '" role="dialog">' +
                                    '<div class="modal-dialog">' +
                                        '<div class="modal-content">' +
                                            '<div class="modal-header">' +
                                                '<button type="button" class="close" data-dismiss="modal">&times;</button>' +
                                                '<h3 class="modal-title">Edit Role</h3>' +
                                            '</div>' +
                                            '<form id="role-edit-form" onsubmit="submitFormEdit(' + roleId + ')">' +
                                                '<div class="modal-body">' +
                                                    '<div class="form-group">' +
                                                        '<label for="role-name-' + roleId + '">Nama Role : </label>\n' +
                                                        '<input id="role-name-' + roleId + '" placeholder="Masukkan nama role.." class="form-control" value="' + data.data[i].roleName + '" required="required" />' +
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

//fungsi yang digunakan untuk menghapus role
var deleteRole = function (roleId) {
    var userId = $('#user-id').val(); // Mengambil user id dari input
    var data = {};

    data['userId'] = userId; // Mengambil value dari input nama role

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'role/' + roleId, //yang diget diatas dibawa ke /login di controller
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

//fungsi yang digunakan pada form search di halaman role
var searchRoles = function () {
    event.preventDefault();
    //mendapatkan isi dari id roles-search-user
    searchText = $('#roles-search-text').val();
    //jika tidak ada isi dari id tersebut maka diset undefined
    if (searchText === '')
        searchText = undefined;
    //ke fungsi untuk menampilkan role
    movePage(page, pageSize, sort);
};

//fungsi yang digunakan untuk pagination
var movePage = function (newPage, pageSize, sort) {
    getRoleCount();

    if( page==1 ){
        numRow = 0;
    }
    var rolePage = Math.ceil(roleCount / 10);

    if (newPage > rolePage)
        newPage = page;
    //jika page lebih dari 1 maka pagination back dishow
    if (newPage > 1)
        $('#pagination-back').show();
    else
        $('#pagination-back').hide();
    //untuk page jika kurang dari role page maka pagination next dishow, kalo engga dihide
    if (newPage < rolePage)
        $('#pagination-next').show();
    else
        $('#pagination-next').hide();

    $('#pagination-page').text(newPage);
    $('#pagination-page-end').text(rolePage);
    //fungsi untuk mendapatkan role
    getRoles(newPage, pageSize, sort);
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
    page = parseInt((roleCount / 10) + 1);
    movePage(page, pageSize, sort);
};

//fungsi yang digunakan untuk sorting
var changeSort = function () {
    var isAsc = nameSort === 1;

    if (isAsc) {
        nameSort = 2;
        sort = 2;

        $('#roles-table-role-name-icon').attr('src', baseUrl + 'icon/sort-down.png');
    } else {
        nameSort = 1;
        sort = 1;

        $('#roles-table-role-name-icon').attr('src', baseUrl + 'icon/sort-up.png');
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

/**
 * Fungsi saat document telah siap
 */
$(document).ready(function () {
    movePage(page, pageSize, sort);
});