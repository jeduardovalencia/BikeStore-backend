// DetalleVentaRepository — ya lo tienes, solo verifica
package com.bike.shop.repository;

import com.bike.shop.entity.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {
    List<DetalleVenta> findByVentaId(Integer ventaId);
    List<DetalleVenta> findByBicicletaCodigo(Integer codigoBicicleta);

    @Query("SELECT dv FROM DetalleVenta dv " +
           "JOIN dv.venta v " +
           "WHERE v.estado = 'completada' " +
           "AND v.fecha BETWEEN :inicio AND :fin")
    List<DetalleVenta> findSalidasEnPeriodo(@Param("inicio") LocalDateTime inicio,
                                             @Param("fin") LocalDateTime fin);
}