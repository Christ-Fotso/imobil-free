package com.realestate.gestion_immobiliere.repository;


import com.realestate.gestion_immobiliere.model.Bien;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface BienRepository  extends JpaRepository<Bien, Long>, JpaSpecificationExecutor<Bien> {

    static Specification<Bien> hasCity(String ville) {
        return (root, query, cb) -> ville == null ? null : cb.equal(cb.lower(root.get("ville")), ville.toLowerCase());
    }

    static Specification<Bien> hasType(String type) {
        return (root, query, cb) -> type == null ? null : cb.equal(cb.lower(root.get("type")), type.toLowerCase());
    }

    static Specification<Bien> minSuperficie(Integer minSuperficie) {
        return (root, query, cb) -> minSuperficie == null ? null : cb.ge(root.get("superficie"), minSuperficie);
    }

    static Specification<Bien> maxSuperficie(Integer maxSuperficie) {
        return (root, query, cb) -> maxSuperficie == null ? null : cb.le(root.get("superficie"), maxSuperficie);
    }

    static Specification<Bien> minPrix(Double minPrix) {
        return (root, query, cb) -> minPrix == null ? null : cb.ge(root.get("prix"), minPrix);
    }

    static Specification<Bien> maxPrix(Double maxPrix) {
        return (root, query, cb) -> maxPrix == null ? null : cb.le(root.get("prix"), maxPrix);
    }

    default List<Bien> findByDynamicFilters(String ville, String type, Integer minSuperficie, Integer maxSuperficie, Double minPrix, Double maxPrix) {
        Specification<Bien> spec = Specification.where(hasCity(ville))
                .and(hasType(type))
                .and(minSuperficie(minSuperficie))
                .and(maxSuperficie(maxSuperficie))
                .and(minPrix(minPrix))
                .and(maxPrix(maxPrix));

        return findAll(spec);
    }
    List<Bien> findByUserId(Long userId);
    @Query("SELECT b FROM Bien b WHERE " +
            "(:ville IS NULL OR b.ville = :ville) AND " +
            "(:type IS NULL OR b.type = :type) AND " +
            "(:minSuperficie IS NULL OR b.superficie >= :minSuperficie) AND " +
            "(:maxSuperficie IS NULL OR b.superficie <= :maxSuperficie) AND " +
            "(:minPrix IS NULL OR b.prix >= :minPrix) AND " +
            "(:maxPrix IS NULL OR b.prix <= :maxPrix)")
    List<Bien> findBiensByFilters(@Param("ville") String ville,
                                  @Param("type") String type,
                                  @Param("minSuperficie") Integer minSuperficie,
                                  @Param("maxSuperficie") Integer maxSuperficie,
                                  @Param("minPrix") Double minPrix,
                                  @Param("maxPrix") Double maxPrix);

}
