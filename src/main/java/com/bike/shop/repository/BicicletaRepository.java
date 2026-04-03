// BicicletaRepository
package com.bike.shop.repository;

import com.bike.shop.entity.Bicicleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.math.BigDecimal;

public interface BicicletaRepository extends JpaRepository<Bicicleta, Integer> {
    List<Bicicleta> findByMarcaIgnoreCase(String marca);
    List<Bicicleta> findByTipoIgnoreCase(String tipo);

    @Query("SELECT b FROM Bicicleta b WHERE b.cantidad <= b.stockMinimo")
    List<Bicicleta> findStockBajo();

    @Query("SELECT b FROM Bicicleta b WHERE b.cantidad = 0")
    List<Bicicleta> findSinStock();

    @Query("SELECT b FROM Bicicleta b WHERE " +
            "LOWER(b.marca) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(b.modelo) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Bicicleta> buscarPorTermino(@Param("termino") String termino);

    @Query("SELECT b FROM Bicicleta b WHERE b.precioVenta BETWEEN :min AND :max")
    List<Bicicleta> findByRangoPrecio(@Param("min") BigDecimal min,
                                      @Param("max") BigDecimal max);
}