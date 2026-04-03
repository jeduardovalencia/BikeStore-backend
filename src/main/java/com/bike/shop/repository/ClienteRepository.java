
package com.bike.shop.repository;

import com.bike.shop.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, String> {
    Optional<Cliente> findByEmail(String email);
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT c FROM Cliente c WHERE " +
            "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "c.documento LIKE CONCAT('%', :termino, '%') OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Cliente> buscarPorTermino(@Param("termino") String termino);
}