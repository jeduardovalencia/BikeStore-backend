package com.bike.shop.repository;

import com.bike.shop.entity.AprobacionVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AprobacionVentaRepository extends JpaRepository<AprobacionVenta, Integer> {

    Optional<AprobacionVenta> findByVentaId(Integer ventaId);
}
