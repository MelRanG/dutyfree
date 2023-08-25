package com.asianaidt.dutyfree.domain.purchase.service;


import com.asianaidt.dutyfree.domain.member.domain.Member;
import com.asianaidt.dutyfree.domain.product.domain.Product;
import com.asianaidt.dutyfree.domain.product.repository.ProductRepository;
import com.asianaidt.dutyfree.domain.purchase.domain.Purchase;
import com.asianaidt.dutyfree.domain.purchase.domain.PurchaseDetail;
import com.asianaidt.dutyfree.domain.purchase.domain.PurchaseLog;
import com.asianaidt.dutyfree.domain.purchase.dto.PurchaseDetailDto;
import com.asianaidt.dutyfree.domain.purchase.dto.PurchaseDto;
import com.asianaidt.dutyfree.domain.purchase.repository.PurchaseDetailRepository;
import com.asianaidt.dutyfree.domain.purchase.repository.PurchaseLogRepository;
import com.asianaidt.dutyfree.domain.purchase.repository.PurchaseRepository;
import com.asianaidt.dutyfree.domain.stock.domain.Stock;
import com.asianaidt.dutyfree.domain.stock.repository.StockRepository;
import com.asianaidt.dutyfree.domain.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseService {
    private final ProductRepository productRepository;
    private final PurchaseRepository purchaseRepository;
    private final PurchaseDetailRepository purchaseDetailRepository;
    private final StockRepository stockRepository;
    private final PurchaseLogRepository purchaseLogRepository;
    private final StockService stockService;

    public List<Purchase> getPurchaseList(Member member) {
        return purchaseRepository.findAllByMemberId(member.getId());
    }

    public Optional<Purchase> getPurchase(Long purchaseId, Member member) {
        return purchaseRepository.findByMemberAndId(member, purchaseId);
    }

    public void addPurchaseDetails(Purchase purchase, List<PurchaseDetailDto> detailDtoList) {
        int totalAmount = 0;

        for(PurchaseDetailDto detailDto : detailDtoList) {
            long productId = detailDto.getProductId();
            Optional<Product> product = productRepository.findById(productId);
            Optional<Stock> stock = stockRepository.findByProductId(productId);

            if(product.isPresent() && stock.isPresent()) {
                // 재고 확인
                stockService.decrease(stock.get().getId(), (long) detailDto.getQuantity());

                PurchaseDetail purchaseDetail = PurchaseDetail.builder()
                        .purchase(purchase)
                        .quantity(detailDto.getQuantity())
                        .product(product.get())
                        .build();

                // 총 수량 합산
                totalAmount += detailDto.getQuantity();

                purchaseDetailRepository.save(purchaseDetail);

                PurchaseLog purchaseLog = PurchaseLog.builder()
                        .regDate(LocalDateTime.now())
                        .price(product.get().getPrice() * detailDto.getQuantity())
                        .brand(product.get().getBrand())
                        .productName(product.get().getName())
                        .productId(product.get().getId())
                        .quantity(detailDto.getQuantity())
                        .category(product.get().getCategory())
                        .build();

                purchaseLogRepository.save(purchaseLog);

            }
        }

        purchase.setTotalAmount(totalAmount);
        purchaseRepository.save(purchase);
    }

    @Transactional
    public void purchaseMany(Member member, PurchaseDto purchaseDto) {
        Purchase purchase = Purchase.builder()
                .regDate(LocalDateTime.now())
                .member(member)
                .build();

        purchaseRepository.save(purchase);

        addPurchaseDetails(purchase, purchaseDto.getDetailList());
    }

    @Transactional()
    public void purchase(Member member, Long productId, PurchaseDto purchaseDto) {
        Optional<Product> product = productRepository.findById(productId);
        Optional<Stock> stock = stockRepository.findByProductId(productId);

        if(product.isPresent() && stock.isPresent()) {
            int totalAmount = purchaseDto.getQuantity();

            Purchase purchase = Purchase.builder()
                    .regDate(LocalDateTime.now())
                    .member(member)
                    .build();

            purchaseRepository.save(purchase);

            if (purchaseDto.getDetailList() == null) {

                log.info("stockId= {}", stock.get().getId());

//                stockService.decrease(stock.get().getId(), (long) totalAmount);

                PurchaseDetail purchaseDetail = PurchaseDetail.builder()
                        .purchase(purchase)
                        .quantity(totalAmount)
                        .product(product.get())
                        .build();

                purchaseDetailRepository.save(purchaseDetail);

                PurchaseLog purchaseLog = PurchaseLog.builder()
                        .regDate(LocalDateTime.now())
                        .price(product.get().getPrice() * totalAmount)
                        .brand(product.get().getBrand())
                        .productName(product.get().getName())
                        .productId(product.get().getId())
                        .quantity(totalAmount)
                        .category(product.get().getCategory())
                        .build();

                purchaseLogRepository.save(purchaseLog);

            }

            purchase.setTotalAmount(totalAmount);
            purchaseRepository.save(purchase);
        }
    }
}
