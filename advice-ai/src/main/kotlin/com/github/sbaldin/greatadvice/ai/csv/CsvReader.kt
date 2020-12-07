package com.github.sbaldin.greatadvice.ai.csv

import com.github.sbaldin.greatadvice.ai.stem.getYandexStemmer
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator
import org.deeplearning4j.text.sentenceiterator.SentenceIterator
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor
import org.slf4j.LoggerFactory
import ru.stachek66.nlp.mystem.holding.MyStem
import ru.stachek66.nlp.mystem.holding.Request
import ru.stachek66.nlp.mystem.model.Info
import scala.None
import scala.Option
import scala.collection.JavaConversions
import java.io.File
import java.lang.IllegalArgumentException
import java.util.*


class AdviceLineIterator(
    file: File,
    preProcessor: SentencePreProcessor = StemSentenceProcessor(),
    private val delegate: BasicLineIterator = BasicLineIterator(file)
) : SentenceIterator by delegate {
    init {
        delegate.preProcessor = preProcessor
    }
}

open class CsvSentencePreProcessor : SentencePreProcessor {
    /**
     * Match all comma not inside []
     */
    private val splitter = Regex(",(?![^\\[]* \\])")

    /**
     * Match all except letters and whitespace
     * \p{L} matches any kind of letter from any language
     * \s matches any whitespace character
     */
    private val replacer = Regex("[^\\p{L}\\s+]")
    override fun preProcess(sentence: String): String {
        //First item id
        //Second sentence
        //Third tags
        val csvLines = sentence.split(splitter)
        if (csvLines.size > 1) {
            val adviceSentence = csvLines[1]
            // remove all special characters
            val result = adviceSentence.replace(replacer, "").toLowerCase(Locale("ru"))
            log.debug("PreProcess input: $sentence output:  $result")
            return result
        } else
            throw IllegalArgumentException("Sentence has wrong delimiter: ${csvLines}!")
    }

    companion object {
        val log = LoggerFactory.getLogger(CsvSentencePreProcessor::class.java)
    }
}

class StemSentenceProcessor(
    private val stemAnalyzer: MyStem = getYandexStemmer(),
    /**
     * Skip stemmization of following words
     */
    private val skippedWords: Set<String> = defaultSkippingWords()
) : CsvSentencePreProcessor() {


    override fun preProcess(sentence: String): String {
        val csvSentence = super.preProcess(sentence)

        val stemmedSentence: Iterable<Info> = JavaConversions.asJavaIterable(
            stemAnalyzer
                .analyze(Request.apply(csvSentence))
                .info()
                .toIterable()
        )
        val result = stemmedSentence.asSequence().map { getInitialFormOfSkippedWord(it) }.joinToString(separator = " ")
        log.debug("PreProcess input: $sentence output:  $result")
        return result
    }

    private fun getInitialFormOfSkippedWord(stemmedWord: Info): String {
        val result = if (stemmedWord.initial() in skippedWords) {
            stemmedWord.initial()
        } else {
            if (stemmedWord.lex().isEmpty) {
                stemmedWord.initial()
            } else {
                stemmedWord.lex().get()
            }
        }
        log.debug("Getting initial form of skipped word: $result ")
        return result
    }

    companion object {
        /**
         * Sometimes for some words stemmer works incorrectly, so just skip stemmezation of such words
         */
        fun defaultSkippingWords() = setOf("нихуя", "блядь") // bad|filthy words are ok here
        val log = LoggerFactory.getLogger(CsvSentencePreProcessor::class.java)
    }
}
