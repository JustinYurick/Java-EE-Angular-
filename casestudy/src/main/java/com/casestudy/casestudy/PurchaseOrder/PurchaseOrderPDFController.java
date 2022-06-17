package com.casestudy.casestudy.PurchaseOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.io.ByteArrayInputStream;

import javax.servlet.http.HttpServletRequest;

import com.casestudy.casestudy.Product.ProductRepository;
import com.casestudy.casestudy.Vendor.VendorRepository;
import com.itextpdf.io.IOException;

@CrossOrigin
@RestController
public class PurchaseOrderPDFController {

        @Autowired
        private VendorRepository vendorRepository;
        @Autowired
        private ProductRepository productRepository;
        @Autowired
        private PurchaseOrderDAO purchaserOrderDAO;

        @RequestMapping(value = "PurchaseOrderPDF", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
        public ResponseEntity<InputStreamResource> streamPDF(HttpServletRequest request)
                        throws IOException, java.io.IOException {
                // get formatted pdf as a stream
                String poid = request.getParameter("poid");
                ByteArrayInputStream bis = PurchaseOrderPDFGenerator.generatePurchaseOrder(poid, purchaserOrderDAO,
                                vendorRepository, productRepository);
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", "inline; filename=examplepurchaseOrder.pdf");
                // dump stream to browser
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                                .body(new InputStreamResource(bis));
        }
}
