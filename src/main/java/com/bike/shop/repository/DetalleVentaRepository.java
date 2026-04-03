// DetalleVentaRepository — ya lo tienes, solo verifica
package com.bike.shop.repository;

import com.bike.shop.entity.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {
    List<DetalleVenta> findByVentaId(Integer ventaId);
    List<DetalleVenta> findByBicicletaCodigo(Integer codigoBicicleta);
}