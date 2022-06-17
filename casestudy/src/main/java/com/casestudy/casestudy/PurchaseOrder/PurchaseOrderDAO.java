package com.casestudy.casestudy.PurchaseOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import com.casestudy.casestudy.Product.Product;
import com.casestudy.casestudy.Product.ProductRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component

// Purchase Order Data access object class
public class PurchaseOrderDAO {

    // private variable
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ProductRepository prodRepo;

    // create method
    @Transactional
    public Long create(PurchaseOrder clientrep) {
        PurchaseOrder realPurchaseOrder = new PurchaseOrder();
        realPurchaseOrder.setVendorid(clientrep.getVendorid());
        realPurchaseOrder.setAmount(clientrep.getAmount());
        realPurchaseOrder.setPodate(LocalDateTime.now());
        entityManager.persist(realPurchaseOrder);
        // loops trough each item in the order
        for (PurchaseOrderLineitem item : clientrep.getItems()) {
            PurchaseOrderLineitem realItem = new PurchaseOrderLineitem();
            realItem.setPoid(realPurchaseOrder.getId());
            realItem.setPrice(item.getPrice());
            realItem.setQty(item.getQty());
            realItem.setProductid(item.getProductid());
            entityManager.persist(realItem);
            // we also need to update the QOO on the product table
            Product prod = prodRepo.findById(item.getProductid()).get();
            prod.setQoo(prod.getQoo() + item.getQty());
            prodRepo.saveAndFlush(prod);
        }
        return realPurchaseOrder.getId();
    }

    // findone takes a PO id
    public PurchaseOrder findOne(long id) {
        PurchaseOrder purchaseOrder = entityManager.find(PurchaseOrder.class, id);
        if (purchaseOrder == null) {
            throw new EntityNotFoundException("Can't find Purchase for ID " + id);
        }
        return purchaseOrder;
    }

    @SuppressWarnings("unchecked")
    public List<PurchaseOrder> findByEmployee(Long vendorid) {
        return entityManager.createQuery("select p from PurchaseOrder p where p.vendorid = :id")
                .setParameter("id", vendorid).getResultList();
    }
}
