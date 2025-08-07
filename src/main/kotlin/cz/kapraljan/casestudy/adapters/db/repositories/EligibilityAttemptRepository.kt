package cz.kapraljan.casestudy.adapters.db.repositories

import cz.kapraljan.casestudy.adapters.db.entities.EligibilityAttempt
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EligibilityAttemptRepository : JpaRepository<EligibilityAttempt, Long>

