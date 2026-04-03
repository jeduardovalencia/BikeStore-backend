// DETALLE_PEDIDO
package com.bike.shop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigo_bicicleta", nullable = false)
    private Bicicleta bicicleta;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_costo_unitario", nullable = false)
    private BigDecimal precioCostoUnitario;
}