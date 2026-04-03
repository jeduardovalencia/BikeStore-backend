// DetallePedidoRepository
package com.bike.shop.repository;

import com.bike.shop.entity.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {
    List<DetallePedido> findByPedidoId(Integer pedidoId);
    List<DetallePedido> findByBicicletaCodigo(Integer codigoBicicleta);
}