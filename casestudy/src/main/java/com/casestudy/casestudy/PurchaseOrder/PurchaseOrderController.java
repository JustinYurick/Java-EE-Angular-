package com.casestudy.casestudy.PurchaseOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Po controler Class
@CrossOrigin
@RestController
public class PurchaseOrderController {
    @Autowired
    private PurchaseOrderDAO purchaseDAO;

    // purchase order mapping
    @PostMapping("/api/purchases")
    public ResponseEntity<Long> addOne(@RequestBody PurchaseOrder clientrep) { // use RequestBody here
        Long reportId = purchaseDAO.create(clientrep);
        return new ResponseEntity<Long>(reportId, HttpStatus.OK);
    }

    @GetMapping("/api/purchases/{id}")
    public ResponseEntity<Iterable<PurchaseOrder>> getOne(@PathVariable long id) {
        Iterable<PurchaseOrder> purchase = purchaseDAO.findByEmployee(id);
        return new ResponseEntity<Iterable<PurchaseOrder>>(purchase, HttpStatus.OK);
    }

}
