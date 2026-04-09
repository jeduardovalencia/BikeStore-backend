// DetallePedidoRepository
package com.bike.shop.repository;

import com.bike.shop.entity.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {
    List<DetallePedido> findByPedidoId(Integer pedidoId);
    List<DetallePedido> findByBicicletaCodigo(Integer codigoBicicleta);

    @Query("SELECT dp FROM DetallePedido dp " +
           "JOIN dp.pedido p " +
           "WHERE p.estado = 'recibido' " +
           "AND p.fecha BETWEEN :inicio AND :fin")
    List<DetallePedido> findEntradasEnPeriodo(@Param("inicio") LocalDateTime inicio,
                                               @Param("fin") LocalDateTime fin);
}