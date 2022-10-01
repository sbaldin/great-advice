package com.github.sbaldin.greatadvice.rest.domain

import com.vladmihalcea.hibernate.type.array.ListArrayType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import javax.persistence.*


/**
 * TODO add version column for future updates
 * @see https://github.com/vladmihalcea/hibernate-types for column types
 */
@Entity
@Table(name ="great_advice", schema = "fucking_great_advice", catalog = "postgres")
@TypeDef(name = "list-array", typeClass = ListArrayType::class)
class GreatAdviceDTO constructor(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var text: String,
    var html: String,
    @Type(type = "list-array")
    @Column(columnDefinition = "text[]")
    var tags: List<String>,
    var conclusions: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GreatAdviceDTO

        if (id != other.id) return false
        if (text != other.text) return false
        if (html != other.html) return false
        if (!tags.equals(other.tags)) return false
        if (conclusions != other.conclusions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + text.hashCode()
        result = 31 * result + html.hashCode()
        result = 31 * result + tags.hashCode()
        result = 31 * result + conclusions.hashCode()
        return result
    }

    override fun toString(): String {
        return "GreatAdviceDTO(id=$id, text='$text', html='$html', tags=$tags, conclusions='$conclusions')"
    }
}
