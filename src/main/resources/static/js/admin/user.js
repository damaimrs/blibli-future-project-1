var page = 1;
var pageSize = 10;
var sort = 1;
var nameSort = 1;
var emailSort = 3;
var roleSort = 7;

var baseUrl = 'http://localhost:9090/';
var baseApiUrl = baseUrl + 'api/';
var userCount;
var roleData;
var usersFilterBy = 0;
var searchText;

var numRow = 0;

var changeCursor = function (obj) {
    $('#' + obj.id).css('cursor', 'pointer');
};

//fungsi untuk add user
var addUser = function (data) {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'user/' + userId, //yang diget diatas dibawa ke /login di controller
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

//fungsi saat submit form Add
var submitFormAdd = function () {
    event.preventDefault();
    //menyimpan id pada variabel
    var userNameInput = $('#user-name');
    var userEmailInput = $('#user-email');
    var userPasswordInput = $('#user-password');
    var userPhoneInput = $('#user-phone');
    var userAddressInput = $('#user-address');
    //bikin object untuk mengirim data
    var data = {};
    //mendapatkan isi dari id disimpan dalam object untuk dikirim
    data['userName'] = userNameInput.val(); // Mengambil value dari input nama user
    data['userEmail'] = userEmailInput.val();
    data['userPassword'] = userPasswordInput.val();
    data['userPhone'] = userPhoneInput.val();
    data['userAddress'] = userAddressInput.val();
    data['role'] = {
        'roleId': $('#user-role').val()
    };
    //ganti isi dari id tersebut jadi ''
    userNameInput.val('');
    userEmailInput.val('');
    userPasswordInput.val('');
    userPhoneInput.val('');
    userAddressInput.val('');

    $('#addModal').modal('toggle');
    addUser(data);
};

var submitFormEdit = function (userId) {
    event.preventDefault();
    //nyimpen id ke dalam variabel
    var userNameInput = $('#user-name-' + userId);
    var userEmailInput = $('#user-email-' + userId);
    var userPasswordInput = $('#user-password-' + userId);
    var userPhoneInput = $('#user-phone-' + userId);
    var userAddressInput = $('#user-address-' + userId);
    //bikin object untuk dikirim
    var data = {};
    //masukin variabel yg diatas ke dalam object
    data['userId'] = userId;
    data['userName'] = userNameInput.val(); // Mengambil value dari input nama user
    data['userEmail'] = userEmailInput.val();
    data['userPassword'] = userPasswordInput.val();
    data['userPhone'] = userPhoneInput.val();
    data['userAddress'] = userAddressInput.val();
    data['role'] = {
        'roleId': $('#user-role-' + userId).val()
    };

    $('#editModal-' + userId).modal('toggle');
    //add user
    addUser(data);
};

/**
 * Fungsi untuk mengambil jumlah role
 */
var getUserCount = function () {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'user/count', // Url yang ingin diakses
        type: 'GET', // Tipe pengaksesan url
        async: false,
        data: {
            userId: userId
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            userCount = data.data;
        },
        error: function (error) {
            console.log(error);
        }
    });
};

//untuk mendapatkan role lalu ditampilkan pada filter dan role pada saat akan menambah user
var getRoles = function () {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'role/all', // Url yang ingin diakses
        type: 'GET', // Tipe pengaksesan url
        async: false,
        data: {
            userId: userId
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            //id saat akan menambah role
            var selectRoles = $('#user-role');
            //id saat filter berdasarkan role
            var selectUsersFilterBy = $('#users-filter-by');
            var i;

            selectRoles.find('option').remove();
            selectUsersFilterBy.find('option').remove();

            roleData = data.data;

            for (i = 0; i < roleData.length; i++) {
                //memasukkan id role dan nama role pada option, yang ditampilkan roleName saja
                selectRoles.append('<option value="' + roleData[i].roleId + '">' + roleData[i].roleName + '</option>');
            }

            if (usersFilterBy === 0)
                selectUsersFilterBy.append('<option value="0" selected="selected">All</option>');
            else
                selectUsersFilterBy.append('<option value="0">All</option>');

            for (i = 0; i < roleData.length; i++) {
                var roleId = roleData[i].roleId;
                //menampilkan kalau role sesuai dengan role yang ada di filter
                if (usersFilterBy === roleId)
                    selectUsersFilterBy.append('<option value="' + roleId + '" selected="selected">' + roleData[i].roleName + '</option>');
                else
                    selectUsersFilterBy.append('<option value="' + roleId + '">' + roleData[i].roleName + '</option>');
            }
        },
        error: function (error) {
            console.log(error);
        }
    });
};

//fungsi yang digunakan untuk filter user berdasarkan role
var changeFilterBy = function () {
    usersFilterBy = parseInt($('#users-filter-by').val());
    movePage(page, pageSize, sort);
};

/**
 * Fungsi untuk mengambil list user dengan atau tanpa search, dengan atau tanpa filter roleId, dan semua list user.
 * Filter/search ada di API
 */
var getUsers = function (page, pageSize, sort) {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'user',
        type: 'GET', // Tipe pengaksesan url
        data: {
            userId: userId,
            //parse int roleId buat filter user berdasarkan roleId
            roleId: parseInt(usersFilterBy),
            //searchText kalau misal dicari berdasarkan text tertentu
            searchText: searchText,
            page: page,
            pageSize: pageSize,
            sort: sort
        }, // Content parameter yang dikirim saat request
        success: function (data) {
            var tableTBody = $('#users-table-body');
            tableTBody.find('tr').remove();

            for (var i = 0; i < data.data.length; i++) {
                numRow = numRow + 1;
                var roleSelect = '';
                var userId = data.data[i].userId;

                for (var j = 0; j < roleData.length; j++) {
                    if (roleData[j].roleId === data.data[i].role.roleId){
                        //untuk menampilkan role yang dipilih jika sudah memilih role
                        roleSelect += '<option value="' + roleData[j].roleId + '" selected="selected">' + roleData[j].roleName + '</option>';}
                    else{
                        //untuk menampilkan role yang tidak diselect pada dropdown
                        roleSelect += '<option value="' + roleData[j].roleId + '">' + roleData[j].roleName + '</option>';}
                }

                tableTBody.append(
                    '<tr id="users-row-' + userId + '">' +
                        '<td>' + (numRow) + '</td>' +
                        '<td>' + data.data[i].userName + '</td>' +
                        '<td>' + data.data[i].userEmail + '</td>' +
                        '<td>' + data.data[i].role.roleName + '</td>' +
                        '<td>' +
                            '<div class="btn-group">' +
                                '<button class="btn btn-warning" data-toggle="modal" data-target="#editModal-' + userId + '">Edit</button>' +
                                '<button class="btn btn-danger" onclick="deleteUser(' + userId + ')">' +
                                    'Delete' +
                                '</button>' +
                                '<div class="modal fade" id="editModal-' + userId + '" role="dialog">' +
                                    '<div class="modal-dialog">' +
                                        '<div class="modal-content">' +
                                            '<div class="modal-header">' +
                                                '<button type="button" class="close" data-dismiss="modal">&times;</button>' +
                                                '<h3 class="modal-title">Edit User</h3>' +
                                            '</div>' +
                                            '<form id="user-edit-form" onsubmit="submitFormEdit(' + userId + ')">' +
                                                '<div class="modal-body">' +
                                                    '<div class="form-group">' +
                                                        '<label for="user-name-' + userId + '">Nama : </label>' +
                                                        '<input id="user-name-' + userId + '" type="text" placeholder="Masukkan nama user.." class="form-control" value="' + data.data[i].userName + '" required="required" />' +
                                                    '</div>' +
                                                    '<div class="form-group">' +
                                                        '<label for="user-email-' + userId + '">Email : </label>' +
                                                        '<input id="user-email-' + userId + '" type="email" placeholder="Masukkan email user.." class="form-control" value="' + data.data[i].userEmail + '" required="required" />' +
                                                    '</div>' +
                                                    '<div class="form-group">' +
                                                        '<label for="user-password-' + userId + '">Password : </label>' +
                                                        '<input id="user-password-' + userId + '" type="password" placeholder="Masukkan password (jika ingin merubah password).." class="form-control" />' +
                                                    '</div>' +
                                                    '<div class="form-group">' +
                                                        '<label for="user-phone-' + userId + '">Nomor Telepon : </label>' +
                                                        '<input id="user-phone-' + userId + '" type="text" placeholder="Masukkan nomor telepon user.." class="form-control" value="' + data.data[i].userPhone + '" required="required" />' +
                                                    '</div>' +
                                                    '<div class="form-group">' +
                                                        '<label for="user-address-' + userId + '">Alamat : </label>' +
                                                        '<input id="user-address-' + userId + '" type="text" placeholder="Masukkan alamat user.." class="form-control" value="' + data.data[i].userAddress + '" required="required" />' +
                                                    '</div>' +
                                                    '<div class="form-group">' +
                                                        '<label for="user-role-' + userId + '">Role : </label>' +
                                                        '<select class="form-control" id="user-role-' + userId + '" required="required">' +
                                                            roleSelect +
                                                        '</select>' +
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

//fungsi yang digunakan untuk delete user
var deleteUser = function (userId) {
    var userIdLogin = $('#user-id').val(); // Mengambil user id dari input
    var data = {};

    data['userId'] = userIdLogin; // Mengambil value dari input nama user

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'user/' + userId, //yang diget diatas dibawa ke /login di controller
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

//fungsi yang digunakan saat ada search pada user
var searchUsers = function () {
    event.preventDefault();

    searchText = $('#users-search-text').val();

    if (searchText === '')
        searchText = undefined;

    movePage(page, pageSize, sort);
};

//fungsi untuk pagination, menampilkan next dan back button juga
var movePage = function (newPage, pageSize, sort) {
    getUserCount();

    if( page == 1){
        numRow = 0;
    }
    var userPage = Math.ceil(userCount / 10);

    if (newPage > userPage)
        newPage = page;

    if (newPage > 1)
        $('#pagination-back').show();
    else
        $('#pagination-back').hide();

    if (newPage < userPage)
        $('#pagination-next').show();
    else
        $('#pagination-next').hide();

    $('#pagination-page').text(newPage);
    $('#pagination-page-end').text(userPage);

    getRoles();
    getUsers(newPage, pageSize, sort);
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

//fungsi untuk sorting berdasarkan nama
var changeSortName = function () {
    var isAsc = nameSort === 1;

    if (isAsc) {
        nameSort = 2;
        sort = 2;

        $('#users-table-user-name-icon').attr('src', baseUrl + 'icon/sort-down.png');
    } else {
        nameSort = 1;
        sort = 1;

        $('#users-table-user-name-icon').attr('src', baseUrl + 'icon/sort-up.png');
    }

    movePage(page, pageSize, sort);
};

//fungsi untuk sorting berdasarkan email
var changeSortEmail = function () {
    var isAsc = emailSort === 3;

    if (isAsc) {
        emailSort = 4;
        sort = 4;

        $('#users-table-user-email-icon').attr('src', baseUrl + 'icon/sort-down.png');
    } else {
        emailSort = 3;
        sort = 3;

        $('#users-table-user-email-icon').attr('src', baseUrl + 'icon/sort-up.png');
    }

    movePage(page, pageSize, sort);
};

//fungsi untuk sorting berdasarkan roleName
var changeSortRoleName = function () {
    var isAsc = roleSort === 7;

    if (isAsc) {
        roleSort = 8;
        sort = 8;

        $('#users-table-user-role-icon').attr('src', baseUrl + 'icon/sort-down.png');
    } else {
        roleSort = 7;
        sort = 7;

        $('#users-table-user-role-icon').attr('src', baseUrl + 'icon/sort-up.png');
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