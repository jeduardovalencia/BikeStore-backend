package com.bike.shop.repository;

import com.bike.shop.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {

    List<Auditoria> findByFechaBetweenOrderByFechaDesc(LocalDateTime inicio, LocalDateTime fin);

    List<Auditoria> findByModuloOrderByFechaDesc(String modulo);

    List<Auditoria> findByFechaBetweenAndModuloOrderByFechaDesc(
            LocalDateTime inicio, LocalDateTime fin, String modulo);

    List<Auditoria> findAllByOrderByFechaDesc();
}
