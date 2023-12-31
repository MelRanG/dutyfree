package com.asianaidt.dutyfree.domain.product.domain;

import com.asianaidt.dutyfree.domain.purchase.domain.PurchaseDetail;
import com.asianaidt.dutyfree.domain.stock.domain.Stock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productId")
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private int price;
    private String description;
    @Column(nullable = false)
    private String brand;
    @Column(nullable = true)
    private String path;
//    @OneToMany(mappedBy = "product")
//    private List<PurchaseDetail> purchaseDetail;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id")
    private Category category;
    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY)
    private Stock stock;
//    @OneToMany(mappedBy = "product")
//    private List<ProductImg> productImgs;

}
