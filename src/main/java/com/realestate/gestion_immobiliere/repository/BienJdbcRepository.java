package com.realestate.gestion_immobiliere.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.realestate.gestion_immobiliere.model.Bien;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BienJdbcRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<Bien> rowMapper = (rs, rowNum) -> {
        Bien bien = new Bien();
        bien.setId(rs.getLong("id"));
        bien.setAdresse(rs.getString("adresse"));
        bien.setVille(rs.getString("ville"));
        bien.setType(rs.getString("type"));
        bien.setNbPieces(rs.getInt("nb_pieces"));
        bien.setSuperficie(rs.getDouble("superficie"));
        bien.setPrix(rs.getDouble("prix"));
        bien.setImageUrl(rs.getString("image_url"));
        return bien;
    };

    public List<Bien> findByCriteria(String ville, String type, Integer minSuperficie, Integer maxSuperficie, Double minPrix, Double maxPrix) {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM bien WHERE 1=1");

        if (ville != null && !ville.isEmpty()) {
            sql.append(" AND LOWER(ville) = LOWER(?)");
            params.add(ville);
        }
        if (type != null && !type.isEmpty()) {
            sql.append(" AND LOWER(type) = LOWER(?)");
            params.add(type);
        }
        if (minSuperficie != null) {
            sql.append(" AND superficie >= ?");
            params.add(minSuperficie);
        }
        if (maxSuperficie != null) {
            sql.append(" AND superficie <= ?");
            params.add(maxSuperficie);
        }
        if (minPrix != null) {
            sql.append(" AND prix >= ?");
            params.add(minPrix);
        }
        if (maxPrix != null) {
            sql.append(" AND prix <= ?");
            params.add(maxPrix);
        }

        return jdbcTemplate.query(sql.toString(), rowMapper, params.toArray());
    }
}
