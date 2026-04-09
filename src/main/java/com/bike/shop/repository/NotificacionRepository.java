package com.bike.shop.repository;

import com.bike.shop.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    List<Notificacion> findByUsuarioDestinoIdOrderByFechaDesc(Integer userId);

    long countByUsuarioDestinoIdAndLeidaFalse(Integer userId);

    Optional<Notificacion> findByIdAndUsuarioDestinoId(Integer id, Integer userId);
}
