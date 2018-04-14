package io.pivotal.pa.cassandrademo.repo;

import io.pivotal.pa.cassandrademo.domain.Species;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by jaguilar on 4/8/18.
 */
public interface SpeciesRepo extends PagingAndSortingRepository<Species, Integer> {
    @Query("SELECT * FROM pks.species LIMIT ?0")
    public List<Species> getLimitedSpecies(@Param("limit") int limit);
}
