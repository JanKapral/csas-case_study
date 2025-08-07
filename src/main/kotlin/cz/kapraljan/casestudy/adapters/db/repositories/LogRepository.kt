package cz.kapraljan.casestudy.adapters.db.repositories

import cz.kapraljan.casestudy.adapters.db.entities.Log
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LogRepository : JpaRepository<Log, Long> {
}