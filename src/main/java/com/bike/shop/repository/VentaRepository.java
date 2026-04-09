// VentaRepository — ya lo tienes, solo verifica
package com.bike.shop.repository;

import com.bike.shop.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {
    List<Venta> findByClienteDocumento(String documento);
    List<Venta> findByEstado(String estado);
    long countByEstado(String estado);
    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT dv.bicicleta.codigo, " +
            "dv.bicicleta.marca, " +
            "dv.bicicleta.modelo, " +
            "SUM(dv.cantidad) as totalVendido " +
            "FROM DetalleVenta dv " +
            "GROUP BY dv.bicicleta.codigo, dv.bicicleta.marca, dv.bicicleta.modelo " +
            "ORDER BY totalVendido DESC")
    List<Object[]> findTopBicicletas();
}