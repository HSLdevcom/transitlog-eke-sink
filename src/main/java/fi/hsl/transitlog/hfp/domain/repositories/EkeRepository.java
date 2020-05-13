package fi.hsl.transitlog.hfp.domain.repositories;

import fi.hsl.transitlog.hfp.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface EkeRepository extends JpaRepository<EkeData, UUID> {
}
