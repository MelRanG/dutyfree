package com.asianaidt.dutyfree.domain.purchase.repository;

import com.asianaidt.dutyfree.domain.purchase.domain.PurchaseLog;
import com.asianaidt.dutyfree.domain.purchase.dto.BrandSalesDto;
import com.asianaidt.dutyfree.domain.purchase.dto.CategorySalesDto;
import com.asianaidt.dutyfree.domain.purchase.dto.DailySalesDto;
import com.asianaidt.dutyfree.domain.purchase.dto.MonthlySalesDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseLogRepository extends JpaRepository<PurchaseLog, Long> {
    @Query("SELECT new com.asianaidt.dutyfree.domain.purchase.dto.MonthlySalesDto(FUNCTION('YEAR', p.regDate), FUNCTION('MONTH', p.regDate), SUM(p.price), SUM(p.quantity)) " +
            "FROM PurchaseLog p " +
            "GROUP BY FUNCTION('YEAR', p.regDate), FUNCTION('MONTH', p.regDate) " +
            "ORDER BY FUNCTION('YEAR', p.regDate) DESC, FUNCTION('MONTH', p.regDate) DESC")
    List<MonthlySalesDto> findMonthlySales(Pageable pageable);

    @Query("SELECT p.brand as brand, SUM(p.price) as totalSales from PurchaseLog p " +
            "GROUP BY p.brand " +
            "ORDER BY totalSales DESC")
    List<BrandSalesDto> findBrandSales(Pageable pageable);

    @Query("SELECT new com.asianaidt.dutyfree.domain.purchase.dto.CategorySalesDto(p.category, SUM(p.price)) from PurchaseLog p " +
            "GROUP BY p.category " +
            "ORDER BY SUM(p.price) DESC")
    List<CategorySalesDto> findCategorySales(Pageable pageable);

    @Query("SELECT FUNCTION('DATE', p.regDate) AS day, SUM(p.price) as totalSales FROM PurchaseLog p " +
            "WHERE YEAR(p.regDate) = :year AND MONTH(p.regDate) = :month " +
            "GROUP BY FUNCTION('DATE', p.regDate) " +
            "ORDER BY FUNCTION('DATE', p.regDate) DESC")
    List<DailySalesDto> findDailySalesForMonth(@Param("year") int year, @Param("month") int month);
}
