package com.github.sbaldin.greatadvice.rest.advice

import com.github.sbaldin.greatadvice.rest.advice.repo.GreatAdviceRepository
import com.github.sbaldin.greatadvice.rest.domain.GreatAdviceDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class GreatAdviceService constructor(
    @Autowired
    private val repository: GreatAdviceRepository
) {
    /**
     * @see https://wiki.postgresql.org/wiki/Count_estimate#:~:text=The%20basic%20SQL%20standard%20query,due%20to%20the%20MVCC%20model.
     */
    var rowCountEstimated: Long = 0

    @PostConstruct
    fun init() {
        rowCountEstimated = repository.count()
    }

    fun random() = repository.random(rowCountEstimated)

    fun findAdviceById(id:Long) = repository.findAdviceById(id)

    fun findAllByTag(tags: List<String>, page: Int, pageSize: Int): Iterable<GreatAdviceDTO> {
        val offset = -pageSize + page * pageSize
        return repository.findAllByTagsQuery(tags.joinToString(separator = ","), offset, pageSize)
    }

    fun tags(): List<String> {
        return repository.tags()
    }
}