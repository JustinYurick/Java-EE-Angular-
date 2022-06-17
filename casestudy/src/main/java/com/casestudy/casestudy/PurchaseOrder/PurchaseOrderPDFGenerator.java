package com.casestudy.casestudy.PurchaseOrder;

import com.casestudy.casestudy.Product.Product;
import com.casestudy.casestudy.Product.ProductRepository;
import com.casestudy.casestudy.Vendor.Vendor;
import com.casestudy.casestudy.Vendor.VendorRepository;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import org.springframework.cglib.beans.BeanCopier.Generator;
import org.springframework.web.servlet.view.document.AbstractPdfView;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

//class that creates the PDF
public abstract class PurchaseOrderPDFGenerator extends AbstractPdfView {
        public static ByteArrayInputStream generatePurchaseOrder(String poid, PurchaseOrderDAO poDAO,
                        VendorRepository vendorRepository, ProductRepository productRepository) throws IOException {
                URL imageUrl = Generator.class.getResource("/static/images/store.png");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(baos);
                // Initialize PDF document to be written to a stream not a file
                PdfDocument pdf = new PdfDocument(writer);
                // Document is the main object
                Document document = new Document(pdf);
                PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
                // add the image to the document
                Image img = new Image(ImageDataFactory.create(imageUrl)).scaleAbsolute(240, 90).setFixedPosition(80,
                                710);
                document.add(img);
                // now let's add a big heading
                document.add(new Paragraph("\n\n"));
                Locale locale = new Locale("en", "US");
                NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
                try {
                        // calls the purchase order findOne methiod
                        PurchaseOrder purchaseOrder = poDAO.findOne(Long.parseLong(poid));
                        document.add(new Paragraph(String.format("Products")).setFont(font).setFontSize(24)
                                        .setMarginRight(75).setTextAlignment(TextAlignment.RIGHT).setBold());
                        document.add(new Paragraph("Purchase Order#:" + poid).setFont(font).setFontSize(16).setBold()
                                        .setMarginRight(90).setMarginTop(-10).setTextAlignment(TextAlignment.RIGHT));
                        document.add(new Paragraph("\n\n"));
                        // calls the vendor findby id method
                        Optional<Vendor> opt = vendorRepository.findById(purchaseOrder.getVendorid());
                        if (opt.isPresent()) {
                                Vendor vendor = opt.get();
                                Table tableVen = new Table(1);
                                tableVen.setWidth(new UnitValue(UnitValue.PERCENT, 20));
                                tableVen.setBackgroundColor(ColorConstants.LIGHT_GRAY);
                                // prints the vendor details table
                                Cell cell = new Cell()
                                                .add(new Paragraph("Vendor").setFont(font).setFontSize(12).setBold())
                                                .setTextAlignment(TextAlignment.CENTER);
                                tableVen.addCell(cell);
                                cell = new Cell().add(new Paragraph(vendor.getName()).setFont(font).setFontSize(12)
                                                .setTextAlignment(TextAlignment.LEFT));
                                tableVen.addCell(cell);
                                cell = new Cell().add(new Paragraph(vendor.getAddress1()).setFont(font).setFontSize(12)
                                                .setTextAlignment(TextAlignment.LEFT));
                                tableVen.addCell(cell);
                                cell = new Cell().add(new Paragraph(vendor.getCity()).setFont(font).setFontSize(12)
                                                .setTextAlignment(TextAlignment.LEFT));
                                tableVen.addCell(cell);
                                cell = new Cell().add(new Paragraph(vendor.getProvince()).setFont(font).setFontSize(12)
                                                .setTextAlignment(TextAlignment.LEFT));
                                tableVen.addCell(cell);
                                cell = new Cell().add(new Paragraph(vendor.getEmail()).setFont(font).setFontSize(12)
                                                .setTextAlignment(TextAlignment.LEFT));
                                tableVen.addCell(cell);
                                document.add(tableVen);
                                document.add(new Paragraph("\n\n"));
                        }
                        // prints out the line items
                        BigDecimal tot = new BigDecimal(0.0);
                        BigDecimal tax = new BigDecimal(0.13);
                        Table table = new Table(5);
                        table.setWidth(new UnitValue(UnitValue.PERCENT, 100));
                        Cell cell = new Cell()
                                        .add(new Paragraph("Product Code").setFont(font).setFontSize(12).setBold())
                                        .setTextAlignment(TextAlignment.CENTER);
                        table.addCell(cell);
                        cell = new Cell().add(new Paragraph("Description").setFont(font).setFontSize(12).setBold())
                                        .setTextAlignment(TextAlignment.CENTER);
                        table.addCell(cell);
                        cell = new Cell().add(new Paragraph("Qty Sold").setFont(font).setFontSize(12).setBold())
                                        .setTextAlignment(TextAlignment.CENTER);
                        table.addCell(cell);
                        cell = new Cell().add(new Paragraph("Price").setFont(font).setFontSize(12).setBold())
                                        .setTextAlignment(TextAlignment.CENTER);
                        table.addCell(cell);
                        cell = new Cell().add(new Paragraph("Ext. Price").setFont(font).setFontSize(12).setBold())
                                        .setTextAlignment(TextAlignment.CENTER);
                        table.addCell(cell);
                        for (PurchaseOrderLineitem line : purchaseOrder.getItems()) {
                                Optional<Product> optx = productRepository.findById(line.getProductid());
                                if (optx.isPresent()) {
                                        // prints out the line items
                                        Product product = optx.get();
                                        tot = tot.add(product.getCostprice()
                                                        .multiply(BigDecimal.valueOf(line.getQty())),
                                                        new MathContext(8, RoundingMode.UP));
                                        // table details
                                        cell = new Cell().add(new Paragraph(product.getId()).setFont(font)
                                                        .setFontSize(12).setTextAlignment(TextAlignment.LEFT));
                                        table.addCell(cell);
                                        cell = new Cell().add(new Paragraph(product.getName()).setFont(font)
                                                        .setFontSize(12).setTextAlignment(TextAlignment.LEFT));
                                        table.addCell(cell);
                                        cell = new Cell().add(new Paragraph(Integer.toString(line.getQty()))
                                                        .setFont(font).setFontSize(12)
                                                        .setTextAlignment(TextAlignment.LEFT));
                                        table.addCell(cell);
                                        cell = new Cell().add(new Paragraph(formatter.format(product.getCostprice())));
                                        table.addCell(cell);
                                        cell = new Cell().add(new Paragraph(formatter.format(product.getCostprice()
                                                        .multiply(BigDecimal.valueOf(line.getQty())))));
                                        table.addCell(cell);
                                }
                        }
                        // report total
                        cell = new Cell(1, 4).add(new Paragraph("Sub Total:")).setBorder(Border.NO_BORDER)
                                        .setTextAlignment(TextAlignment.RIGHT);
                        table.addCell(cell);
                        cell = new Cell().add(new Paragraph(formatter.format(tot)))
                                        .setTextAlignment(TextAlignment.RIGHT)
                                        .setBackgroundColor(ColorConstants.YELLOW);
                        table.addCell(cell);
                        cell = new Cell(1, 4).add(new Paragraph("Tax:")).setBorder(Border.NO_BORDER)
                                        .setTextAlignment(TextAlignment.RIGHT);
                        table.addCell(cell);
                        cell = new Cell().add(new Paragraph(formatter.format(tot.multiply(tax))))
                                        .setTextAlignment(TextAlignment.RIGHT)
                                        .setBackgroundColor(ColorConstants.YELLOW);
                        table.addCell(cell);
                        cell = new Cell(1, 4).add(new Paragraph("PO Total:")).setBorder(Border.NO_BORDER)
                                        .setTextAlignment(TextAlignment.RIGHT);
                        table.addCell(cell);
                        cell = new Cell().add(new Paragraph(formatter.format(tot.add((tot.multiply(tax))))))
                                        .setTextAlignment(TextAlignment.RIGHT)
                                        .setBackgroundColor(ColorConstants.YELLOW);
                        table.addCell(cell);
                        document.add(table);
                        document.close();
                } catch (Exception ex) {
                        Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
                }
                // finally send stream back to the controller
                return new ByteArrayInputStream(baos.toByteArray());
        }
}