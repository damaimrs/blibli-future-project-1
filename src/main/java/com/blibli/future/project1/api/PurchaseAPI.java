package com.blibli.future.project1.api;

import com.blibli.future.project1.model.*;
import com.blibli.future.project1.service.PurchaseItemService;
import com.blibli.future.project1.service.PurchaseService;
import com.blibli.future.project1.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@RestController
public class PurchaseAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseAPI.class);

    private PurchaseService purchaseService;
    private PurchaseItemService purchaseItemService;
    private UserService userService;

    //kolom apa aja yang bisa disort index (0,1,2,3,4,5)
    private final String[] SORT_PROPERTIES = {
            "purchaseDate",
            "purchaseTable",
            "purchaseWaitingTime",
            "purchaseTotal",
            "cashier.userEmail",
            "purchaseStatus"
    };

    private final String[] SORT_DIRECTION = {
            "asc",
            "desc"
    };

    @Autowired
    public PurchaseAPI(PurchaseService purchaseService, PurchaseItemService purchaseItemService,
                       UserService userService) {
        this.purchaseService = purchaseService;
        this.purchaseItemService = purchaseItemService;
        this.userService = userService;
    }

    @GetMapping("/api/purchase/count")
    public ResponseEntity<Response<Long>> purchaseCount(@RequestParam Integer userId) {
        String message;
        Long count;
        User user = userService.findUserById(userId);

        if (user == null) {
            message = "Access denied";
            count = null;
        } else {
            message = "Access success";
            count = purchaseService.getPurchaseCount();
        }

        Response<Long> response = new Response<>(message, count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //untuk mendapatkan jumlah purchase dengan status tertentu, digunakan di histori pembelian
    @GetMapping("/api/purchase/count/status/{status}")
    public ResponseEntity<Response<Long>> purchaseCountByPurchaseStatus(@PathVariable String status,
                                                                        @RequestParam Integer userId) {
        String message;
        Long count;
        User user = userService.findUserById(userId);

        if (user == null) {
            message = "Access denied";
            count = null;
        } else {
            PurchaseStatus purchaseStatus = PurchaseStatus.valueOf(status);

            message = "Access success";
            count = purchaseService.getPurchaseCountByPurchaseStatus(purchaseStatus);
        }

        Response<Long> response = new Response<>(message, count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //fungsi untuk menghitung jumlah pembelian berdasarkan hari, digunakan di dashboard cashier
    @GetMapping("/api/purchase/count/date")
    public ResponseEntity<Response<Long>> purchaseCountByDate(@RequestParam(required = false) String startDate,
                                                              @RequestParam(required = false) String endDate,
                                                              @RequestParam Integer userId) {
        String message;
        Long count;
        User user = userService.findUserById(userId);

        if (user == null) {
            message = "Access denied";
            count = null;
        } else {
            Date purchaseDate = new Date(), endPurchaseDate;
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

            if (startDate != null) {
                try {
                    purchaseDate = simpleDateFormat.parse(startDate);
                } catch (ParseException e) {
                    purchaseDate = new Date();
                }
            }

            calendar.setTime(purchaseDate);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            purchaseDate = calendar.getTime();
            endPurchaseDate = purchaseDate;

            if (endDate != null) {
                try {
                    endPurchaseDate = simpleDateFormat.parse(endDate);
                } catch (ParseException e) {
                    endPurchaseDate = purchaseDate;
                }
            }

            calendar.setTime(endPurchaseDate);

            if (endDate == null) {
                calendar.set(Calendar.DAY_OF_MONTH, (calendar.get(Calendar.DAY_OF_MONTH) + 1));
            }

            endPurchaseDate = calendar.getTime();

            message = "Access success";
            count = purchaseService.getPurchaseCountByPurchaseDate(purchaseDate, endPurchaseDate);
        }

        Response<Long> response = new Response<>(message, count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //fungsi untuk mendapatkan total pendapatan dari seluruh pembelian
    @GetMapping("/api/purchase/total")
    public ResponseEntity<Response<Long>> purchaseTotal(@RequestParam Integer userId) {
        String message;
        Long total;
        User user = userService.findUserById(userId);

        if (user == null) {
            message = "Access denied";
            total = null;
        } else {
            message = "Access success";
            total = purchaseService.getTotalPurchaseTotal();

            if (total == null)
                total = 0L;
        }

        Response<Long> response = new Response<>(message, total);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //fungsi untuk mendapatkan total pendapatan dari seluruh pembelian
    @GetMapping("/api/purchase/total/status/{status}")
    public ResponseEntity<Response<Long>> purchaseTotal(@PathVariable String status,
                                                        @RequestParam Integer userId) {
        String message;
        Long total;
        User user = userService.findUserById(userId);

        if (user == null) {
            message = "Access denied";
            total = null;
        } else {
            PurchaseStatus purchaseStatus = PurchaseStatus.valueOf(status);

            message = "Access success";
            total = purchaseService.getTotalPurchaseTotal(purchaseStatus);

            if (total == null)
                total = 0L;
        }

        Response<Long> response = new Response<>(message, total);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //fungsi untuk mendapatkan total pendapatan dari seluruh pembelian
    @GetMapping("/api/purchase/total/date")
    public ResponseEntity<Response<Long>> purchaseTotal(@RequestParam Integer userId,
                                                        @RequestParam(required = false) String startDate,
                                                        @RequestParam(required = false) String endDate) {
        String message;
        Long total;
        User user = userService.findUserById(userId);

        if (user == null) {
            message = "Access denied";
            total = null;
        } else {
            message = "Access success";

            Date purchaseDate = new Date(), endPurchaseDate;
            //bikin object calendar
            Calendar calendar = Calendar.getInstance();
            //format datenya
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            //kalau ada parameter start date
            if (startDate != null) {
                try {
                    //purchaseDate diisi dengan startDate yang formatnya sudah diubah
                    purchaseDate = simpleDateFormat.parse(startDate);
                } catch (ParseException e) {
                    //kalau salah parsing
                    purchaseDate = new Date();
                }
            }
            //calendar digunakan untuk ambil waktu
            calendar.setTime(purchaseDate);
            //ngeset jam, menit, detik, milidetik dengan 0 karena di calendar waktunya mulai taun-milidetik
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            //ambil date yang baru setelah jam, menit, detik dan milidetiknya dihilangkan
            purchaseDate = calendar.getTime();
            endPurchaseDate = purchaseDate;
            //kalau endDatenya ada
            if (endDate != null) {
                try {
                    endPurchaseDate = simpleDateFormat.parse(endDate);
                } catch (ParseException e) {
                    endPurchaseDate = purchaseDate;
                }
            }

            calendar.setTime(endPurchaseDate);
            //kalau endDatenya null
            if (endDate == null) {
                //endDate dibuat sehari setelah purchaseDate
                calendar.set(Calendar.DAY_OF_MONTH, (calendar.get(Calendar.DAY_OF_MONTH) + 1));
            }

            endPurchaseDate = calendar.getTime();

            total = purchaseService.getTotalPurchaseTotal(purchaseDate, endPurchaseDate);

            if (total == null)
                total = 0L;
        }

        Response<Long> response = new Response<>(message, total);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //fungsi untuk mendapatkan total pendapatan dari pembelian pada tanggal tertentu
    @GetMapping("/api/purchase/total/status/{status}/date")
    public ResponseEntity<Response<Long>> purchaseTotal(@PathVariable String status,
                                                        @RequestParam Integer userId,
                                                        @RequestParam(required = false) String startDate,
                                                        @RequestParam(required = false) String endDate) {
        String message;
        Long total;
        User user = userService.findUserById(userId);

        if (user == null) {
            message = "Access denied";
            total = null;
        } else {
            message = "Access success";

            PurchaseStatus purchaseStatus = PurchaseStatus.valueOf(status);
            Date purchaseDate = new Date(), endPurchaseDate;
            //bikin object calendar
            Calendar calendar = Calendar.getInstance();
            //format datenya
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            //kalau ada parameter start date
            if (startDate != null) {
                try {
                    //purchaseDate diisi dengan startDate yang formatnya sudah diubah
                    purchaseDate = simpleDateFormat.parse(startDate);
                } catch (ParseException e) {
                    //kalau salah parsing
                    purchaseDate = new Date();
                }
            }
            //calendar digunakan untuk ambil waktu
            calendar.setTime(purchaseDate);
            //ngeset jam, menit, detik, milidetik dengan 0 karena di calendar waktunya mulai taun-milidetik
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            //ambil date yang baru setelah jam, menit, detik dan milidetiknya dihilangkan
            purchaseDate = calendar.getTime();
            endPurchaseDate = purchaseDate;
            //kalau endDatenya ada
            if (endDate != null) {
                try {
                    endPurchaseDate = simpleDateFormat.parse(endDate);
                } catch (ParseException e) {
                    endPurchaseDate = purchaseDate;
                }
            }

            calendar.setTime(endPurchaseDate);
            //kalau endDatenya null
            if (endDate == null) {
                //endDate dibuat sehari setelah purchaseDate
                calendar.set(Calendar.DAY_OF_MONTH, (calendar.get(Calendar.DAY_OF_MONTH) + 1));
            }

            endPurchaseDate = calendar.getTime();

            total = purchaseService.getTotalPurchaseTotal(purchaseStatus, purchaseDate, endPurchaseDate);

            if (total == null)
                total = 0L;
        }

        Response<Long> response = new Response<>(message, total);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //fungsi untuk get all purchase + sorting pada halaman pemesanan awal
    //parameter isinya false -> gaada isinya gapapa tapi nanti jadinya null
    @GetMapping("/api/purchase")
    public ResponseEntity<Response<ArrayList<Purchase>>> purchaseList(@RequestParam Integer userId,
                                                                      @RequestParam(required = false) String purchaseStatus,
                                                                      @RequestParam(required = false) String startDate,
                                                                      @RequestParam(required = false) String endDate,
                                                                      @RequestParam(required = false, defaultValue = "1") Integer page,
                                                                      @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                                      @RequestParam(required = false, defaultValue = "1") Integer sort) {
        User user = userService.findUserById(userId); //ngecek user udah login apa belum
        ArrayList<Purchase> purchases;
        String message;

        if (user == null) {
            message = "Access denied";
            purchases = null;
        } else {
            int sortPropertiesIndex = 0;
            int sortDirectionIndex = 0;

            if (page <= 0)
                page = 0;

            if (page > 0)
                page -= 1;

            if (pageSize <= 0)
                pageSize = 10;

            /*
                cara sorting pada API :
                - variabel sort didapat dari parse javascriptnya, ditampung dalam variabel sort
                  (variabel sort nilainya bisa digunakan untuk sorting berdasarkan nama kolom dan tipe sorting(asc atau desc))
                - sort akan di mod 2 untuk menentukan direction, lalu disimpan pada variabel sortDirectionIndex
                - untuk sorting berdasarkan kolom apa, akan dilakukan perhitungan dan disimpan pada variael sortPropertiesIndex
                - setelah itu dibuat arraylist bernama sortProperties
                - selanjutnya akan dilakukan sorting berdasarkan kolom tersebut
                - kodingan bawahnya yang calendar" aku ga ngerti yassalam (untuk ambil tanggal)
                - setelah disorting datanya akan diparse lagi ke JS

             */
            //range sort antara 1 - 10
            if (sort >= 1 && sort <= 10) {
                //dimod 2 untuk menentukan dia ganjil genap
                boolean isPrime = sort % 2 == 0;

                //menentukan direction dari ganjil genap, kalo genap Asc kalo ganjil Desc
                sortDirectionIndex = isPrime ? 1 : 0;

                //untuk menentukan kolom yg bakal disort (dibagi 2 soalnya kolom yg bisa disort cuma 5), ceil dibulatkan ke atas
                sortPropertiesIndex = (int) (Math.ceil((double) sort / 2) - 1);
            }

            ArrayList<String> sortProperties = new ArrayList<>();
            //ambil sort_properties diatas sesuai index yg sudah dihitung sebelumnya, disimpen ke sortProperties
            sortProperties.add(SORT_PROPERTIES[sortPropertiesIndex]);

            //mengambil direction untuk sorting dari SORT_DIRECTION index ke sortDirectionIndex
            Sort.Direction sortDirection = Sort.Direction.fromString(SORT_DIRECTION[sortDirectionIndex]);

            //bikin object sort yg berisi direction dan kolom patokan sorting
            Sort sortObj = new Sort(sortDirection, sortProperties);
            //bikin object paging sekalian disorting (sortingnya order by di database jadi gaada kodingan disini)
            PageRequest pageRequest = new PageRequest(page, pageSize, sortObj);

            //line if 244 sampe tutup if untuk filter
            //kalau mau filter pake status (parameter purchaseStatusnya ga null)
            if (purchaseStatus != null) {
                //kalau mau filter pake start dan end date
                if (startDate != null || endDate != null) {
                    //bikin object tanggal hari ini (new Date())
                    Date purchaseDate = new Date(), endPurchaseDate;
                    //bikin object calendar
                    Calendar calendar = Calendar.getInstance();
                    //format datenya
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    //kalau ada parameter start date
                    if (startDate != null) {
                        try {
                            //purchaseDate diisi dengan startDate yang formatnya sudah diubah
                            purchaseDate = simpleDateFormat.parse(startDate);
                        } catch (ParseException e) {
                            //kalau salah parsing
                            purchaseDate = new Date();
                        }
                    }
                    //calendar digunakan untuk ambil waktu
                    calendar.setTime(purchaseDate);
                    //ngeset jam, menit, detik, milidetik dengan 0 karena di calendar waktunya mulai taun-milidetik
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    //ambil date yang baru setelah jam, menit, detik dan milidetiknya dihilangkan
                    purchaseDate = calendar.getTime();
                    endPurchaseDate = purchaseDate;
                    //kalau endDatenya ada
                    if (endDate != null) {
                        try {
                            endPurchaseDate = simpleDateFormat.parse(endDate);
                        } catch (ParseException e) {
                            endPurchaseDate = purchaseDate;
                        }
                    }

                    calendar.setTime(endPurchaseDate);
                    //kalau endDatenya null
                    if (endDate == null) {
                        //endDate dibuat sehari setelah purchaseDate
                        calendar.set(Calendar.DAY_OF_MONTH, (calendar.get(Calendar.DAY_OF_MONTH) + 1));
                    }

                    endPurchaseDate = calendar.getTime();
                    //ambil data berdasarkan yang diminta
                    purchases = new ArrayList<>(purchaseService.findAllPurchaseByPurchaseStatus(PurchaseStatus.valueOf(purchaseStatus), purchaseDate, endPurchaseDate, pageRequest).getContent());
                } else {
                    //kalau mau nampilin data pake ketentuan status
                    purchases = new ArrayList<>(purchaseService.findAllPurchaseByPurchaseStatus(PurchaseStatus.valueOf(purchaseStatus), pageRequest).getContent());
                }
            } else {
                //kalau ga pake filter maka tampilin seluruh data
                purchases = new ArrayList<>(purchaseService.findAllPageable(pageRequest).getContent());
            }

            for (Purchase purchase : purchases) {
                ArrayList<PurchaseItem> purchaseItems = new ArrayList<>(purchaseItemService.findAllPurchaseItemByPurchaseId(purchase.getPurchaseId()));

                for (PurchaseItem purchaseItem : purchaseItems)
                    purchaseItem.setPurchase(null);

                purchase.setPurchaseItems(purchaseItems);
            }
            //sampai ini hamba ga ngerti
            message = "Find purchases success";
        }

        Response<ArrayList<Purchase>> response = new Response<>(message, purchases);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //fungsi untuk mendapatkan seluruh data purchase pada DB tanpa sorting
    @GetMapping("/api/purchase/all")
    public ResponseEntity<Response<ArrayList<Purchase>>> purchaseListAll(@RequestParam Integer userId) {
        User user = userService.findUserById(userId);
        ArrayList<Purchase> purchases;
        String message;

        if (user == null) {
            message = "Access denied";
            purchases = null;
        } else {
            message = "Find purchases success";
            purchases = new ArrayList<>(purchaseService.findAll());
        }

        Response<ArrayList<Purchase>> response = new Response<>(message, purchases);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //fungsi untuk menambah purchase
    @PostMapping("/api/purchase/{userId}")
    public ResponseEntity<Response<Integer>> addPurchase(@PathVariable Integer userId, @RequestBody Purchase purchase) {
        User user = userService.findUserById(userId);
        Integer data;
        String message;

        if (user == null) {
            message = "Access denied";
            data = null;
        } else {
            try {
                // getPurchaseCountByPurchaseStatusAndPurchaseTable -> buat mencari apakah ada yang sudah melakukan pemesanan pada nomor meja tersebut
                // kalau sudah memesan di meja tersebut gabisa pesen lagi
                long purchaseProgressTableCount = purchaseService.getPurchaseCountByPurchaseStatusAndPurchaseTable(purchase.getPurchaseStatus(), purchase.getPurchaseTable());
                boolean isValid = purchaseProgressTableCount == 0;

                if (isValid) {
                    boolean isEdit = purchase.getPurchaseId() != 0;
                    message = isEdit ? "Purchase edit " : "Purchase add ";
                    //kalau dia add, datenya disetting sekarang
                    if (!isEdit) {
                        purchase.setPurchaseDate(new Date());
                    }

                    //ngeset chasiernya
                    purchase.setCashier(user);
                    //nambah purchase dengan item"
                    purchase = purchaseService.addPurchaseWithPurchaseItem(purchase);

                    boolean isSuccess = purchase != null;
                    message += isSuccess ? "success" : "failed";
                    data = isSuccess ? 1 : 0;
                } else {
                    message = "Purchase add failed - Table already reserved";
                    data = 0;
                }
            } catch (DataIntegrityViolationException e) {
                message = "Purchase add failed - Purchase already exists";
                data = 0;
            } catch (Exception e) {
                message = "Purchase add failed - Internal Server Error";
                data = 0;
            }
        }

        Response<Integer> response = new Response<>(message, data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //API yg dituju saat button bayar dipencet
    @PostMapping("/api/purchase/paid/{userId}/{purchaseId}")
    public ResponseEntity<Response<Integer>> paidPurchase(@PathVariable Integer userId,
                                                          @PathVariable Integer purchaseId) {
        User user = userService.findUserById(userId);
        Integer data;
        String message;

        if (user == null) {
            message = "Access denied";
            data = null;
        } else {
            try {
                message = "Paid purchase ";

                //mencari purchase dengan id tertentu
                Purchase purchase = purchaseService.findPurchaseById(purchaseId);
                //mengganti status purchase dengan status dibayar
                purchase.setPurchaseStatus(PurchaseStatus.PAID);
                //menambah purchase dengan status yang telah diperbaharui
                purchase = purchaseService.addPurchase(purchase);

                boolean isSuccess = purchase != null;
                message += isSuccess ? "success" : "failed";
                data = isSuccess ? 1 : 0;
            } catch (Exception e) {
                message = "Paid purchase failed - Internal Server Error";
                data = 0;
            }
        }

        Response<Integer> response = new Response<>(message, data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //fungsi untuk menghapus purchase
    @DeleteMapping("/api/purchase/{purchaseId}")
    /*
        mapping untuk menghapus data pemesanan
     */
    public ResponseEntity<Response<Integer>> deletePurchase(@PathVariable Integer purchaseId, @RequestBody Map<String, Integer> params) {
        Integer userId = params.get("userId");
        User user = userService.findUserById(userId);
        Integer data;
        String message;

        if (user == null) {
            message = "Access denied";
            data = null;
        } else {
            try {
                purchaseService.deletePurchase(purchaseId); //ke purchaseService ada fungsi deletePurchase

                message = "Delete purchase success";
                data = 1;
            } catch (EmptyResultDataAccessException e) {
                message = "Delete purchase failed - Purchase not found";
                data = 0;
            } catch (DataIntegrityViolationException e) {
                message = "Delete purchase failed - Purchase in use";
                data = 0;
            } catch (Exception e) {
                message = "Delete purchase failed - Internal Server Error";
                data = 0;
            }
        }

        Response<Integer> response = new Response<>(message, data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
