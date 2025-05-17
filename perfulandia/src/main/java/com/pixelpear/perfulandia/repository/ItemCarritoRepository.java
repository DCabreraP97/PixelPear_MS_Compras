package com.pixelpear.perfulandia.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pixelpear.perfulandia.model.ItemCarrito;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    List<ItemCarrito> findByAlias(String alias);
    
    void deleteByIdProductoAndAlias(Long idProducto, String alias);

    void deleteByAlias(String alias);

    Optional<ItemCarrito> findByIdProductoAndAlias(Long idProducto, String alias);

}
