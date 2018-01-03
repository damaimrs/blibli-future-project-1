var page = 1;
var pageSize = 10;
var sort = 1;
var dateSort = 1;
var tableSort = 3;
var waitingTimeSort = 5;
var totalSort = 7;
var cashierSort = 9;

var baseUrl = 'http://localhost:9090/';
var baseApiUrl = baseUrl + 'api/';
var purchaseCount;
var menu = [];
var purchase = {};
var purchases = [];
var waitingTime = {};

var numRow = 0;

var changeCursor = function (obj) {
    $('#' + obj.id).css('cursor', 'pointer');
};

//fungsi untuk menambah pemesanan
var addPurchase = function (data) {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'purchase/' + userId, //ke purchase API
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        dataType: 'json',
        success: function (data) {
            alert(data.message);

            if (data.data === 1) {              //kondisi jika return dari API datanya = 1
                if (purchase['purchaseId'] === undefined) {
                    //kondisi jika purchaseId undefined maka dihapus semua purchaseItemsnya
                    deleteAllPurchaseItems();
                    //tabelnya dikosongin
                    $('#purchase-table').val('');
                }

                movePage(page, pageSize, sort);
            }
        },
        error: function (error) {
            console.log(error);
        }
    });
};

//fungsi untuk submit form Add Pemesanan
var submitFormAdd = function () {
    event.preventDefault();

    purchase['purchaseTable'] = parseInt($('#purchase-table').val());   //mendapatkan data purchase table
    purchase['purchaseStatus'] = 'PROGRESS';    //statusnya progress

    var isSuccess = purchase['purchaseTable'] > 0;

    if (isSuccess) {
        for (var purchaseKey in purchase['purchaseItems']) {
            //kondisi jika jumlah itemnya undefined atau <=0
            if (purchase['purchaseItems'][purchaseKey].purchaseItemQuantity === undefined || purchase['purchaseItems'][purchaseKey].purchaseItemQuantity <= 0) {
                isSuccess = false;
                break;
            }
        }
    }

    if (isSuccess) {
        $('#addModal').modal('toggle');
        addPurchase(purchase);      //ke fungsi addPurchase
    } else {
        alert('Error in purchase data');
    }
};

//fungsi untuk mendapatkan jumlah purchase dengan status PROGRESS
var getPurchaseCount = function () {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'purchase/count/status/PROGRESS', // ke PurchaseAPI url ini
        type: 'GET', // Tipe pengaksesan url
        async: false,
        data: {
            userId: userId  // Content parameter yang dikirim saat request
        },
        success: function (data) {
            purchaseCount = data.data;  //data yg didapatkan
        },
        error: function (error) {
            console.log(error);
        }
    });
};

var getMenuCategories = function (purchaseId) {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'menu-category/all', // url pada API yg akan dituju, ada pada MenuCategoryAPI
        type: 'GET', // Tipe pengaksesan url
        async: false,
        data: {
            userId: userId // Content parameter yang dikirim saat request
        },
        success: function (data) {
            //ngecek id pemesanan, kalau ada id pemesanan(klik pemesanan yang ada di tampilan pemesanan) maka dia akan menampilkan kategori menu dengan menggunakan id pemesanan
            var selectMenuCategories = purchaseId === undefined ? $('#menu-menu-category') : $('#menu-menu-category-' + purchaseId );
            selectMenuCategories.find('option').remove();   //cari string option terus diremove

            menuCategoryData = data.data;

            for (var i = 0; i < menuCategoryData.length; i++) {
                // menampilkan pada html -> menuCategoryId dan menuCategoryName dengan bentuk option
                selectMenuCategories.append('<option value="' + menuCategoryData[i].menuCategoryId + '">' + menuCategoryData[i].menuCategoryName + '</option>');
            }

            if (purchaseId !== undefined)
                getMenus(purchaseId);
        },
        error: function (error) {
            console.log(error);
        }
    });
};

//get menus untuk modal add
var getMenus = function (purchaseId) {
    var isEdit = purchaseId !== undefined;

    var userId = $('#user-id').val(); // Mengambil user id dari input
    var menuCategoryId = !isEdit ?  $('#menu-menu-category').val() : $('#menu-menu-category-' + purchaseId).val();    //mengambil id dari menu category yang dipilih

    var purchaseEdit;

    if (isEdit) {
        purchaseEdit = purchases[purchaseId];
    }

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'menu/all/menu-category/' + menuCategoryId,   //ke MenuAPI untuk mendapatkan menu yang berada pada menu kategori tertentu
        type: 'GET', // Tipe pengaksesan url
        data: {
            userId: userId  // Content parameter yang dikirim saat request
        },
        success: function (data) {
            var tableTBody = !isEdit ? $('#menus-table-body') : $('#menus-table-body-' + purchaseId);    //menyimpan id pada html page ke variabel tableTBody
            tableTBody.find('tr').remove();             //mencari string tr lalu diremove

            menu = [];

            for (var i = 0; i < data.data.length; i++) {
                var menuId = data.data[i].menuId;
                var menuStatus = data.data[i].menuStatus;
                var disabledElm = '';
                var dangerBtn = 'btn-danger';
                var onClick = '';
                var btnName = 'Add';
                var menuPrice = data.data[i].menuPrice;

                var tBodyId = 'menus-row-';
                var btnId = 'btn-option-';
                var isPurchased = false;

                if (isEdit) {
                    tBodyId += purchaseId + '-';
                    btnId += purchaseId + '-';
                }

                tBodyId += menuId;
                btnId += menuId;

                menuPrice = menuPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".");
                menu[menuId] = data.data[i];

                //jika status menu inavailable maka buttonnya didisable
                if (menuStatus === 'INAVAILABLE') {
                    disabledElm = 'disabled="disabled"';
                } else {
                    //jika status menunya available maka disetting onclick
                    onClick = 'onclick="menuToPurchaseItem(' + menuId + ')"';
                }

                if (isEdit) {
                    for (var j = 0; j < purchaseEdit['purchaseItems'].length; j++) {
                        if (purchaseEdit['purchaseItems'][j].menu.menuId === menuId) {
                            isPurchased = true;
                            break;
                        }
                    }
                } else {
                    isPurchased = purchase['purchaseItems'][menuId] !== undefined;
                }

                if (isPurchased) {
                    onClick = '';
                    disabledElm = 'disabled="disabled"';
                    btnName = 'Already Added';
                } else {
                    dangerBtn = 'btn-info';
                    onClick = 'onclick="menuToPurchaseItem(' + menuId;

                    if (isEdit) {
                        onClick += ', ' + purchaseId;
                    }

                    onClick += ')"';
                }

                //menampilkan menu yang telah diget pada html page
                tableTBody.append(
                    '<tr id="' + tBodyId + '">' +
                        '<td>' + (i + 1) + '</td>' +
                        '<td>' + data.data[i].menuName + '</td>' +
                        '<td>' + menuPrice + '</td>' +
                        '<td>' + data.data[i].menuWaitingTime + '</td>' +
                        '<td>' + menuStatus + '</td>' +
                        '<td>' +
                            '<div class="btn-group">' +
                                '<button id="' + btnId + '" class="btn ' + dangerBtn + '" type="button"' + onClick + ' ' + disabledElm + '>' + btnName + '</button>' +
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

//fungsi untuk menampilkan dan mengotak atik menu yang ditambahkan (setelah memencet tombol add pada row menu yang tersedia)
var menuToPurchaseItem = function (menuId, purchaseId) {
    var isEdit = purchaseId !== undefined;

    //untuk mengambil button pada menu dengan id menus-row-menu-id
    var menuRow = !isEdit ? $('#menus-row-' + menuId).find('#btn-option-' + menuId) : $('#menus-row-' + purchaseId + '-' + menuId).find('#btn-option-' + purchaseId + '-' + menuId);
    var purchaseItemsTableBody = !isEdit ? $('#purchase-items-table-body') : $('#purchase-items-table-body-' + purchaseId);

    var trId = 'purchase-items-row-';
    var quantityId = 'purchase-item-quantity-';
    var totalPriceId = 'purchase-item-total-price-';

    if (isEdit) {
        trId += purchaseId + '-';
        quantityId += purchaseId + '-';
        totalPriceId += purchaseId + '-';
    }

    trId += menuId;
    quantityId += menuId;
    totalPriceId += menuId;

    //id tabel yang ada di html diambil disimpan sbg variabel
    var row;
    var purchaseMenu;

    if (isEdit) {
        purchases[purchaseId]['purchaseItems'][menuId] = {
            'menu': menu[menuId]
        };

        purchaseMenu = purchases[purchaseId]['purchaseItems'][menuId]['menu'];
    } else {
        //mengisi purchase {purchaseItems[menuId] dengan menu ke [menuId] }
        purchase['purchaseItems'][menuId] = {
            'menu': menu[menuId]
        };

        purchaseMenu = purchase['purchaseItems'][menuId]['menu'];
    }

    //untuk menghitung jumlah row yang ada dengan cara untuk menghitung tr, jumlah akan digunakan untuk menentukan index row selanjutnya
    row = purchaseItemsTableBody.find('tr').length;

    //untuk hapus atribut btn-info
    menuRow.removeClass('btn-info');
    //menambahkan atribut btn-danger (btn biru jadi btn merah)
    menuRow.addClass('btn-danger');
    //biar ga bisa diklik (disable(atribut), disable(isi))
    menuRow.attr('disabled', 'disabled');
    //inclicknya dihapus biar ga bisa diklik
    menuRow.removeAttr('onclick');
    menuRow.text('Already Added');

    var onClick = 'deletePurchaseItem(' + menuId;

    if (isEdit) {
        onClick += ', ' + purchaseId;
    }

    onClick += ')';

    //memasukkan menu pada tabel menu yang dipesan
    purchaseItemsTableBody.append(
        '<tr id="' + trId + '">' +
            '<td>' + (row + 1) + '</td>' +
            '<td>' + purchaseMenu.menuName + '</td>' +
            '<td>' + purchaseMenu.menuCategory.menuCategoryName + '</td>' +
            '<td>' +
                '<input id="' + quantityId + '" type="number" class="form-control" onchange="calculatePurchaseItemTotalPrice(' + menuId + ')" value="0" required="required" />' +    //jumlah item pada masing" menu, ke fungsi calculatePurchaseItemTotalPrice()
            '</td>' +
            '<td>' +
                '<input id="' + totalPriceId + '" type="number" class="form-control" value="0" disabled="disabled" required="required" />' +    //nilai akan diperbarui, perhitungan di fungsi calculatePurchaseItemTotalPrice()
            '</td>' +
            '<td>' +
                '<div class="btn-group">' +
                    '<button class="btn btn-danger" type="button" onclick="' + onClick + '">Hapus</button>' +    //button untuk menghapus item menu yg dipesan, ke fungsi deletePurchaseItem()
                '</div>' +
            '</td>' +
        '</tr>'
    );

    calculateItemWaitingTime();     //menghitung waiting time setelah menambah menu
};

//fungsi yang digunakan untuk menghapus menu yang dipesan (masing-masing menu)
//diimplementasikan pada button hapus saat penambahan menu di pemesanan
var deletePurchaseItem = function (menuId, purchaseId) {
    var isEdit = purchaseId !== undefined;

    var menuRow = !isEdit ? $('#menus-row-' + menuId).find('#btn-option-' + menuId) : $('#menus-row-' + purchaseId + '-' + menuId).find('#btn-option-' + purchaseId + '-' + menuId);
    var purchaseItemsRow = !isEdit ? $('#purchase-items-row-' + menuId) : $('#purchase-items-row-' + purchaseId + '-' + menuId);

    //td:eq(0) mengambil td index ke 0
    var rowDeleted = purchaseItemsRow.find('td:eq(0)').text();
    var menuStatus = !isEdit ? purchase['purchaseItems'][menuId]['menu'].menuStatus : purchases[purchaseId]['purchaseItems'][menuId]['menu'].menuStatus;  //var untuk menyimpan status menu

    var purchaseItemTotalPriceInput = !isEdit ? $('#purchase-item-total-price-' + menuId) : $('#purchase-item-total-price-' + purchaseId + '-' + menuId);    //var untuk menyimpan total harga untuk menu tertentu
    var purchaseTotalPriceInput = !isEdit ? $('#purchase-total') : $('#purchase-total-' + purchaseId);                             //var untuk menyimpan total harga keseluruhan pemesanan
    var lastTotalPrice = purchaseTotalPriceInput.val() - purchaseItemTotalPriceInput.val(); //mengurangi total harga pemesanan dengan total harga menu karena menu dihapus

    menuRow.removeClass('btn-danger');
    menuRow.addClass('btn-info');
    menuRow.removeAttr('disabled');
    menuRow.removeAttr('onclick');
    menuRow.text('Add');

    //jika status menu inavailable maka buttonnya didisable
    if (menuStatus === 'INAVAILABLE') {
        menuRow.attr('disabled', 'disabled');
    } else {
       var onClick = 'menuToPurchaseItem(' + menuId;

        if (isEdit) {
            onClick += ', ' + purchaseId;
        }

        onClick += ')';

        //jika statusnya available maka bisa ditambahkan
        menuRow.attr('onclick', onClick);
    }

    //mengganti isi purchaseTotalPriceInput dengan lastTotalPrice
    purchaseTotalPriceInput.val(lastTotalPrice);

    if (isEdit) {
        purchases[purchaseId]['purchaseTotal'] = lastTotalPrice;
    } else {
        purchase['purchaseTotal'] = lastTotalPrice;
    }

    //menghapus row
    purchaseItemsRow.remove();

    var itemsTableBodyId = '#purchase-items-table-body';

    if (isEdit) {
        itemsTableBodyId += '-' + purchaseId;
    }

    $(itemsTableBodyId).each(function () {
        var rowIndex = $(this).find('td:eq(0)').text();

        if (rowIndex > rowDeleted) {
            $(this).find('td:eq(0)').text(rowIndex - 1);
        }
    });

    if (isEdit) {
        purchases[purchaseId]['purchaseItems'][menuId] = undefined;
    } else {
        purchase['purchaseItems'][menuId] = undefined;
    }

    calculateItemWaitingTime();     //menghitung ulang waktu tunggu karena ada pesanan yang dihapus
};

//fungsi untuk menghapus semua purchaseItems jika gagal menambah
var deleteAllPurchaseItems = function () {
    //menghapus tiap item
    for (var purchaseKey in purchase['purchaseItems']) {
        deletePurchaseItem(purchaseKey);
    }
};

//fungsi untuk menghitung waiting time
var calculateItemWaitingTime = function () {
    var totalWaitingTime = 0;
    //bikin object waitingtime
    waitingTime = {};

    //looping tiap purchaseItems
    for (var purchaseKey in purchase['purchaseItems']) {
        //kondisi jika purchaseItems[purchaseKey] tidak undefined / ada isinya
        if (purchase['purchaseItems'][purchaseKey] !== undefined) {
            /*
            algoritma :
            hitung waiting time per kategori makanan, cari yang paling besar per kategori
            tambah antara semua kategori
             */

            //bikin var untuk nyimpen waiting time per kategori (semua kategori yang dipesan diitung)
            var waitingTimeAtCategory = waitingTime[purchase['purchaseItems'][purchaseKey]['menu'].menuCategory.menuCategoryName];

            //kalo misal belum nentuin waitingtime di kategori tertentu, data waiting time untuk menu di kategori itu langsung disimpen
            if (waitingTimeAtCategory === undefined)
                waitingTime[purchase['purchaseItems'][purchaseKey]['menu'].menuCategory.menuCategoryName] = purchase['purchaseItems'][purchaseKey]['menu'].menuWaitingTime;
            else {
                //kalo udah nyimpen waiting time untuk suatu kategori, cari waiting time terbesar baru disimpen
                if (waitingTimeAtCategory < purchase['purchaseItems'][purchaseKey]['menu'].menuWaitingTime)
                    waitingTime[purchase['purchaseItems'][purchaseKey]['menu'].menuCategory.menuCategoryName] = purchase['purchaseItems'][purchaseKey]['menu'].menuWaitingTime;
            }
        }
    }

    //buat menjumlah waiting time di semua kategori yg dipesan
    for (var waitingTimeKey in waitingTime)
        totalWaitingTime += waitingTime[waitingTimeKey];
    //mengganti isi dari purchase-waiting-time dengan totalWaitingTime
    $('#purchase-waiting-time').val(totalWaitingTime);
    purchase['purchaseWaitingTime'] = totalWaitingTime;
};

//fungsi untuk menghitung total harga untuk masing" menu (jika jumlah item menu yang dipesan diubah")
var calculatePurchaseItemTotalPrice = function (menuId) {
    /*
    algoritma :
    misal ada satu pemesanan:
    @20.000 * 2 = 40.000
    @30.000 * 2 = 60.000
    total = 100.000

    terus mau edit @20.000 jadi cuma 1, jadi yang total harga item @20.000 harus dihapus dulu
    100.000 - 40.000 = 60.000
    baru dihitung ulang untuk item itu
    60.000 + 20.000 = 80.000
     */
    var purchaseItemTotalPriceInput = $('#purchase-item-total-price-' + menuId);    //var untuk menyimpan total harga bagi masing" menu jika jumlahnya diubah
    var purchaseItemQuantityInput = $('#purchase-item-quantity-' + menuId);         //var untuk menyimpan jumlah item bagi masing" menu
    var purchaseTotalPriceInput = $('#purchase-total');                             //var untuk menyimpan jumlah harga yang harus dibayarkan untuk semua menu yang dipesan

    var lastTotalPrice = purchaseTotalPriceInput.val() - purchaseItemTotalPriceInput.val();                 //var untuk menyimpan total harga keseluruhan terakhir sebelum ditambah total harga per item baru
    var totalPrice = purchaseItemQuantityInput.val() * purchase['purchaseItems'][menuId]['menu'].menuPrice; //var untuk menyimpan harga menu * item yang dipesan

    purchaseTotalPriceInput.val(lastTotalPrice + totalPrice);
    purchaseItemTotalPriceInput.val(totalPrice);
    //purchaseItemTotalPriceInput.val(totalPrice) -> mengganti isi dari purchaseItemTotalPriceIput (refer ke line 407) dengan totalprice

    purchase['purchaseItems'][menuId].purchaseItemQuantity = purchaseItemQuantityInput.val();
    purchase['purchaseTotal'] = lastTotalPrice + totalPrice;
};

//fungsi untuk mengambil seluruh data purchase dan ditampilkan pada tabel pemesanan
var getPurchase = function (page, pageSize, sort) {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'purchase',       //diarahkan ke purchaseAPI cari yang mappingnya bertipe get namanya baseUrl/purchase
        type: 'GET', // Tipe pengaksesan url
        data: {                         // data yang diparse ke API
            userId: userId,
            purchaseStatus: "PROGRESS",
            page: page,
            pageSize: pageSize,
            sort: sort
        }, // Content parameter yang dikirim saat request
        success: function (data) {      //kalau sukses
            var tableTBody = $('#purchases-table-body');
            tableTBody.find('tr').remove();

            for (var i = 0; i < data.data.length; i++) {
                numRow++;

                var purchaseId = data.data[i].purchaseId;       //mengisi variabel dengan data yang diparse
                var purchaseDate = new Date(data.data[i].purchaseDate);
                var purchaseTotal = data.data[i].purchaseTotal;
                purchaseTotal = purchaseTotal.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");     //currency format

                purchases[purchaseId] = data.data[i];

                var purchaseItems = data.data[i].purchaseItems;
                var purchaseItemsRows = '';

                for (var j = 0; j < purchaseItems.length; j++) {
                    var purchaseMenu = purchaseItems[j].menu;

                    purchases[purchaseId]['purchaseItems'][purchaseMenu.id] = {
                        'menu': purchaseMenu
                    };

                    purchaseItemsRows +=
                        '<tr id="purchase-items-row-' + purchaseId + '-' + purchaseMenu.id + '">' +
                            '<td>' + (j + 1) + '</td>' +
                            '<td>' + purchaseMenu.menuName + '</td>' +
                            '<td>' + purchaseMenu.menuCategory.menuCategoryName + '</td>' +
                            '<td>' +
                                '<input id="purchase-item-quantity-' + purchaseId + '-' + purchaseMenu.id + '" type="number" class="form-control" onchange="calculatePurchaseItemTotalPrice(' + purchaseMenu.id + ')" value="' + purchaseItems[j].purchaseItemQuantity + '" required="required" />' +    //jumlah item pada masing" menu, ke fungsi calculatePurchaseItemTotalPrice()
                            '</td>' +
                            '<td>' +
                                '<input id="purchase-item-total-price-' + purchaseId + '-' + purchaseMenu.id + '" type="number" class="form-control" value="0" disabled="disabled" required="required" />' +    //nilai akan diperbarui, perhitungan di fungsi calculatePurchaseItemTotalPrice()
                            '</td>' +
                            '<td>' +
                                '<div class="btn-group">' +
                                    '<button class="btn btn-danger" type="button" onclick="deletePurchaseItem(' + purchaseMenu.id + ', ' + purchaseId + ')">Hapus</button>' +    //button untuk menghapus item menu yg dipesan, ke fungsi deletePurchaseItem()
                                '</div>' +
                            '</td>' +
                        '</tr>';
                }

                tableTBody.append(              //masukin data ke tabel di html
                    '<tr id="menus-row-' + purchaseId + '">' +
                        '<td>' + (numRow) + '</td>' +
                        '<td>' +
                            purchaseDate.getHours() + ':' + purchaseDate.getMinutes() + ':' + purchaseDate.getSeconds() + ', ' +
                            purchaseDate.getDate() + '/' + (purchaseDate.getMonth() + 1) + '/' + purchaseDate.getFullYear() +       //format get buat waktu
                        '</td>' +
                        '<td>' + data.data[i].purchaseTable + '</td>' +
                        '<td>' + data.data[i].purchaseWaitingTime + ' Menit ' + '</td>' +
                        '<td>' + 'Rp. ' + purchaseTotal + ',- ' + '</td>' +
                        '<td>' + data.data[i].cashier.userName + '</td>' +
                        '<td>' +
                            '<div class="btn-group">' +
                                '<button class="btn btn-info" data-toggle="modal" data-target="#editModal-' + purchaseId + '">Edit</button>' +
                                '<button class="btn btn-success" data-toggle="modal" data-target="#paidModal-' + purchaseId + '">Bayar</button>' +
                                '<button class="btn btn-danger" onclick="deletePurchase(' + purchaseId + ')">Hapus</button>' +  //ke fungsi deletePurchase
                                '<div class="modal fade" id="editModal-' + purchaseId + '" role="dialog">' +    //modal buat edit
                                    '<div class="modal-dialog">' +
                                        '<div class="modal-content">' +
                                            '<div class="modal-header">' +
                                                '<button type="button" class="close" data-dismiss="modal">&times;</button>' +
                                                '<h3 class="modal-title">Detail Pembelian</h3>' +
                                                '<h6 class="modal-title">Tanggal : ' +
                                                    purchaseDate.getHours() + ':' + purchaseDate.getMinutes() + ':' + purchaseDate.getSeconds() + ', ' +
                                                    purchaseDate.getDate() + '/' + (purchaseDate.getMonth() + 1) + '/' + purchaseDate.getFullYear() +   //get waktu di modal edit
                                                '</h6>' +
                                            '</div>' +
                                            '<form id="purchase-edit-form" onsubmit="submitFormEdit(' + purchaseId + ')">' +        //ke fungsi submitFormEdit untuk submit form edit
                                                '<div class="modal-body">' +
                                                    '<div class="form-group">' +
                                                        '<label for="purchase-table-' + purchaseId + '">Nomor meja : </label>' +
                                                        '<input id="purchase-table-' + purchaseId + '" type="number" placeholder="Masukkan nomor meja.." class="form-control" value="' + data.data[i].purchaseTable + '" required="required" />' +
                                                    '</div>' +
                                                    '<hr />' +
                                                    '<div class="form-group">' +
                                                        '<label for="menu-menu-category-' + purchaseId + '">Kategori menu : </label>' +
                                                        '<select class="form-control" id="menu-menu-category-' + purchaseId + '" onchange="getMenus(' + purchaseId + ')" required="required">' +
                                                        '</select>' +
                                                    '</div>' +
                                                    '<table id="menus-table-' + purchaseId + '" class="table table-hover">' +   //tabel untuk menampilkan menu
                                                        '<thead>' +
                                                            '<tr>' +
                                                                '<th>No</th>' +
                                                                '<th>Nama</th>' +
                                                                '<th>Harga</th>' +
                                                                '<th>Waktu Tunggu</th>' +
                                                                '<th>Status</th>' +
                                                                '<th></th>' +
                                                            '</tr>' +
                                                        '</thead>' +
                                                        '<tbody id="menus-table-body-' + purchaseId + '">' +
                                                        '</tbody>' +
                                                    '</table>' +
                                                    '<hr />' +
                                                    '<table id="purchase-items-table-' + purchaseId + '" class="table table-hover">' +
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
                                                        '<tbody id="purchase-items-table-body-' + purchaseId + '">' +
                                                            purchaseItemsRows +
                                                        '</tbody>' +
                                                    '</table>' +
                                                    '<hr />' +
                                                    '<div class="form-group">' +
                                                        '<label for="purchase-waiting-time-' + purchaseId + '">Waktu tunggu (Menit) : </label>' +
                                                        '<input id="purchase-waiting-time-' + purchaseId + '" type="number" class="form-control" value="' + data.data[i].purchaseWaitingTime + '" disabled="disabled" required="required" />' +
                                                    '</div>' +
                                                    '<div class="form-group">' +
                                                        '<label for="purchase-total-' + purchaseId + '">Total (Rupiah) : </label>' +
                                                        '<input id="purchase-total-' + purchaseId + '" type="number" class="form-control" value="' + data.data[i].purchaseTotal + '" disabled="disabled" required="required" />' +
                                                    '</div>' +
                                                '</div>' +
                                                '<div class="modal-footer">' +
                                                    '<button id="btn-edit" type="submit" class="btn btn-info">Edit</button>' +
                                                '</div>' +
                                            '</form>' +
                                        '</div>' +
                                    '</div>' +
                                '</div>' +
                                '<div class="modal fade" id="paidModal-' + purchaseId + '" role="dialog">' +    //untuk modal bayar
                                    '<div class="modal-dialog">' +
                                        '<div class="modal-content">' +
                                            '<div class="modal-header">' +
                                                '<button type="button" class="close" data-dismiss="modal">&times;</button>' +
                                                '<h3 class="modal-title">Bayar Pemesanan</h3>' +
                                            '</div>' +
                                            '<form id="menu-edit-form" onsubmit="submitFormPaidPurchase(' + purchaseId + ')" style="padding: 20px;">' + //ke fungsi submitFormPaidPurchase untuk submit form bayar
                                                '<div class="modal-body">' +
                                                    '<div class="form-group">' +
                                                        '<label for="purchase-total-' + purchaseId + '">Total (Rupiah) : </label>' +
                                                        '<input id="purchase-total-' + purchaseId + '" type="number" class="form-control" value="' + data.data[i].purchaseTotal + '" required="required" disabled="disabled" />' +  //menampilkan total harga yg harus dibayar pada pemesanan dengan id tertentu
                                                    '</div>' +
                                                    '<div class="form-group">' +
                                                        '<label for="purchase-paid-' + purchaseId + '">Pembayaran (Rupiah) : </label>' +
                                                        '<input id="purchase-paid-' + purchaseId + '" onchange="calculateChange(' + purchaseId + ')" type="number" placeholder="Masukkan pembayaran.." class="form-control" required="required" />' +   //ke fungsi calculateChange
                                                    '</div>' +
                                                    '<div class="form-group">' +
                                                        '<label for="purchase-change-' + purchaseId + '">Kembalian (Rupiah) : </label>' +
                                                        '<input id="purchase-change-' + purchaseId + '" type="number" class="form-control" value="0" required="required" disabled="disabled" />' +
                                                    '</div>' +
                                                '</div>' +
                                                '<div class="modal-footer">' +
                                                    '<button id="btn-paid-' + purchaseId + '" type="submit" class="btn btn-success" disabled="disabled">Bayar</button>' +
                                                '</div>' +
                                            '</form>' +
                                        '</div>' +
                                    '</div>' +
                                '</div>' +
                            '</div>' +
                        '</td>' +
                    '</tr>'
                );

                getMenuCategories(purchaseId);
            }
        },
        error: function (error) {
            console.log(error);
        }
    });
};

//fungsi untuk menghitung kembalian
var calculateChange = function (purchaseId) {
    var purchaseTotalInput = $('#purchase-total-' + purchaseId);    //untuk total harga yang harus dibayar berdasarkan pemesanan
    var purchasePaidInput = $('#purchase-paid-' + purchaseId);      //untuk uang yang dibayarkan
    var purchaseChangeInput = $('#purchase-change-' + purchaseId);  //untuk kembalian yang diberikan
    var purchasePaidBtn = $('#btn-paid-' + purchaseId);             //untuk button bayar

    var purchaseChange = purchasePaidInput.val() - purchaseTotalInput.val();    //kembalian = get data dengan syntax .val pada halaman html dengan id purchase... dikurangi dengan total harga

    if (purchaseChange < 0) {
        purchasePaidBtn.attr('disabled', 'disabled');   //kalau kembalian kurang dari 0 maka button paid didisable
    } else {
        purchasePaidBtn.removeAttr('disabled');
    }

    purchaseChangeInput.val(purchaseChange);
};

// fungsi untuk submit form bayar
var submitFormPaidPurchase = function (purchaseId) {
    event.preventDefault();
    
    $('#paidModal-' + purchaseId).modal('toggle');
    paidPurchase(purchaseId);
};

var paidPurchase = function (purchaseId) {
    var userId = $('#user-id').val(); // Mengambil user id dari input

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'purchase/paid/' + userId + '/' + purchaseId, //ke PurchaseAPI purchase/paid untuk mengganti status pemesanan menjadi dibayar
        type: 'POST',
        success: function (data) {
            alert(data.message);

            if (data.data === 1) {
                alert('Kembalian yang harus diberikan Rp. ' + $('#purchase-change-' + purchaseId).val() + ',-');    //menampilkan alert kembalian yang harus diberikan
                movePage(page, pageSize, sort);     //menampilkan halaman pemesanan
            }
        },
        error: function (error) {
            console.log(error);
        }
    });
};

/*
    fungsi untuk menghapus pemesanan
 */
var deletePurchase = function (purchaseId) {
    var userId = $('#user-id').val(); // Mengambil user id dari input
    var data = {};

    data['userId'] = userId; // Mengambil value dari input nama role

    /**
     * Fungsi ajax
     */
    $.ajax({
        url: baseApiUrl + 'purchase/' + purchaseId, //ke API yang mappingnya baseApiUrl/purchase/{purchaseId}
        type: 'DELETE',    // tipenya delete
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

    purchase['purchaseItems'] = [];
    purchases = [];

    //getMenuCategories() untuk menampilkan kategori menu
    getMenuCategories();
    //getMenus() untuk menampilkan menu
    getMenus();
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
    page = parseInt((purchaseCount / 10) + 1);
    movePage(page, pageSize, sort);
};

/*
    cara sorting =
    - panggil fungsi masing-masing sorting eg: changeSortDate
    - nilai variabel sort akan diganti sesuai dengan apa yang mau disorting, misal date mau disorting
      secara ascending maka nilai sort akan diubah jadi 2
    - nilai sort akan diparse dibawa pada fungsi movePage
    - pada fungsi movePage akan diparse dibawa pada fungsi getPurchase dimana akan request data purchase
    - selanjutnya pada fungsi getPurchase akan diparse ke API baseApiUrl/purchase
    - kalau sukses, akan didapatkan data dimana data sudah tersorting lalu ditampilkan
 */
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