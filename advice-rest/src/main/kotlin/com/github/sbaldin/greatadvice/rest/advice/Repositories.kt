package com.github.sbaldin.greatadvice.rest.advice.repo

import com.github.sbaldin.greatadvice.rest.domain.GreatAdviceDTO
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*

interface GreatAdviceRepository : CrudRepository<GreatAdviceDTO, Long> {

    @Query(
        value = """
            select *
            from fucking_great_advice.great_advice as ga
            where array_length(ga.tags, 1) > 0 and ga.tags @> string_to_array(:tags, ',')
            OFFSET :offset LIMIT :pageSize
        """,
        nativeQuery = true
    )
    fun findAllByTagsQuery(@Param("tags") tags: String, offset: Int, pageSize: Int): Iterable<GreatAdviceDTO>

    @Query(
        value = "SELECT * FROM fucking_great_advice.great_advice OFFSET floor(random() * :rowCountEstimated) LIMIT 1",
        nativeQuery = true
    )
    fun random(@Param("rowCountEstimated") rowCountEstimated: Long): GreatAdviceDTO?

    @Query(
        value = "SELECT reltuples AS estimate FROM pg_class WHERE relname = 'great_advice';",
        nativeQuery = true
    )
    override fun count(): Long


    @Query(
        value = "select * from fucking_great_advice.great_advice where id=:advice_id",
        nativeQuery = true
    )
    fun findAdviceById(@Param("advice_id") id: Long): GreatAdviceDTO?

    @Query(
        value = "SELECT distinct unnest(tags) from fucking_great_advice.great_advice where array_length(tags, 1) > 0",
        nativeQuery = true
    )
    fun tags(): List<String>

}
