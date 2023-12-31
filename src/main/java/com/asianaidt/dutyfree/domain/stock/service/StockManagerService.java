package com.asianaidt.dutyfree.domain.stock.service;

import com.asianaidt.dutyfree.domain.stock.domain.Stock;
import com.asianaidt.dutyfree.domain.stock.domain.StockManager;
import com.asianaidt.dutyfree.domain.stock.domain.StockStatus;
import com.asianaidt.dutyfree.domain.stock.dto.StockManagerRequestDto;
import com.asianaidt.dutyfree.domain.stock.dto.StockStatusResponseDto;
import com.asianaidt.dutyfree.domain.stock.repository.StockManagerRepository;
import com.asianaidt.dutyfree.domain.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StockManagerService {
    private final StockManagerRepository stockManagerRepository;
    private final StockRepository stockRepository;

    public StockManager insertStock(StockManagerRequestDto dto){
        Stock stock = stockRepository.findById(dto.getStockId()).orElseThrow();
        return stockManagerRepository.save(StockManagerRequestDto.toEntity(dto,stock));

    }

    public Stock updateStockStatus(Long id){
        StockManager stockManager = stockManagerRepository.findById(id).orElseThrow(() -> new RuntimeException("재고관리 테이블에 해당하는 아이디가 없습니다."));
        stockManager.updateStatus();
        stockManagerRepository.save(stockManager);
        Stock stock = stockRepository.findById(stockManager.getStock().getId()).orElseThrow(() -> new RuntimeException("재고 아이디가 없습니다."));
        stock.updateQuantity(stockManager.getQuantity());
        return stockRepository.save(stock);
    }

    public Page<StockManager> getStockManagerList(Pageable pageable, StockStatus status){
        return stockManagerRepository.findAllByStatus(pageable, status);
    }

    public Page<StockStatusResponseDto> getStockManagerProgress(Pageable pageable){
        return stockRepository.findByStockManagerAndProductAndStockStatus(pageable, StockStatus.PROGRESS);
    }

    public Page<StockStatusResponseDto> getStockManagerCompleted(Pageable pageable){
        return stockRepository.findByStockManagerAndProductAndStockStatus(pageable, StockStatus.COMPLETED);
    }


}
