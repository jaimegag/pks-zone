package io.pivotal.pa.cassandrademo.repo;

import io.pivotal.pa.cassandrademo.domain.Species;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by jaguilar on 4/8/18.
 */
public interface SpeciesRepo extends CrudRepository<Species, Integer> {
    @Query("SELECT * FROM pks.species LIMIT ?0")
    public List<Species> getLimitedSpecies(@Param("limit") int limit);

    @Query("SELECT * FROM pks.species WHERE common_name=?0 ALLOW FILTERING")
    public List<Species> findByCommon_name(@Param("common_name") String commonName);

    @Query("SELECT * FROM pks.species WHERE county=?0 ALLOW FILTERING")
    public List<Species> findByCounty(@Param("county") String county);
}
